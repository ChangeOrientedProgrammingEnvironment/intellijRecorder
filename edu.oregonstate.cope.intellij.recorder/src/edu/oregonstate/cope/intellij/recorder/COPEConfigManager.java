package edu.oregonstate.cope.intellij.recorder;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by michaelhilton on 4/15/14.
 */
public class COPEConfigManager implements Configurable {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JPanel myPanel;
    private JTextField textField2;
    private JTextField updateSiteField;
    private JTextArea ConnectionStatus;
    private JComponent myComponent;
    private COPEComponent copeComponent;
    private String updateURL;

    @Nls
    @Override
    public String getDisplayName() {
        return "COPE Settings";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        connectionStatus();

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length < 1) {
            return null;
        } else {
            copeComponent = (COPEComponent) openProjects[0].getComponent("COPEComponent");
        }

        updateURL = copeComponent.getRecorder().getInstallationProperties().getProperty("updateURL");

        updateSiteField.setText(updateURL);

        //myComponent.disable();
        myComponent = (JComponent) myPanel;
        return myComponent;
    }

    @Override
    public boolean isModified() {
        // String s1 = updateURL;
        // String s2 = updateSiteField.toString();

        if (updateURL.compareTo(updateSiteField.toString()) == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        connectionStatus();
        String newURL = updateSiteField.getText();
        System.out.println("Apply");
        if (!updateURL.isEmpty()) {
            copeComponent.getRecorder().getInstallationProperties().addProperty("updateURL", newURL);
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }

    private void connectionStatus() {

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length < 1) {

        } else {
            copeComponent = (COPEComponent) openProjects[0].getComponent("COPEComponent");
            try {

                String updateURL = copeComponent.getRecorder().getInstallationProperties().getProperty("updateURL");
                URL url = null;
                url = new URL(updateURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
                    ConnectionStatus.setText("Unable to reach update Server");
                }else{
                    ConnectionStatus.setText("");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
