package fr.asi.xsp.ccexport.wizard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.asi.xsp.ccexport.util.IProjectUtils;

/**
 * Fourni les infos à l'arbre qui affiche la liste des projets
 * @author Lionel HERVIER
 */
public class LocalJavaProjectProvider implements ITreeContentProvider {

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object element) {
		// On ne s'intéresse qu'au workspace
		if ( !(element instanceof IWorkspace))
			return new Object[0];
		IWorkspace workspace = (IWorkspace) element;
		
		// Parcours les projets
		IProject[] allProjects = workspace.getRoot().getProjects();
		List<IProject> projects = new ArrayList<IProject>();
		for( int i = 0; i < allProjects.length; i++ ) {
			IProject p = allProjects[i];
			
			// On ne retient que les prjets ouverts
			if( !p.isOpen() )
				continue;
			
			// On ne retient que les projets locaux
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
