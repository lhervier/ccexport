package fr.asi.xsp.ccexport.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

import fr.asi.xsp.ccexport.Constants;
import fr.asi.xsp.ccexport.util.IProjectUtils;

/**
 * Handler pour désassocier le NSF à un projet de library
 * @author Lionel HERVIER
 */
public class UnsetupHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection se = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(se instanceof StructuredSelection))
			return null;
		
		StructuredSelection sse = (StructuredSelection) se;
		@SuppressWarnings("unchecked")
		List selList = sse.toList();
		IProject prj = null;
		for (Object o : selList) {
			if ((o instanceof IProjectNature)) {
				IProjectNature nature = (IProjectNature) o;
				prj = nature.getProject();
				break;
			}
		}
		if (!DominoResourcesPlugin.isDominoDesignerProject(prj))
			return null;
		
		final IProject project = prj;
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			/**
			 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected void execute(IProgressMonitor arg0) throws CoreException, InvocationTargetException, InterruptedException {
				// Supprime les propriétés
				project.setPersistentProperty(Constants.PROP_PROJECT_NAME, null);
				project.setPersistentProperty(Constants.PROP_SOURCE_FOLDER, null);
				project.setPersistentProperty(Constants.PROP_CLASSES_PACKAGE, null);
				project.setPersistentProperty(Constants.PROP_XSPCONFIG_PACKAGE, null);
				project.setPersistentProperty(Constants.PROP_XSPCONFIG_FILE, null);
				
				// Ajoute le builder au projet
				IProjectUtils.removeBuilderFromProject(project, Constants.BUILDER_ID);
				
				// Rafraîchit le projet
				project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, false, operation);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
