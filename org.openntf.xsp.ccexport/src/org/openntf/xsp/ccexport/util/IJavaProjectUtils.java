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
 * Usefull methods to managed java projects
 * @author Lionel HERVIER
 */
public class IJavaProjectUtils {

	/**
	 * Returns the source folders of a java project
	 * @param javaProject the projet
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
	 * Create a package into a java project. Only returns a package that already exists
	 * @param javaProject the (java) projectintp which we will create the package
	 * @param srcFolder the source folder to create the package into
	 * @param pkg the name of the package to create
	 * @param monitor a monitor
	 * @return the package (or null is the source folder does not exists)
	 * @throws CoreException in cas of problem
	 */
	public static IPackageFragment createPackage(
			IJavaProject javaProject, 
			IPath srcFolderPath, 
			String pkg, 
			IProgressMonitor monitor) throws CoreException {
		// Get the source folder
		IProject project = javaProject.getProject();
		IFolder root = project.getFolder(srcFolderPath);
		
		// Check that the source folder is declared as such
		if( !IJavaProjectUtils.getSourceFolders(javaProject).contains(root.getFullPath()) )
			return null;
		
		return javaProject.getPackageFragmentRoot(root).createPackageFragment(pkg, true, monitor);
	}
}
