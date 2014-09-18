package fr.asi.xsp.ccexport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class Constants {

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
	 * Nom de la propri�t� qui contient le nom du projet dans lequel exporter
	 */
	public static final QualifiedName PROP_PROJECT_NAME = new QualifiedName("fr.asi.xsp.ccexport", "ProjectName");
	
	/**
	 * Nom de la propri�t� qui contient le nom du r�pertoire source dans lequel exporter
	 */
	public static final QualifiedName PROP_SOURCE_FOLDER = new QualifiedName("fr.asi.xsp.ccexport", "SourceFolder");
	
	/**
	 * Nom de la propri�t� qui contient le nom du package dans lequel exporter les classes java
	 */
	public static final QualifiedName PROP_CLASSES_PACKAGE = new QualifiedName("fr.asi.xsp.ccexport", "ClassesPackage");
	
	/**
	 * Nom de la propri�t� qui contient le nom du package dans lequel exporter les xsp-config
	 */
	public static final QualifiedName PROP_XSPCONFIG_PACKAGE = new QualifiedName("fr.asi.xsp.ccexport", "XspConfigPackage");
	
	/**
	 * Nom de la propri�t� qui contient le nom du fichier dans lequel exporter les noms des xsp-config
	 */
	public static final QualifiedName PROP_XSPCONFIG_FILE = new QualifiedName("fr.asi.xsp.ccexport", "XspConfigFile");
	
	// ==================================================================================================
	
	/**
	 * Retourne le nom du projet dans lequel exporter
	 * @param prj le projet
	 * @return le nom du projet dans lequel exporter
	 * @throws CoreException en cas de probl�me
	 */
	public static String getProp_projectName(IProject project) throws CoreException {
		return project.getPersistentProperty(Constants.PROP_PROJECT_NAME);
	}
	
	/**
	 * Retourne le nom du rep source dans lequel exporter
	 * @param prj le projet
	 * @return le nom du rep source
	 * @throws CoreException en cas de probl�me
	 */
	public static String getProp_sourceFolder(IProject project) throws CoreException {
		return project.getPersistentProperty(Constants.PROP_SOURCE_FOLDER);
	}
	
	/**
	 * Retourne le nom du package dans lequel exporter les fichiers java
	 * @param prj le projet
	 * @return le nom du package
	 * @throws CoreException en cas de probl�me
	 */
	public static String getProp_classesPackage(IProject project) throws CoreException {
		return project.getPersistentProperty(Constants.PROP_CLASSES_PACKAGE);
	}
	
	/**
	 * Retourne le nom du package dans lequel exporter les xsp-config
	 * @param prj le projet
	 * @return le nom du projet dans lequel exporter
	 * @throws CoreException en cas de probl�me
	 */
	public static String getProp_xspConfigPackage(IProject project) throws CoreException {
		return project.getPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE);
	}
	
	/**
	 * Retourne le nom du fichier dans lequel exporter les noms des xsp-config
	 * @param prj le projet
	 * @return le nom du fichier dans lequel exporter
	 * @throws CoreException en cas de probl�me
	 */
	public static String getProp_xspConfigFile(IProject project) throws CoreException {
		return project.getPersistentProperty(Constants.PROP_XSPCONFIG_FILE);
	}
}