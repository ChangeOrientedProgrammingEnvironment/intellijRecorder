package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by michaelhilton on 3/25/14.
 */
public class COPEStatus implements StatusBarWidget, StatusBarWidget.IconPresentation {

    private final Boolean updateReady;

    public COPEStatus(Boolean updateReady) {
        this.updateReady = updateReady;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        if(updateReady){
            Icon test = IconLoader.getIcon("copeLogo_UpdateReady.png");
            return test;
        }else{
            Icon test = IconLoader.getIcon("copeLogo.png");
            return test;
        }

    }

    @Nullable
    @Override
    public String getTooltipText() {
        return "COPE Recorder plugin active";
    }

    @Nullable
    @Override
    public Consumer<MouseEvent> getClickConsumer() {
        if(updateReady) {
            return new Consumer<MouseEvent>() {
                public void consume(MouseEvent mouseEvent) {
                    // update();
                    Messages.showMessageDialog("Your version of COPE is out of date.  Please update your plugin!", "COPE", Messages.getInformationIcon());

                }
            };
        }else{
            return null;
        }
    }

    @NotNull
    @Override
    public String ID() {
        return "COPE STATUS ID";
    }


    @Nullable
    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return this;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {

    }


    @Override
    public void dispose() {

    }

}
