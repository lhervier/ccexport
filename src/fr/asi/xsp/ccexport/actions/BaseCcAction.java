package fr.asi.xsp.ccexport.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import fr.asi.xsp.ccexport.Constants;

public abstract class BaseCcAction {

	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath JAVA_FOLDER_PACKAGE = new Path("xsp");
	
	// =================================================================================================
	
	/**
	 * Le projet Java source (la base nsf)
	 */
	private IProject srcProject;
	
	/**
	 * Le projet Java de destination
	 */
	private IProject destProject;
	
	/**
	 * Constructeur
	 * @param srcProject le projet Java source (celui de la base NSF)
	 * @throws CoreException en cas de pb
	 */
	public BaseCcAction(IProject srcProject) throws CoreException {
		this.srcProject = srcProject;
		this.destProject = ResourcesPlugin.getWorkspace().getRoot().getProject(
				Constants.getProp_projectName(srcProject)
		);
	}
	
	/**
	 * Ex�cute l'action
	 * @param cc le nom du custom control que lequel ex�cuter l'action
	 * @param monitor le moniteur
	 */
	public abstract void execute(String cc, IProgressMonitor monitor);

	// =======================================================================================
	
	/**
	 * @return the srcProject
	 */
	public IProject getSrcProject() {
		return srcProject;
	}

	/**
	 * @return the destProject
	 */
	public IProject getDestProject() {
		return destProject;
	}
}
