package fr.asi.xsp.ccexport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public abstract class AbstractAction {

	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers les custom controls
	 */
	public final static IPath JAVA_FOLDER_PACKAGE = new Path("xsp");
	
	// =================================================================================================
	
	/**
	 * Le projet Java source (la base nsf)
	 */
	private IProject srcProject;
	
	/**
	 * Le projet Java de destination
	 */
	private IProject destProject;
	
	/**
	 * Le nom dossier contenant le code source dans le projet de destination
	 */
	private String sourcesFolder;
	
	/**
	 * Le nom du package dans lequel exporter la classe Java
	 */
	private String javaPkg;
	
	/**
	 * Le nom du package dans lequel exporter les fichiers xsp-config
	 */
	private String xspConfigPkg;
	
	/**
	 * Constructeur
	 * @param srcProject le projet Java source (celui de la base NSF)
	 * @param destProject le projet Java de destination (la Library)
	 * @param sourcesFolder le nom dossier contenant le code source dans le projet de destination 
	 * @param javaPkg le nom du package dans lequel exporter la classe Java
	 * @param xspConfigPkg le nom du package dans lequel exporter les fichiers xsp-config
	 */
	public AbstractAction(
			IProject srcProject, 
			IProject destProject, 
			String sourcesFolder, 
			String javaPkg, 
			String xspConfigPkg) {
		this.srcProject = srcProject;
		this.destProject = destProject;
		this.sourcesFolder = sourcesFolder;
		this.javaPkg = javaPkg;
		this.xspConfigPkg = xspConfigPkg;
	}
	
	/**
	 * Exécute l'action
	 * @param cc le nom du custom control que lequel exécuter l'action
	 * @param monitor le moniteur
	 */
	public abstract void execute(String cc, IProgressMonitor monitor);

	// =======================================================================================
	
	/**
	 * @return the srcProject
	 */
	public IProject getSrcProject() {
		return srcProject;
	}

	/**
	 * @return the destProject
	 */
	public IProject getDestProject() {
		return destProject;
	}

	/**
	 * @return the sourcesFolder
	 */
	public String getSourcesFolder() {
		return sourcesFolder;
	}

	/**
	 * @return the javaPkg
	 */
	public String getJavaPkg() {
		return javaPkg;
	}

	/**
	 * @return the xspConfigPkg
	 */
	public String getXspConfigPkg() {
		return xspConfigPkg;
	}
	
}
