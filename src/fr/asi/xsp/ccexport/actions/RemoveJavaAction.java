package fr.asi.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

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
	 * Supprime un fichier java
	 * @param classFile le nom du fichier java à supprimer
	 * @param monitor le moniteur
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		String classFile = file.getName();
		System.out.println("Supprime " + classFile);
		try {
			// Le chemin vers le fichier .java
			IPath javaPath = PropUtils
					.getProp_sourceFolder(this.srcProject)
					.append(PropUtils.getProp_classesPath(this.srcProject))
					.append(classFile);
			IFile java = this.destProject.getFile(javaPath);
			if( java.exists() )
				java.delete(true, new NullProgressMonitor());
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
