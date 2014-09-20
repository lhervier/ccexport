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
import org.eclipse.core.runtime.NullProgressMonitor;

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
	 * Exporte un Custom control
	 * @param xspConfig le fichier .xsp-config � exporter
	 * @param monitor le moniteur
	 */
	@Override
	public void execute(IFile file, IProgressMonitor monitor) {
		String xspConfig = file.getName();
		System.out.println("Exporte " + xspConfig);
		
		InputStream in = null;
		Reader reader = null;
		try {
			// Le xsp-config source
			IPath srcXspConfigPath = Constants.CC_FOLDER_PATH.append(xspConfig);
			IFile srcXspConfig = this.srcProject.getFile(srcXspConfigPath);
			
			// Le xsp-config de destination
			IPath destXspConfigPath = PropUtils.getProp_sourceFolder(this.srcProject)
					.append(PropUtils.getProp_xspConfigPath(this.srcProject))
					.append(xspConfig);
			IFile destXspConfig = this.destProject.getFile(destXspConfigPath);
			
			// Supprime fichier s'il existe dans la destination
			if( destXspConfig.exists() )
				destXspConfig.delete(true, new NullProgressMonitor());
			
			// Copie le xsp-config source dans la destination
			srcXspConfig.copy(
					destXspConfig.getFullPath(), 
					true, 
					new NullProgressMonitor()
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
					"<composite-file>/" + PropUtils.getProp_classesPath(this.srcProject) + "/$1</composite-file>"
			);
			InputStream updatedIn = new ByteArrayInputStream(s.getBytes(destXspConfig.getCharset()));
			destXspConfig.setContents(updatedIn, true, false, new NullProgressMonitor());
			
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
