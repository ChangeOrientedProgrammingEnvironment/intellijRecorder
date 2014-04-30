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

        System.out.println("started: " + s);
    }

    @Override
    public void refactoringDone(@NotNull String s, @Nullable RefactoringEventData refactoringEventData) {
        isRefactorinInProgress = false;

        System.out.println("ended: " + s);
    }

    @Override
    public void conflictsDetected(@NotNull String s, @NotNull RefactoringEventData refactoringEventData) {

    }

    @Override
    public void undoRefactoring(@NotNull String s) {
        System.out.println("undone:" + s);
    }

    public boolean isRefactoringInProgress(){
        return isRefactorinInProgress;
    }
}