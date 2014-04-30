package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by mihai on 4/24/14.
 */
public class RefactoringListener implements RefactoringEventListener {
    private boolean isRefactorinInProgress;
    private RecorderFacade recorder;

    public RefactoringListener(RecorderFacade recorder) {
        this.recorder = recorder;
    }

    @Override
    public void refactoringStarted(@NotNull String s, @Nullable RefactoringEventData refactoringEventData) {
        isRefactorinInProgress = true;

        recorder.getClientRecorder().recordRefactoring(s, null);
    }

    @Override
    public void refactoringDone(@NotNull String s, @Nullable RefactoringEventData refactoringEventData) {
        isRefactorinInProgress = false;

        recorder.getClientRecorder().recordRefactoringEnd(s, null);
    }

    @Override
    public void conflictsDetected(@NotNull String s, @NotNull RefactoringEventData refactoringEventData) {

    }

    @Override
    public void undoRefactoring(@NotNull String s) {
        recorder.getClientRecorder().recordRefactoringUndo(s, null);
    }

    public boolean isRefactoringInProgress(){
        return isRefactorinInProgress;
    }
}