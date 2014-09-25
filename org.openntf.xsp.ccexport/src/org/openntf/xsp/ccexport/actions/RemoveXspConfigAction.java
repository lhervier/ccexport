package org.openntf.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.PropUtils;


/**
 * Supprime le fichier xsp-config correspondant à un custom control
 * @author Lionel HERVIER
 */
public class RemoveXspConfigAction extends BaseResourceAction {

	/**
	 * Constructeur
	 * @param srcProject
	 */
	public RemoveXspConfigAction(IProject srcProject) {
		super(srcProject);
	}

	/**
	 * Supprime un fichier .xsp-config
	 * @param xspConfig le fichier .xsp-config à supprimer
	 * @param monitor le moniteur
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		ConsoleUtils.info("Removing xsp-config: " + file.getFullPath());
		
		String xspConfig = file.getName();
		try {
			// Le chemin vers le .xsp-config dans le projet de destination
			IPath xspConfigDest = PropUtils
					.getProp_sourceFolderPath(this.srcProject)
					.append(PropUtils.getProp_xspConfigPath(this.srcProject))
					.append(xspConfig);
			IFile xspConfigFile = this.destProject.getFile(xspConfigDest);
			
			// Supprime fichier
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
