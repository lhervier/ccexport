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
import com.ibm.designer.domino.ide.resources.util.NsfUtil;

import fr.asi.xsp.ccexport.Utils;
import fr.asi.xsp.ccexport.actions.SyncAction;

/**
 * Handler pour associer le NSF � un projet de library
 * @author Lionel HERVIER
 */
public class SetupHandler extends AbstractHandler {

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
				// D�fini les propri�t�s
				project.setPersistentProperty(Utils.PROP_PROJECT_NAME, "fr.asi.xsp.test.library");
				project.setPersistentProperty(Utils.PROP_SOURCE_FOLDER, "src");
				project.setPersistentProperty(Utils.PROP_CLASSES_PACKAGE, "fr.asi.xsp.test.composants.xsp");
				project.setPersistentProperty(Utils.PROP_XSPCONFIG_PACKAGE, "fr.asi.xsp.test.composants.config");
				
				// Ajoute le builder au projet
				NsfUtil.addBuilderToProject(project, "fr.asi.xsp.ccexport.builder");
				
				// Force une synchro
				SyncAction action = new SyncAction(project);
				action.execute(new NullProgressMonitor());
				
				// Rafra�chit le projet
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
