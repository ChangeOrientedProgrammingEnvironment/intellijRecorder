package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Created by michaelhilton on 3/25/14.
 */
public class COPEStatus implements StatusBarWidget, StatusBarWidget.IconPresentation {
    @NotNull
    @Override
    public Icon getIcon() {
        Icon test = IconLoader.getIcon("cope20.png");
        return test;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return "COPE!!";
    }

    @Nullable
    @Override
    public Consumer<MouseEvent> getClickConsumer() {
        return null;
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
