package org.openntf.xsp.ccexport.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.openntf.xsp.ccexport.Constants;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.PropUtils;
import org.openntf.xsp.ccexport.util.Utils;

/**
 * Action to export the .java files generated from a custom control by the XPage compiler.
 * @author Lionel HERVIER
 */
public class ExportJavaAction extends BaseResourceAction {

	/**
	 * Constructor
	 * @param srcProject The source project (NSF)
	 */
	public ExportJavaAction(IProject srcProject) {
		super(srcProject);
	}
	
	/**
	 * @see org.openntf.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		ConsoleUtils.info("Exporting java: " + file.getFullPath());
		
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		InputStream in = null;
		Reader reader = null;
		try {
			IJavaProject src = JavaCore.create(this.srcProject);
			IJavaProject dest = JavaCore.create(this.destProject);
			
			// The .java source (as a "real" java file, not as an IFile)
			String classFile = file.getName();
			IPath javaSrcPath = Constants.JAVA_PACKAGE.append(classFile);
			IJavaElement javaSrc = src.findElement(javaSrcPath);
			
			// The destination package (as a "real" package, not as an IFolder)
			IPath sourcesFolderPath = PropUtils.getProp_sourceFolderPath(this.srcProject);
			IFolder sourcesFolder = this.destProject.getFolder(sourcesFolderPath);
			IPackageFragmentRoot packageRoot = dest.getPackageFragmentRoot(sourcesFolder);
			IPackageFragment destPackage = packageRoot.getPackageFragment(PropUtils.getProp_javaPackage(this.srcProject));
			
			// Copy the Java class
			// IJavaElement will update the package name for us (same as when copying/pasting a java class into another package).
			src.getJavaModel().copy(
					new IJavaElement[] {javaSrc}, 
					new IJavaElement[] {destPackage}, 
					null, 
					null, 
					true, 
					progress.newChild(50)
			);
			
			// The destination .java file
			IPath javaDestPath = destPackage.getResource().getProjectRelativePath().append(classFile);
			IFile javaDest = this.destProject.getFile(javaDestPath);
			
			// Update the content to reference the java class instead of the xsp files
			in = javaDest.getContents();
			reader = new InputStreamReader(in, javaDest.getCharset());
			char[] buffer = new char[4 * 1024];
			StringBuffer sb = new StringBuffer();
			int read = reader.read(buffer);
			while( read != -1 ) {
				sb.append(buffer, 0, read);
				read = reader.read(buffer);
			}
			String s = sb.toString();
			s = s.replaceAll("\"/(.*).xsp\"", "\"/" + PropUtils.getProp_javaPackage(this.srcProject).replace('.', '/') + "/$1\"");
			
			InputStream updatedIn = new ByteArrayInputStream(s.getBytes(javaDest.getCharset()));
			javaDest.setContents(
					updatedIn, 
					true, 
					false, 
					progress.newChild(50)
			);
			
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} finally {
			Utils.closeQuietly(reader);
			Utils.closeQuietly(in);
		}
	}
}
