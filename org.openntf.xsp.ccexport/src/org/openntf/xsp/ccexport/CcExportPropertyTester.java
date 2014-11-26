package org.openntf.xsp.ccexport;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.openntf.xsp.ccexport.util.PropUtils;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

/**
 * Property tester to activate/hide the menus
 * @author Lionel HERVIER
 */
public class CcExportPropertyTester extends PropertyTester {

	/**
	 * Project is a Domino Project
	 */
	public final static String PROP_ISDOMINOPROJECT = "isDominoProject";
	
	/**
	 * Builder is running
	 */
	public final static String PROP_ISSETUP = "isSetup";
	
	/**
	 * Builder is NOT running
	 */
	public final static String PROP_ISUNSETUP = "isUnSetup";
	
	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (property == null)
			return false;
		if( !(receiver instanceof IStructuredSelection) )
			return false;
		
		IStructuredSelection selection = (IStructuredSelection) receiver;
		if( selection.size() != 1 )
			return false;
		
		Object obj = selection.getFirstElement();
		if ( !(obj instanceof IAdaptable))
			return false;
		IAdaptable adaptable = (IAdaptable) obj;
		
		IProject prj = (IProject) adaptable.getAdapter(IProject.class);
		if( prj == null || !prj.isOpen() )
			return false;
		
		if( PROP_ISDOMINOPROJECT.equals(property) )
			return DominoResourcesPlugin.isDominoDesignerProject(prj);
		else if( PROP_ISSETUP.equals(property) )
			return null != PropUtils.getProp_destProjectName(prj);
		else if( PROP_ISUNSETUP.equals(property) )
			return null == PropUtils.getProp_destProjectName(prj);
		else
			return false;
	}
}
