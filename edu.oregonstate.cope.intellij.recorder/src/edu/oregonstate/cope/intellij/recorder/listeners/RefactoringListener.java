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
    public static final int MAXIMUM_REFACTORING_EXECUTION_TIME_MILLIS = 4000;

    private boolean isRefactorinInProgress;
    private long refactoringStartTime; //because IntelliJ misses refactoring events ...

    private RecorderFacade recorder;

    public RefactoringListener(RecorderFacade recorder) {
        this.recorder = recorder;
    }

    @Override
    public void refactoringStarted(@NotNull String s, @Nullable RefactoringEventData refactoringEventData) {
        isRefactorinInProgress = true;
        refactoringStartTime = System.currentTimeMillis();

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
        long currentTimeMillis = System.currentTimeMillis();
        boolean isRefactoringRecent = (currentTimeMillis - refactoringStartTime) < MAXIMUM_REFACTORING_EXECUTION_TIME_MILLIS;

        return isRefactorinInProgress && isRefactoringRecent;
    }
}