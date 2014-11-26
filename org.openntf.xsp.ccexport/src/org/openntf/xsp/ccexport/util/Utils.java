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

/**
 * Usefull methods
 * @author Lionel HERVIER
 */
public class Utils {

	/**
	 * Update the string so the first letter is uppercase
	 * @param s the string
	 * @return the string with first letter uppercase
	 */
	public static String normalizeMaj(String s) {
		if( s.length() <= 1 )
			return s.toUpperCase();
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	/**
	 * Update the string so the first letter is lowercase
	 * @param s the string
	 * @return the string with first letter lowercase
	 */
	public static String normalizeMin(String s) {
		if( s.length() <= 1 )
			return s.toUpperCase();
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
	
	/**
	 * Remove the extension from a file name
	 * @param fileName the file name (with extension)
	 * @return the file name (without extension)
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if( pos == -1 )
			return fileName;
		return fileName.substring(0, pos);
	}
	
	/**
	 * To close a stream, reader, writer, etc...
	 * @param o the object to close
	 */
	public static void closeQuietly(Closeable o) {
		if( o == null )
			return;
		try {
			o.close();
		} catch (IOException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Initialize the link between an NSF project and a plug-in project
	 * @param prj the source project
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 * @return true of ok. False otherwise.
	 */
	public static boolean initializeLink(IProject nsfProject, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		try {
			IProject project = PropUtils.getProp_destProject(nsfProject);
			
			// Check that dest project exists and is of Java nature
			if( !project.exists() )
				return false;
			if( !IProjectUtils.hasNature(project, JavaCore.NATURE_ID) )
				return false;
			
			// Open dest project if needed 
			if( !project.isOpen() )
				project.open(progress.newChild(25));
			progress.setWorkRemaining(75);
			
			// Create the packages
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
			
			// Create the folder that will contain the xsp-config.list file
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
	 * To ckeck if a given project is using our builder
	 * @param project the source project (NSF)
	 * @return true is our builder is running. False otherwise.
	 * @throws CoreException in cas of trouble.
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
	 * Check for an empty string
	 * @param s the string
	 * @return true if the string is empty. False otherwise.
	 */
	public static boolean isEmpty(String s) {
		if( s == null )
			return true;
		return s.length() == 0;
	}
	
	/**
	 * Check if two arrays are equals. Understand:
	 * - If they have the same number of elements
	 * - If each element is equals regarding the .equals methods.
	 * This method accepts arrays with null elements.
	 * @param t1 the first array
	 * @param t2 the second array
	 * @return true of they are equals. False otherwise.
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
