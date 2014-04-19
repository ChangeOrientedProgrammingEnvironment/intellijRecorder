package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.editor.event.DocumentEvent;
import edu.oregonstate.cope.clientRecorder.ChangeOrigin;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;

/**
 * Created by caius on 3/3/14.
 */
public class DocumentListener implements com.intellij.openapi.editor.event.DocumentListener {

	private String filePath;
	private ClientRecorder recorder;
	private final CommandExecutionListener commandListener;

    public DocumentListener(String filePath, CommandExecutionListener commanListener, ClientRecorder recorder) {
        this.filePath = filePath;
        this.recorder = recorder;
		this.commandListener = commanListener;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        int offset = event.getOffset();
        int length = event.getOldLength();
        CharSequence text = event.getNewFragment();

		String changeOrigin = ChangeOrigin.USER;
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
}
