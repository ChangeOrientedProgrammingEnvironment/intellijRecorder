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

    public static final String ID = "edu.oregonstate.cope.intellij.recorder";

    private final String IDE = "IDEA";
    private Project project;
    private RecorderFacade recorder;
    private IntelliJStorageManager storageManager;
    private Key<COPEBeforeRunTask> providerID;

    private static COPEComponent component = null;

    public COPEComponent(Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
        component = this;
    }

    @Override
    public void disposeComponent() {
        component = null;
    }

    public static COPEComponent getInstance() {
        return component;
    }

    @Override
    public void projectOpened() {
        storageManager = new IntelliJStorageManager(project);
        recorder = new RecorderFacade(storageManager, IDE);

        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener(this, recorder.getClientRecorder()));

        VirtualFileManager.getInstance().addVirtualFileListener(new FileListener(this, recorder));
        RunManagerEx runManager = (RunManagerEx) RunManagerEx.getInstance(project);

        runManager.addRunManagerListener(new COPERunManagerListener());

        BeforeRunTaskProvider<COPEBeforeRunTask> beforeRunTaskProvider = getBeforeRunTaskProvider();
        if (beforeRunTaskProvider == null) {
            System.out.println("Could not find provider");
            return;
        }
        providerID = beforeRunTaskProvider.getId();
        for (RunConfiguration runConfiguration : runManager.getAllConfigurationsList()) {
            List<BeforeRunTask> beforeRunTasks = runManager.getBeforeRunTasks(runConfiguration);
            if (!containsCOPEListener(beforeRunTasks)) {
                beforeRunTasks.add(beforeRunTaskProvider.createTask(runConfiguration));
                runManager.setBeforeRunTasks(runConfiguration, beforeRunTasks, true);
            }
        }
    }

    private boolean containsCOPEListener(List<BeforeRunTask> tasks) {
        for (BeforeRunTask task : tasks)
            if (providerID.equals(task.getProviderId()))
                return true;
        return false;
    }

    private BeforeRunTaskProvider<COPEBeforeRunTask> getBeforeRunTaskProvider() {
        BeforeRunTaskProvider<COPEBeforeRunTask> beforeRunTaskProvider = null;
        BeforeRunTaskProvider<BeforeRunTask>[] extensions = Extensions.getExtensions(BeforeRunTaskProvider.EXTENSION_POINT_NAME, project);
        for (BeforeRunTaskProvider<? extends BeforeRunTask> extension : extensions) {
            String name = extension.getName();
            if (name.equals(COPEBeforeRunTaskProvider.EXTENSION_NAME))
                beforeRunTaskProvider = (BeforeRunTaskProvider<COPEBeforeRunTask>) extension;
        }
        return beforeRunTaskProvider;
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
