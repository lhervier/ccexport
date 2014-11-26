package org.openntf.xsp.ccexport.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.openntf.xsp.ccexport.util.ConsoleUtils;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

public abstract class AbstractExportCcHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// We need a selection on a file or a folder
		ISelection se = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(se instanceof StructuredSelection))
			return null;
		
		// Get a hand on the project
		StructuredSelection sse = (StructuredSelection) se;
		@SuppressWarnings("unchecked")
		List selList = sse.toList();
		IProject prj = null;
		for( Object o : selList ) {
			if( o instanceof IProjectNature ) {
				IProjectNature nature = (IProjectNature) o;
				prj = nature.getProject();
				break;
			}
			if( o instanceof IProject ) {
				prj = (IProject) o;
				break;
			}
		}
		
		// Only working on Domino projects
		if (!DominoResourcesPlugin.isDominoDesignerProject(prj))
			return null;
		
		// Run the handler
		final IProject project = prj;
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			/**
			 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				try {
					AbstractExportCcHandler.this.execute(project, monitor);
				} finally {
					if( monitor != null ) monitor.done();
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, false, operation);
		} catch (InvocationTargetException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
		return null;
	}
	
	/**
	 * Run the handler
	 * @param prj the current project
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * 		to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * 		reported and that the operation cannot be cancelled.
	 * @throws CoreException en cas de pb
	 * @throws InterruptedException si le moniteur est interrompu
	 */
	public abstract void execute(final IProject project, IProgressMonitor monitor) throws CoreException, InterruptedException;
}
