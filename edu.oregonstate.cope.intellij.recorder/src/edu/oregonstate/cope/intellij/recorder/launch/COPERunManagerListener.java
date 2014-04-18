package edu.oregonstate.cope.intellij.recorder.launch;

import com.intellij.execution.RunManagerListener;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import org.jetbrains.annotations.NotNull;

/**
* Created by caius on 4/1/14.
*/
public class COPERunManagerListener implements RunManagerListener {

    @Override
    public void runConfigurationSelected() {
    }

    @Override
    public void beforeRunTasksChanged() {

    }

    @Override
    public void runConfigurationAdded(@NotNull RunnerAndConfigurationSettings settings) {
        RunConfiguration runConfiguration = settings.getConfiguration();
        COPEComponent.getInstance(runConfiguration.getProject()).addCOPETaskToRunConfiguration(runConfiguration);
    }

    @Override
    public void runConfigurationRemoved(@NotNull RunnerAndConfigurationSettings settings) {
    }

    @Override
    public void runConfigurationChanged(@NotNull RunnerAndConfigurationSettings settings) {
    }
}
