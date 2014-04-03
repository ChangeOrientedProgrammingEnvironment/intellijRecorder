package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ApplicationComponent {

    COPEStatus status;
    protected Path workspaceDirectory;
    protected Path permanentDirectory;
    private static final String SURVEY_FILENAME = "survey.txt";
    public final static String EMAIL_FILENAME = "email.txt";


    private ClientRecorder recorder;

    public COPEComponent() {
    }

    public void initComponent() {
        status = new COPEStatus();

        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter() {
            @Override
            public void projectOpened(Project project) {
                StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                if(statusBar != null){
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
            public void projectClosed(Project project) {

            }
        });
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }

//    public InstallerOperation(Path workspaceDirectory, Path permanentDirectory) {
//        this.permanentDirectory = permanentDirectory;
//        this.workspaceDirectory = workspaceDirectory;
//    }

    private void CheckIfSurveyExists() throws IOException {
        String fileName = getFileName();
        File workspaceFile = workspaceDirectory.resolve(fileName).toFile();
        File permanentFile = permanentDirectory.resolve(fileName).toFile();

        if (workspaceFile.exists() && permanentFile.exists()) {
            // System.out.println(this.getClass() + " both files exist");
            doBothFilesExists();
        }

        else if (!workspaceFile.exists() && permanentFile.exists()) {
            // System.out.println(this.getClass() + " only permanent");
            doOnlyPermanentFileExists(workspaceFile, permanentFile);
        }

        else if (workspaceFile.exists() && !permanentFile.exists()) {
            // System.out.println(this.getClass() + " only workspace");
            doOnlyWorkspaceFileExists(workspaceFile, permanentFile);
        }

        else if (!workspaceFile.exists() && !permanentFile.exists()) {
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

    protected void doBothFilesExists() {
    }

    protected String getFileName() {
        return SURVEY_FILENAME;
    }

    protected void doNoFileExists(File workspaceFile, File permanentFile) throws IOException {

        Survey dialog = new Survey();
        dialog.pack();
        dialog.setVisible(true);

        JSONObject survey = dialog.getSurveyResults();
        String email = dialog.getEmail();

//        SurveyProvider sw;
//        if (Platform.inDevelopmentMode())
//            sw = SurveyWizard.takeFakeSurvey();
//        else
//            sw = SurveyWizard.takeRealSurvey();
//
//        writeContentsToFile(workspaceFile.toPath(), sw.getSurveyResults());
//        writeContentsToFile(permanentFile.toPath(), sw.getSurveyResults());
//
//        handleEmail(sw.getEmail());
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
