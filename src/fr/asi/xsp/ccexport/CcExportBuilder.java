package fr.asi.xsp.ccexport;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CcExportBuilder extends IncrementalProjectBuilder {

	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int arg0, Map arg1, IProgressMonitor arg2)
			throws CoreException {
		IResourceDelta delta = this.getDelta(this.getProject());
		if( delta == null )
			return null;
		System.out.println("----- building -----");
		delta.accept(new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				int kind = delta.getKind();
				
				if( kind != IResourceDelta.ADDED && kind != IResourceDelta.CHANGED && kind != IResourceDelta.REMOVED )
					return true;
				
				IResource currResource = delta.getResource();
				if (currResource.getType() != IResource.FILE)
					return true;
				
				if( kind == IResourceDelta.ADDED )
					System.out.print("Ajout ");
				else if( kind == IResourceDelta.CHANGED )
					System.out.print("Modification ");
				else if( kind == IResourceDelta.REMOVED )
					System.out.print("Suppression ");
				
				IFile file = (IFile) currResource;
				System.out.println(file.getFullPath());
				return true;
			}
		});
		System.out.println("----- fin building -----");
		return null;
	}

}
