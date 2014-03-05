package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.editor.event.DocumentEvent;

/**
 * Created by caius on 3/3/14.
 */
public class DocumentListener implements com.intellij.openapi.editor.event.DocumentListener {

    private String filePath;

    public DocumentListener(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        int offset = event.getOffset();
        int length = event.getOldLength();
        CharSequence text = event.getNewFragment();

        System.out.println("In file: " + filePath + ", at " + offset + ", of size " + length + " was added: " + text.toString());
    }
}
