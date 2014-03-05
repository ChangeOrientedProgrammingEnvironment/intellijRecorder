package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class EditorFactoryListener implements com.intellij.openapi.editor.event.EditorFactoryListener {


    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        VirtualFile baseDir = event.getEditor().getProject().getBaseDir();
        Document document = event.getEditor().getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String filePath = file.getCanonicalPath();
        document.addDocumentListener(new DocumentListener(filePath));

        System.out.println("Editor opened: " + filePath);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
    }
}
