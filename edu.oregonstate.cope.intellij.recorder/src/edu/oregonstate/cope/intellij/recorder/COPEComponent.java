package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.project.Project;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import org.jetbrains.annotations.NotNull;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ProjectComponent {

    private final String IDE = "IDEA";
    private Project project;
    private RecorderFacade recorder;

    public COPEComponent(Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {
    }

    @Override
    public void projectOpened() {
        String basePath = project.getBasePath();
        System.out.println(basePath);
        recorder = new RecorderFacade(new IntelliJStorageManager(basePath), IDE);
        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(recorder.getClientRecorder(), basePath));

        RefreshListener refreshListener = new RefreshListener();
        VirtualFileManager.getInstance().addVirtualFileManagerListener(refreshListener);
        VirtualFileManager.getInstance().addVirtualFileListener(new FileListener(recorder, refreshListener));
    }

    @Override
    public void projectClosed() {

    }

    @NotNull
    public String getComponentName() {
        return "COPEComponent";
    }

    public RecorderFacade getRecorder() {
        return recorder;
    }

}
