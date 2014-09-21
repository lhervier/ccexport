package fr.asi.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.asi.xsp.ccexport.util.PropUtils;

/**
 * Supprime la version compilée d'un custom control
 * @author Lionel HERVIER
 */
public class RemoveJavaAction extends BaseResourceAction {

	/**
	 * Constructeur
	 * @param srcProject
	 */
	public RemoveJavaAction(IProject srcProject) {
		super(srcProject);
	}

	/**
	 * @see fr.asi.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		String classFile = file.getName();
		try {
			// Le chemin vers le fichier .java
			IPath javaPath = PropUtils
					.getProp_sourceFolder(this.srcProject)
					.append(PropUtils.getProp_classesPath(this.srcProject))
					.append(classFile);
			IFile java = this.destProject.getFile(javaPath);
			if( java.exists() )
				java.delete(true, monitor);
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
