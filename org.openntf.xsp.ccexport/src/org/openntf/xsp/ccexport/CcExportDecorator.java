package org.openntf.xsp.ccexport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.Utils;


/**
 * Décorateur pour adapter le nom du projet source
 * @author Lionel HERVIER
 */
public class CcExportDecorator extends LabelProvider implements ILabelDecorator {

	/**
	 * Retourne notre décorateur
	 */
	public static CcExportDecorator getDecorator() {
		IDecoratorManager dm = PlatformUI.getWorkbench().getDecoratorManager();
		return (CcExportDecorator) dm.getLabelDecorator(Constants.DECORATOR_ID);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	@Override
	public String decorateText(String title, Object element) {
		try {
			// On ne re-décore que le titre du projet
			if( !(element instanceof IProject) )
				return title;
			IProject project = (IProject) element;
			
			// On ne re-décore que les projets ouverts
			if( !project.isOpen() )
				return title;
			
			// On ne re-décore que les projets qui sont liés à notre builder
			if( !Utils.isUsingExportCc(project) )
				return title;
			
			return title + " [Exporting Cc into " + project.getPersistentProperty(Constants.PROP_DEST_PROJECT_NAME) + "]";
		} catch(CoreException e) {
			ConsoleUtils.error(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	@Override
	public Image decorateImage(Image image, Object element) {
		return image;
	}
	
	/**
	 * Pour rafraîchir un ensemble de ressources
	 * utilisant notre décorateur
	 * @param res les ressources
	 */
	public void refresh(final IResource[] res) {
		final CcExportDecorator decorator = getDecorator();
		if( decorator == null )
			return;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				CcExportDecorator.this.fireLabelProviderChanged(
						new LabelProviderChangedEvent( 
								decorator,
								res
						)	
				); 
			}
		});
	}
}
