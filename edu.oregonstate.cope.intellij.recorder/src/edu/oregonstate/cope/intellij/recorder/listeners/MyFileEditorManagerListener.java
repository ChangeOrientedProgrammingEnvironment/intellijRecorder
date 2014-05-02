package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mihai on 5/1/14.
 */
public class MyFileEditorManagerListener implements FileEditorManagerListener {
    private final COPEComponent copeComponent;
    private final ClientRecorder clientRecorder;
    private final Map<VirtualFile, DocumentListener> listenerMap;

    public MyFileEditorManagerListener(COPEComponent copeComponent, ClientRecorder clientRecorder) {
        this.copeComponent = copeComponent;
        this.clientRecorder = clientRecorder;

        listenerMap = new HashMap<>();
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        String filePath = getPathOfAffectedFile(file);
        Document document = FileDocumentManager.getInstance().getDocument(file);

        if (filePath == null) {
            return;
        }

        if (copeComponent.fileIsInCOPEStructure(file)) {
            return;
        }

        if (listenerMap.containsKey(file))
            return;

        final DocumentListener documentListener = new DocumentListener(filePath, copeComponent.getCommandListener(), copeComponent.getRefactoringListener(), clientRecorder);
        document.addDocumentListener(documentListener);

        listenerMap.put(file, documentListener);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        String filePath = getPathOfAffectedFile(file);
        Document document = FileDocumentManager.getInstance().getDocument(file);

        if (filePath == null) {
            return;
        }

        clientRecorder.recordFileClose(filePath);

        if (document == null)
            return;

        DocumentListener documentListener = listenerMap.get(file);
        document.removeDocumentListener(documentListener);

        listenerMap.remove(file);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
    }

    private String getPathOfAffectedFile(VirtualFile file) {
        return file.getCanonicalPath();
    }
}
