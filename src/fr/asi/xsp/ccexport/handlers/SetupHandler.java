package fr.asi.xsp.ccexport.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.actions.SyncAction;
import fr.asi.xsp.ccexport.util.IProjectUtils;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Handler pour associer le NSF à un projet de library
 * @author Lionel HERVIER
 */
public class SetupHandler extends AbstractExportCcHandler {

	/**
	 * @see fr.asi.xsp.ccexport.handlers.AbstractExportCcHandler#execute(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void execute(final IProject project) {
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			/**
			 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				// Défini les propriétés
				project.setPersistentProperty(Constants.PROP_DEST_PROJECT_NAME, "fr.asi.xsp.test.library");
				project.setPersistentProperty(Constants.PROP_SOURCE_FOLDER, "src");
				project.setPersistentProperty(Constants.PROP_CLASSES_PACKAGE, "fr.asi.xsp.test.composants.xsp");
				project.setPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE, "fr.asi.xsp.test.composants.config");
				project.setPersistentProperty(Constants.PROP_XSPCONFIG_FILE, "xsp-config.list");
				
				// Ajoute le builder au projet
				IProjectUtils.addBuilderToProject(project, Constants.BUILDER_ID, new NullProgressMonitor());
				
				// Initialise le projet de destination
				if( !Utils.initializeLink(project, new NullProgressMonitor()) )
					return;
				
				// Force une synchro
				SyncAction action = new SyncAction(project);
				action.execute(new NullProgressMonitor());
				
				// Rafraîchit le projet
				project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, false, operation);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
