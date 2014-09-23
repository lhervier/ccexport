package org.openntf.xsp.ccexport.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.openntf.xsp.ccexport.Constants;
import org.openntf.xsp.ccexport.util.ExtensionVisitor;
import org.openntf.xsp.ccexport.util.PropUtils;
import org.openntf.xsp.ccexport.util.Utils;


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
	 * Le projet de destination
	 */
	private IProject destProject;
	
	/**
	 * Constructeur
	 * @param project le projet
	 */
	public SyncAction(IProject project) {
		this.project = project;
		this.destProject = PropUtils.getProp_destProject(project);
	}
	
	/**
	 * Exécute la synchro
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		// Lance une initialisation
		Utils.initializeLink(
				this.project, 
				progress.newChild(25)
		);
		
		// Une Map qu'on va remplir avec les noms des CC du NSF.
		// Elle nous permettra ensuite de détecter ceux qu'il faut supprimer dans la destination
		final Set<String> ccs = new HashSet<String>();
		
		IFolder ccFolder = this.project.getFolder(Constants.CC_FOLDER_PATH);
		final SubMonitor subProgress1 = SubMonitor.convert(progress.newChild(25));
		ccFolder.accept(new ExtensionVisitor("xsp-config") {
			@Override
			public void visit(IFile file) throws CoreException {
				subProgress1.setWorkRemaining(10000);
				
				// On n'exporte que ceux qui commencent par le prefixe
				if( !file.getName().startsWith(PropUtils.getProp_ccPrefix(SyncAction.this.project)) )
					return;
				
				// Le nom du custom control
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				ccs.add(cc);
				
				// Exporte le xsp-config
				new ExportXspConfigAction(SyncAction.this.project).execute(
						file, 
						subProgress1.newChild(1)
				);
				
				// Exporte le fichier Java
				IFile javaFile = SyncAction.this.project.getFile(
						Constants.JAVA_FOLDER_PATH
								.append(Constants.JAVA_PACKAGE)
								.append(Utils.normalizeMaj(cc) + ".java")
				);
				new ExportJavaAction(SyncAction.this.project).execute(
						javaFile, 
						subProgress1.newChild(1)
				);
			}
		});
		progress.setWorkRemaining(50);
		
		// Supprime ceux qui n'existent plus
		IPath xspConfigPath = PropUtils
				.getProp_sourceFolderPath(this.project)
				.append(PropUtils.getProp_xspConfigPath(this.project));
		IFolder xspConfigFolder = this.destProject.getFolder(xspConfigPath);
		final SubMonitor subProgress2 = SubMonitor.convert(progress.newChild(25));
		xspConfigFolder.accept(new ExtensionVisitor("xsp-config") {
			@Override
			public void visit(IFile file) throws CoreException {
				subProgress2.setWorkRemaining(10000);
				
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				if( ccs.contains(cc) )
					return;
				
				// Supprime le xsp-config
				new RemoveXspConfigAction(SyncAction.this.project).execute(
						file, 
						subProgress2.newChild(1)
				);
				
				// Supprime le fichier java
				IFile javaFile = SyncAction.this.destProject.getFile(
						PropUtils.getProp_sourceFolderPath(SyncAction.this.project)
								.append(PropUtils.getProp_javaPath(SyncAction.this.project))
								.append(cc + ".java")
				);
				new RemoveJavaAction(SyncAction.this.project).execute(
						javaFile, 
						subProgress2.newChild(1)
				);
			}
		});
		progress.setWorkRemaining(25);
		
		// Met à jour le xsp-config.list
		GenerateXspConfigListAction generateAction = new GenerateXspConfigListAction(this.project);
		generateAction.execute(progress.newChild(25));
	}
}
