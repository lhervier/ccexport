package org.openntf.xsp.ccexport.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.openntf.xsp.ccexport.Constants;
import org.openntf.xsp.ccexport.util.BooleanHolder;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.ExtensionVisitor;
import org.openntf.xsp.ccexport.util.PropUtils;


/**
 * Action pour générer le fichier qui contient la liste 
 * des références aux xsp-config
 * @author Lionel HERVIER
 */
public class GenerateXspConfigListAction {

	/**
	 * Le projet source
	 */
	private IProject srcProject;
	
	/**
	 * Le projet de destination
	 */
	private IProject destProject;
	
	/**
	 * Constructeur
	 * @param srcProject
	 */
	public GenerateXspConfigListAction(IProject project) {
		this.srcProject = project;
		this.destProject = PropUtils.getProp_destProject(project);
	}
	
	/**
	 * Exécute l'action
	 * @throws CoreException en cas de pb
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		// Le dossier qui contient les xsp-config
		IFolder ccFolder = this.srcProject.getFolder(Constants.CC_FOLDER_PATH);
		
		// La liste
		final StringWriter list = new StringWriter();
		final BooleanHolder initialized = new BooleanHolder(false);
		
		// Parcours le dossier
		final SubMonitor subProgress = SubMonitor.convert(progress.newChild(30));
		ccFolder.accept(new ExtensionVisitor("xsp-config") {
			/**
			 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
			 */
			@Override
			public void visit(IFile file) throws CoreException {
				if( !file.getName().startsWith(PropUtils.getProp_ccPrefix(GenerateXspConfigListAction.this.srcProject)) )
					return;
				if( initialized.value )
					list.append("\n");
				else
					initialized.value = true;
				list.append("/" + PropUtils.getProp_xspConfigPath(GenerateXspConfigListAction.this.srcProject).append(file.getName()));
				subProgress.setTaskName("Extracting path of " + file.getName());
				subProgress.worked(1);
				subProgress.setWorkRemaining(10000);
			}
		});
		progress.setWorkRemaining(70);
		
		// Le fichier dans lequel écrire. Comme Utils.initialize a été appelé, on est sûr que le dossier existe. 
		IFile file = this.destProject.getFile(PropUtils.getProp_xspConfigList(this.srcProject));
		if( !file.exists() )
			file.create(
					new ByteArrayInputStream(new byte[0]), 
					true, 
					progress.newChild(10)
			);
		progress.setWorkRemaining(60);
		
		InputStream in;
		try {
			in = new ByteArrayInputStream(list.toString().getBytes(file.getCharset()));
		} catch (UnsupportedEncodingException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
		file.setContents(
				in, 
				true, 
				false, 
				progress.newChild(60)
		);
	}
}
