package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class EditorFactoryListener implements com.intellij.openapi.editor.event.EditorFactoryListener {

    private COPEComponent copeComponent;
    private ClientRecorder recorder;
    private String basePath;

    public EditorFactoryListener(COPEComponent copeComponent, ClientRecorder recorder) {
        this.copeComponent = copeComponent;
        this.recorder = recorder;
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        String filePath = getPathOfAffectedFile(event);
        if (filePath == null)
            return;
        if (copeComponent.fileIsInCOPEStructure(getAffectedVirtualFile(event))) {
            return;
        }

        Document document = event.getEditor().getDocument();
        DocumentListener documentListener = new DocumentListener(filePath, copeComponent.getCommandListener(), recorder);
        document.addDocumentListener(documentListener);

        recorder.recordFileOpen(filePath);
    }

    private String getPathOfAffectedFile(EditorFactoryEvent event) {
        VirtualFile file = getAffectedVirtualFile(event);
        if (file == null) {
            return null;
        }
        return file.getCanonicalPath();
    }

    private VirtualFile getAffectedVirtualFile(EditorFactoryEvent event) {
        VirtualFile baseDir = event.getEditor().getProject().getBaseDir();
        return FileDocumentManager.getInstance().getFile(event.getEditor().getDocument());
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        String filePath = getPathOfAffectedFile(event);

        recorder.recordFileClose(filePath);
    }
}
