package org.openntf.xsp.ccexport.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.openntf.xsp.ccexport.Constants;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.PropUtils;
import org.openntf.xsp.ccexport.util.Utils;

/**
 * Action to export an .xsp-config file.
 * @author Lionel HERVIER
 */
public class ExportXspConfigAction extends BaseResourceAction {

	/**
	 * Constructor
	 * @param srcProject The source project (NSF)
	 */
	public ExportXspConfigAction(IProject srcProject) {
		super(srcProject);
	}
	
	/**
	 * @see org.openntf.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		ConsoleUtils.info("Exporting xsp-config: " + file.getFullPath());
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		InputStream in = null;
		Reader reader = null;
		try {
			// The source .xsp-config file
			String xspConfig = file.getName();
			IPath srcXspConfigPath = Constants.CC_FOLDER_PATH.append(xspConfig);
			IFile srcXspConfig = this.srcProject.getFile(srcXspConfigPath);
			
			// Le destination .xsp-config file
			IPath destXspConfigPath = PropUtils.getProp_sourceFolderPath(this.srcProject)
					.append(PropUtils.getProp_xspConfigPath(this.srcProject))
					.append(xspConfig);
			IFile destXspConfig = this.destProject.getFile(destXspConfigPath);
			
			// Remove the file if it already exists into the destination project
			if( destXspConfig.exists() )
				destXspConfig.delete(true, progress.newChild(10));
			progress.setWorkRemaining(90);
			
			// Copy the source file into the destination
			srcXspConfig.copy(
					destXspConfig.getFullPath(), 
					true, 
					progress.newChild(40)
			);
			
			// Update the content of the file, so we are referencing the java class instead of the xsp file 
			in = destXspConfig.getContents();
			reader = new InputStreamReader(in, destXspConfig.getCharset());
			char[] buffer = new char[4 * 1024];
			StringBuffer sb = new StringBuffer();
			int read = reader.read(buffer);
			while( read != -1 ) {
				sb.append(buffer, 0, read);
				read = reader.read(buffer);
			}
			String s = sb.toString();
			
			s = s.replaceAll(
					"<composite-file>/(.*).xsp</composite-file>", 
					"<composite-file>/" + PropUtils.getProp_javaPath(this.srcProject) + "/$1</composite-file>"
			);
			InputStream updatedIn = new ByteArrayInputStream(s.getBytes(destXspConfig.getCharset()));
			destXspConfig.setContents(
					updatedIn, 
					true, 
					false, 
					progress.newChild(50)
			);
			
			// TODO: Update the .xsp-config file to include the content of the .xsp file as a design definition
			
		} catch (CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} finally {
			Utils.closeQuietly(reader);
			Utils.closeQuietly(in);
		}
	}
}
