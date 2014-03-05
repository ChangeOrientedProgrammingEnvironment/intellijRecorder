package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.editor.event.DocumentEvent;
import edu.oregonstate.cope.clientRecorder.ChangeOrigin;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;

/**
 * Created by caius on 3/3/14.
 */
public class DocumentListener implements com.intellij.openapi.editor.event.DocumentListener {

    private String filePath;
    private ClientRecorder recorder;

    public DocumentListener(String filePath, ClientRecorder recorder) {
        this.filePath = filePath;
        this.recorder = recorder;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        int offset = event.getOffset();
        int length = event.getOldLength();
        CharSequence text = event.getNewFragment();

        recorder.recordTextChange(text.toString(), offset, length, filePath, ChangeOrigin.USER);
    }
}
