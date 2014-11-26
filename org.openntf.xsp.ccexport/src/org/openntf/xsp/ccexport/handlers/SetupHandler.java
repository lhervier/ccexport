package org.openntf.xsp.ccexport.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.openntf.xsp.ccexport.CcExportDecorator;
import org.openntf.xsp.ccexport.Constants;
import org.openntf.xsp.ccexport.actions.SyncAction;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.IProjectUtils;
import org.openntf.xsp.ccexport.util.Utils;
import org.openntf.xsp.ccexport.wizard.SetupWizard;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

/**
 * Handler to associate an NSF to plug-in project
 * @author Lionel HERVIER
 */
public class SetupHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// We need a selection on a file or a folder
		ISelection se = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(se instanceof StructuredSelection))
			return null;
		
		// Get a hand on the project
		StructuredSelection sse = (StructuredSelection) se;
		@SuppressWarnings("unchecked")
		List selList = sse.toList();
		IProject prj = null;
		for (Object o : selList) {
			if( o instanceof IProjectNature ) {
				IProjectNature nature = (IProjectNature) o;
				prj = nature.getProject();
				break;
			}
			if( o instanceof IProject ) {
				prj = (IProject) o;
				break;
			}
		}
		
		// Only working on Domino projects
		if (!DominoResourcesPlugin.isDominoDesignerProject(prj))
			return null;
		
		ConsoleUtils.info("Setting up Cc Export");
		
		// Run the wizard
		final SetupWizard wizard = new SetupWizard(prj);
		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		if( wizardDialog.open() != Window.OK )
			return null;
		
		// Associate the nsf with the project
		final IProject project = prj;
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				SubMonitor progress = SubMonitor.convert(monitor, 100);
				try {
					// Add properties
					project.setPersistentProperty(
							Constants.PROP_DEST_PROJECT_NAME, 
							wizard.getDestProjectName()
					);
					project.setPersistentProperty(
							Constants.PROP_SOURCE_FOLDER, 
							wizard.getSourceFolder()
					);
					project.setPersistentProperty(
							Constants.PROP_CLASSES_PACKAGE, 
							wizard.getJavaExportPackage()
					);
					project.setPersistentProperty(
							Constants.PROP_XSPCONFIG_PACKAGE, 
							wizard.getXspConfigExportPackage()
					);
					project.setPersistentProperty(
							Constants.PROP_XSPCONFIG_FILE, 
							wizard.getXspConfigList()
					);
					project.setPersistentProperty(
							Constants.PROP_CCPREFIX, 
							wizard.getCcPrefix()
					);
					
					// Add the builder
					IProjectUtils.addBuilderToProject(
							project, 
							Constants.BUILDER_ID, 
							progress.newChild(25)
					);
					
					// Initialize the destination project
					if( !Utils.initializeLink(project, progress.newChild(25) ) )
						return;
					
					// Force synchronisation
					SyncAction action = new SyncAction(project);
					action.execute(progress.newChild(25));
				} catch(CoreException e) {
					ConsoleUtils.error(e);
					throw new RuntimeException(e);
				} finally {
					if( monitor != null ) monitor.done();
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, false, operation);
			
			// Refresh project
			project.refreshLocal(
					IProject.DEPTH_ZERO, 
					new NullProgressMonitor()
			);
			
			// Refresh decorator
			CcExportDecorator.getDecorator().refresh(new IResource[] {project});
			
		} catch (InvocationTargetException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
		return null;
	}
}
