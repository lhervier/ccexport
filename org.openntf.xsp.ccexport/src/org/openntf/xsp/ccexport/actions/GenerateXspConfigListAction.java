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
 * Action to generate the file that contains the classpath path to the .xsp-config files
 * This file is used by the XPages Library implementation.
 * @author Lionel HERVIER
 */
public class GenerateXspConfigListAction {

	/**
	 * The source project
	 */
	private IProject srcProject;
	
	/**
	 * The destination project
	 */
	private IProject destProject;
	
	/**
	 * Constructor
	 * @param srcProject
	 */
	public GenerateXspConfigListAction(IProject project) {
		this.srcProject = project;
		this.destProject = PropUtils.getProp_destProject(project);
	}
	
	/**
	 * Run the action
	 * @throws CoreException in cas of trouble
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		// The folder that contains the .xsp-config files
		IFolder ccFolder = this.srcProject.getFolder(Constants.CC_FOLDER_PATH);
		
		// Prepare the list
		final StringWriter list = new StringWriter();
		final BooleanHolder initialized = new BooleanHolder(false);
		
		// Walk through the folder
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
		
		// The file to write into. As Utils.initialize have been called, we are certain that the folder exists. 
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
