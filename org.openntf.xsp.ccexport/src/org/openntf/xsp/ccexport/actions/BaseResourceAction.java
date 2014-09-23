package org.openntf.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xsp.ccexport.util.PropUtils;


/**
 * Action pour exporter un custom control compil� (en fichier java).
 * @author Lionel HERVIER
 */
public abstract class BaseResourceAction {

	/**
	 * Le projet
	 */
	protected IProject srcProject;
	
	/**
	 * Le projet de destination
	 */
	protected IProject destProject;
	
	/**
	 * Constructeur
	 * @param srcProject le projet Java source (celui de la base NSF)
	 */
	public BaseResourceAction(IProject srcProject) {
		this.srcProject = srcProject;
		this.destProject = PropUtils.getProp_destProject(srcProject);
	}
	
	/**
	 * Ex�cute une action
	 * @param file le fichier � exporter (relatif au projet source)
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 */
	public abstract void execute(IFile file, IProgressMonitor monitor);
}