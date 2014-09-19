package fr.asi.xsp.ccexport;

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
}
