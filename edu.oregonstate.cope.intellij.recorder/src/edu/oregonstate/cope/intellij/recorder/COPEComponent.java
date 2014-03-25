package edu.oregonstate.cope.intellij.recorder;

import com.intellij.ide.impl.dataRules.VirtualFileArrayRule;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.vfs.*;
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
        SimpleFileProvider fileProvider = new SimpleFileProvider("test.json");
        fileProvider.setRootDirectory(".");

        ChangePersister changePersister = ChangePersister.instance();
        changePersister.setFileManager(fileProvider);

        recorder = new ClientRecorder();
        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(recorder));

        VirtualFileManager.getInstance().addVirtualFileListener(new FileListener(recorder));
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }
}
