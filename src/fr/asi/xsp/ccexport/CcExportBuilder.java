package fr.asi.xsp.ccexport;

import java.util.Map;

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

import fr.asi.xsp.ccexport.actions.BaseResourceAction;
import fr.asi.xsp.ccexport.actions.ExportJavaAction;
import fr.asi.xsp.ccexport.actions.ExportXspConfigAction;
import fr.asi.xsp.ccexport.actions.GenerateXspConfigListAction;
import fr.asi.xsp.ccexport.actions.RemoveJavaAction;
import fr.asi.xsp.ccexport.actions.RemoveXspConfigAction;
import fr.asi.xsp.ccexport.actions.SyncAction;
import fr.asi.xsp.ccexport.util.BooleanHolder;
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
		// Initialise le lien entre les deux projets (au cas où)
		if( !Utils.initializeLink(this.getProject(), new NullProgressMonitor()) )
			return null;
		
		// Pour détecter s'il est nécessaire de mettre à jour le xsp-config.list
		final BooleanHolder updateXspConfigList = new BooleanHolder(false);
		
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
				
				// Détecte si on créé ou supprime un xsp-config
				// (pour pouvoir mettre à jour le xsp-config.list)
				if( "xsp-config".equals(file.getFileExtension()) )
					if( kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED )
						updateXspConfigList.value = true;
				
				// Exécute l'action
				BaseResourceAction action;
				if( exporting )
					if( "xsp-config".equals(file.getFileExtension()) )
						action = new ExportXspConfigAction(CcExportBuilder.this.getProject());
					else
						action = new ExportJavaAction(CcExportBuilder.this.getProject());
				else
					if( "xsp-config".equals(file.getFileExtension()) )
						action = new RemoveXspConfigAction(CcExportBuilder.this.getProject());
					else
						action = new RemoveJavaAction(CcExportBuilder.this.getProject());
				action.execute(file, new NullProgressMonitor());
				
				return true;
			}
		});
		
		// Met à jour le fichier xsp-config.list si c'est nécessaire
		if( updateXspConfigList.value ) {
			GenerateXspConfigListAction action = new GenerateXspConfigListAction(this.getProject());
			action.execute(new NullProgressMonitor());
		}
		
		// Sauver le workspace déclenche la re-compile des classes qu'on a ajoutées/modifiée/supprimées 
		ResourcesPlugin.getWorkspace().save(false, new NullProgressMonitor());
		return null;
	}

}
