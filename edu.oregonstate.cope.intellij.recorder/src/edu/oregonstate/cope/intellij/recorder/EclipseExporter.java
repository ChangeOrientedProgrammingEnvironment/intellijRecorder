package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.impl.storage.ClassPathStorageUtil;
import com.intellij.openapi.roots.impl.storage.ClasspathStorage;
import org.jetbrains.jps.eclipse.model.JpsEclipseClasspathSerializer;

import java.util.ArrayList;
import java.util.List;

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

    public EclipseExporter(Project project) {
        this.project = project;
    }

    public void export() {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        List<Module> compatibleModules = new ArrayList<>();
        List<Module> incompatibleModules = new ArrayList<>();
        for (Module module : modules) {
            if (!JpsEclipseClasspathSerializer.CLASSPATH_STORAGE_ID.equals(ClassPathStorageUtil.getStorageType(module))) {
                try {
                    ClasspathStorage.getProvider(JpsEclipseClasspathSerializer.CLASSPATH_STORAGE_ID).assertCompatible(ModuleRootManager.getInstance(module));
                    compatibleModules.add(module);
                } catch (ConfigurationException e1) {
                    incompatibleModules.add(module);
                }
            }
        }

        System.out.println(compatibleModules);

    }

}
