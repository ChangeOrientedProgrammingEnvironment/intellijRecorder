package edu.oregonstate.cope.intellij.recorder.installation;

import edu.oregonstate.cope.clientRecorder.installer.SurveyProvider;
import edu.oregonstate.cope.intellij.recorder.Survey;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Created by mihai on 4/24/14.
 */
public class IJSurveyProvider implements SurveyProvider {

    private volatile String surveyResults;
    private volatile String emailResults;

    @Override
    public String getSurveyResults() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    public void run(){
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
                Survey dialog = new Survey();
                dialog.pack();
                dialog.setVisible(true);


                surveyResults = dialog.getSurveyResults().toJSONString();
                emailResults = dialog.getEmail();
//            }
//        };
//
//        Thread t = new Thread(r);
//        t.start();
    }
}
