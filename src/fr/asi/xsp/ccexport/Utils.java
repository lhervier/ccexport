package fr.asi.xsp.ccexport;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;

public class Utils {

	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath JAVA_FOLDER_PATH = new Path("Local");
	
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
	 * Vérifie qu'un package existe bien dans un projet Java, et le créé si nécessaire.
	 * @param javaProject le projet (java) dans lequel le créer
	 * @param srcFolder le répertoire source dans lequel créer les packages
	 * @param pkg le nom du package à créer
	 * @param monitor le moniteur
	 * @return le package
	 * @throws CoreException en cas de problème
	 */
	public static IPackageFragment createPackage(IJavaProject javaProject, IPath srcFolderPath, String pkg, IProgressMonitor monitor) throws CoreException {
		// Récupère le dossier qui doit contenir les sources
		IProject project = javaProject.getProject();
		IFolder root = project.getFolder(srcFolderPath);
		
		// Vérifie que le dossier soit bien un dossier qui contient du code source
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
		IPath ccPath = CC_FOLDER_PATH.append(cc + ".xsp-config");
		IFile ccFile = project.getFile(ccPath);
		return ccFile.exists();
	}
}
