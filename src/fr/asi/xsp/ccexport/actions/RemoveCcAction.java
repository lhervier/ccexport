package fr.asi.xsp.ccexport.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;


public class RemoveCcAction extends AbstractAction {

	/**
	 * Constructeur
	 * @param srcProject
	 * @throws CoreException en cas de pb
	 */
	public RemoveCcAction(IProject srcProject) throws CoreException {
		super(srcProject);
	}

	/**
	 * Supprime un fichier .xsp-config
	 * @param xspConfig le fichier .xsp-config à supprimer
	 * @param monitor le moniteur
	 * @throws CoreException en cas de problème
	 */
	public void removeXspConfig(String xspConfig, IProgressMonitor monitor) throws CoreException {
		// Le chemin vers le .xsp-config dans le projet de destination
		IPath xspConfigDest = new Path(this.getSourcesFolder()).append(this.getXspConfigPkg().replace('.', '/')).append(xspConfig);
		IFile xspConfigFile = this.getDestProject().getFile(xspConfigDest);
		
		// Supprime fichier
		if( xspConfigFile.exists() )
			xspConfigFile.delete(true, new NullProgressMonitor());
		
		// TODO: Adapte le xsp-config.list
	}
	
	/**
	 * Supprime un fichier java
	 * @param classFile le nom du fichier java à supprimer
	 * @param monitor le moniteur
	 * @throws CoreException en cas de problème
	 */
	public void removeJava(String classFile, IProgressMonitor monitor) throws CoreException {
		// Le chemin vers le fichier .java
		IPath javaPath = new Path(this.getSourcesFolder()).append(this.getJavaPkg().replace('.', '/')).append(classFile);
		IFile java = this.getDestProject().getFile(javaPath);
		if( java.exists() )
			java.delete(true, new NullProgressMonitor());
	}
	
	/**
	 * @see fr.asi.xsp.ccexport.actions.AbstractAction#execute(String, IProgressMonitor)
	 */
	@Override
	public void execute(String cc, IProgressMonitor monitor) {
		String classFile = cc.substring(0, 1).toUpperCase() + cc.substring(1) + ".java";
		String xspConfig = cc + ".xsp-config";
		try {
			this.removeXspConfig(xspConfig, new NullProgressMonitor());
			this.removeJava(classFile, new NullProgressMonitor());
		} catch(CoreException e) {
			throw new RuntimeException(e);
		}
	}

}
