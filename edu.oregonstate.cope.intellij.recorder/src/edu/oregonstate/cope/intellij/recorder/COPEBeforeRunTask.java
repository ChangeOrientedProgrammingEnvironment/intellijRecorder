package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.BeforeRunTask;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/28/14.
 */
public class COPEBeforeRunTask extends BeforeRunTask<COPEBeforeRunTask> {

    protected COPEBeforeRunTask(@NotNull Key<COPEBeforeRunTask> providerId) {
        super(providerId);
    }

}
