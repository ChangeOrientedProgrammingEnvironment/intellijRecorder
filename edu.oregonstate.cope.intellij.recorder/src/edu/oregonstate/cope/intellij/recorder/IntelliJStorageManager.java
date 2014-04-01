package edu.oregonstate.cope.intellij.recorder;

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
        String basePath = project.getBasePath();
        onlyFile = new File(basePath + "/.cope");
        onlyFile.mkdir();

        return onlyFile;
    }

    public File getBundleStorage() {
        return getLocalStorage();
    }

    public File getVersionedLocalStorage() {
        return getLocalStorage();
    }

    public File getVersionedBundleStorage() {
        return getLocalStorage();
    }

    public boolean isPathInManagedStorage(String path) {
        return (path.contains(getLocalStorage().getName())          ||
               path.contains(getBundleStorage().getName())          ||
               path.contains(getVersionedBundleStorage().getName()) ||
               path.contains(getVersionedLocalStorage().getName()));
    }
}
