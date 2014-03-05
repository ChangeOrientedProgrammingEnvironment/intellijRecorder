package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.EditorFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ApplicationComponent {

    public COPEComponent() {
    }

    public void initComponent() {
        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener());
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }
}
