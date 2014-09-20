package fr.asi.xsp.ccexport.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.ExtensionVisitor;
import fr.asi.xsp.ccexport.util.PropUtils;
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
	 * @param monitor le moniteur
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		System.out.println("Lancement d'une synchronisation totale");
		
		// Lance une initialisation
		Utils.initializeLink(this.project, new NullProgressMonitor());
		
		// Une Map qu'on va remplir avec les noms des CC du NSF.
		// Elle nous permettra ensuite de détecter ceux qu'il faut supprimer dans la destination
		final Set<String> ccs = new HashSet<String>();
		
		IFolder ccFolder = this.project.getFolder(Constants.CC_FOLDER_PATH);
		ccFolder.accept(new ExtensionVisitor("xsp-config") {
			@Override
			public void visit(IFile file) throws CoreException {
				// Le nom du custom control
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				ccs.add(cc);
				
				// Exporte le xsp-config
				new ExportXspConfigAction(SyncAction.this.project).execute(
						file, 
						new NullProgressMonitor()
				);
				
				// Exporte le fichie rJava
				IFile javaFile = SyncAction.this.project.getFile(
						Constants.JAVA_FOLDER_PATH
								.append(Constants.JAVA_PACKAGE)
								.append(Utils.normalizeMaj(cc) + ".java")
				);
				new ExportJavaAction(SyncAction.this.project).execute(
						javaFile, 
						new NullProgressMonitor()
				);
			}
		});
		
		// Supprime ceux qui n'existent plus
		IPath xspConfigPath = PropUtils
				.getProp_sourceFolder(this.project)
				.append(PropUtils.getProp_xspConfigPath(this.project));
		IFolder xspConfigFolder = this.destProject.getFolder(xspConfigPath);
		xspConfigFolder.accept(new ExtensionVisitor("xsp-config") {
			@Override
			public void visit(IFile file) throws CoreException {
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				if( ccs.contains(cc) )
					return;
				
				// Supprime le xsp-config
				new RemoveXspConfigAction(SyncAction.this.project).execute(
						file, 
						new NullProgressMonitor()
				);
				
				// Supprime le fichier java
				IFile javaFile = SyncAction.this.destProject.getFile(
						PropUtils.getProp_sourceFolder(SyncAction.this.project)
								.append(PropUtils.getProp_classesPath(SyncAction.this.project))
								.append(cc + ".java")
				);
				new RemoveJavaAction(SyncAction.this.project).execute(
						javaFile, 
						new NullProgressMonitor()
				);
			}
		});
		
		// Met à jour le xsp-config.list
		GenerateXspConfigListAction generateAction = new GenerateXspConfigListAction(this.project);
		generateAction.execute(new NullProgressMonitor());
	}
}
