package edu.oregonstate.cope.intellij.recorder;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by caius on 3/3/14.
 */
public class COPEComponent implements ProjectComponent {

    public static final String ID = "edu.oregonstate.cope.intellij.recorder";

    private final String IDE = "IDEA";
    private Project project;
    private RecorderFacade recorder;
    private IntelliJStorageManager storageManager;

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
        storageManager = new IntelliJStorageManager(project);
        recorder = new RecorderFacade(storageManager, IDE);

        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(this, recorder.getClientRecorder()));

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
