package fr.asi.xsp.ccexport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class Constants {

	/**
	 * L'id de notre builder
	 */
	public final static String BUILDER_ID = "fr.asi.xsp.ccexport.builder";
	
	// ==========================================================
	
	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath JAVA_FOLDER_PATH = new Path("Local");
	
	// ==========================================================================================
	
	/**
	 * Nom de la propriété qui contient le nom du projet dans lequel exporter
	 */
	public static final QualifiedName PROP_DEST_PROJECT_NAME = new QualifiedName("fr.asi.xsp.ccexport", "ProjectName");
	
	/**
	 * Nom de la propriété qui contient le nom du répertoire source dans lequel exporter
	 */
	public static final QualifiedName PROP_SOURCE_FOLDER = new QualifiedName("fr.asi.xsp.ccexport", "SourceFolder");
	
	/**
	 * Nom de la propriété qui contient le nom du package dans lequel exporter les classes java
	 */
	public static final QualifiedName PROP_CLASSES_PACKAGE = new QualifiedName("fr.asi.xsp.ccexport", "ClassesPackage");
	
	/**
	 * Nom de la propriété qui contient le nom du package dans lequel exporter les xsp-config
	 */
	public static final QualifiedName PROP_XSPCONFIG_PACKAGE = new QualifiedName("fr.asi.xsp.ccexport", "XspConfigPackage");
	
	/**
	 * Nom de la propriété qui contient le nom du fichier dans lequel exporter les noms des xsp-config
	 */
	public static final QualifiedName PROP_XSPCONFIG_FILE = new QualifiedName("fr.asi.xsp.ccexport", "XspConfigFile");
	
	// ==================================================================================================
	
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
		String name = Constants.getProp_destProjectName(project);
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
	public static IPath getProp_sourceFolder(IProject project) {
		try {
			return new Path(project.getPersistentProperty(Constants.PROP_SOURCE_FOLDER));
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retourne le nom du package dans lequel exporter les fichiers java
	 * @param prj le projet
	 * @return le nom du package
	 */
	public static String getProp_classesPackage(IProject project) {
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
	public static IPath getProp_classesPath(IProject project) {
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
	public static String getProp_xspConfigFile(IProject project) {
		try {
			return project.getPersistentProperty(Constants.PROP_XSPCONFIG_FILE);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
