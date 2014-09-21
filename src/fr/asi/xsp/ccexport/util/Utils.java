package fr.asi.xsp.ccexport.util;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

public class Utils {

	/**
	 * Passe le premier caractère en majuscule
	 * @param s la chaîne
	 * @return la chaîne avec le 1er caractère en majuscule
	 */
	public static String normalizeMaj(String s) {
		if( s.length() <= 1 )
			return s.toUpperCase();
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	/**
	 * Passe le premier caractère en minuscule
	 * @param s la chaîne
	 * @return la chaîne avec le 1er caractère en minuscule
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
	 * @param o l'objet à fermer
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
			
			// Vérifie que le projet existe et est de nature java
			if( !project.exists() )
				return false;
			if( !IProjectUtils.hasNature(project, JavaCore.NATURE_ID) )
				return false;
			
			// Ouvre le projet si nécessaire
			if( !project.isOpen() )
				project.open(progress.newChild(25));
			progress.setWorkRemaining(75);
			
			// Créé les deux packages
			IJavaProject javaProject = JavaCore.create(project);
			IPackageFragment javaPkg = IJavaProjectUtils.createPackage(
					javaProject, 
					PropUtils.getProp_sourceFolder(nsfProject), 
					PropUtils.getProp_classesPackage(nsfProject), 
					progress.newChild(25)
			);
			if( javaPkg == null )
				return false;
			IPackageFragment xspConfigPkg = IJavaProjectUtils.createPackage(
					javaProject, 
					PropUtils.getProp_sourceFolder(nsfProject), 
					PropUtils.getProp_xspConfigPackage(nsfProject), 
					progress.newChild(25)
			);
			if( xspConfigPkg == null )
				return false;
			
			// Créé le dossier qui va contenir le fichier xsp-config.list
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
}
