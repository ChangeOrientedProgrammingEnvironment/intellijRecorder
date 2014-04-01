package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.RunManagerListener;
import com.intellij.execution.RunnerAndConfigurationSettings;
import org.jetbrains.annotations.NotNull;

/**
* Created by caius on 4/1/14.
*/
class COPERunManagerListener implements RunManagerListener {

    @Override
    public void runConfigurationSelected() {
        System.out.println("Run config selected");
    }

    @Override
    public void beforeRunTasksChanged() {
        System.out.println("Before run tasks changed");

    }

    @Override
    public void runConfigurationAdded(@NotNull RunnerAndConfigurationSettings settings) {
        System.out.println("Run config added");
    }

    @Override
    public void runConfigurationRemoved(@NotNull RunnerAndConfigurationSettings settings) {

    }

    @Override
    public void runConfigurationChanged(@NotNull RunnerAndConfigurationSettings settings) {
        System.out.println("Run config changed");
    }
}
