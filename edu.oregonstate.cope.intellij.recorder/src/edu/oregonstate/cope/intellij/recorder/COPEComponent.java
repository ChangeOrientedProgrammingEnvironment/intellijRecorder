package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ApplicationComponent {

    COPEStatus status;

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
                Survey dialog = new Survey();
                dialog.pack();
                dialog.setVisible(true);
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
}
