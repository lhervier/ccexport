package fr.asi.xsp.ccexport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

public class CcExportBuilder extends IncrementalProjectBuilder {

	/**
	 * Le chemin vers les custom controls
	 */
	private final static IPath CC_FOLDER_PATH = new Path("CustomControls");
	
	/**
	 * Le chemin vers les custom controls
	 */
	private final static IPath XSP_FOLDER_PATH = new Path("Local/xsp");
	
	// ======================================================================
	
	/**
	 * Le nom du projet vers lequel exporter
	 */
	private String destProjectName;
	
	/**
	 * Le répertoire source dans lequel exporter
	 */
	private IPath srcFolderPath;
	
	/**
	 * Le package dans lequel exporter les classes
	 */
	private String javaPkgName;
	
	/**
	 * Le package dans lequel exporter les xsp-config
	 */
	private String xspConfigPkgName;
	
	// =======================================================================
	
	/**
	 * Le projet Java source
	 */
	private IJavaProject srcProject;
	
	/**
	 * Le projet Java de destination
	 */
	private IJavaProject destProject;
	
	/**
	 * Le package dans lequel exporter les classes Java
	 */
	private IPackageFragment javaPkg;
	
	/**
	 * Le package dans lequel exporter les xsp-config
	 */
	private IPackageFragment xspConfigPkg;
	
	/**
	 * La liste des noms de xsp-config qui ont déjà été traités
	 */
	private Set<String> processedXspConfig;
	
	/**
	 * La liste des noms de custom controls qui ont déjà été traitées
	 */
	private Set<String> processedCc;
	
	/**
	 * Constructeur
	 */
	public CcExportBuilder() {
		this.destProjectName = "fr.asi.xsp.test.library";
		this.srcFolderPath = new Path("src");
		this.javaPkgName = "fr.asi.xsp.test.composants.xsp";
		this.xspConfigPkgName = "fr.asi.xsp.test.composants.config";
	}
	
	/**
	 * Initialisation du builder
	 * @throws CoreException 
	 */
	public boolean initialize() throws CoreException {
		// Récupère le projet de destination sous la forme d'un projet Java
		IProject _destProject = Utils.getProjectFromName(this.destProjectName);
		if( !_destProject.exists() )
			return false;
		if( !Utils.isOfNature(_destProject, JavaCore.NATURE_ID) )
			return false;
		if( !_destProject.isOpen() )
			_destProject.open(new NullProgressMonitor());
		this.destProject = JavaCore.create(_destProject);
		
		// Créé les deux packages
		this.javaPkg = Utils.createPackage(destProject, this.srcFolderPath, this.javaPkgName, new NullProgressMonitor());
		if( this.javaPkg == null )
			return false;
		this.xspConfigPkg = Utils.createPackage(destProject, this.srcFolderPath, this.xspConfigPkgName, new NullProgressMonitor());
		if( this.xspConfigPkg == null )
			return false;
		
		// Récupère le projet courant sous la forme d'un projet Java
		this.srcProject = JavaCore.create(this.getProject());
		
		// Une Set qui contient les noms des CC déjà traités
		this.processedXspConfig = new HashSet<String>();
		this.processedCc = new HashSet<String>();
		
		return true;
	}
	
	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, final IProgressMonitor monitor) throws CoreException {
		// Initialise l'objet
		if( !this.initialize() )
			return null;
		
		// Parcours le delta
		IResourceDelta delta = this.getDelta(this.getProject());
		if( delta == null )
			return null;
		delta.accept(new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				// On n'accepte que les ajout/modif/suppression
				int kind = delta.getKind();
				if( kind != IResourceDelta.ADDED && kind != IResourceDelta.CHANGED && kind != IResourceDelta.REMOVED )
					return true;
				
				// On n'accepte que les modifs sur des fichiers
				IResource currResource = delta.getResource();
				if (currResource.getType() != IResource.FILE)
					return true;
				
				// Récupère la ressource a builder
				IFile file = (IFile) currResource;
				IPath location = file.getProjectRelativePath();
				
				// ======================================================================================================
				// Exporte le xsp-config: 
				// Comme on reporte le contenu du xsp dans le xsp-config, on réagit au changement de l'un ou de l'autre
				// ======================================================================================================
				if( CcExportBuilder.CC_FOLDER_PATH.isPrefixOf(location) ) {
					
					String xspConfig = Utils.getFileNameWithoutExtension(location.lastSegment()) + ".xsp-config";
					if( CcExportBuilder.this.processedXspConfig.contains(xspConfig) )
						return true;
					CcExportBuilder.this.processedXspConfig.add(xspConfig);
					
					IPath xspConfigDest = CcExportBuilder.this.xspConfigPkg.getResource().getProjectRelativePath().append(xspConfig);
					
					// Ajout ou modification
					if( kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED ) {
						InputStream in = null;
						Reader reader = null;
						
						// On copie le xsp-config
						try {
							IFile xspConfigSrc = CcExportBuilder.this.srcProject.getProject().getFile(CC_FOLDER_PATH.append(xspConfig));
							IFile dest = CcExportBuilder.this.destProject.getProject().getFile(xspConfigDest);
							if( dest.exists() )
								dest.delete(true, new NullProgressMonitor());
							
							// Copie le xsp-config
							xspConfigSrc.copy(
									dest.getFullPath(), 
									true, 
									new NullProgressMonitor()
							);
							
							// Et on l'adapte
							in = dest.getContents();
							reader = new InputStreamReader(in, dest.getCharset());
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
									"<composite-file>/" + CcExportBuilder.this.javaPkgName.replace('.', '/') + "/$1</composite-file>"
							);
							InputStream updatedIn = new ByteArrayInputStream(s.getBytes(dest.getCharset()));
							dest.setContents(updatedIn, true, false, new NullProgressMonitor());
							
						} catch (CoreException e) {
							throw new RuntimeException(e);
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						} finally {
							Utils.closeQuietly(reader);
							Utils.closeQuietly(in);
						}
					
					// Suppression
					} else if( kind == IResourceDelta.REMOVED ) {
						try {
							IFile dest = CcExportBuilder.this.destProject.getProject().getFile(xspConfigDest);
							if( dest.exists() )
								dest.delete(true, new NullProgressMonitor());
						} catch (CoreException e) {
							throw new RuntimeException(e);
						}
					}
				
				// Exporte une classe Java:
				// On ne réagit surtout pas à la modif du XSP car le fichier java peut ne pas exister
				// ===================================================================================
				} else if( CcExportBuilder.XSP_FOLDER_PATH.isPrefixOf(location) ) {
					
					String cc = Utils.getFileNameWithoutExtension(location.lastSegment()) + ".xsp";
					if( CcExportBuilder.this.processedCc.contains(cc) )
						return true;
					CcExportBuilder.this.processedCc.add(cc);
					
					// Ajout ou modification
					if( kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED ) {
						
						// Vérifie qu'il correspond à un custom control (et pas à une XPage)
						IFile min = CcExportBuilder.this.srcProject.getProject().getFile(CC_FOLDER_PATH.append(cc.substring(0, 1).toLowerCase() + cc.substring(1)));
						IFile maj = CcExportBuilder.this.srcProject.getProject().getFile(CC_FOLDER_PATH.append(cc.substring(0, 1).toUpperCase() + cc.substring(1)));
						if( !min.exists() && maj.exists() )
							return true;
						
						InputStream in = null;
						Reader reader = null;
						try {
							// Copie la classe
							IPath javaSrcPath = new Path("xsp").append(location.lastSegment());
							IJavaElement javaSrc = CcExportBuilder.this.srcProject.findElement(javaSrcPath);
							CcExportBuilder.this.srcProject.getJavaModel().copy(
									new IJavaElement[] {javaSrc}, 
									new IJavaElement[] {CcExportBuilder.this.javaPkg}, 
									null, 
									null, 
									true, 
									new NullProgressMonitor()
							);
							
							// Adapte la classe
							IPath javaDestPath = CcExportBuilder.this.javaPkg.getResource().getProjectRelativePath().append(location.lastSegment());
							IFile javaDest = CcExportBuilder.this.destProject.getProject().getFile(javaDestPath);
							
							in = javaDest.getContents();
							reader = new InputStreamReader(in, javaDest.getCharset());
							char[] buffer = new char[4 * 1024];
							StringBuffer sb = new StringBuffer();
							int read = reader.read(buffer);
							while( read != -1 ) {
								sb.append(buffer, 0, read);
								read = reader.read(buffer);
							}
							String s = sb.toString();
							s = s.replaceAll("\"/(.*).xsp\"", "\"/" + CcExportBuilder.this.javaPkgName.replace('.', '/') + "/$1\"");
							
							InputStream updatedIn = new ByteArrayInputStream(s.getBytes(javaDest.getCharset()));
							javaDest.setContents(updatedIn, true, false, new NullProgressMonitor());
							
						} catch (CoreException e) {
							throw new RuntimeException(e);
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						} finally {
							Utils.closeQuietly(reader);
							Utils.closeQuietly(in);
						}
					
					// Suppression
					} else if( kind == IResourceDelta.REMOVED ) {
						try {
							IPath javaPath = CcExportBuilder.this.srcFolderPath.append(CcExportBuilder.this.javaPkgName.replace('.', '/')).append(location.lastSegment());
							IFile javaFile = CcExportBuilder.this.destProject.getProject().getFile(javaPath);
							if( !javaFile.exists() )
								return true;
							javaFile.delete(true, new NullProgressMonitor());
						} catch (CoreException e) {
							throw new RuntimeException(e);
						}
					}
				}
				return true;
			}
		});
		
		// Sauver le workspace déclenche la re-compile des classes qu'on a ajoutées/modifiée/supprimées 
		ResourcesPlugin.getWorkspace().save(false, new NullProgressMonitor());
		return null;
	}

}
