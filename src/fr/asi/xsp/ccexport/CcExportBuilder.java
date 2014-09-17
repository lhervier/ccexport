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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import fr.asi.xsp.ccexport.actions.AbstractAction;
import fr.asi.xsp.ccexport.actions.ExportCcAction;
import fr.asi.xsp.ccexport.actions.RemoveCcAction;

/**
 * Une Builder capable d'exporter un Custom Control dans un
 * projet de type Library
 * FIXME: En l'�tat, ne changer que les propri�t�s d'un CC l'exporte 2 fois.
 * @author Lionel HERVIER
 */
public class CcExportBuilder extends IncrementalProjectBuilder {

	/**
	 * Initialisation du builder
	 * @param monitor le moniteur
	 * @throws CoreException 
	 */
	public boolean initialize(IProgressMonitor monitor) throws CoreException {
		IProject nsfProject = this.getProject();
		
		IProject project = Utils.getProjectFromName(nsfProject.getPersistentProperty(Utils.PROP_PROJECT_NAME));
		
		// V�rifie que le projet existe et est de nature java
		if( !project.exists() )
			return false;
		if( !Utils.isOfNature(project, JavaCore.NATURE_ID) )
			return false;
		
		// Ouvre le projet si n�cessaire
		if( !project.isOpen() )
			project.open(new NullProgressMonitor());
		
		// Cr�� les deux packages
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragment javaPkg = Utils.createPackage(
				javaProject, 
				new Path(nsfProject.getPersistentProperty(Utils.PROP_SOURCE_FOLDER)), 
				nsfProject.getPersistentProperty(Utils.PROP_CLASSES_PACKAGE), 
				new NullProgressMonitor()
		);
		if( javaPkg == null )
			return false;
		IPackageFragment xspConfigPkg = Utils.createPackage(
				javaProject, 
				new Path(nsfProject.getPersistentProperty(Utils.PROP_SOURCE_FOLDER)), 
				nsfProject.getPersistentProperty(Utils.PROP_XSPCONFIG_PACKAGE), 
				new NullProgressMonitor()
		);
		if( xspConfigPkg == null )
			return false;
		
		return true;
	}
	
	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException {
		// Initialise l'objet
		if( !this.initialize(new NullProgressMonitor()) )
			return null;
		
		// Les deux actions
		final IProject prj = this.getProject();
		final AbstractAction exportAction = new ExportCcAction(prj);
		final AbstractAction removeAction = new RemoveCcAction(prj);
		
		// Une Set qui contient les noms des CC d�j� trait�s dans le build
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
				
				// Est ce qu'on exporte ? Et est ce qu'on supprime ?
				boolean exporting = kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED;
				
				// R�cup�re la ressource a builder
				IFile file = (IFile) currResource;
				IPath location = file.getProjectRelativePath();
				
				// On ne s'int�resse qu'aux ressources qui sont dans le rep "CustomControls", ou dans le package "xsp" du rep source "Local"
				if( !Utils.CC_FOLDER_PATH.isPrefixOf(location) && !Utils.JAVA_FOLDER_PATH.append("xsp").isPrefixOf(location) )
					return true;
				
				// On ne s'int�resse qu'aux fichiers java et xsp-config
				String ext = location.getFileExtension();
				if( !"xsp-config".equals(ext) && !"java".equals(ext) )
					return true;
				
				// Le nom du Custom Control qu'on exporte : On peut le d�duire � partir du fichier (que ce soit un .java, .xsp-config ou .xsp)
				String cc = Utils.getFileNameWithoutExtension(location.lastSegment());
				if( "java".equals(location.getFileExtension()) ) {
					// Sanity check
					String first = cc.substring(0, 1);
					if( !first.equals(first.toUpperCase()) )
						throw new RuntimeException("La classe: " + location + " vient d'�tre modifi�e. Or, son nom ne commence pas par une Majuscule !!");
					
					// Habituellement, les noms de cc commencent par une minuscule. Alors on tente en premier...
					cc = cc.substring(0, 1).toLowerCase() + cc.substring(1);
					
					// Cas o� on exporte => On regarde si on trouve le .xsp dans le projet source
					if( exporting ) {
						if( !Utils.ccExists(CcExportBuilder.this.getProject(), cc) ) {
							cc = cc.substring(0, 1).toUpperCase() + cc.substring(1);	// Sinon, on tente avec une majuscule
							if( !Utils.ccExists(CcExportBuilder.this.getProject(), cc) )
								return true;											// On doit �tre face au .java d'une XPage
						}
					
					// Cas o� on supprime => On regarde si on trouve le .xsp-config dans le projet dest (qu'on n'a pas encore pu supprimer � ce niveau)
					} else {
						String src;
						String xspConfigPkg;
						try {
							src = prj.getPersistentProperty(Utils.PROP_SOURCE_FOLDER);
							xspConfigPkg = prj.getPersistentProperty(Utils.PROP_XSPCONFIG_PACKAGE);
						} catch(CoreException e) {
							throw new RuntimeException(e);
						}
						IPath xspConfigPath = new Path(src).append(xspConfigPkg.replace('.', '/')).append(cc + ".xsp-config");
						IFile xspConfigFile = removeAction.getDestProject().getFile(xspConfigPath);
						if( !xspConfigFile.exists() )
							cc = cc.substring(0, 1).toUpperCase() + cc.substring(1);		// Si on supprime une XPage, on ne trouvera ni sa classe, ni son xsp-config dans le projet de dest.
					}
				}
				
				// On l'a peut �tre d�j� trait� pendant ce build
				if( processedCc.contains(cc) )
					return true;
				processedCc.add(cc);
				
				// Ex�cute l'action
				if( exporting )
					exportAction.execute(cc, new NullProgressMonitor());
				else
					removeAction.execute(cc, new NullProgressMonitor());
				
				return true;
			}
		});
		
		// Sauver le workspace d�clenche la re-compile des classes qu'on a ajout�es/modifi�e/supprim�es 
		ResourcesPlugin.getWorkspace().save(false, new NullProgressMonitor());
		return null;
	}

}
