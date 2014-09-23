package fr.asi.xsp.ccexport.actions;

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

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.PropUtils;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Action pour exporter un custom control compilé (en fichier java).
 * FIXME: Il ne sait pas gérer la traduction telle que gérée par les XPages avec les fichiers .properties.
 * @author Lionel HERVIER
 */
public class ExportJavaAction extends BaseResourceAction {

	/**
	 * Constructeur
	 * @param srcProject le projet Java source (celui de la base NSF)
	 */
	public ExportJavaAction(IProject srcProject) {
		super(srcProject);
	}
	
	/**
	 * @see fr.asi.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		System.out.println("Exporting java: " + file.getFullPath());
		
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		InputStream in = null;
		Reader reader = null;
		try {
			IJavaProject src = JavaCore.create(this.srcProject);
			IJavaProject dest = JavaCore.create(this.destProject);
			
			// Le .java source (sous la forme d'un vrai fichier .java, pas d'un IFile)
			String classFile = file.getName();
			IPath javaSrcPath = Constants.JAVA_PACKAGE.append(classFile);
			IJavaElement javaSrc = src.findElement(javaSrcPath);
			
			// Le package de destination (sous la forme d'un vrai package, pas d'un IFolder)
			IPath sourcesFolderPath = PropUtils.getProp_sourceFolderPath(this.srcProject);
			IFolder sourcesFolder = this.destProject.getFolder(sourcesFolderPath);
			IPackageFragmentRoot packageRoot = dest.getPackageFragmentRoot(sourcesFolder);
			IPackageFragment destPackage = packageRoot.getPackageFragment(PropUtils.getProp_javaPackage(this.srcProject));
			
			// Copie la classe
			// Passer par un IJavaElement permet d'adapter le package déclaré dans la classe
			src.getJavaModel().copy(
					new IJavaElement[] {javaSrc}, 
					new IJavaElement[] {destPackage}, 
					null, 
					null, 
					true, 
					progress.newChild(50)
			);
			
			// Le .java de destination
			IPath javaDestPath = destPackage.getResource().getProjectRelativePath().append(classFile);
			IFile javaDest = this.destProject.getFile(javaDestPath);
			
			// Adapte son contenu
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
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Utils.closeQuietly(reader);
			Utils.closeQuietly(in);
		}
	}
}
