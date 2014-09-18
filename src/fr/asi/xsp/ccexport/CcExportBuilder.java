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

import fr.asi.xsp.ccexport.actions.BaseCcAction;
import fr.asi.xsp.ccexport.actions.ExportCcAction;
import fr.asi.xsp.ccexport.actions.RemoveCcAction;
import fr.asi.xsp.ccexport.actions.SyncAction;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Une Builder capable d'exporter un Custom Control dans un
 * projet de type Library
 * FIXME: En l'état, ne changer que les propriétés d'un CC l'exporte 2 fois.
 * @author Lionel HERVIER
 */
public class CcExportBuilder extends IncrementalProjectBuilder {

	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#startupOnInitialize()
	 */
	@Override
	protected void startupOnInitialize() {
		try {
			// Initialise l'objet
			if( !Utils.initializeLink(this.getProject(), new NullProgressMonitor()) )
				return;
			
			// Lance une synchro
			SyncAction action = new SyncAction(this.getProject());
			action.execute(new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(
			int kind, 
			@SuppressWarnings("unchecked")
			Map args, 
			final IProgressMonitor monitor) throws CoreException {
		// Initialise l'objet
		if( !Utils.initializeLink(this.getProject(), new NullProgressMonitor()) )
			return null;
		
		// Les deux actions
		final IProject prj = this.getProject();
		final BaseCcAction exportAction = new ExportCcAction(prj);
		final BaseCcAction removeAction = new RemoveCcAction(prj);
		
		// Une Set qui contient les noms des CC déjà traités dans le build
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
				
				// Est ce qu'on exporte ? Ou est ce qu'on supprime ?
				boolean exporting = kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED;
				
				// Récupère la ressource à builder
				IFile file = (IFile) currResource;
				IPath location = file.getProjectRelativePath();
				
				// On ne s'intéresse qu'aux ressources qui sont dans le rep "CustomControls", ou dans le package "xsp" du rep source "Local"
				if( !Constants.CC_FOLDER_PATH.isPrefixOf(location) && !Constants.JAVA_FOLDER_PATH.append("xsp").isPrefixOf(location) )
					return true;
				
				// On ne s'intéresse qu'aux fichiers java et xsp-config
				String ext = location.getFileExtension();
				if( !"xsp-config".equals(ext) && !"java".equals(ext) )
					return true;
				
				// Le nom du Custom Control qu'on exporte : On peut le déduire à partir du fichier, mais c'est plus compliqué si on a que le .java
				String cc = Utils.getFileNameWithoutExtension(location.lastSegment());
				if( "java".equals(location.getFileExtension()) ) {
					// Sanity check
					String first = cc.substring(0, 1);
					if( !first.equals(first.toUpperCase()) )
						throw new RuntimeException("Le nom de la classe " + location + " ne commence pas par une Majuscule. Elle ne peut pas correspondre à un Custom Control.");
					
					// Habituellement, les noms de cc commencent par une minuscule. Alors on tente en premier...
					cc = Utils.normalizeMin(cc);
					
					// Cas où on exporte => On regarde si on trouve le .xsp-config dans le projet source
					if( exporting ) {
						if( !Utils.ccExists(CcExportBuilder.this.getProject(), cc) ) {
							cc = Utils.normalizeMaj(cc);									// Sinon, on tente avec une majuscule
							if( !Utils.ccExists(CcExportBuilder.this.getProject(), cc) )
								return true;												// On doit être face au .java d'une XPage
						}
					
					// Cas où on supprime => On regarde si on trouve le .xsp-config dans le projet dest (qu'on n'a pas encore pu supprimer à ce niveau)
					} else {
						String src = Constants.getProp_sourceFolder(prj);
						String xspConfigPkg = Constants.getProp_xspConfigPackage(prj);
						IPath xspConfigPath = new Path(src).append(xspConfigPkg.replace('.', '/')).append(cc + ".xsp-config");
						IFile xspConfigFile = removeAction.getDestProject().getFile(xspConfigPath);
						if( !xspConfigFile.exists() )
							cc = cc.substring(0, 1).toUpperCase() + cc.substring(1);		// Si on supprime une XPage, on ne trouvera ni sa classe, ni son xsp-config dans le projet de dest.
					}
				}
				
				// On l'a peut être déjà traité pendant ce build
				if( processedCc.contains(cc) )
					return true;
				processedCc.add(cc);
				
				// Exécute l'action
				if( exporting )
					exportAction.execute(cc, new NullProgressMonitor());
				else
					removeAction.execute(cc, new NullProgressMonitor());
				
				return true;
			}
		});
		
		// Sauver le workspace déclenche la re-compile des classes qu'on a ajoutées/modifiée/supprimées 
		ResourcesPlugin.getWorkspace().save(false, new NullProgressMonitor());
		return null;
	}

}
