package org.openntf.xsp.ccexport.util;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.openntf.xsp.ccexport.Constants;


public class Utils {

	/**
	 * Passe le premier caract�re en majuscule
	 * @param s la cha�ne
	 * @return la cha�ne avec le 1er caract�re en majuscule
	 */
	public static String normalizeMaj(String s) {
		if( s.length() <= 1 )
			return s.toUpperCase();
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	/**
	 * Passe le premier caract�re en minuscule
	 * @param s la cha�ne
	 * @return la cha�ne avec le 1er caract�re en minuscule
	 */
	public static String normalizeMin(String s) {
		if( s.length() <= 1 )
			return s.toUpperCase();
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
	
	/**
	 * Retourne un nom de fichier sans son extension
	 * @param fileName
	 * @return le nom du fichier sans son extension
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if( pos == -1 )
			return fileName;
		return fileName.substring(0, pos);
	}
	
	/**
	 * Pour fermer une stream
	 * @param o l'objet � fermer
	 */
	public static void closeQuietly(Closeable o) {
		if( o == null )
			return;
		try {
			o.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Initialise la liaison entre les 2 projets
	 * @param prj le projet source
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 * @return true si c'est ok. False sinon.
	 */
	public static boolean initializeLink(IProject nsfProject, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		try {
			IProject project = PropUtils.getProp_destProject(nsfProject);
			
			// V�rifie que le projet existe et est de nature java
			if( !project.exists() )
				return false;
			if( !IProjectUtils.hasNature(project, JavaCore.NATURE_ID) )
				return false;
			
			// Ouvre le projet si n�cessaire
			if( !project.isOpen() )
				project.open(progress.newChild(25));
			progress.setWorkRemaining(75);
			
			// Cr�� les deux packages
			IJavaProject javaProject = JavaCore.create(project);
			IPackageFragment javaPkg = IJavaProjectUtils.createPackage(
					javaProject, 
					PropUtils.getProp_sourceFolderPath(nsfProject), 
					PropUtils.getProp_javaPackage(nsfProject), 
					progress.newChild(25)
			);
			if( javaPkg == null )
				return false;
			IPackageFragment xspConfigPkg = IJavaProjectUtils.createPackage(
					javaProject, 
					PropUtils.getProp_sourceFolderPath(nsfProject), 
					PropUtils.getProp_xspConfigPackage(nsfProject), 
					progress.newChild(25)
			);
			if( xspConfigPkg == null )
				return false;
			
			// Cr�� le dossier qui va contenir le fichier xsp-config.list
			IPath xspConfigListPath = PropUtils.getProp_xspConfigListPath(nsfProject);
			IPath folderPath = xspConfigListPath.removeLastSegments(1);
			IProjectUtils.createFolder(
					project, 
					folderPath, 
					progress.newChild(25)
			);
			
			return true;
		} finally {
			progress.setWorkRemaining(0);
		}
	}
	
	/**
	 * Permet de savoir si le projet utilise notre builder
	 * @param project le projet
	 * @return true si notre builder est utilis�. False sinon.
	 * @throws CoreException en cas de probl�me
	 */
	public static boolean isUsingExportCc(IProject project) throws CoreException {
		IProjectDescription projectDesc = project.getDescription();
		ICommand[] initBuildSpec = projectDesc.getBuildSpec();
		for( int j = 0; j < initBuildSpec.length; j++ ) {
			if (Constants.BUILDER_ID.equals(initBuildSpec[j].getBuilderName()))
				return true;
		}
		return false;
	}
	
	/**
	 * Pour savoir si une cha�ne est vide
	 * @param s la cha�ne
	 * @return true si la cha�ne est vide, ou null
	 */
	public static boolean isEmpty(String s) {
		if( s == null )
			return true;
		return s.length() == 0;
	}
	
	/**
	 * Pour savoir si deux tableaux sont �gaux
	 * @param t1 le 1er tableau
	 * @param t2 le second tableau
	 * @return true si leur contenu est identique
	 */
	public static boolean equals(Object[] t1, Object[] t2) {
		if( t1 == null && t2 == null )
			return true;
		if( t1 == null || t2 == null )
			return false;
		if( t1.length != t2.length )
			return false;
		for( int i=0; i<t1.length; i++ )
			if( !t1[i].equals(t2[i]) )
				return false;
		return true;
	}
}