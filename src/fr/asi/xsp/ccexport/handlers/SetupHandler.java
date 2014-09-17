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
import com.ibm.designer.domino.ide.resources.util.NsfUtil;

import fr.asi.xsp.ccexport.Utils;

/**
 * Handler pour associer le NSF à un projet de library
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
		
		try {
			// Défini les propriétés
			prj.setPersistentProperty(Utils.PROP_PROJECT_NAME, "fr.asi.xsp.test.library");
			prj.setPersistentProperty(Utils.PROP_SOURCE_FOLDER, "src");
			prj.setPersistentProperty(Utils.PROP_CLASSES_PACKAGE, "fr.asi.xsp.test.composants.xsp");
			prj.setPersistentProperty(Utils.PROP_XSPCONFIG_PACKAGE, "fr.asi.xsp.test.composants.config");
			
			// Ajoute le builder au projet
			NsfUtil.addBuilderToProject(prj, "fr.asi.xsp.ccexport.builder");
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

}
