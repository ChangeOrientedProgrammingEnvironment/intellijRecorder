package edu.oregonstate.cope.intellij.recorder.installation;

import edu.oregonstate.cope.clientRecorder.RecorderFacadeInterface;
import edu.oregonstate.cope.clientRecorder.installer.*;

import java.util.ArrayList;

/**
 * Created by mihai on 4/24/14.
 */
public class IJInstaller extends Installer {

    public IJInstaller(RecorderFacadeInterface recorder, InstallerHelper installerHelper) {
        super(recorder, installerHelper);
    }

    @Override
    protected ArrayList<InstallerOperation> getInstallOperations() {
        ArrayList<InstallerOperation> operations = new ArrayList<>();

        operations.add(new ConfigInstallOperation());
        operations.add(new IJSurveyOperation());
        operations.add(new EmailInstallOperation());

        return operations;
    }
}
