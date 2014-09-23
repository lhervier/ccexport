package fr.asi.xsp.ccexport;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class Constants {

	/**
	 * Notre namespace
	 */
	public final static String NAMESPACE = "fr.asi.xsp.ccexport";
	
	/**
	 * L'id de notre builder
	 */
	public final static String BUILDER_ID = "fr.asi.xsp.ccexport.builder";
	
	/**
	 * L'id de notre d�corateur
	 */
	public final static String DECORATOR_ID = "fr.asi.xsp.ccexport.CcExportDecorator";
	
	// ==========================================================
	
	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers le r�pertoire source "Local" du NSF
	 */
	public final static IPath JAVA_FOLDER_PATH = new Path("Local");
	
	/**
	 * Package dans lequel son g�n�r�s les fichiers java des custom controls
	 */
	public final static IPath JAVA_PACKAGE = new Path("xsp");
	
	// ==========================================================================================
	
	/**
	 * Nom de la propri�t� qui contient le nom du projet dans lequel exporter
	 */
	public static final QualifiedName PROP_DEST_PROJECT_NAME = new QualifiedName(NAMESPACE, "ProjectName");
	
	/**
	 * Nom de la propri�t� qui contient le nom du r�pertoire source dans lequel exporter
	 */
	public static final QualifiedName PROP_SOURCE_FOLDER = new QualifiedName(NAMESPACE, "SourceFolder");
	
	/**
	 * Nom de la propri�t� qui contient le nom du package dans lequel exporter les classes java
	 */
	public static final QualifiedName PROP_CLASSES_PACKAGE = new QualifiedName(NAMESPACE, "ClassesPackage");
	
	/**
	 * Nom de la propri�t� qui contient le nom du package dans lequel exporter les xsp-config
	 */
	public static final QualifiedName PROP_XSPCONFIG_PACKAGE = new QualifiedName(NAMESPACE, "XspConfigPackage");
	
	/**
	 * Nom de la propri�t� qui contient le nom du fichier dans lequel exporter les noms des xsp-config
	 */
	public static final QualifiedName PROP_XSPCONFIG_FILE = new QualifiedName(NAMESPACE, "XspConfigFile");
	
	/**
	 * Nom de la propri�t� qui contient le prefixe des cc � exporter
	 */
	public static final QualifiedName PROP_CCPREFIX = new QualifiedName(NAMESPACE, "CcPrefix");
}
