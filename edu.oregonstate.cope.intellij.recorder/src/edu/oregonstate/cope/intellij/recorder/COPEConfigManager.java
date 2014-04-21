package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by michaelhilton on 4/15/14.
 */
public class COPEConfigManager implements Configurable {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JPanel myPanel;
    private JTextField textField2;
    private JTextField updateSiteField;
    private JComponent myComponent;

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

        updateSiteField.setText("URL");

        myComponent = (JComponent) myPanel;
        return myComponent;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        System.out.println("Apply");
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
