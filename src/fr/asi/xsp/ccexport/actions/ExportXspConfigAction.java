package fr.asi.xsp.ccexport.actions;

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

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.PropUtils;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Action pour exporter un fichier xsp-config.
 * @author Lionel HERVIER
 */
public class ExportXspConfigAction extends BaseResourceAction {

	/**
	 * Constructeur
	 * @param srcProject le projet Java source (celui de la base NSF)
	 */
	public ExportXspConfigAction(IProject srcProject) {
		super(srcProject);
	}
	
	/**
	 * @see fr.asi.xsp.ccexport.actions.BaseResourceAction#execute(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		
		InputStream in = null;
		Reader reader = null;
		try {
			// Le xsp-config source
			String xspConfig = file.getName();
			IPath srcXspConfigPath = Constants.CC_FOLDER_PATH.append(xspConfig);
			IFile srcXspConfig = this.srcProject.getFile(srcXspConfigPath);
			
			// Le xsp-config de destination
			IPath destXspConfigPath = PropUtils.getProp_sourceFolderPath(this.srcProject)
					.append(PropUtils.getProp_xspConfigPath(this.srcProject))
					.append(xspConfig);
			IFile destXspConfig = this.destProject.getFile(destXspConfigPath);
			
			// Supprime fichier s'il existe dans la destination
			if( destXspConfig.exists() )
				destXspConfig.delete(true, progress.newChild(10));
			progress.setWorkRemaining(90);
			
			// Copie le xsp-config source dans la destination
			srcXspConfig.copy(
					destXspConfig.getFullPath(), 
					true, 
					progress.newChild(40)
			);
			
			// Adapte le xsp-config de destination
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
			
			// TODO: Adapte le fichier .xsp-config pour y inclure le contenu du .xsp en design definition
			
		} catch (CoreException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Utils.closeQuietly(reader);
			Utils.closeQuietly(in);
		}
	}
}
