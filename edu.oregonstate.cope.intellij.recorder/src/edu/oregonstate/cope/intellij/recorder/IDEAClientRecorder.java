package edu.oregonstate.cope.intellij.recorder;

import edu.oregonstate.cope.clientRecorder.ChangePersister;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.clientRecorder.RecordException;
import edu.oregonstate.cope.clientRecorder.util.COPELogger;
import org.json.simple.JSONObject;

/**
 * Created by caius on 4/9/14.
 */
public class IDEAClientRecorder extends ClientRecorder {

    public void recordIDEALaunch(String xmlLaunchString) {
        try {
            changePersister.persist(buildIDEALaunchJSON(xmlLaunchString));
        } catch (RecordException e) {
            COPELogger.getInstance().error(this, "Could not persist IDEA launch", e);
        }
    }

    protected JSONObject buildIDEALaunchJSON(String xmlLaunchString) {
        JSONObject json = buildCommonJSONObj(IDEAEvents.ideaLaunch);
        json.put(IdeaJsonConstants.IDEA_LAUNCH_XML, xmlLaunchString);
        return json;
    }
}
