package fr.asi.xsp.ccexport.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

public abstract class AbstractExportCcHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Il nous faut une sélection sur un fichier/dossier
		ISelection se = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(se instanceof StructuredSelection))
			return null;
		
		// Récupère le projet
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
		
		// On ne fonctionne que sur un projet de type Domino
		if (!DominoResourcesPlugin.isDominoDesignerProject(prj))
			return null;
		
		// Exécute le handler
		this.execute(prj);
		return null;
	}
	
	/**
	 * Exécute le handler
	 * @param prj le projet courant
	 */
	public abstract void execute(final IProject project);
}
