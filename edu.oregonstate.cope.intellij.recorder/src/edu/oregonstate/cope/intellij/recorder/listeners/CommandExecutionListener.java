package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;

//copy pasted from edu.oregonstate.cope.eclipse.listeners.CommandExecutionListener
public class CommandExecutionListener implements AnActionListener {

    private static boolean saveInProgress = false;
    private static boolean cutInProgress = false;
    private static boolean pasteInProgress = false;
    private static boolean undoInProgress = false;
    private static boolean redoInProgress = false;

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (isCopy(anAction)) {
            recordCopy();
        }
        if (isCut(anAction)) {
            cutInProgress = true;
        }
        if (isPaste(anAction))
            pasteInProgress = true;
        if (isUndo(anAction))
            undoInProgress = true;
        if (isRedo(anAction))
            redoInProgress = true;
        if (isFileSave(anAction))
            saveInProgress  = true;
    }

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        if (isFileSave(anAction))
            saveInProgress = false;
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
                || action instanceof com.intellij.openapi.editor.actions.PasteAction;
    }

    private boolean isUndo(AnAction action) {
        return action instanceof com.intellij.ide.actions.PasteAction
                || action instanceof com.intellij.openapi.editor.actions.PasteAction;
    }

    private boolean isRedo(AnAction action) {
        return action instanceof com.intellij.ide.actions.RedoAction;
    }

    private void recordCopy() {
    }


    private boolean isFileSave(AnAction action) {
        return false;
    }

    public static boolean isSaveInProgress() {
        return saveInProgress;
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