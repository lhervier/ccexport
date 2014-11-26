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
 * Action to force synchronisation
 * @author Lionel HERVIER
 */
public class SyncAction {

	/**
	 * The source project
	 */
	private IProject project;
	
	/**
	 * The destination project
	 */
	private IProject destProject;
	
	/**
	 * Constructor
	 * @param project the projet
	 */
	public SyncAction(IProject project) {
		this.project = project;
		this.destProject = PropUtils.getProp_destProject(project);
	}
	
	/**
	 * Run the synchronisation
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		// Initialization
		Utils.initializeLink(
				this.project, 
				progress.newChild(25)
		);
		
		// Map that will be filled with the CC names.
		// Will be usefull to detect the removed ones.
		final Set<String> ccs = new HashSet<String>();
		
		IFolder ccFolder = this.project.getFolder(Constants.CC_FOLDER_PATH);
		final SubMonitor subProgress1 = SubMonitor.convert(progress.newChild(25));
		ccFolder.accept(new ExtensionVisitor("xsp-config") {
			@Override
			public void visit(IFile file) throws CoreException {
				subProgress1.setWorkRemaining(10000);
				
				// Only accepting the ones that starts with the right prefix.
				if( !file.getName().startsWith(PropUtils.getProp_ccPrefix(SyncAction.this.project)) )
					return;
				
				// Custom control name
				String cc = Utils.getFileNameWithoutExtension(file.getName());
				ccs.add(cc);
				
				// Export the .xsp-config file
				new ExportXspConfigAction(SyncAction.this.project).execute(
						file, 
						subProgress1.newChild(1)
				);
				
				// Export the .java file
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
		
		// Remove the remaining ones
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
				
				// Remove the .xsp-config file
				new RemoveXspConfigAction(SyncAction.this.project).execute(
						file, 
						subProgress2.newChild(1)
				);
				
				// Remove the .java file
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
		
		// Update the xsp-config.list file
		GenerateXspConfigListAction generateAction = new GenerateXspConfigListAction(this.project);
		generateAction.execute(progress.newChild(25));
	}
}
