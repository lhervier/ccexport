package fr.asi.xsp.ccexport.util;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import fr.asi.xsp.ccexport.Constants;

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
	 * Vérifie si un custom control donné existe
	 * @param project le projet (domino)
	 * @param cc le nom du custom control
	 * @return true s'il existe. False sinon.
	 */
	public static boolean ccExists(IProject project, String cc) {
		IPath ccPath = Constants.CC_FOLDER_PATH.append(cc + ".xsp-config");
		IFile ccFile = project.getFile(ccPath);
		return ccFile.exists();
	}
	
	/**
	 * Initialise la liaison entre les 2 projets
	 * @param prj le projet source
	 * @return true si c'est ok. False sinon.
	 */
	public static boolean initializeLink(IProject nsfProject, IProgressMonitor monitor) throws CoreException {
		IProject project = IProjectUtils.getProjectFromName(Constants.getProp_projectName(nsfProject));
		
		// Vérifie que le projet existe et est de nature java
		if( !project.exists() )
			return false;
		if( !IProjectUtils.hasNature(project, JavaCore.NATURE_ID) )
			return false;
		
		// Ouvre le projet si nécessaire
		if( !project.isOpen() )
			project.open(new NullProgressMonitor());
		
		// Créé les deux packages
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragment javaPkg = IJavaProjectUtils.createPackage(
				javaProject, 
				new Path(Constants.getProp_sourceFolder(nsfProject)), 
				Constants.getProp_classesPackage(nsfProject), 
				new NullProgressMonitor()
		);
		if( javaPkg == null )
			return false;
		IPackageFragment xspConfigPkg = IJavaProjectUtils.createPackage(
				javaProject, 
				new Path(Constants.getProp_sourceFolder(nsfProject)), 
				Constants.getProp_xspConfigPackage(nsfProject), 
				new NullProgressMonitor()
		);
		if( xspConfigPkg == null )
			return false;
		
		return true;
	}
}
