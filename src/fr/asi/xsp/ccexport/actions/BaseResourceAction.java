package fr.asi.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.asi.xsp.ccexport.util.PropUtils;

/**
 * Action pour exporter un custom control compilé (en fichier java).
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
	 * Exécute une action
	 * @param file le fichier à exporter (relatif au projet source)
	 * @param monitor le moniteur
	 */
	public abstract void execute(IFile file, IProgressMonitor monitor);
}
