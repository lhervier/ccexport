package org.openntf.xsp.ccexport;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class Constants {

	/**
	 * Our namespace
	 */
	public final static String NAMESPACE = "org.openntf.xsp.ccexport";
	
	/**
	 * Our builder's id
	 */
	public final static String BUILDER_ID = "org.openntf.xsp.ccexport.builder";
	
	/**
	 * Our decorator's id
	 */
	public final static String DECORATOR_ID = "org.openntf.xsp.ccexport.CcExportDecorator";
	
	// ==========================================================
	
	/**
	 * Folder where the custom controls are stored inside the NSF
	 */
	public final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Folder where the .java file are stored inside the NSF
	 */
	public final static IPath JAVA_FOLDER_PATH = new Path("Local");
	
	/**
	 * Package where the .java file are generated
	 */
	public final static IPath JAVA_PACKAGE = new Path("xsp");
	
	// ==========================================================================================
	
	/**
	 * Name of the property that contains the name of the project we will export into
	 */
	public static final QualifiedName PROP_DEST_PROJECT_NAME = new QualifiedName(NAMESPACE, "ProjectName");
	
	/**
	 * Name of the property that contains the name of the source folder we will export .java files into
	 */
	public static final QualifiedName PROP_SOURCE_FOLDER = new QualifiedName(NAMESPACE, "SourceFolder");
	
	/**
	 * Name of the property that contains the name of the package we will export .java files into
	 */
	public static final QualifiedName PROP_CLASSES_PACKAGE = new QualifiedName(NAMESPACE, "ClassesPackage");
	
	/**
	 * Name of the property that contains the name of the package we will export .xsp-config files into
	 */
	public static final QualifiedName PROP_XSPCONFIG_PACKAGE = new QualifiedName(NAMESPACE, "XspConfigPackage");
	
	/**
	 * Name of the property that contains the name of the file we will export the .xsp-config path into
	 */
	public static final QualifiedName PROP_XSPCONFIG_FILE = new QualifiedName(NAMESPACE, "XspConfigFile");
	
	/**
	 * Name of the property that contains the prefix
	 */
	public static final QualifiedName PROP_CCPREFIX = new QualifiedName(NAMESPACE, "CcPrefix");
}
