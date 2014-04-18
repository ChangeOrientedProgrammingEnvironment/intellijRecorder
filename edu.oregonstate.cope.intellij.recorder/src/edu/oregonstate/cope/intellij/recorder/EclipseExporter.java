package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.impl.storage.ClassPathStorageUtil;
import com.intellij.openapi.roots.impl.storage.ClasspathStorage;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.io.ZipUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.EclipseJDOMUtil;
import org.jetbrains.idea.eclipse.ConversionException;
import org.jetbrains.idea.eclipse.EclipseXml;
import org.jetbrains.idea.eclipse.IdeaXml;
import org.jetbrains.idea.eclipse.conversion.DotProjectFileHelper;
import org.jetbrains.idea.eclipse.conversion.EclipseClasspathWriter;
import org.jetbrains.idea.eclipse.conversion.EclipseUserLibrariesHelper;
import org.jetbrains.idea.eclipse.conversion.IdeaSpecificSettings;
import org.jetbrains.jps.eclipse.model.JpsEclipseClasspathSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * This class has a lot of it's contents from
 * org.jetbrains.idea.eclipse.export.ExportEclipseProjectsAction.
 * <p/>
 * Since the creators of IDEA do not provide a class who has only
 * the responsabilty of exporting a project, this is supposed to
 * fill in that role.
 * <p/>
 * Created by caius on 4/15/14.
 */
public class EclipseExporter {

    private Project project;
    private File localStorage;

    public EclipseExporter(Project project, File localStorage) {
        this.project = project;
        this.localStorage = localStorage;
    }

    public void export() {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        List<Module> compatibleModules = new ArrayList<>();
        List<Module> incompatibleModules = new ArrayList<>();
        for (Module module : modules) {
            boolean wasEclipseFriendly = true;
            if (!isModuleEclipseFriendly(module)) {
                makeModuleEclipseFriendly(compatibleModules, incompatibleModules, module);
                wasEclipseFriendly = false;
            }
            String storageRoot = getStorageRoot(module);
            File zipFile = createZipFile(module.getName(), localStorage);
            try {
                ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFile));
                ZipUtil.addDirToZipRecursively(outputStream, null, new File(storageRoot), module.getName(), null, null);
                outputStream.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                System.out.println("MASSIVE FAILURE ADDING CONTENTS TO THE ZIP FILE");
            }

            if (!wasEclipseFriendly) {
                getClassPathFile(storageRoot).delete();
                getProjectFile(storageRoot).delete();
            }
        }

        try {
            EclipseUserLibrariesHelper.appendProjectLibraries(project, new File("Libs"));
        } catch (IOException e1) {
            System.out.println("MASSIVE FAILURE ADDING THE LIBS TO THE PROJECT");
        }

        project.save();
    }

    private boolean isModuleEclipseFriendly(Module module) {
        String storageRoot = getStorageRoot(module);
        File classpathFile = getClassPathFile(storageRoot);
        File projectFile = getProjectFile(storageRoot);
        return classpathFile.exists() && projectFile.exists();
    }

    private File getProjectFile(String storageRoot) {
        return Paths.get(storageRoot, ".project").toFile();
    }

    private File getClassPathFile(String storageRoot) {
        return Paths.get(storageRoot, ".classpath").toFile();
    }


    private void makeModuleEclipseFriendly(List<Module> compatibleModules, List<Module> incompatibleModules, Module module) {
        if (!JpsEclipseClasspathSerializer.CLASSPATH_STORAGE_ID.equals(ClassPathStorageUtil.getStorageType(module))) {
            try {
                ClasspathStorage.getProvider(JpsEclipseClasspathSerializer.CLASSPATH_STORAGE_ID).assertCompatible(ModuleRootManager.getInstance(module));
                compatibleModules.add(module);
            } catch (ConfigurationException e1) {
                incompatibleModules.add(module);
                return;
            }
        }

        final ModuleRootModel model = ModuleRootManager.getInstance(module);
        final String storageRoot = getStorageRoot(module);
        try {
            final Element classpathElement = new Element(EclipseXml.CLASSPATH_TAG);

            final EclipseClasspathWriter classpathWriter = new EclipseClasspathWriter(model);
            classpathWriter.writeClasspath(classpathElement, null);
            final File classpathFile = new File(storageRoot, EclipseXml.CLASSPATH_FILE);
            if (!FileUtil.createIfDoesntExist(classpathFile)) return;
            EclipseJDOMUtil.output(new Document(classpathElement), classpathFile, project);

            final Element ideaSpecific = new Element(IdeaXml.COMPONENT_TAG);
            if (IdeaSpecificSettings.writeIDEASpecificClasspath(ideaSpecific, model)) {
                final File emlFile = new File(storageRoot, module.getName() + EclipseXml.IDEA_SETTINGS_POSTFIX);
                if (!FileUtil.createIfDoesntExist(emlFile)) return;
                EclipseJDOMUtil.output(new Document(ideaSpecific), emlFile, project);
            }

            DotProjectFileHelper.saveDotProjectFile(module, storageRoot);
        } catch (ConversionException e) {
        } catch (IOException e) {
        } catch (WriteExternalException e) {
            System.out.println("MASSIVE FAILURE MAKING THE PROJECT ECLIPSE FRIENDLY");
        }
    }

    private String getStorageRoot(Module module) {
        final ModuleRootModel model = ModuleRootManager.getInstance(module);
        final VirtualFile[] contentRoots = model.getContentRoots();         //todo
        return contentRoots.length == 1 ? contentRoots[0].getPath() : ClasspathStorage.getStorageRootFromOptions(module);
    }

    private File createZipFile(String moduleName, File localStorage) {
        String localStorageAbsolutePath = localStorage.getAbsolutePath();
        File zipFile = Paths.get(localStorageAbsolutePath, moduleName + System.currentTimeMillis() + ".zip").toFile();
        try {
            zipFile.createNewFile();
        } catch (IOException e) {
            System.out.println("MASSIVE FAILURE MAKING THE ZIP FILE");
        }
        return zipFile;
    }

}
