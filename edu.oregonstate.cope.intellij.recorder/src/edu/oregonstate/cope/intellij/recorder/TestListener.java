package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestStatusListener;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.ProjectManager;

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
        public static final Result ERROR= new Result("Esrror"); //$NON-NLS-1$
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
        int i = 1;

        for (AbstractTestProxy test: root.getAllTests()){
            if (!test.isLeaf())
                continue;
            print(test.getName());
            print(test.isLeaf());
            print(test.getDuration() + "");
            print("isPassed:" + test.isPassed());
            print("isDefect:" + test.isDefect());
        }
    }

    private void print(Object s){
        System.out.println(s);
    }
}
