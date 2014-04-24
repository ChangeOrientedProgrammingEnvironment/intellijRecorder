package edu.oregonstate.cope.intellij.recorder.installation;

import edu.oregonstate.cope.clientRecorder.installer.InstallerHelper;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;

/**
 * Created by mihai on 4/24/14.
 */
public class IJInstallerHelper implements InstallerHelper {

    private COPEComponent component;

    public void IJInstallerHelper(COPEComponent component){
        this.component = component;
    }

    @Override
    public String getPluginVersion() {
        return component.getPluginVersion();
    }

    @Override
    public void takeSnapshotOfAllProjects() {
        component.takeSnapshotOfProject();
    }
}
