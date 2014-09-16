package fr.asi.xsp.ccexport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class Utils {

	/**
	 * Retourne un projet à partir de son nom
	 * @param name le nom du projet
	 * @return le projet
	 */
	public static IProject getProjectFromName(String name) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(name);
	}
	
	/**
	 * Pour savoir si un projet est d'une nature donnée
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
	 * Retourne les répertoires source d'un
	 * projet de type Java
	 * @param project le projet
	 * @throws CoreException 
	 */
	public static List<IPath> getSourceFolders(IProject project) throws CoreException {
		List<IPath> ret = new ArrayList<IPath>();
		
		if( !isOfNature(project, JavaCore.NATURE_ID) )
			return ret;
		
		IJavaProject javaProject = JavaCore.create(project);
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
}
