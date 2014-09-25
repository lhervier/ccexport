package org.openntf.xsp.ccexport.wizard;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.openntf.xsp.ccexport.util.ConsoleUtils;
import org.openntf.xsp.ccexport.util.IJavaProjectUtils;
import org.openntf.xsp.ccexport.util.Utils;


/**
 * Page du wizard pour la sélection du projet cible
 * @author Lionel HERVIER
 */
public class SelectProjectPage extends WizardPage {

	/**
	 * Le control principal
	 */
	private Composite container;
	
	/**
	 * Le TreeView pour afficher les projets
	 * sur lesquels se brancher
	 */
	private TreeViewer tree;
	
	/**
	 * Constructeur
	 * @param wizard le wizard parent
	 */
	public SelectProjectPage() {
		super("Select project to export into");
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getWizard()
	 */
	@Override
	public SetupWizard getWizard() {
		return (SetupWizard) super.getWizard();
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return !Utils.isEmpty(this.getWizard().getDestProjectName());
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		this.container = new Composite(parent, SWT.NONE);
		this.container.setLayout(layout);
		this.container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		new Label(this.container, SWT.NONE).setText("Destination project : ");
		
		this.tree = new TreeViewer(this.container);
		this.tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.tree.setContentProvider(new LocalJavaProjectProvider());
		this.tree.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		this.tree.setComparator(new ViewerComparator());
		this.tree.setInput(ResourcesPlugin.getWorkspace());
		
		final SetupWizard wizard = this.getWizard();
		this.tree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IProject p = (IProject) selection.getFirstElement();
				if( p == null ) {
					wizard.setDestProjectName("");
					wizard.getSourceFolders().clear();
				} else {
					wizard.setDestProjectName(p.getName());
					try {
						IJavaProject javaProject = JavaCore.create(p);
						List<IPath> sourceFolders = IJavaProjectUtils.getSourceFolders(javaProject);
						wizard.getSourceFolders().clear();
						for( IPath path : sourceFolders ) {
							wizard.getSourceFolders().add(path.removeFirstSegments(1).toString());
						}
					} catch(CoreException e) {
						ConsoleUtils.error(e);
						throw new RuntimeException(e);
					}
				}
				wizard.setDestProjectName(p == null ? "" : p.getName());
				wizard.getContainer().updateButtons();
			}
		});
		
		this.setControl(this.container);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.container.dispose();
	}
}
