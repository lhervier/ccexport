package fr.asi.xsp.ccexport;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;

public class Utils {

	/**
	 * Retourne un projet � partir de son nom
	 * @param name le nom du projet
	 * @return le projet
	 */
	public static IProject getProjectFromName(String name) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(name);
	}
	
	/**
	 * Pour savoir si un projet est d'une nature donn�e
	 * @param project le projet
	 * @param natureId l'ID de la nature
	 * @return true si le projet est de cette nature
	 * @throws CoreException 
	 */
	public static boolean isOfNature(IProject project, String natureId) throws CoreException {
		String[] natures = project.getDescription().getNatureIds();
		for( String nature : natures ) {
			if( natureId.equals(nature) )
				return true;
		}
		return false;
	}
	
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
	public static IPackageFragment createPackage(IJavaProject javaProject, IPath srcFolderPath, String pkg, IProgressMonitor monitor) throws CoreException {
		// R�cup�re le dossier qui doit contenir les sources
		IProject project = javaProject.getProject();
		IFolder root = project.getFolder(srcFolderPath);
		
		// V�rifie que le dossier soit bien un dossier qui contient du code source
		if( !Utils.getSourceFolders(javaProject).contains(root.getFullPath()) )
			return null;
		
		return javaProject.getPackageFragmentRoot(root).createPackageFragment(pkg, true, monitor);
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
}
