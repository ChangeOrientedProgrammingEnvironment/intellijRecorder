package edu.oregonstate.cope.intellij.recorder.installation;

import edu.oregonstate.cope.clientRecorder.installer.SurveyOperation;
import edu.oregonstate.cope.clientRecorder.installer.SurveyProvider;

/**
 * Created by mihai on 4/24/14.
 */
public class IJSurveyOperation extends SurveyOperation {
    @Override
    protected SurveyProvider runSurvey() {
        IJSurveyProvider ijSurveyProvider = new IJSurveyProvider();
        ijSurveyProvider.run();

        return ijSurveyProvider;
    }
}
