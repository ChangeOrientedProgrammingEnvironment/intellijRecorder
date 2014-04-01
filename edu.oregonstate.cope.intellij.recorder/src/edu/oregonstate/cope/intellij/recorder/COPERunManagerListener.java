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
    }

    @Override
    public void beforeRunTasksChanged() {

    }

    @Override
    public void runConfigurationAdded(@NotNull RunnerAndConfigurationSettings settings) {
        COPEComponent.getInstance().addCOPETaskToRunConfiguration(settings.getConfiguration());
    }

    @Override
    public void runConfigurationRemoved(@NotNull RunnerAndConfigurationSettings settings) {
    }

    @Override
    public void runConfigurationChanged(@NotNull RunnerAndConfigurationSettings settings) {
    }
}
