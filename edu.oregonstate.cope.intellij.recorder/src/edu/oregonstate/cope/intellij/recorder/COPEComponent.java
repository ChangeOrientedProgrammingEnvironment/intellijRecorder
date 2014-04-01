package edu.oregonstate.cope.intellij.recorder;

import com.intellij.execution.*;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.project.Project;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ProjectComponent {

    private final String IDE = "IDEA";
    private Project project;
    private RecorderFacade recorder;
    private IntelliJStorageManager storageManager;

    public COPEComponent(Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
        System.out.println("The fucking component has been initialized");
    }

    @Override
    public void disposeComponent() {
        System.out.println("The fucking component has been disposed");
    }

    @Override
    public void projectOpened() {
        System.out.println("The fucking project has been opened");
        String basePath = project.getBasePath();

        storageManager = new IntelliJStorageManager(basePath);
        recorder = new RecorderFacade(storageManager, IDE);

        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(recorder.getClientRecorder(), basePath));

        VirtualFileManager.getInstance().addVirtualFileListener(new FileListener(this, recorder));
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

    public boolean fileIsInProject(VirtualFile file) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();

        return projectFileIndex.isInContent(file);
    }

    public boolean fileIsInCOPEStructure(VirtualFile file) {
        return storageManager.isPathInManagedStorage(file.getPath());
    }
}
