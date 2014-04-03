package edu.oregonstate.cope.intellij.recorder;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import edu.oregonstate.cope.clientRecorder.StorageManager;

import java.io.File;

/**
 * Created by caius on 3/25/14.
 */
public class IntelliJStorageManager implements StorageManager {

    private File onlyFile;
    private Project project;

    public IntelliJStorageManager(Project project) {
        this.project = project;
    }

    public File getLocalStorage() {
        File copeDirectory = makeCopeDirectory(project.getBasePath());

        return copeDirectory;
    }

    public File getBundleStorage() {
        File path = getPluginDescriptor().getPath();

        return makeCopeDirectory(path.getAbsolutePath());
    }

    public File getVersionedLocalStorage() {
        return getVersionedStorage(getLocalStorage());
    }

    public File getVersionedBundleStorage() {
        return getVersionedStorage(getBundleStorage());
    }

    public boolean isPathInManagedStorage(String path) {
        return (path.contains(getLocalStorage().getName())          ||
               path.contains(getBundleStorage().getName())          ||
               path.contains(getVersionedBundleStorage().getName()) ||
               path.contains(getVersionedLocalStorage().getName()));
    }

    private IdeaPluginDescriptor getPluginDescriptor() {
        return PluginManager.getPlugin(PluginId.getId(COPEComponent.ID));
    }

    private File makeCopeDirectory(String basePath) {
        File copeFile = new File(basePath, ".cope");
        copeFile.mkdir();

        return copeFile;
    }

    private File getVersionedStorage(File file) {
        return new File(file, getPluginDescriptor().getVersion());
    }
}
