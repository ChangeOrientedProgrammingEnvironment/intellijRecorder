package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class EditorFactoryListener implements com.intellij.openapi.editor.event.EditorFactoryListener {

    private ClientRecorder recorder;
    private String basePath;

    public EditorFactoryListener(ClientRecorder recorder, String basePath) {
        this.recorder = recorder;
        this.basePath = basePath;
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        String filePath = getPathOfAffectedFile(event);
        if (filePath == null)
            return;
        if (filePath.startsWith(basePath + "./cope"))
            return;
        Document document = event.getEditor().getDocument();
        document.addDocumentListener(new DocumentListener(filePath, recorder));

        recorder.recordFileOpen(filePath);
    }

    private String getPathOfAffectedFile(EditorFactoryEvent event) {
        VirtualFile baseDir = event.getEditor().getProject().getBaseDir();
        VirtualFile file = FileDocumentManager.getInstance().getFile(event.getEditor().getDocument());
        if (file == null)
            return null;
        return file.getCanonicalPath();
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        String filePath = getPathOfAffectedFile(event);

        recorder.recordFileClose(filePath);
    }
}
