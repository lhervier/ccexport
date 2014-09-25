package org.openntf.xsp.ccexport;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.openntf.xsp.ccexport.actions.BaseResourceAction;
import org.openntf.xsp.ccexport.actions.ExportJavaAction;
import org.openntf.xsp.ccexport.actions.ExportXspConfigAction;
import org.openntf.xsp.ccexport.actions.GenerateXspConfigListAction;
import org.openntf.xsp.ccexport.actions.RemoveJavaAction;
import org.openntf.xsp.ccexport.actions.RemoveXspConfigAction;
import org.openntf.xsp.ccexport.actions.SyncAction;
import org.openntf.xsp.ccexport.util.BooleanHolder;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.PropUtils;
import org.openntf.xsp.ccexport.util.Utils;


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
		final IProject project = this.getProject();
		Job job = new Job("Exporting Custom Controls") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor progress = SubMonitor.convert(monitor, 100);
				try {
					// Initialise l'objet
					if( !Utils.initializeLink(project, progress.newChild(20)) )
						return Status.OK_STATUS;
					
					// Lance une synchro
					SyncAction action = new SyncAction(project);
					action.execute(progress.newChild(80));
					
					return Status.OK_STATUS;
				} catch(CoreException e) {
					ConsoleUtils.error(e);
					throw new RuntimeException(e);
				} finally {
					if( monitor != null ) monitor.done();
				}
			}
		};
		job.schedule();
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
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		try {
			
			// Initialise le lien entre les deux projets (au cas où)
			if( !Utils.initializeLink(this.getProject(), progress.newChild(10)) )
				return null;
			
			// Pour détecter s'il est nécessaire de mettre à jour le xsp-config.list
			final BooleanHolder updateXspConfigList = new BooleanHolder(false);
			
			// Parcours le delta
			IResourceDelta delta = this.getDelta(this.getProject());
			if( delta == null )
				return null;
			final SubMonitor subProgress = SubMonitor.convert(progress.newChild(50));
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					subProgress.setWorkRemaining(2);
					
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
					
					// On ne s'intéresse qu'aux cc qui commencent par le bon préfixe
					if( !file.getName().toUpperCase().startsWith(PropUtils.getProp_ccPrefix(CcExportBuilder.this.getProject()).toUpperCase()) )
						return true;
					
					// On ne s'intéresse qu'aux fichiers .java qui correspondent à un Custom Control
					// Il faut filtrer les XPages. Pour cela, on regarde si on trouve le fichier .xsp
					// Mais le xsp est supprimé AVANT le fichier java...
					// En cas de suppression, on essaie donc de supprimer la classe, quoi qu'il se passe
					if( exporting && "java".equals(ext) ) {
						String cc = Utils.getFileNameWithoutExtension(file.getName());
						cc = Utils.normalizeMin(cc);		// On essaie d'abord en minuscule
						IFile xsp = CcExportBuilder.this.getProject().getFile(Constants.CC_FOLDER_PATH.append(cc + ".xsp"));
						if( !xsp.exists() ) {
							cc = Utils.normalizeMaj(cc);
							xsp = CcExportBuilder.this.getProject().getFile(Constants.CC_FOLDER_PATH.append(cc + ".xsp"));
							if( !xsp.exists() )
								return true;
						}
					}
					
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
					action.execute(file, subProgress.newChild(1));
					
					return true;
				}
			});
			progress.setWorkRemaining(40);
			
			// Met à jour le fichier xsp-config.list si c'est nécessaire
			if( updateXspConfigList.value ) {
				GenerateXspConfigListAction action = new GenerateXspConfigListAction(this.getProject());
				action.execute(progress.newChild(30));
			}
			progress.setWorkRemaining(20);
			
			// Sauver le workspace déclenche la re-compile des classes qu'on a ajoutées/modifiée/supprimées 
			ResourcesPlugin.getWorkspace().save(false, progress.newChild(20));
			return null;
		} finally {
			if( monitor != null ) monitor.done();
		}
	}

}
