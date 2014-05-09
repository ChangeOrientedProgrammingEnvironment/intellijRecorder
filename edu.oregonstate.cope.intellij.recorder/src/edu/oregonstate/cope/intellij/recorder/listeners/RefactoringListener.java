package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringEventData;
import com.intellij.refactoring.listeners.RefactoringEventListener;
import com.intellij.usageView.UsageInfo;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import edu.oregonstate.cope.intellij.recorder.RecorderPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by mihai on 4/24/14.
 */
public class RefactoringListener implements RefactoringEventListener {
    public static final int MAXIMUM_REFACTORING_EXECUTION_TIME_MILLIS = 4000;

    private boolean isRefactorinInProgress;
    private long refactoringStartTime; //because IntelliJ misses refactoring events ...

	private COPEComponent copeComponent;
	private RecorderFacade recorder;

    public RefactoringListener(COPEComponent copeComponent, RecorderFacade recorder) {
		this.copeComponent = copeComponent;
		this.recorder = recorder;
    }

    @Override
    public void refactoringStarted(@NotNull String s, @Nullable RefactoringEventData refactoringEventData) {
        isRefactorinInProgress = true;
        refactoringStartTime = System.currentTimeMillis();

        Map argumentsMap = constructArgumentsMap(refactoringEventData);

        recorder.getClientRecorder().recordRefactoring(s, argumentsMap);
    }

    private Map constructArgumentsMap(RefactoringEventData refactoringEventData) {
        Collection<UsageInfo> usageInfo = refactoringEventData.getUserData(RefactoringEventData.USAGE_INFOS_KEY);
        PsiElement psiElement = refactoringEventData.getUserData(RefactoringEventData.PSI_ELEMENT_KEY);
        PsiElement[] elementArray = refactoringEventData.getUserData(RefactoringEventData.PSI_ELEMENT_ARRAY_KEY);

        Map<String, Object> argumentsMap = newMap();

        if(psiElement != null){
            argumentsMap.put("psiElement", constructPSIMap(psiElement));
        }

        if (elementArray != null){
			List<Object> elementsList = new ArrayList<>();

            for (PsiElement element : elementArray){
				elementsList.add(constructPSIMap(element));
            }

            argumentsMap.put("elements", elementsList);
        }

        return argumentsMap;
    }

	private Map constructPSIMap(PsiElement psiElement) {
		Map psiMap = newMap();

		String canonicalPath = psiElement.getContainingFile().getVirtualFile().getCanonicalPath();
		String truncatedPath = copeComponent.truncateAbsolutePath(canonicalPath);

		psiMap.put("psiType", psiElement.getClass().getName());
		psiMap.put("qualified", RecorderPsiUtil.getQualifiedName(psiElement));
		psiMap.put("file", truncatedPath);
		psiMap.put("offset", psiElement.getTextOffset() + "");
		psiMap.put("length", psiElement.getTextLength() + "");

		return psiMap;
	}

	private HashMap<String, Object> newMap() {
        return new HashMap<String, Object>();
    }

    @Override
    public void refactoringDone(@NotNull String s, @Nullable RefactoringEventData refactoringEventData) {
        isRefactorinInProgress = false;

		Map argumentsMap = constructArgumentsMap(refactoringEventData);

        recorder.getClientRecorder().recordRefactoringEnd(s, argumentsMap);
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