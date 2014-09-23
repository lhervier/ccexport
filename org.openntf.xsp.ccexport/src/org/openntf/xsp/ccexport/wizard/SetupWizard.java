package org.openntf.xsp.ccexport.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.openntf.xsp.ccexport.util.Utils;


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
	 * La liste des répertoires source
	 */
	private List<String> sourceFolders = new ArrayList<String>();
	
	/**
	 * Le chemin vers le répertoire source où exporter
	 */
	private String sourceFolder = "";
	
	/**
	 * Le package dans lequel exporter les fichiers java
	 */
	private String javaExportPackage = "org.openntf.xsp.ccexport.components";
	
	/**
	 * Le package dans lequel exporter les fichiers xsp-config
	 */
	private String xspConfigExportPackage = "org.openntf.xsp.ccexport.config";
	
	/**
	 * Le nom du fichier dans lequel exporter la liste des fichiers xsp-config
	 */
	private String xspConfigList = "xsp-config.list";
	
	/**
	 * Le préfixe des custom control à exporter
	 */
	private String ccPrefix = "";
	
	/**
	 * Constructeur
	 * @param project le projet source
	 */
	public SetupWizard(IProject project) {
		this.project = project;
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
		if( Utils.isEmpty(this.destProjectName) )
			return false;
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

	/**
	 * @return the sourceFolders
	 */
	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	/**
	 * @param sourceFolders the sourceFolders to set
	 */
	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}

	/**
	 * @return the ccPrefix
	 */
	public String getCcPrefix() {
		return ccPrefix;
	}

	/**
	 * @param ccPrefix the ccPrefix to set
	 */
	public void setCcPrefix(String ccPrefix) {
		this.ccPrefix = ccPrefix;
	}
}
