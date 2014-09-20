package fr.asi.xsp.ccexport.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Une visiteur qui ne donne que les fichiers ayant une certaines extension
 * @author Lionel HERVIER
 */
public abstract class ExtensionVisitor implements IResourceVisitor {

	/**
	 * L'extension
	 */
	private String ext;
	
	/**
	 * Constructeur
	 * @param ext l'extension
	 */
	public ExtensionVisitor(String ext) {
		this.ext = ext;
	}
	
	/**
	 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
	 */
	@Override
	public final boolean visit(IResource resource) throws CoreException {
		if( resource.getType() != IResource.FILE )
			return true;
		IFile file = (IFile) resource;
		if( !this.ext.equals(file.getFileExtension()) )
			return true;
		
		this.visit(file);
		return false;
	}

	/**
	 * Viste un fichier qui a l'extension requise
	 * @param file le fichier
	 * @throws CoreException en cas de pb
	 */
	public abstract void visit(IFile file) throws CoreException;
}
