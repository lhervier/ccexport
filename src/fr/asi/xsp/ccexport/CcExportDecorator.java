package fr.asi.xsp.ccexport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.asi.xsp.ccexport.util.Utils;

/**
 * Décorateur pour adapter le nom du projet source
 * @author Lionel HERVIER
 */
public class CcExportDecorator extends LabelProvider implements ILabelDecorator {

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
			
			return title + " [Exporting Cc into " + project.getPersistentProperty(Constants.PROP_DEST_PROJECT_NAME);
		} catch(CoreException e) {
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
}
