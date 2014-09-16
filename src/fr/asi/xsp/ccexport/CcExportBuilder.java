package fr.asi.xsp.ccexport;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class CcExportBuilder extends IncrementalProjectBuilder {

	/**
	 * Le chemin vers les custom controls
	 */
	private final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers les custom controls
	 */
	private final static IPath XSP_FOLDER_PATH = new Path("Local/xsp");
	
	/**
	 * Le nom du projet vers lequel exporter
	 */
	private String destProjectName;
	
	/**
	 * Le répertoire source dans lequel exporter
	 */
	private IPath srcFolderPath;
	
	/**
	 * Le package dans lequel exporter les classes
	 */
	private String classesPkg;
	
	/**
	 * Le package dans lequel exporter les xsp-config
	 */
	private String xspConfigPkg;
	
	/**
	 * Constructeur
	 */
	public CcExportBuilder() {
		this.destProjectName = "test-export";
		this.srcFolderPath = new Path("src");
		this.classesPkg = "fr.asi.xsp.composants.xsp";
		this.xspConfigPkg = "fr.asi.xsp.composants.config";
	}
	
	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException {
		// Récupère le projet de destination sous la forme d'un projet Java
		IProject destProject = Utils.getProjectFromName(this.destProjectName);
		if( !destProject.exists() )
			return null;
		if( !Utils.isOfNature(destProject, JavaCore.NATURE_ID) )
			return null;
		if( !destProject.isOpen() )
			destProject.open(new NullProgressMonitor());
		final IJavaProject javaDestProject = JavaCore.create(destProject);
		
		// Créé les deux packages
		final IPackageFragment javaPkg = Utils.createPackage(javaDestProject, this.srcFolderPath, this.classesPkg, new NullProgressMonitor());
		if( javaPkg == null )
			return null;
		final IPackageFragment xspConfigPkg = Utils.createPackage(javaDestProject, this.srcFolderPath, this.xspConfigPkg, new NullProgressMonitor());
		if( xspConfigPkg == null )
			return null;
		
		// Récupère le projet courant sous la forme d'un projet Java
		final IJavaProject javaCurrProject = JavaCore.create(this.getProject());
		
		// Une Set qui contient les noms des CC déjà traités
		final Set<String> processedXspConfig = new HashSet<String>();
		final Set<String> processedCc = new HashSet<String>();
		
		// Parcours le delta
		IResourceDelta delta = this.getDelta(this.getProject());
		if( delta == null )
			return null;
		delta.accept(new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				// On n'accepte que les ajout/modif/suppression
				int kind = delta.getKind();
				if( kind != IResourceDelta.ADDED && kind != IResourceDelta.CHANGED && kind != IResourceDelta.REMOVED )
					return true;
				
				// On n'accepte que les modifs sur des fichiers
				IResource currResource = delta.getResource();
				if (currResource.getType() != IResource.FILE)
					return true;
				
				// Récupère la ressource a builder
				IFile file = (IFile) currResource;
				IPath location = file.getProjectRelativePath();
				
				// ======================================================================================================
				// Exporte le xsp-config: 
				// Comme on reporte le contenu du xsp dans le xsp-config, on réagit au changement de l'un ou de l'autre
				// ======================================================================================================
				if( CcExportBuilder.CC_FOLDER_PATH.isPrefixOf(location) ) {
					
					String xspConfig = Utils.getFileNameWithoutExtension(location.lastSegment()) + ".xsp-config";
					if( processedXspConfig.contains(xspConfig) )
						return true;
					processedXspConfig.add(xspConfig);
					
					IPath xspConfigDest = xspConfigPkg.getResource().getProjectRelativePath().append(xspConfig);
					
					// Ajout ou modification
					if( kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED ) {
						// On copie le xsp-config
						try {
							IFile xspConfigSrc = javaCurrProject.getProject().getFile("CustomControls/" + xspConfig);
							IFile dest = javaDestProject.getProject().getFile(xspConfigDest);
							if( dest.exists() )
								dest.delete(true, new NullProgressMonitor());
							
							xspConfigSrc.copy(
									dest.getFullPath(), 
									true, 
									new NullProgressMonitor()
							);
						} catch (CoreException e) {
							throw new RuntimeException(e);
						}
					
					// Suppression
					} else if( kind == IResourceDelta.REMOVED ) {
						try {
							IFile dest = javaDestProject.getProject().getFile(xspConfigDest);
							if( dest.exists() )
								dest.delete(true, new NullProgressMonitor());
						} catch (CoreException e) {
							throw new RuntimeException(e);
						}
					}
				
				// Exporte une classe Java:
				// On ne réagit surtout pas à la modif du XSP car le fichier java peut ne pas exister
				// ===================================================================================
				} else if( CcExportBuilder.XSP_FOLDER_PATH.isPrefixOf(location) ) {
					
					String cc = Utils.getFileNameWithoutExtension(location.lastSegment()) + ".xsp";
					if( processedCc.contains(cc) )
						return true;
					processedCc.add(cc);
					
					// Suppression
					if( kind == IResourceDelta.REMOVED ) {
						try {
							IJavaElement java = javaDestProject.findElement(javaPkg.getPath().append(location.lastSegment()));
							javaDestProject.getJavaModel().delete(new IJavaElement[] {java}, true, new NullProgressMonitor());
						} catch (JavaModelException e) {
							throw new RuntimeException(e);
						}
						
					// Ajout ou modification
					} else if( kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED ) {
						
						// Vérifie qu'il correspond à un custom control (et pas à une XPage)
						IFile min = javaCurrProject.getProject().getFile("CustomControls/" + cc.substring(0, 1).toLowerCase() + cc.substring(1));
						IFile maj = javaCurrProject.getProject().getFile("CustomControls/" + cc.substring(0, 1).toUpperCase() + cc.substring(1));
						if( !min.exists() && maj.exists() )
							return true;
						
						// Copie la classe
						try {
							IPath javaPath = new Path("xsp").append(location.lastSegment());
							IJavaElement java = javaCurrProject.findElement(javaPath);
							javaCurrProject.getJavaModel().copy(
									new IJavaElement[] {java}, 
									new IJavaElement[] {javaPkg}, 
									null, 
									null, 
									true, 
									new NullProgressMonitor()
							);
						} catch (JavaModelException e) {
							throw new RuntimeException(e);
						}
					}
				}
				return true;
			}
		});
		
		// Sauver le workspace déclenche la re-compile des classes qu'on a ajouté/modifié/supprimées 
		ResourcesPlugin.getWorkspace().save(false, new NullProgressMonitor());
		return null;
	}

}
