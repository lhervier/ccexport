package org.openntf.xsp.ccexport.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xsp.ccexport.actions.SyncAction;
import org.openntf.xsp.ccexport.util.ConsoleUtils;

/**
 * Handler to synchronize everything 
 * @author Lionel HERVIER
 */
public class SyncHandler extends AbstractExportCcHandler {
	
	/**
	 * @see org.openntf.xsp.ccexport.handlers.AbstractExportCcHandler#execute(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(final IProject project, IProgressMonitor monitor) {
		ConsoleUtils.info("Launching full Cc Export");
		try {
			SyncAction action = new SyncAction(project);
			action.execute(monitor);
		} catch(CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}

}
