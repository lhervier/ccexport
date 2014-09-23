package org.openntf.xsp.ccexport.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.openntf.xsp.ccexport.Constants;


/**
 * M�thodes pratique pour acc�der aux propri�t�s du projet source
 * @author Lionel HERVIER
 */
public class PropUtils {

	/**
	 * Retourne le nom du projet dans lequel exporter
	 * @param prj le projet
	 * @return le nom du projet dans lequel exporter
	 */
	public static String getProp_destProjectName(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_DEST_PROJECT_NAME);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le projet de destination
	 * @param prj le projet source
	 * @return le projet de destination
	 */
	public static IProject getProp_destProject(IProject project) {
		String name = PropUtils.getProp_destProjectName(project);
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(name);
	}
	
	/**
	 * Retourne le nom du rep source dans lequel exporter
	 * @param prj le projet
	 * @return le nom du rep source
	 */
	public static String getProp_sourceFolder(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_SOURCE_FOLDER);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le chemin vers le rep source dans lequel exporter
	 * @param prj le projet
	 * @return le nom du rep source
	 */
	public static IPath getProp_sourceFolderPath(IProject project) {
		return new Path(PropUtils.getProp_sourceFolder(project));
	}
	
	/**
	 * Retourne le nom du package dans lequel exporter les fichiers java
	 * @param prj le projet
	 * @return le nom du package
	 */
	public static String getProp_javaPackage(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_CLASSES_PACKAGE);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le chemin vers le package dans lequel exporter les fichiers java
	 * @param prj le projet
	 * @return le chemin vers le package
	 */
	public static IPath getProp_javaPath(IProject project) {
		try {
			return new Path(project.getPersistentProperty(Constants.PROP_CLASSES_PACKAGE).replace('.', '/'));
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le nom du package dans lequel exporter les xsp-config
	 * @param prj le projet
	 * @return le nom du projet dans lequel exporter
	 */
	public static String getProp_xspConfigPackage(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le chemin du package dans lequel exporter les xsp-config
	 * @param prj le projet
	 * @return le nom du projet dans lequel exporter
	 */
	public static IPath getProp_xspConfigPath(IProject project) {
		try {
			return new Path(project.getPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE).replace('.', '/'));
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le nom du fichier dans lequel exporter les noms des xsp-config
	 * @param prj le projet
	 * @return le nom du fichier dans lequel exporter
	 */
	public static String getProp_xspConfigList(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_XSPCONFIG_FILE);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le chemin vers le fichier o� on va exporter les noms des xsp-config
	 * @param prj le projet
	 * @return le chemin vers le fichier xsp-config.list
	 */
	public static IPath getProp_xspConfigListPath(IProject project) {
		return new Path(PropUtils.getProp_xspConfigList(project));
	}
	
	/**
	 * Retourne le pr�fixe des cc � exporter
	 * @param prj le projet
	 * @return le prefixe des cc � exporter
	 */
	public static String getProp_ccPrefix(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_CCPREFIX);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
}