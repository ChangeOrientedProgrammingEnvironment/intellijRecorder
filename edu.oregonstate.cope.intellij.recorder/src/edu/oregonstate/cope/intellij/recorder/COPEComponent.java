package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.EditorFactory;
import edu.oregonstate.cope.clientRecorder.ChangePersister;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.clientRecorder.fileOps.EventFilesProvider;
import edu.oregonstate.cope.clientRecorder.fileOps.SimpleFileProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ApplicationComponent {

    private ClientRecorder recorder;

    public COPEComponent() {
    }

    public void initComponent() {
        ChangePersister changePersister = ChangePersister.instance();
        changePersister.setFileManager(new SimpleFileProvider("./test.json"));

        recorder = new ClientRecorder();
        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(recorder));
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }
}
