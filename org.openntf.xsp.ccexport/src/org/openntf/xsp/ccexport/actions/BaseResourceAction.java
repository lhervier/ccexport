package org.openntf.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xsp.ccexport.util.PropUtils;

/**
 * Action to export a resource
 * @author Lionel HERVIER
 */
public abstract class BaseResourceAction {

	/**
	 * The source project (the nsf project)
	 */
	protected IProject srcProject;
	
	/**
	 * The destination project (plug-in project with the XPage Library inside)
	 */
	protected IProject destProject;
	
	/**
	 * Constructor
	 * @param srcProject
	 */
	public BaseResourceAction(IProject srcProject) {
		this.srcProject = srcProject;
		this.destProject = PropUtils.getProp_destProject(srcProject);
	}
	
	/**
	 * Run the action
	 * @param file the file to export (related to source project)
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 */
	public abstract void execute(IFile file, IProgressMonitor monitor);
}
