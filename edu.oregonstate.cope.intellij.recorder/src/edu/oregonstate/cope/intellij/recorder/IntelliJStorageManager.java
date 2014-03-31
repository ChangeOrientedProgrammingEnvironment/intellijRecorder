package edu.oregonstate.cope.intellij.recorder;

import edu.oregonstate.cope.clientRecorder.StorageManager;

import java.io.File;

/**
 * Created by caius on 3/25/14.
 */
public class IntelliJStorageManager implements StorageManager {

    private File onlyFile;

    public IntelliJStorageManager(String basePath) {
        onlyFile = new File(basePath + "/.cope");
        onlyFile.mkdir();
        System.out.println(onlyFile.getAbsolutePath());
    }

    public File getLocalStorage() {
        return onlyFile;
    }

    public File getBundleStorage() {
        return onlyFile;
    }

    public File getVersionedLocalStorage() {
        return onlyFile;
    }

    public File getVersionedBundleStorage() {
        return onlyFile;
    }

    public boolean isPathInManagedStorage(String path) {
        return (path.contains(getLocalStorage().getName())          ||
               path.contains(getBundleStorage().getName())          ||
               path.contains(getVersionedBundleStorage().getName()) ||
               path.contains(getVersionedLocalStorage().getName()));
    }
}
