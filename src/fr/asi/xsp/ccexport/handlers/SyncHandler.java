package fr.asi.xsp.ccexport.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import fr.asi.xsp.ccexport.actions.SyncAction;

/**
 * Handler chargé de tout synchroniser 
 * @author Lionel HERVIER
 */
public class SyncHandler extends AbstractExportCcHandler {
	
	/**
	 * @see fr.asi.xsp.ccexport.handlers.AbstractExportCcHandler#execute(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void execute(final IProject project) {
		try {
			SyncAction action = new SyncAction(project);
			action.execute(new NullProgressMonitor());
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
	}

}
