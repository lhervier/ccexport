package fr.asi.xsp.ccexport.handlers;

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

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

import fr.asi.xsp.ccexport.CcExportDecorator;
import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.actions.SyncAction;
import fr.asi.xsp.ccexport.util.IProjectUtils;
import fr.asi.xsp.ccexport.util.Utils;
import fr.asi.xsp.ccexport.wizard.SetupWizard;

/**
 * Handler pour associer le NSF à un projet de library
 * @author Lionel HERVIER
 */
public class SetupHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Il nous faut une sélection sur un fichier/dossier
		ISelection se = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(se instanceof StructuredSelection))
			return null;
		
		// Récupère le projet
		StructuredSelection sse = (StructuredSelection) se;
		@SuppressWarnings("unchecked")
		List selList = sse.toList();
		IProject prj = null;
		for (Object o : selList) {
			if ((o instanceof IProjectNature)) {
				IProjectNature nature = (IProjectNature) o;
				prj = nature.getProject();
				break;
			}
		}
		
		// On ne fonctionne que sur un projet de type Domino
		if (!DominoResourcesPlugin.isDominoDesignerProject(prj))
			return null;
		
		// Exécute le wizard
		final SetupWizard wizard = new SetupWizard(prj);
		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		if( wizardDialog.open() != Window.OK )
			return null;
		
		// Associe le projet
		final IProject project = prj;
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				SubMonitor progress = SubMonitor.convert(monitor, 100);
				try {
					// Défini les propriétés
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
					
					// Ajoute le builder au projet
					IProjectUtils.addBuilderToProject(
							project, 
							Constants.BUILDER_ID, 
							progress.newChild(25)
					);
					
					// Initialise le projet de destination
					if( !Utils.initializeLink(project, progress.newChild(25) ) )
						return;
					
					// Force une synchro
					SyncAction action = new SyncAction(project);
					action.execute(progress.newChild(25));
				} catch(CoreException e) {
					throw new RuntimeException(e);
				} finally {
					if( monitor != null ) monitor.done();
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, false, operation);
			
			// Rafraîchit le projet
			project.refreshLocal(
					IProject.DEPTH_ZERO, 
					new NullProgressMonitor()
			);
			
			// Rafraîchit le décorator
			CcExportDecorator.getDecorator().refresh(new IResource[] {project});
			
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
