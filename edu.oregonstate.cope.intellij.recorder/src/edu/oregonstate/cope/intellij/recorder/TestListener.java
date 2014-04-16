package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.Location;
import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.info.MethodLocation;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestStatusListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;

import java.util.Arrays;

/**
 * Created by mihai on 4/8/14.
 */
public class TestListener extends TestStatusListener{

    //copy paste from org.eclipse.jdt.junit.model.ITestElement.Result
    public static final class Result {
        /** state that describes that the test result is undefined */
        public static final Result UNDEFINED= new Result("Undefined"); //$NON-NLS-1$
        /** state that describes that the test result is 'OK' */
        public static final Result OK= new Result("OK"); //$NON-NLS-1$
        /** state that describes that the test result is 'Error' */
        public static final Result ERROR= new Result("Error"); //$NON-NLS-1$
        /** state that describes that the test result is 'Failure' */
        public static final Result FAILURE= new Result("Failure"); //$NON-NLS-1$
        /** state that describes that the test result is 'Ignored' */
        public static final Result IGNORED= new Result("Ignored"); //$NON-NLS-1$

        private String fName;
        private Result(String name) {
            fName= name;
        }
        public String toString() {
            return fName;
        }
    }

    @Override
    public void testSuiteFinished(AbstractTestProxy root) {
        for (AbstractTestProxy test: root.getAllTests()){
            if (!test.isLeaf())
                continue;

            recordTestRun(test);
        }
    }

    private void recordTestRun(AbstractTestProxy test){
        Project project= getProject(test);

        String qualifiedTestName = constructQualifiedName(test, project);
        Result testResult = computeTestResult(test);
        Double testTime = getTestTimeInSeconds(test);

        COPEComponent copeComponent = project.getComponent(COPEComponent.class);
        copeComponent.getRecorder().getClientRecorder().recordTestRun(qualifiedTestName, testResult.toString(), testTime);
    }

    private Double getTestTimeInSeconds(AbstractTestProxy test) {
        return test.getDuration() / 1000.0;
    }

    private String constructQualifiedName(AbstractTestProxy test, Project project) {
        Location location = getLocation(test, project);

        if(location instanceof MethodLocation) {
            MethodLocation methodLocation = (MethodLocation) location;
            PsiClass testClass = methodLocation.getContainingClass();

            return testClass.getQualifiedName() + "." + methodLocation.getPsiElement().getName();
        }

        System.err.println("Is not a MethodLocation: " + location.getClass());
        return null;
    }

    private Project getProject(AbstractTestProxy test) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();

        for (Project openedProject : openProjects) {
            Location location = getLocation(test, openedProject);

            if (location != null) {
                return openedProject;
            }
        }

        return null;
    }

    private Location getLocation(AbstractTestProxy test, Project project){
        return test.getLocation(project, GlobalSearchScope.allScope(project));
    }

    private Result computeTestResult(AbstractTestProxy test) {
        if(test.isPassed())
            return Result.OK;

        int testMagnitude = test.getMagnitude();

        if (testMagnitude == PoolOfTestStates.ERROR_INDEX)
            return Result.ERROR;

        if (testMagnitude == PoolOfTestStates.FAILED_INDEX)
            return Result.FAILURE;

        System.err.println("!!! Undefined test state: " + testMagnitude);
        return Result.UNDEFINED;
    }
}
