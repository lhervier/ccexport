package fr.asi.xsp.ccexport.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.IProjectUtils;

/**
 * Handler pour désassocier le NSF à un projet de library
 * @author Lionel HERVIER
 */
public class UnsetupHandler extends AbstractExportCcHandler {

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
			protected void execute(IProgressMonitor arg0) throws CoreException, InvocationTargetException, InterruptedException {
				// Supprime les propriétés
				project.setPersistentProperty(Constants.PROP_DEST_PROJECT_NAME, null);
				project.setPersistentProperty(Constants.PROP_SOURCE_FOLDER, null);
				project.setPersistentProperty(Constants.PROP_CLASSES_PACKAGE, null);
				project.setPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE, null);
				project.setPersistentProperty(Constants.PROP_XSPCONFIG_FILE, null);
				
				// Ajoute le builder au projet
				IProjectUtils.removeBuilderFromProject(project, Constants.BUILDER_ID);
				
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
