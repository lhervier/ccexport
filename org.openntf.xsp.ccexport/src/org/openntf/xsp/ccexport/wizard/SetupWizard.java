package org.openntf.xsp.ccexport.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.openntf.xsp.ccexport.util.Utils;

/**
 * Wizard to setup cc export
 * @author Lionel HERVIER
 */
public class SetupWizard extends Wizard {

	/**
	 * Page to select a destination project
	 */
	private SelectProjectPage selectProjectPage = new SelectProjectPage();
	
	/**
	 * Page for other options
	 */
	private OtherOptionsPage otherOptionsPage = new OtherOptionsPage();
	
	// ============================================================================
	
	/**
	 * The source project
	 */
	private IProject project;
	
	// ============================================================================
	
	/**
	 * Path to the destination project
	 */
	private String destProjectName = "";
	
	/**
	 * Destination project source folders
	 */
	private List<String> sourceFolders = new ArrayList<String>();
	
	/**
	 * Path to the source folder we will export into
	 */
	private String sourceFolder = "";
	
	/**
	 * Package name where we will export java files
	 */
	private String javaExportPackage = "org.openntf.xsp.ccexport.components";
	
	/**
	 * Package name where we will export xsp-config files
	 */
	private String xspConfigExportPackage = "org.openntf.xsp.ccexport.config";
	
	/**
	 * Name of the file we will export classpath path pf xsp-config files
	 */
	private String xspConfigList = "xsp-config.list";
	
	/**
	 * Prefix of the Custom control we will export
	 */
	private String ccPrefix = "";
	
	/**
	 * Constructor
	 * @param project the source project
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
