package org.openntf.xsp.ccexport.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.openntf.xsp.ccexport.Constants;

/**
 * Userfull methods to access properties of the source project
 * @author Lionel HERVIER
 */
public class PropUtils {

	/**
	 * Returns the name of the project we will export into
	 * @param prj the source project 
	 * @return the name of the destination project
	 */
	public static String getProp_destProjectName(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_DEST_PROJECT_NAME);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the project we will export into
	 * @param prj the source project
	 * @return the destination project
	 */
	public static IProject getProp_destProject(IProject project) {
		String name = PropUtils.getProp_destProjectName(project);
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(name);
	}
	
	/**
	 * Returns the name of the source folder we will export into
	 * @param prj the source project
	 * @return the name of the source folder we will export into
	 */
	public static String getProp_sourceFolder(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_SOURCE_FOLDER);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the source folder we will export into
	 * @param prj the source project
	 * @return the source folder we will export into
	 */
	public static IPath getProp_sourceFolderPath(IProject project) {
		return new Path(PropUtils.getProp_sourceFolder(project));
	}
	
	/**
	 * Returns the name of the package we will export .java files into
	 * @param prj the project
	 * @return the name of the package we will export .java files into
	 */
	public static String getProp_javaPackage(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_CLASSES_PACKAGE);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Return the package we will export .java files into
	 * @param prj the source project
	 * @return the package we will export .java files into
	 */
	public static IPath getProp_javaPath(IProject project) {
		try {
			return new Path(project.getPersistentProperty(Constants.PROP_CLASSES_PACKAGE).replace('.', '/'));
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the name of the package we will export .xsp-config files into
	 * @param prj the source project
	 * @return the name of the package we will export .xsp-config files into
	 */
	public static String getProp_xspConfigPackage(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the package we will export .java files into
	 * @param prj the source project
	 * @return the package we will export .java files into
	 */
	public static IPath getProp_xspConfigPath(IProject project) {
		try {
			return new Path(project.getPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE).replace('.', '/'));
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the name of the file where we will export the classpath pathes to the .xsp-config files
	 * @param prj the source project
	 * @return the name of the file where we will export the classpath pathes to the .xsp-config files
	 */
	public static String getProp_xspConfigList(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_XSPCONFIG_FILE);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the file where we will export the classpath pathes to the .xsp-config files
	 * @param prj the source project
	 * @return the file where we will export the classpath pathes to the .xsp-config files
	 */
	public static IPath getProp_xspConfigListPath(IProject project) {
		return new Path(PropUtils.getProp_xspConfigList(project));
	}
	
	/**
	 * Returns the prefix of the custom controls to export
	 * @param prj the source project
	 * @return the prefix of the custom controls to export
	 */
	public static String getProp_ccPrefix(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_CCPREFIX);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
}
