package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caius on 3/3/14.
 */
public class EditorFactoryListener implements com.intellij.openapi.editor.event.EditorFactoryListener {

    private COPEComponent copeComponent;
    private ClientRecorder recorder;
    private String basePath;

    private Map<Document, DocumentListener> listenerMap;
    private Map<String, Document> documentMap;

    public EditorFactoryListener(COPEComponent copeComponent, ClientRecorder recorder) {
        this.copeComponent = copeComponent;
        this.recorder = recorder;
        this.listenerMap = new HashMap<Document, DocumentListener>();
        this.documentMap = new HashMap<String, Document>();
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        String filePath = getPathOfAffectedFile(event);
        if (filePath == null)
            return;
        if (copeComponent.fileIsInCOPEStructure(getAffectedVirtualFile(event))) {
            return;
        }

        final Document document = event.getEditor().getDocument();
        final DocumentListener documentListener = new DocumentListener(filePath, copeComponent.getCommandListener(), copeComponent.getRefactoringListener(), recorder);
        document.addDocumentListener(documentListener);
        documentMap.put(filePath, document);
        listenerMap.put(document, documentListener);

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

        Document document = documentMap.get(filePath);
        if (document == null)
            return;
        DocumentListener documentListener = listenerMap.get(document);
        document.removeDocumentListener(documentListener);
    }
}
