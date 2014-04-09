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
