package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.Extensions;
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
import org.json.simple.JSONObject;
import org.quartz.SchedulerException;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.List;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ProjectComponent {

    public static final String ID = "edu.oregonstate.cope.intellij.recorder";

    COPEStatus status;
    protected Path workspaceDirectory;
    protected Path permanentDirectory;
    private static final String SURVEY_FILENAME = "survey.txt";
    public final static String EMAIL_FILENAME = "email.txt";

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

        workspaceDirectory = storageManager.getLocalStorage().getAbsoluteFile().toPath();
        permanentDirectory = storageManager.getBundleStorage().getAbsoluteFile().toPath();


        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            status = new COPEStatus();
            statusBar.addWidget(status);
        }


        try {
            CheckIfSurveyExists();
        } catch (IOException e) {
            //WHAT SHOULD WE DO WITH THIS ERROR?
            e.printStackTrace();
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

    private void CheckIfSurveyExists() throws IOException {
        String fileName = getFileName();
        File workspaceFile = workspaceDirectory.resolve(fileName).toFile();
        File permanentFile = permanentDirectory.resolve(fileName).toFile();

        if (workspaceFile.exists() && permanentFile.exists()) {
            // System.out.println(this.getClass() + " both files exist");
            //DO NOTHING
        } else if (!workspaceFile.exists() && permanentFile.exists()) {
            // System.out.println(this.getClass() + " only permanent");
            doOnlyPermanentFileExists(workspaceFile, permanentFile);
        } else if (workspaceFile.exists() && !permanentFile.exists()) {
            // System.out.println(this.getClass() + " only workspace");
            doOnlyWorkspaceFileExists(workspaceFile, permanentFile);
        } else if (!workspaceFile.exists() && !permanentFile.exists()) {
            // System.out.println(this.getClass() + " neither files exist");
            doNoFileExists(workspaceFile, permanentFile);
        }
    }

    protected void doOnlyWorkspaceFileExists(File workspaceFile, File permanentFile) throws IOException {
        Files.copy(workspaceFile.toPath(), permanentFile.toPath());
    }

    protected void doOnlyPermanentFileExists(File workspaceFile, File permanentFile) throws IOException {
        Files.copy(permanentFile.toPath(), workspaceFile.toPath());
    }


    protected String getFileName() {
        return SURVEY_FILENAME;
    }

    protected void doNoFileExists(final File workspaceFile, final File permanentFile) throws IOException {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Survey dialog = new Survey();
                dialog.pack();
                dialog.setVisible(true);


                JSONObject survey = dialog.getSurveyResults();
                String email = dialog.getEmail();

                try {
                    writeContentsToFile(workspaceFile.toPath(), survey.toString());
                    writeContentsToFile(permanentFile.toPath(), survey.toString());
                    handleEmail(email);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void handleEmail(String email) throws IOException {
        doFor(permanentDirectory, email);
        doFor(workspaceDirectory, email);
    }

    private void doFor(Path parentDirectory, String email) throws IOException {
        Path emailFile = parentDirectory.resolve(EMAIL_FILENAME);
        Files.deleteIfExists(emailFile);
        writeContentsToFile(emailFile, email);
    }

    protected void writeContentsToFile(Path filePath, String fileContents) throws IOException {
        Files.write(filePath, fileContents.getBytes(), StandardOpenOption.CREATE);
    }

	public CommandExecutionListener getCommandListener() {
		return commandListener;
	}

    public Project getProject() {
        return project;
    }
}
