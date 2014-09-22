package fr.asi.xsp.ccexport.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import fr.asi.xsp.ccexport.util.PropUtils;
import fr.asi.xsp.ccexport.util.Utils;

/**
 * Wizard pour configurer l'export
 * @author Lionel HERVIER
 */
public class SetupWizard extends Wizard {

	/**
	 * La page pour sélectionner le projet de destination
	 */
	private SelectProjectPage selectProjectPage = new SelectProjectPage();
	
	/**
	 * La page pour définir les autres options
	 */
	private OtherOptionsPage otherOptionsPage = new OtherOptionsPage();
	
	// ============================================================================
	
	/**
	 * Le projet source
	 */
	private IProject project;
	
	// ============================================================================
	
	/**
	 * Le chemin vers le projet où exporter
	 */
	private String destProjectName = "";
	
	/**
	 * Le chemin vers le répertoire source où exporter
	 */
	private String sourceFolder = "src";
	
	/**
	 * Le package dans lequel exporter les fichiers java
	 */
	private String javaExportPackage = "org.openntf.xsp.mylibrary.components";
	
	/**
	 * Le package dans lequel exporter les fichiers xsp-config
	 */
	private String xspConfigExportPackage = "org.openntf.xsp.mylibrary.config";
	
	/**
	 * Le nom du fichier dans lequel exporter la liste des fichiers xsp-config
	 */
	private String xspConfigList = "xsp-config.list";
	
	/**
	 * Constructeur
	 * @param project le projet source
	 */
	public SetupWizard(IProject project) {
		this.project = project;
		
		String destProjectName = PropUtils.getProp_destProjectName(project);
		if( destProjectName != null )
			this.destProjectName = destProjectName;
		
		String sourceFolder = PropUtils.getProp_sourceFolder(project);
		if( sourceFolder != null )
			this.sourceFolder = sourceFolder;
		
		String javaPackage = PropUtils.getProp_javaPackage(project);
		if( javaPackage != null )
			this.javaExportPackage = javaPackage;
		
		String xspConfigPackage = PropUtils.getProp_xspConfigPackage(project);
		if( xspConfigPackage != null )
			this.xspConfigExportPackage = xspConfigPackage;
		
		String xspConfigList = PropUtils.getProp_xspConfigList(project);
		if( xspConfigList != null )
			this.xspConfigList = xspConfigList;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		this.addPage(this.selectProjectPage);
		this.addPage(this.otherOptionsPage);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		if( Utils.isEmpty(this.sourceFolder) )
			return false;
		if( Utils.isEmpty(this.javaExportPackage) )
			return false;
		if( Utils.isEmpty(this.xspConfigExportPackage) )
			return false;
		if( Utils.isEmpty(this.xspConfigList) )
			return false;
		return true;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return true;
	}
	
	/**
	 * Retourne le projet courant
	 * @return le projet courant
	 */
	public IProject getProject() {
		return this.project;
	}
	
	// ====================================================================================
	
	/**
	 * @return the destProjectName
	 */
	public String getDestProjectName() {
		return destProjectName;
	}

	/**
	 * @param destProjectName the destProjectName to set
	 */
	public void setDestProjectName(String destProjectName) {
		this.destProjectName = destProjectName;
	}

	/**
	 * @return the sourceFolder
	 */
	public String getSourceFolder() {
		return sourceFolder;
	}

	/**
	 * @param sourceFolder the sourceFolder to set
	 */
	public void setSourceFolder(String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	/**
	 * @return the javaExportPackage
	 */
	public String getJavaExportPackage() {
		return javaExportPackage;
	}

	/**
	 * @param javaExportPackage the javaExportPackage to set
	 */
	public void setJavaExportPackage(String javaExportPackage) {
		this.javaExportPackage = javaExportPackage;
	}

	/**
	 * @return the xspConfigExportPackage
	 */
	public String getXspConfigExportPackage() {
		return xspConfigExportPackage;
	}

	/**
	 * @param xspConfigExportPackage the xspConfigExportPackage to set
	 */
	public void setXspConfigExportPackage(String xspConfigExportPackage) {
		this.xspConfigExportPackage = xspConfigExportPackage;
	}

	/**
	 * @return the xspConfigList
	 */
	public String getXspConfigList() {
		return xspConfigList;
	}

	/**
	 * @param xspConfigList the xspConfigList to set
	 */
	public void setXspConfigList(String xspConfigList) {
		this.xspConfigList = xspConfigList;
	}
}
