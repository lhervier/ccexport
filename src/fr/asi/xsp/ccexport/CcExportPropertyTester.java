package fr.asi.xsp.ccexport;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

/**
 * Testeur de propriétés pour activer/masquer les menus
 * @author Lionel HERVIER
 */
public class CcExportPropertyTester extends PropertyTester {

	/**
	 * Pour savoir si on est sur un projet Domino
	 */
	public final static String PROP_ISDOMINOPROJECT = "isDominoProject";
	
	/**
	 * Pour savoir si la synchro est en place
	 */
	public final static String PROP_ISSETUP = "isSetup";
	
	/**
	 * Pour savoir si la synchro n'est PAS en place
	 */
	public final static String PROP_ISUNSETUP = "isUnSetup";
	
	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (property == null) {
			return false;
		}
		if( !(receiver instanceof IStructuredSelection) ) {
			return false;
		}
		
		IStructuredSelection selection = (IStructuredSelection) receiver;
		if( selection.size() != 1 ) {
			return false;
		}
		
		Object obj = selection.getFirstElement();
		if ( !(obj instanceof IAdaptable)) {
			return false;
		}
		IAdaptable adaptable = (IAdaptable) obj;
		
		IProject prj = (IProject) adaptable.getAdapter(IProject.class);
		if( prj == null || !prj.isOpen() ) {
			return false;
		}
		
		if( PROP_ISDOMINOPROJECT.equals(property) ) {
			boolean ret = DominoResourcesPlugin.isDominoDesignerProject(prj);
			return ret;
		} else if( PROP_ISSETUP.equals(property) ) {
			boolean ret = null != Constants.getProp_projectName(prj);
			return ret;
		} else if( PROP_ISUNSETUP.equals(property) ) {
			boolean ret = null == Constants.getProp_projectName(prj);
			return ret;
		} else {
			return false;
		}
	}
}
