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
import com.jcraft.jsch.JSchException;
import edu.oregonstate.cope.clientRecorder.Properties;
import edu.oregonstate.cope.fileSender.FTPConnectionProperties;
import edu.oregonstate.cope.fileSender.SFTPUploader;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.GeneralSecurityException;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by michaelhilton on 4/15/14.
 */
public class COPEConfigManager implements Configurable {
    private JTextField hostnameField;
    private JPasswordField passwordField;
    private JPanel myPanel;
    private JTextField usernameField;
    private JTextField updateSiteField;
    private JTextArea ConnectionStatus;
    private JTextField portField;
    private JComponent myComponent;
    private COPEComponent copeComponent;
    private String updateURL;

    private String hostname;
    private int port;
    private String username;
    private String password;

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

        Properties workspaceProperties = copeComponent.getRecorder().getWorkspaceProperties();
        FTPConnectionProperties ftpProperties = new FTPConnectionProperties();
        String preferencesHostname = workspaceProperties.getProperty(COPEComponent.PREFERENCES_HOSTNAME);
        String preferencesPort = workspaceProperties.getProperty(COPEComponent.PREFERENCES_PORT);
        String preferencesUsername = workspaceProperties.getProperty(COPEComponent.PREFERENCES_USERNAME);
        String preferencesPassword = workspaceProperties.getProperty(COPEComponent.PREFERENCES_PASSWORD);
        if(preferencesHostname != null && !preferencesHostname.isEmpty()
                && preferencesPort != null && !preferencesPort.isEmpty()
                && preferencesUsername != null && !preferencesUsername.isEmpty()
                && preferencesPassword != null && !preferencesPassword.isEmpty()
                ) {
            hostnameField.setText(preferencesHostname);
            portField.setText(preferencesPort);
            usernameField.setText(preferencesUsername);
            try {
                    passwordField.setText(ftpProperties.decrypt(preferencesPassword));
                } catch (GeneralSecurityException | IOException e1) {
                    e1.printStackTrace();
                }
        } else {
            hostnameField.setText(ftpProperties.getHost());
            portField.setText(Integer.toString(ftpProperties.getPort()));
            usernameField.setText(ftpProperties.getUsername());
            passwordField.setText(ftpProperties.getPassword());
        }

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
        try {
            saveFTPProperties();
        } catch (UnknownHostException e) {
            showMessageDialog(null, "Unable to connect to host");
        } catch (JSchException e) {
            showMessageDialog(null, "Unable to establish connection using specified credentials");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void saveFTPProperties() throws UnknownHostException, JSchException, GeneralSecurityException, UnsupportedEncodingException {
        hostname = hostnameField.getText();
        port = Integer.parseInt(portField.getText());
        username = usernameField.getText();
        password = passwordField.getText();
        new SFTPUploader(hostname, port, username, password);
        copeComponent.getRecorder().getWorkspaceProperties().addProperty("hostname", hostname);
        copeComponent.getRecorder().getWorkspaceProperties().addProperty("port", Integer.toString(port));
        copeComponent.getRecorder().getWorkspaceProperties().addProperty("username", username);
        FTPConnectionProperties ftpConnectionProperties = new FTPConnectionProperties();
        copeComponent.getRecorder().getWorkspaceProperties().addProperty("password", ftpConnectionProperties.encrypt(password));
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
