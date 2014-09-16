package fr.asi.xsp.ccexport;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class CcExportBuilder extends IncrementalProjectBuilder {

	/**
	 * Le nom du projet vers lequel exporter
	 */
	private String destProjectName;
	
	/**
	 * Le répertoire source dans lequel exporter
	 */
	private String srcFolder;
	
	/**
	 * Le package dans lequel exporter les classes
	 */
	private String pckg;
	
	/**
	 * Constructeur
	 */
	public CcExportBuilder() {
		this.destProjectName = "test-export";
		this.srcFolder = "src";
		this.pckg = "fr.asi.xsp.composants.xsp";
	}
	
	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException {
		// Récupère le projet de destination
		IProject destProject = Utils.getProjectFromName(this.destProjectName);
		if( !destProject.exists() )
			return null;
		if( !destProject.isOpen() )
			destProject.open(new NullProgressMonitor());
		final IJavaProject javaDestProject = JavaCore.create(destProject);
		
		// Récupère le répertoire source dans lequel on va copier les classes
		// On vérifie que le chemin vers lequel on exporte est bien le chemin d'un rep contenant des sources
		List<IPath> srcs = Utils.getSourceFolders(destProject);
		IFolder srcFolder = destProject.getFolder(this.srcFolder);
		IPath srcPath = srcFolder.getFullPath();
		if( !srcs.contains(srcPath) )
			return null;
		
		// Vérifie que le package existe bien. On le créé si nécessaire.
		// On récupère au passage le répertoire dans lequel on va copier les classes.
		String[] pkgPath = this.pckg.split("\\.");
		IFolder root = srcFolder;
		for( int i=0; i<pkgPath.length; i++ ) {
			IFolder currFolder = root.getFolder(pkgPath[i]);
			if( !currFolder.exists() )
				currFolder.create(true, true, new NullProgressMonitor());
			root = currFolder;
		}
		
		// Récupère le projet courant
		final IProject currProject = this.getProject();
		final IJavaProject javaCurrProject = JavaCore.create(currProject);
		IFolder ccFolder = currProject.getFolder("CustomControls");
		final IPath ccFolderPath = ccFolder.getProjectRelativePath();
		
		// Une Set qui contient les noms des CC déjà traités
		final Set<String> processed = new HashSet<String>();
		
		// Parcours le delta
		IResourceDelta delta = this.getDelta(currProject);
		if( delta == null )
			return null;
		delta.accept(new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				// On n'accepte que les ajout/modif et suppr
				int kind = delta.getKind();
				if( kind != IResourceDelta.ADDED && kind != IResourceDelta.CHANGED && kind != IResourceDelta.REMOVED )
					return true;
				
				// On n'accepte que les modifs sur des fichiers
				IResource currResource = delta.getResource();
				if (currResource.getType() != IResource.FILE)
					return true;
				
				// Récupère la ressource a construire
				IFile file = (IFile) currResource;
				IPath location = file.getProjectRelativePath();
				
				// On ne s'intéresse qu'aux custom controls
				if( !ccFolderPath.isPrefixOf(location) )
					return true;
				
				// On ne s'intéresse qu'aux custom controls qu'on a pas déjà traités dans ce delta
				String cc = file.getName();
				int pos = cc.lastIndexOf('.');
				if( pos != -1 ) {
					cc = cc.substring(0, pos);
				}
				cc = cc.substring(0, 1).toUpperCase() + cc.substring(1);
				if( processed.contains(cc) )
					return true;
				processed.add(cc);
				
				if( kind == IResourceDelta.ADDED )
					System.out.print("CC Ajout ");
				else if( kind == IResourceDelta.CHANGED )
					System.out.print("CC Modification ");
				else if( kind == IResourceDelta.REMOVED )
					System.out.print("CC Suppression ");
				System.out.println(cc);
				
				// On copie sa classe
				IPath javaPath = new Path("xsp/" + cc + ".java");
				IPath packagePath = new Path(CcExportBuilder.this.pckg.replace('.', '/'));
				try {
					IJavaElement java = javaCurrProject.findElement(javaPath);
					IJavaElement pkg = javaDestProject.findElement(packagePath);
					
					IJavaModel model = javaCurrProject.getJavaModel();
					model.copy(
							new IJavaElement[] {java}, 
							new IJavaElement[] {pkg}, 
							null, 
							null, 
							true, 
							new NullProgressMonitor()
					);
				} catch (JavaModelException e) {
					throw new RuntimeException(e);
				}
				
				
				
				// On copie son xsp-config
				
				
				return true;
			}
		});
		
		
		return null;
	}

}
