package fr.asi.xsp.ccexport;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;

public class DominoProjectPropertyTester extends PropertyTester {

	public final static String PROPERTY_NAME = "isDominoProject";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (property == null)
			return false;
		if( !(receiver instanceof IStructuredSelection) )
			return false;
		if( !property.equals(PROPERTY_NAME) )
			return false;
		
		IStructuredSelection selection = (IStructuredSelection) receiver;
		if (selection.isEmpty())
			return false;
		
		Object obj = selection.getFirstElement();
		if ( !(obj instanceof IAdaptable))
			return false;
		IAdaptable adaptable = (IAdaptable) obj;
		
		IProject prj = (IProject) adaptable.getAdapter(IProject.class);
		return DominoResourcesPlugin.isDominoDesignerProject(prj);
	}
}
