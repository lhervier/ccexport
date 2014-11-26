package org.openntf.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.PropUtils;

/**
 * Action to remove a .xsp-config file.
 * @author Lionel HERVIER
 */
public class RemoveXspConfigAction extends BaseResourceAction {

	/**
	 * Constructor
	 * @param srcProject
	 */
	public RemoveXspConfigAction(IProject srcProject) {
		super(srcProject);
	}

	/**
	 * @see org.openntf.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		ConsoleUtils.info("Removing xsp-config: " + file.getFullPath());
		
		String xspConfig = file.getName();
		try {
			// The path to the .xsp-config file
			IPath xspConfigDest = PropUtils
					.getProp_sourceFolderPath(this.srcProject)
					.append(PropUtils.getProp_xspConfigPath(this.srcProject))
					.append(xspConfig);
			IFile xspConfigFile = this.destProject.getFile(xspConfigDest);
			
			// Remove the file
			if( xspConfigFile.exists() )
				xspConfigFile.delete(
						true, 
						monitor
				);
		} catch(CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
}
