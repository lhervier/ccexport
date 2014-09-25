package org.openntf.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.PropUtils;


/**
 * Supprime la version compil�e d'un custom control
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
	 * @see org.openntf.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		ConsoleUtils.info("Removing Java: " + file.getFullPath());
		
		String classFile = file.getName();
		try {
			// Le chemin vers le fichier .java
			IPath javaPath = PropUtils
					.getProp_sourceFolderPath(this.srcProject)
					.append(PropUtils.getProp_javaPath(this.srcProject))
					.append(classFile);
			IFile java = this.destProject.getFile(javaPath);
			if( java.exists() )
				java.delete(true, monitor);
		} catch(CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}
}
