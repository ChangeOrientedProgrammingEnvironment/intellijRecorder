package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.Nullable;

/**
 * Created by caius on 3/28/14.
 */
public class COPEBeforeRunTaskProvider extends BeforeRunTaskProvider<COPEBeforeRunTask> {

    public static final String EXTENSION_NAME = "COPE Run Recorder";

    Key<COPEBeforeRunTask> launchProvider = new Key<COPEBeforeRunTask>("edu.oregonstate.cope.intellij.launchprovider");

    @Override
    public Key<COPEBeforeRunTask> getId() {
        return launchProvider;
    }

    @Override
    public String getName() {
        return EXTENSION_NAME;
    }

    @Override
    public String getDescription(COPEBeforeRunTask task) {
        return "COPE Run Recorder";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Nullable
    @Override
    public COPEBeforeRunTask createTask(RunConfiguration runConfiguration) {
        return new COPEBeforeRunTask(launchProvider);
    }

    @Override
    public boolean configureTask(RunConfiguration runConfiguration, COPEBeforeRunTask task) {
        return true;
    }

    @Override
    public boolean canExecuteTask(RunConfiguration configuration, COPEBeforeRunTask task) {
        return true;
    }

    @Override
    public boolean executeTask(DataContext context, RunConfiguration configuration, ExecutionEnvironment env, COPEBeforeRunTask task) {
        System.out.println("Task was executed");
        return true;
    }
}
