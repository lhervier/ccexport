package fr.asi.xsp.ccexport.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Action pour tout synchroniser
 * @author Lionel HERVIER
 */
public class SyncAction {

	/**
	 * Le projet source
	 */
	private IProject project;
	
	/**
	 * Constructeur
	 * @param project le projet
	 */
	public SyncAction(IProject project) {
		this.project = project;
	}
	
	/**
	 * Exécute la synchro
	 * @param monitor le moniteur
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		// Une Map qu'on va remplir avec les noms des CC du NSF.
		// Elle nous permettra ensuite de détecter ceux qu'il faut supprimer dans la destination
		final Set<String> ccs = new HashSet<String>();
		
		// Exporte les Custom Control qui existent
		final BaseCcAction exportAction = new ExportCcAction(this.project);
		IFolder ccFolder = this.project.getFolder(Constants.CC_FOLDER_PATH);
		ccFolder.accept(new IResourceVisitor() {
			@Override
			public boolean visit(IResource resource) throws CoreException {
				if( resource.getType() != IResource.FILE )
					return true;
				IFile file = (IFile) resource;
				
				if( !"xsp".equals(file.getFileExtension()) )
					return true;
				
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				ccs.add(cc);
				exportAction.execute(cc, new NullProgressMonitor());
				
				return true;
			}
		});
		
		// Supprime ceux qui n'existent plus
		final BaseCcAction removeAction = new RemoveCcAction(this.project);
		IPath xspConfigPath = new Path(Constants.getProp_sourceFolder(this.project))
				.append(Constants.getProp_xspConfigPackage(this.project).replace('.', '/'));
		IFolder xspConfigFolder = removeAction.getDestProject().getFolder(xspConfigPath);
		xspConfigFolder.accept(new IResourceVisitor() {
			@Override
			public boolean visit(IResource resource) throws CoreException {
				if( resource.getType() != IResource.FILE )
					return true;
				IFile file = (IFile) resource;
				
				if( !"xsp-config".equals(file.getFileExtension()) )
					return true;
				
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				if( !ccs.contains(cc) )
					removeAction.execute(cc, new NullProgressMonitor());
				
				return true;
			}
		});
	}
}
