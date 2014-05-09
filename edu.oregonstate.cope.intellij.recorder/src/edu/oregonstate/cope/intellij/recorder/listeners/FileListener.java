package edu.oregonstate.cope.intellij.recorder.listeners;

import com.intellij.openapi.vfs.*;
import edu.oregonstate.cope.clientRecorder.RecorderFacade;
import edu.oregonstate.cope.intellij.recorder.COPEComponent;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class FileListener implements VirtualFileListener {
    private COPEComponent copeComponent;
    private RecorderFacade recorderFacade;

    public static final List<String> knownTextFiles = Arrays.asList(new String[]{"txt", "java", "xml", "mf", "c", "cpp", "c", "h"});

    public FileListener(COPEComponent copeComponent, RecorderFacade recorderFacade) {
        this.copeComponent = copeComponent;
        this.recorderFacade = recorderFacade;
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        if (copeComponent.shouldIgnoreFile(event.getFile())) {
            return;
        }

        if (event.isFromRefresh()) {
            recordRefresh(event);
        } else if (event.isFromSave()) {
            recordSave(event);
        }
    }

    private void recordSave(VirtualFileEvent event) {
        String filePath = event.getFile().getCanonicalPath();
        long modificationStamp = event.getNewModificationStamp();

        recorderFacade.getClientRecorder().recordFileSave(copeComponent.truncateAbsolutePath(filePath), modificationStamp);
    }

    private void recordRefresh(VirtualFileEvent event) {
        String fileContents = getFileContents(event.getFile());
        String filePath = event.getFile().getCanonicalPath();
        long modificationStamp = event.getNewModificationStamp();

        recorderFacade.getClientRecorder().recordRefresh(fileContents, copeComponent.truncateAbsolutePath(filePath), modificationStamp);
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        if (copeComponent.shouldIgnoreFile(event.getFile())) {
            return;
        }

        VirtualFile file = event.getFile();
        String path = file.getPath();

        recorderFacade.getClientRecorder().recordResourceAdd(copeComponent.truncateAbsolutePath(path), getFileContents(file));
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        if (copeComponent.shouldIgnoreFile(event.getFile())) {
            return;
        }

        String path = event.getFile().getPath();

        recorderFacade.getClientRecorder().recordResourceDelete(copeComponent.truncateAbsolutePath(path));
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

    //TODO copy pasted from /edu.oregonstate.cope.eclipse/src/edu/oregonstate/cope/eclipse/listeners/ResourceListener.java
    protected String getFileContents(VirtualFile file) {
        String fileExtension = file.getExtension();
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
            if (/*COPEPlugin.*/knownTextFiles.contains(fileExtension))
                return getTextFileContents(inputStream);
            else
                return getBinaryFileContents(inputStream);
        } catch (/*CoreException |*/ IOException e) {
            //COPEPlugin.getDefault().getLogger().error(this, "Could not get contents of file", e);
            System.err.println("Could not get contents of file + " + file.getName());
        }

        return "";
    }

    /**
     * I return the contents of the file as a String.
     *
     * @param inputStream the input stream to read from
     * @return the String containg the file contents, or gibberish if the file is a binary file
     * @throws IOException if I cannot read from the file
     */
    private String getTextFileContents(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        readFromTo(inputStream, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return new String(bytes);
    }

    /**
     * I return a base64 encoding of the file contents.
     *
     * @param inputStream the InputStream I have to read from.
     * @return the base64 string containing the encoded file contents.
     * @throws IOException if I cannot read from the InputStream.
     */
    private String getBinaryFileContents(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        readFromTo(inputStream, byteArrayOutputStream);
        byte[] byteArray = Base64.encodeBase64(byteArrayOutputStream.toByteArray());
        byteArrayOutputStream.close();
        return new String(byteArray);
    }

    private void readFromTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        do {
            byte[] b = new byte[1024];
            int read = inputStream.read(b, 0, 1024);
            if (read == -1)
                break;
            outputStream.write(b, 0, read);
        } while (true);
    }
}
