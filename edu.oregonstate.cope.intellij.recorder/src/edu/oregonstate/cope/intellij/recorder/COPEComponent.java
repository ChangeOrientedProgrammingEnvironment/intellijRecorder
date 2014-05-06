package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.clientRecorder.Uninstaller;
import edu.oregonstate.cope.fileSender.FileSender;
import edu.oregonstate.cope.fileSender.FileSenderParams;
import edu.oregonstate.cope.intellij.recorder.installation.IJInstaller;
import edu.oregonstate.cope.intellij.recorder.installation.IJInstallerHelper;
import edu.oregonstate.cope.intellij.recorder.launch.COPEBeforeRunTask;
import edu.oregonstate.cope.intellij.recorder.launch.COPEBeforeRunTaskProvider;
import edu.oregonstate.cope.intellij.recorder.launch.COPERunManagerListener;
import edu.oregonstate.cope.intellij.recorder.listeners.*;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;

import java.io.IOException;
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
    private CommandExecutionListener commandListener;
    private RefactoringListener refactoringListener;
    private MyFileEditorManagerListener fileEditorListener;
    private FilelessDocumentListener documentListener;

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

        Uninstaller uninstaller = recorder.getUninstaller();

        if (uninstaller.isUninstalled())
            return;

        if (uninstaller.shouldUninstall())
            performUninstall(uninstaller);
        else
            performStartup();
    }

    private void performUninstall(Uninstaller uninstaller) {
        uninstaller.setUninstall();

        String title = "COPE recorder shutting down";
        String message = "The time allotted for the study has expired. "
                + "The recorder plugin has shut down permanently and you may delete it if you wish to do so. "
                + "\n\nThank you for your participation!";

        Messages.showInfoMessage(project, message, title);

    }

    private void performStartup() {
        if (recorder.isFirstStart()) {
            initWorkspace();
        }

        runInstaller();

        registerCommandListener();

        registerRefactoringListener();

        registerEditorDocumentListeners();

        registerFileListener();

        registerLaunchListener();

        initFileSender();

        addUpdateURLIfAbsent();

        doStatusBarIcon();
    }

    private void registerRefactoringListener() {
        refactoringListener = new RefactoringListener(recorder);

        project.getMessageBus().connect().subscribe(RefactoringEventListener.REFACTORING_EVENT_TOPIC, refactoringListener);
    }

    private void runInstaller() {
        try {
            new IJInstaller(recorder, new IJInstallerHelper(this)).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerCommandListener() {
        commandListener = new CommandExecutionListener(this);
        ActionManager.getInstance().addAnActionListener(commandListener);
    }

    private void registerEditorDocumentListeners() {
        fileEditorListener = new MyFileEditorManagerListener(this, recorder.getClientRecorder());
        FileEditorManager.getInstance(project).addFileEditorManagerListener(fileEditorListener);

        documentListener = new FilelessDocumentListener(getCommandListener(), getRefactoringListener(), recorder.getClientRecorder());
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(documentListener);
    }

    private void registerFileListener() {
        fileListener = new FileListener(this, recorder);
        VirtualFileManager.getInstance().addVirtualFileListener(fileListener);
    }

    private void registerLaunchListener() {
        runManager = (RunManagerEx) RunManagerEx.getInstance(project);

        runManager.addRunManagerListener(new COPERunManagerListener());

        beforeRunTaskProvider = getBeforeRunTaskProvider();
        if (beforeRunTaskProvider != null) {
            providerID = beforeRunTaskProvider.getId();

            for (RunConfiguration runConfiguration : runManager.getAllConfigurationsList()) {
                addCOPETaskToRunConfiguration(runConfiguration);
            }
        }
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
        if (recorder.getUninstaller().isUninstalled())
            return;

        EditorFactory.getInstance().getEventMulticaster().removeDocumentListener(documentListener);
        VirtualFileManager.getInstance().removeVirtualFileListener(fileListener);
        FileEditorManager.getInstance(project).removeFileEditorManagerListener(fileEditorListener);
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

    public boolean ignoreFile(VirtualFile file) {
        if (!fileIsInProject(file)) {
            return true;
        }

        if (fileIsInCOPEStructure(file)) {
            return true;
        }

        return false;
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

    public RefactoringListener getRefactoringListener(){
        return refactoringListener;
    }

    public Project getProject() {
        return project;
    }

    public String getPluginVersion() {
        return PluginManager.getPlugin(PluginId.getId(COPEComponent.ID)).getVersion();
    }


}
