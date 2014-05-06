package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import edu.oregonstate.cope.clientRecorder.ChangeOrigin;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;

/**
 * Created by caius on 3/3/14.
 */
public class FilelessDocumentListener implements com.intellij.openapi.editor.event.DocumentListener {

    private COPEComponent copeComponent;
    private ClientRecorder recorder;

    private RefactoringListener refactoringListener;
    private final CommandExecutionListener commandListener;

    public FilelessDocumentListener(COPEComponent copeComponent, CommandExecutionListener commandListener, RefactoringListener refactoringListener, ClientRecorder recorder) {
        this.copeComponent = copeComponent;
        this.recorder = recorder;

        this.refactoringListener = refactoringListener;
		this.commandListener = commandListener;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        String filePath = getFilePath(event);

        if (filePath == null)
            return;

        int offset = event.getOffset();
        int length = event.getOldLength();
        CharSequence text = event.getNewFragment();

		String changeOrigin = ChangeOrigin.USER;

        if (refactoringListener.isRefactoringInProgress())
            changeOrigin = ChangeOrigin.REFACTORING;
		if(commandListener.isCutInProgress())
			changeOrigin = ChangeOrigin.CUT;
		if(commandListener.isPasteInProgress())
			changeOrigin = ChangeOrigin.PASTE;
		if(commandListener.isRedoInProgress())
			changeOrigin = ChangeOrigin.REDO;
		if(commandListener.isUndoInProgress())
			changeOrigin = ChangeOrigin.UNDO;

		recorder.recordTextChange(text.toString(), offset, length, filePath, changeOrigin);
    }

    private String getFilePath(DocumentEvent event) {
        Document document = event.getDocument();

        if(document == null)
            return null;

        VirtualFile file = FileDocumentManager.getInstance().getFile(document);

        if (file == null)
            return null;

        return file.getCanonicalPath();
    }
}
