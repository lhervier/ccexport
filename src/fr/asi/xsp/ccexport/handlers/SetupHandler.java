package fr.asi.xsp.ccexport.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

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
	 * @see fr.asi.xsp.ccexport.handlers.AbstractExportCcHandler#execute(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(final IProject project, IProgressMonitor monitor) throws CoreException, InterruptedException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		// Défini les propriétés
		project.setPersistentProperty(Constants.PROP_DEST_PROJECT_NAME, "fr.asi.xsp.test.library");
		project.setPersistentProperty(Constants.PROP_SOURCE_FOLDER, "src");
		project.setPersistentProperty(Constants.PROP_CLASSES_PACKAGE, "fr.asi.xsp.test.composants.xsp");
		project.setPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE, "fr.asi.xsp.test.composants.config");
		project.setPersistentProperty(Constants.PROP_XSPCONFIG_FILE, "xsp-config.list");
		
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
		
		// Rafraîchit le projet
		project.refreshLocal(
				IProject.DEPTH_INFINITE, 
				progress.newChild(25)
		);
	}
}
