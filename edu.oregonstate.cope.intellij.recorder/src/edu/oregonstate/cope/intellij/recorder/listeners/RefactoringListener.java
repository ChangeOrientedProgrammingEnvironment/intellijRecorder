package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.usageView.UsageInfo;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.intellij.recorder.RecorderPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        Map argumentsMap = constructArgumentsMap(refactoringEventData);

        recorder.getClientRecorder().recordRefactoring(s, null);
    }

    private Map constructArgumentsMap(RefactoringEventData refactoringEventData) {
        Collection<UsageInfo> usageInfo = refactoringEventData.getUserData(RefactoringEventData.USAGE_INFOS_KEY);
        PsiElement psiElement = refactoringEventData.getUserData(RefactoringEventData.PSI_ELEMENT_KEY);
        PsiElement[] elementArray = refactoringEventData.getUserData(RefactoringEventData.PSI_ELEMENT_ARRAY_KEY);

        Map<String, Object> argumentsMap = newMap();

        if(psiElement != null){
            argumentsMap.put("psiElement", RecorderPsiUtil.getQualifiedName(psiElement));
        }

        if (elementArray != null){
            Map<String, Object> elementsArray = newMap();

            for (PsiElement element : elementArray){
                if (element instanceof PsiStatement)
                    System.err.println("ref arg map not implemented for statements");
                else
                    elementsArray.put("element", RecorderPsiUtil.getQualifiedName(element));
            }

            argumentsMap.put("elements", elementArray);
        }

        return argumentsMap;
    }

    private HashMap<String, Object> newMap() {
        return new HashMap<String, Object>();
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