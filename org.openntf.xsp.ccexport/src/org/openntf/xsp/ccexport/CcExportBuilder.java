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
 * Builder to export a custom control into a plug-in projects 
 * that hosts an XPage Library.
 * TODO: If you are using internationalisation with XPages, .properties files 
 * associated with custom controls are not exported... 
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
					
					// Synchro launch
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
			
			// Initialize the link between the two projects (if needed)
			if( !Utils.initializeLink(this.getProject(), progress.newChild(10)) )
				return null;
			
			// To detect if it's necessary to update the xsp-config.list file
			final BooleanHolder updateXspConfigList = new BooleanHolder(false);
			
			// Walk through the delta 
			IResourceDelta delta = this.getDelta(this.getProject());
			if( delta == null )
				return null;
			final SubMonitor subProgress = SubMonitor.convert(progress.newChild(50));
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					subProgress.setWorkRemaining(2);
					
					// Only accepting additions/updates/deletions
					int kind = delta.getKind();
					if( kind != IResourceDelta.ADDED && kind != IResourceDelta.CHANGED && kind != IResourceDelta.REMOVED )
						return true;
					
					// Only accepting updates on files
					IResource currResource = delta.getResource();
					if (currResource.getType() != IResource.FILE)
						return true;
					
					// Are we exporting or removing ?
					boolean exporting = kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED;
					
					// Get the resource to build
					IFile file = (IFile) currResource;
					IPath location = file.getProjectRelativePath();
					
					// Only working with resources that are stored into the "CustomControls" folder, 
					// or inside the "xsp" package of the "Local" source folder
					if( !Constants.CC_FOLDER_PATH.isPrefixOf(location) && !Constants.JAVA_FOLDER_PATH.append("xsp").isPrefixOf(location) )
						return true;
					
					// Only working on .java and .xsp-config files
					String ext = location.getFileExtension();
					if( !"xsp-config".equals(ext) && !"java".equals(ext) )
						return true;
					
					// Only working on custom controls with the right prefix
					if( !file.getName().toUpperCase().startsWith(PropUtils.getProp_ccPrefix(CcExportBuilder.this.getProject()).toUpperCase()) )
						return true;
					
					// On ne s'intéresse qu'aux fichiers .java qui correspondent à un Custom Control
					// Il faut filtrer les XPages. Pour cela, on regarde si on trouve le fichier .xsp
					// Mais le xsp est supprimé AVANT le fichier java...
					// En cas de suppression, on essaie donc de supprimer la classe, quoi qu'il se passe
					// Only working on .java files that corresponds to a custom control.
					// We have to filter XPages. To do this, we are trying to find a the .xsp file.
					// In cas of removal, the .xsp file is removed BEFORE the .java file...
					// So, when removing an .xsp file, we try to remove the .java file anyway.
					if( exporting && "java".equals(ext) ) {
						String fileName = Utils.getFileNameWithoutExtension(file.getName());
						fileName = fileName.replaceAll("_005f", "_");		// "_" in custom controls names are replaces with "_005f"
						String cc = fileName;
						cc = Utils.normalizeMin(fileName);		// First try lowercase
						IFile xsp = CcExportBuilder.this.getProject().getFile(Constants.CC_FOLDER_PATH.append(cc + ".xsp"));
						if( !xsp.exists() ) {
							cc = Utils.normalizeMaj(fileName);	// Second try with first letter uppercase
							xsp = CcExportBuilder.this.getProject().getFile(Constants.CC_FOLDER_PATH.append(cc + ".xsp"));
							if( !xsp.exists() )
								return true;
						}
					}
					
					// Are we creating or removing a .xsp-config file ?
					// If it is the case, we will have to update the xsp-config.list file.
					if( "xsp-config".equals(file.getFileExtension()) )
						if( kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED )
							updateXspConfigList.value = true;
					
					// Run the action
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
			
			// Update the xsp-config.list file if needed
			if( updateXspConfigList.value ) {
				GenerateXspConfigListAction action = new GenerateXspConfigListAction(this.getProject());
				action.execute(progress.newChild(30));
			}
			progress.setWorkRemaining(20);
			
			// Save the workbench will trigger compilation of the classes we updated/added 
			ResourcesPlugin.getWorkspace().save(false, progress.newChild(20));
			return null;
		} finally {
			if( monitor != null ) monitor.done();
		}
	}

}
