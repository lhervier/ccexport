package org.openntf.xsp.ccexport.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Visitor to walk through files with a given extension only
 * @author Lionel HERVIER
 */
public abstract class ExtensionVisitor implements IResourceVisitor {

	/**
	 * The files extension
	 */
	private String ext;
	
	/**
	 * Constructor
	 * @param ext the extension
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
	 * Visit a file that have the right extension
	 * @param file the file
	 * @throws CoreException in cas of trouble
	 */
	public abstract void visit(IFile file) throws CoreException;
}
