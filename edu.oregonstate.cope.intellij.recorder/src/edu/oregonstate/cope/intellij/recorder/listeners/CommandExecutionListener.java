package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import sun.plugin.dom.core.CoreConstants;

//copy pasted from edu.oregonstate.cope.eclipse.listeners.CommandExecutionListener
public class CommandExecutionListener implements AnActionListener {

    private static boolean cutInProgress = false;
    private static boolean pasteInProgress = false;
    private static boolean undoInProgress = false;
    private static boolean redoInProgress = false;
    private final COPEComponent copeComponent;

    public CommandExecutionListener(COPEComponent copeComponent) {
        this.copeComponent = copeComponent;
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {

        if (!copeComponent.getProject().equals(anActionEvent.getProject()))
            return;

		if (isCopy(anAction)) {
            recordCopy(dataContext, anActionEvent);
        }
        if (isCut(anAction))
            cutInProgress = true;
        if (isPaste(anAction))
            pasteInProgress = true;
        if (isUndo(anAction))
            undoInProgress = true;
        if (isRedo(anAction))
            redoInProgress = true;
    }

    private void recordCopy(DataContext dataContext, AnActionEvent anActionEvent) {
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        if (editor == null)
            return;

        SelectionModel selection = editor.getSelectionModel();
        String selectedText = selection.getSelectedText();

        if (selectedText == null)
            return;

        String path = getPath(editor);

        copeComponent.getRecorder().getClientRecorder().recordCopy(path, selection.getSelectionStart(), selectedText.length(), selectedText);
    }

    private String getPath(Editor editor) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        return file.getCanonicalPath();
    }

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (isCut(anAction))
            cutInProgress = false;
        if (isPaste(anAction))
            pasteInProgress = false;
        if (isUndo(anAction))
            undoInProgress = false;
        if (isRedo(anAction))
            redoInProgress = false;
    }

    @Override
    public void beforeEditorTyping(char c, DataContext dataContext) {

    }

    private boolean isCopy(AnAction action) {
        return action instanceof com.intellij.ide.actions.CopyAction
                || action instanceof com.intellij.openapi.editor.actions.CopyAction;
    }

    private boolean isCut(AnAction action) {
        return action instanceof com.intellij.ide.actions.CutAction
                || action instanceof com.intellij.openapi.editor.actions.CutAction;
    }

    private boolean isPaste(AnAction action) {
        return action instanceof com.intellij.ide.actions.PasteAction
                || action instanceof com.intellij.openapi.editor.actions.PasteAction
				|| action instanceof com.intellij.openapi.editor.actions.SimplePasteAction
				|| action instanceof com.intellij.openapi.editor.actions.MultiplePasteAction;
    }

    private boolean isUndo(AnAction action) {
        return action instanceof com.intellij.ide.actions.UndoAction;
    }

    private boolean isRedo(AnAction action) {
        return action instanceof com.intellij.ide.actions.RedoAction;
    }

    public static boolean isCutInProgress() {
        return cutInProgress;
    }

    public static boolean isPasteInProgress() {
        return pasteInProgress;
    }

    public static boolean isUndoInProgress() {
        return undoInProgress;
    }

    public static boolean isRedoInProgress() {
        return redoInProgress;
    }
}