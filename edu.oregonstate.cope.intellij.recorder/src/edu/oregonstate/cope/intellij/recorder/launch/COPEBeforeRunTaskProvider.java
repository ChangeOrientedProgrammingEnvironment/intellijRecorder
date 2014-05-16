package edu.oregonstate.cope.intellij.recorder.launch;

import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import edu.oregonstate.cope.intellij.recorder.IDEAClientRecorder;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by caius on 3/28/14.
 */
public class COPEBeforeRunTaskProvider extends BeforeRunTaskProvider<COPEBeforeRunTask> {

    public static final String EXTENSION_NAME = "COPE Run Recorder";

    private Key<COPEBeforeRunTask> launchProvider = new Key<COPEBeforeRunTask>("edu.oregonstate.cope.intellij.launchprovider");

    public COPEBeforeRunTaskProvider() {
    }

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
    public boolean executeTask(DataContext context, final RunConfiguration configuration, ExecutionEnvironment env, COPEBeforeRunTask task) {
        try {
            IDEAClientRecorder ideaClientRecorder = (IDEAClientRecorder) env.getProject().getComponent(COPEComponent.class).getRecorder().getClientRecorder();

            Element element = new Element("launchRecording");
            element.setAttribute("launchType", configuration.getClass().getCanonicalName());
            configuration.writeExternal(element);
            String xmlString = new XMLOutputter().outputString(element);
            ideaClientRecorder.recordIDEALaunch(xmlString);
        } catch (WriteExternalException e) {
        }

        return true;
    }
}
