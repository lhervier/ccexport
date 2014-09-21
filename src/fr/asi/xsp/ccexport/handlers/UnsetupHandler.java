package fr.asi.xsp.ccexport.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.IProjectUtils;

/**
 * Handler pour désassocier le NSF à un projet de library
 * @author Lionel HERVIER
 */
public class UnsetupHandler extends AbstractExportCcHandler {

	/**
	 * @see fr.asi.xsp.ccexport.handlers.AbstractExportCcHandler#execute(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(final IProject project, IProgressMonitor monitor) throws CoreException, InterruptedException {
		// Supprime les propriétés
		project.setPersistentProperty(Constants.PROP_DEST_PROJECT_NAME, null);
		project.setPersistentProperty(Constants.PROP_SOURCE_FOLDER, null);
		project.setPersistentProperty(Constants.PROP_CLASSES_PACKAGE, null);
		project.setPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE, null);
		project.setPersistentProperty(Constants.PROP_XSPCONFIG_FILE, null);
		
		// Ajoute le builder au projet
		IProjectUtils.removeBuilderFromProject(project, Constants.BUILDER_ID);
		
		// Rafraîchit le projet
		project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
	}
}
