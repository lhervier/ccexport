package org.openntf.xsp.ccexport.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;

/**
 * M�thodes utiles pour g�rer les projets Java
 * @author Lionel HERVIER
 */
public class IJavaProjectUtils {

	/**
	 * Retourne les r�pertoires source d'un
	 * projet de type Java
	 * @param javaProject le projet
	 * @throws CoreException 
	 */
	public static List<IPath> getSourceFolders(IJavaProject javaProject) throws CoreException {
		List<IPath> ret = new ArrayList<IPath>();
		
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		for( int i=0; i<entries.length; i++ ) {
			IClasspathEntry entry = entries[i];
			int kind = entry.getEntryKind();
			if( kind == IClasspathEntry.CPE_SOURCE ) {
				IPath path = entry.getPath();
				ret.add(path);
			}
		}
		
		return ret;
	}
	
	/**
	 * V�rifie qu'un package existe bien dans un projet Java, et le cr�� si n�cessaire.
	 * @param javaProject le projet (java) dans lequel le cr�er
	 * @param srcFolder le r�pertoire source dans lequel cr�er les packages
	 * @param pkg le nom du package � cr�er
	 * @param monitor le moniteur
	 * @return le package
	 * @throws CoreException en cas de probl�me
	 */
	public static IPackageFragment createPackage(
			IJavaProject javaProject, 
			IPath srcFolderPath, 
			String pkg, 
			IProgressMonitor monitor) throws CoreException {
		// R�cup�re le dossier qui doit contenir les sources
		IProject project = javaProject.getProject();
		IFolder root = project.getFolder(srcFolderPath);
		
		// V�rifie que le dossier soit bien un dossier qui contient du code source
		if( !IJavaProjectUtils.getSourceFolders(javaProject).contains(root.getFullPath()) )
			return null;
		
		return javaProject.getPackageFragmentRoot(root).createPackageFragment(pkg, true, monitor);
	}
}
