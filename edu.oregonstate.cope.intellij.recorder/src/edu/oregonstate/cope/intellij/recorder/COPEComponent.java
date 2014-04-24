package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.fileSender.FileSender;
import edu.oregonstate.cope.fileSender.FileSenderParams;
import edu.oregonstate.cope.intellij.recorder.launch.COPEBeforeRunTask;
import edu.oregonstate.cope.intellij.recorder.launch.COPEBeforeRunTaskProvider;
import edu.oregonstate.cope.intellij.recorder.launch.COPERunManagerListener;
import edu.oregonstate.cope.intellij.recorder.listeners.CommandExecutionListener;
import edu.oregonstate.cope.intellij.recorder.listeners.EditorFactoryListener;
import edu.oregonstate.cope.intellij.recorder.listeners.FileListener;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.List;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ProjectComponent {

    public static final String ID = "edu.oregonstate.cope.intellij.recorder";

    COPEStatus status;

    public final static String PREFERENCES_HOSTNAME = "hostname";
    public final static String PREFERENCES_PORT = "port";
    public final static String PREFERENCES_USERNAME = "username";
    public final static String PREFERENCES_PASSWORD = "password";

    private final String IDE = "IDEA";
    private Project project;
    private RecorderFacade recorder;
    private IntelliJStorageManager storageManager;
    private Key<COPEBeforeRunTask> providerID;
    private RunManagerEx runManager;
    private BeforeRunTaskProvider<COPEBeforeRunTask> beforeRunTaskProvider;

    private FileListener fileListener;
    private EditorFactoryListener editorFactoryListener;

    private CommandExecutionListener commandListener;

    public COPEComponent(Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    public static COPEComponent getInstance(Project project) {
        return project.getComponent(COPEComponent.class);
    }

    @Override
    public void projectOpened() {
        storageManager = new IntelliJStorageManager(project);
        recorder = new RecorderFacade(storageManager, IDE);

        if (recorder.isFirstStart())
            initWorkspace();

        commandListener = new CommandExecutionListener(this);
        ActionManager.getInstance().addAnActionListener(commandListener);

        editorFactoryListener = new EditorFactoryListener(this, recorder.getClientRecorder());
        EditorFactory.getInstance().addEditorFactoryListener(editorFactoryListener);

        fileListener = new FileListener(this, recorder);
        VirtualFileManager.getInstance().addVirtualFileListener(fileListener);

        runManager = (RunManagerEx) RunManagerEx.getInstance(project);

        runManager.addRunManagerListener(new COPERunManagerListener());

        beforeRunTaskProvider = getBeforeRunTaskProvider();
        if (beforeRunTaskProvider == null) {
            System.out.println("Could not find provider");
            return;
        }
        providerID = beforeRunTaskProvider.getId();
        for (RunConfiguration runConfiguration : runManager.getAllConfigurationsList()) {
            addCOPETaskToRunConfiguration(runConfiguration);
        }

        initFileSender();

        addUpdateURLIfAbsent();

        doStatusBarIcon();
    }

    private void doStatusBarIcon() {
        CheckRESTVersion crv = new CheckRESTVersion(this,project);
        Boolean updateReady = crv.isThereNewCOPEVersion();

        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            status = new COPEStatus(updateReady);
            statusBar.addWidget(status);
        }
    }

    private void addUpdateURLIfAbsent() {
        String updateURL = recorder.getInstallationProperties().getProperty("updateURL");
        if(!(updateURL == null)) {
            if(updateURL.isEmpty()){
                recorder.getInstallationProperties().addProperty("updateURL","http://cope.eecs.oregonstate.edu/IDEARecorder/updatePlugins.xml");
            }
        }else{
            recorder.getInstallationProperties().addProperty("updateURL","http://cope.eecs.oregonstate.edu/IDEARecorder/updatePlugins.xml");
        }
    }

    public void addCOPETaskToRunConfiguration(RunConfiguration runConfiguration) {
        List<BeforeRunTask> beforeRunTasks = runManager.getBeforeRunTasks(runConfiguration);
        if (!containsCOPEListener(beforeRunTasks)) {
            beforeRunTasks.add(beforeRunTaskProvider.createTask(runConfiguration));
            runManager.setBeforeRunTasks(runConfiguration, beforeRunTasks, true);
        }
    }

    private boolean containsCOPEListener(List<BeforeRunTask> tasks) {
        for (BeforeRunTask task : tasks)
            if (providerID.equals(task.getProviderId()))
                return true;
        return false;
    }

    private BeforeRunTaskProvider<COPEBeforeRunTask> getBeforeRunTaskProvider() {
        BeforeRunTaskProvider<COPEBeforeRunTask> beforeRunTaskProvider = null;
        BeforeRunTaskProvider<BeforeRunTask>[] extensions = Extensions.getExtensions(BeforeRunTaskProvider.EXTENSION_POINT_NAME, project);
        for (BeforeRunTaskProvider<? extends BeforeRunTask> extension : extensions) {
            String name = extension.getName();
            if (name.equals(COPEBeforeRunTaskProvider.EXTENSION_NAME))
                beforeRunTaskProvider = (BeforeRunTaskProvider<COPEBeforeRunTask>) extension;
        }
        return beforeRunTaskProvider;
    }


    @Override
    public void projectClosed() {
        VirtualFileManager.getInstance().removeVirtualFileListener(fileListener);
        EditorFactory.getInstance().removeEditorFactoryListener(editorFactoryListener);
        ActionManager.getInstance().removeAnActionListener(commandListener);

        takeSnapshotOfProject(project);
    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }

    public RecorderFacade getRecorder() {
        return recorder;
    }
    public IntelliJStorageManager getStorageManager() {
        return storageManager;
    }

    public boolean fileIsInProject(VirtualFile file) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();

        return projectFileIndex.isInContent(file);
    }

    public boolean fileIsInCOPEStructure(VirtualFile file) {
        return storageManager.isPathInManagedStorage(file.getPath());
    }

    private void initFileSender() {
        try {
            new FileSender(new FileSenderParams(
                recorder.getLogger(),
                storageManager.getLocalStorage(),
                recorder.getWorkspaceProperties(),
                recorder.getWorkspaceID()
            ));
        } catch (ParseException | SchedulerException e) {
            recorder.getLogger().error(e, e.getMessage());
        }
    }

    private void initWorkspace() {
        takeSnapshotOfProject(project);
    }

    private void takeSnapshotOfProject(Project project) {
        new EclipseExporter(project, storageManager.getLocalStorage(), recorder).export();
    }

    public void takeSnapshotOfProject(){
        takeSnapshotOfProject(project);
    }

	public CommandExecutionListener getCommandListener() {
		return commandListener;
	}

    public Project getProject() {
        return project;
    }

    public String getPluginVersion() {
        return PluginManager.getPlugin(PluginId.getId(COPEComponent.ID)).getVersion();
    }

}
