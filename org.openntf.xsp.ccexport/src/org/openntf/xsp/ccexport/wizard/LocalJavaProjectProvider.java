package org.openntf.xsp.ccexport.wizard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.openntf.xsp.ccexport.util.IProjectUtils;

/**
 * Tree Content Provider to display the list of java projects
 * @author Lionel HERVIER
 */
public class LocalJavaProjectProvider implements ITreeContentProvider {

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object element) {
		// Only working with workspace elements
		if ( !(element instanceof IWorkspace))
			return new Object[0];
		IWorkspace workspace = (IWorkspace) element;
		
		// Walk through the projects
		IProject[] allProjects = workspace.getRoot().getProjects();
		List<IProject> projects = new ArrayList<IProject>();
		for( int i = 0; i < allProjects.length; i++ ) {
			IProject p = allProjects[i];
			
			// Only working with opened projects
			if( !p.isOpen() )
				continue;
			
			// Only working with local projects (To filter Domino Databases)
			URI projectURI = p.getLocationURI();
			if( !"file".equalsIgnoreCase(projectURI.getScheme()) )
				continue;
			
			if( !IProjectUtils.hasNature(p, JavaCore.NATURE_ID) )
				continue;
			
			projects.add(p);
		}
		
		return projects.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object element) {
		return this.getChildren(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if( !(element instanceof IResource) )
			return null;
		IResource res = (IResource) element;
		return res.getParent();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return this.getChildren(element).length > 0;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
