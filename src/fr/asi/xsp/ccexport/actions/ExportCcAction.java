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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Action pour exporter un custom control.
 * FIXME: Il ne sait pas gérer la traduction telle que gérée par les XPages avec les fichiers .properties.
 * @author Lionel HERVIER
 */
public class ExportCcAction extends BaseCcAction {

	/**
	 * Constructeur
	 * @param srcProject le projet Java source (celui de la base NSF)
	 */
	public ExportCcAction(IProject srcProject) {
		super(srcProject);
	}
	
	/**
	 * Exporte un Custom control
	 * @param xspConfig le fichier .xsp-config à exporter
	 * @param monitor le moniteur
	 * @throws CoreException en cas de problème
	 * @throws IOException en cas de problème
	 */
	public void exportXspConfig(String xspConfig, IProgressMonitor monitor) throws CoreException, IOException {
		InputStream in = null;
		Reader reader = null;
		try {
			// Le xsp-config source
			IPath srcXspConfigPath = CC_FOLDER_PATH.append(xspConfig);
			IFile srcXspConfig = this.getSrcProject().getFile(srcXspConfigPath);
			
			// Le xsp-config de destination
			IPath destXspConfigPath = Constants.getProp_sourceFolder(this.getSrcProject())
					.append(Constants.getProp_xspConfigPath(this.getSrcProject()))
					.append(xspConfig);
			IFile destXspConfig = this.getDestProject().getFile(destXspConfigPath);
			
			// Supprime fichier s'il existe dans la destination
			if( destXspConfig.exists() )
				destXspConfig.delete(true, new NullProgressMonitor());
			
			// Copie le xsp-config source dans la destination
			srcXspConfig.copy(
					destXspConfig.getFullPath(), 
					true, 
					new NullProgressMonitor()
			);
			
			// Adapte le xsp-config de destination
			in = destXspConfig.getContents();
			reader = new InputStreamReader(in, destXspConfig.getCharset());
			char[] buffer = new char[4 * 1024];
			StringBuffer sb = new StringBuffer();
			int read = reader.read(buffer);
			while( read != -1 ) {
				sb.append(buffer, 0, read);
				read = reader.read(buffer);
			}
			String s = sb.toString();
			
			s = s.replaceAll(
					"<composite-file>/(.*).xsp</composite-file>", 
					"<composite-file>/" + Constants.getProp_classesPath(this.getSrcProject()) + "/$1</composite-file>"
			);
			InputStream updatedIn = new ByteArrayInputStream(s.getBytes(destXspConfig.getCharset()));
			destXspConfig.setContents(updatedIn, true, false, new NullProgressMonitor());
			
			// TODO: Adapte le fichier .xsp-config pour y inclure le contenu du .xsp en design definition
			
			// TODO: Adapte le fichier xsp-config.list à la racine du projet
			
		} finally {
			Utils.closeQuietly(reader);
			Utils.closeQuietly(in);
		}
	}
	
	/**
	 * Exporte une classe Java
	 * @param classeFile le nom de la classe Java
	 * @param monitor le moniteur
	 * @throws CoreException en cas de problème
	 * @throws IOException en cas de problème
	 */
	public void exportJava(String classFile, IProgressMonitor monitor) throws CoreException, IOException {
		InputStream in = null;
		Reader reader = null;
		try {
			IJavaProject src = JavaCore.create(this.getSrcProject());
			IJavaProject dest = JavaCore.create(this.getDestProject());
			
			// Le .java source (sous la forme d'un vrai fichier .java, pas d'un IFile)
			IPath javaSrcPath = JAVA_FOLDER_PACKAGE.append(classFile);
			IJavaElement javaSrc = src.findElement(javaSrcPath);
			
			// Le package de destination (sous la forme d'un vrai package, pas d'un IFolder)
			IPath sourcesFolderPath = Constants.getProp_sourceFolder(this.getSrcProject());
			IFolder sourcesFolder = this.getDestProject().getFolder(sourcesFolderPath);
			IPackageFragmentRoot packageRoot = dest.getPackageFragmentRoot(sourcesFolder);
			IPackageFragment destPackage = packageRoot.getPackageFragment(Constants.getProp_classesPackage(this.getSrcProject()));
			
			// Copie la classe
			// Passer par un IJavaElement permet d'adapter le package déclaré dans la classe
			src.getJavaModel().copy(
					new IJavaElement[] {javaSrc}, 
					new IJavaElement[] {destPackage}, 
					null, 
					null, 
					true, 
					new NullProgressMonitor()
			);
			
			// Le .java de destination
			IPath javaDestPath = destPackage.getResource().getProjectRelativePath().append(classFile);
			IFile javaDest = this.getDestProject().getFile(javaDestPath);
			
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
			s = s.replaceAll("\"/(.*).xsp\"", "\"/" + Constants.getProp_classesPackage(this.getSrcProject()).replace('.', '/') + "/$1\"");
			
			InputStream updatedIn = new ByteArrayInputStream(s.getBytes(javaDest.getCharset()));
			javaDest.setContents(updatedIn, true, false, new NullProgressMonitor());
			
		} finally {
			Utils.closeQuietly(reader);
			Utils.closeQuietly(in);
		}
	}

	/**
	 * @see fr.asi.xsp.ccexport.actions.BaseCcAction#execute(String, IProgressMonitor)
	 */
	@Override
	public void execute(String cc, IProgressMonitor monitor) {
		String classFile = cc.substring(0, 1).toUpperCase() + cc.substring(1) + ".java";
		String xspConfig = cc + ".xsp-config";
		try {
			this.exportXspConfig(xspConfig, new NullProgressMonitor());
			this.exportJava(classFile, new NullProgressMonitor());
		} catch(CoreException e) {
			throw new RuntimeException(e);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
