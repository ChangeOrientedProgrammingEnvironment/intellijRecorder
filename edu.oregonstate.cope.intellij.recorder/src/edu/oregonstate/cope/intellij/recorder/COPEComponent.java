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
//        ChangePersister changePersister = ChangePersister.instance();
//        changePersister.setFileManager(new SimpleFileProvider("./test.json"));
//
//        recorder = new ClientRecorder();
//        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(recorder));

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
//                String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
//                Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());




                /*
                JFrame.setDefaultLookAndFeelDecorated(true);
                JFrame frame = new JFrame("[=] There's a JPanel in here! [=]");

                JPanel jp = new JPanel(new SpringLayout());
                jp.setOpaque(true);

                frame.setContentPane(jp);

                JLabel lTitle = new JLabel("Thanks you for participating in the study titled: Understanding TDD Practices.  Please answer just a few demographic questions before you begin using the plugin",JLabel.TRAILING);
                jp.add(lTitle);

                JLabel l = new JLabel("Name:",JLabel.TRAILING);
                jp.add(l);
                JTextField textField = new JTextField(10);
                l.setLabelFor(textField);
                jp.add(textField);


                //SpringUtilities.makeCompactGrid(jp,2,2,6,6,6,6);



                // The other bits and pieces that make our program a bit more stable.
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(290, 100);
                frame.setVisible(true);
                */

            }

            @Override
            public void projectClosed(Project project) {

            }
        });

/*    This will be an example for survey
      ActionManager am = ActionManager.getInstance();
        TextBoxes action = new TextBoxes();
        // Passes an instance of your custom TextBoxes class to the registerAction method of the ActionManager class.
        am.registerAction("MyPluginAction", action);
        // Gets an instance of the WindowMenu action group.
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("WindowMenu");
        // Adds a separator and a new menu command to the WindowMenu group on the main menu.
        windowM.addSeparator();
        windowM.add(action);
      */


    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }
}
