package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.vfs.*;
import edu.oregonstate.cope.clientRecorder.ClientRecorder;
import org.jetbrains.annotations.NotNull;

class FileListener implements VirtualFileListener {
    private ClientRecorder recorder;

    public FileListener(ClientRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {

    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {

    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        System.out.println("CREATED FILE: " + event.getFileName());
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        System.out.println("DELETED FILE: " + event.getFileName());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {

    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {

    }

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {

    }

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {

    }

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {

    }
}
