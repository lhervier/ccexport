package fr.asi.xsp.ccexport.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

import fr.asi.xsp.ccexport.Utils;

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
		
		try {
			// Supprime les propriétés
			prj.setPersistentProperty(Utils.PROP_PROJECT_NAME, null);
			prj.setPersistentProperty(Utils.PROP_SOURCE_FOLDER, null);
			prj.setPersistentProperty(Utils.PROP_CLASSES_PACKAGE, null);
			prj.setPersistentProperty(Utils.PROP_XSPCONFIG_PACKAGE, null);
			
			// Ajoute le builder au projet
			Utils.removeBuilderFromProject(prj, "fr.asi.xsp.ccexport.builder");
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

}
