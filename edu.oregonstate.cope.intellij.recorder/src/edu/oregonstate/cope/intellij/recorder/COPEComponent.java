package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.fileSender.FileSender;
import edu.oregonstate.cope.fileSender.FileSenderParams;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;
import java.text.ParseException;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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

    public COPEComponent(Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    public void projectOpened() {
        storageManager = new IntelliJStorageManager(project);
        recorder = new RecorderFacade(storageManager, IDE);

        if (recorder.isFirstStart())
            initWorkspace();

        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(this, recorder.getClientRecorder()));

        VirtualFileManager.getInstance().addVirtualFileListener(new FileListener(this, recorder));

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



    @Override
    public void projectClosed() {

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
            //DO NOTHING
        } else if (!workspaceFile.exists() && permanentFile.exists()) {
            doOnlyPermanentFileExists(workspaceFile, permanentFile);
        } else if (workspaceFile.exists() && !permanentFile.exists()) {
            doOnlyWorkspaceFileExists(workspaceFile, permanentFile);
        } else if (!workspaceFile.exists() && !permanentFile.exists()) {
            doNoFileExists(workspaceFile, permanentFile);
        }
    }

    private void initWorkspace() {
        takeSnapshotOfProject(project);
    }

    private void takeSnapshotOfProject(Project project) {
        new EclipseExporter(project).export();
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

}
