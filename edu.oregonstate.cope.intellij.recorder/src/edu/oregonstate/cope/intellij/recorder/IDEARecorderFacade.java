package edu.oregonstate.cope.intellij.recorder;

import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.clientRecorder.StorageManager;

/**
 * Created by mihai on 5/6/14.
 */
public class IDEARecorderFacade extends RecorderFacade{


    public IDEARecorderFacade(StorageManager manager, String IDE) {
        super(manager, IDE);
    }

    @Override
    protected ClientRecorder instantiateRecorder() {
        return new IDEAClientRecorder();
    }
}
