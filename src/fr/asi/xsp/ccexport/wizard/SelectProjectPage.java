package fr.asi.xsp.ccexport.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.asi.xsp.ccexport.util.Utils;

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
	 * La zone de texte pour saisir le nom du projet de destination
	 */
	private Text text;
	
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
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return false;
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
		layout.numColumns = 2;
		
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		
		this.container = new Composite(parent, SWT.NONE);
		this.container.setLayout(layout);
		
		new Label(this.container, SWT.NONE).setText("Destination project : ");
		this.text = new Text(this.container, SWT.BORDER | SWT.SINGLE);
		this.text.setText(this.getWizard().getDestProjectName());
		this.text.setLayoutData(layoutData);
		this.text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {}
			@Override
			public void keyReleased(KeyEvent event) {
				String text = SelectProjectPage.this.text.getText();
				SelectProjectPage.this.getWizard().setDestProjectName(text);
				SelectProjectPage.this.setPageComplete(true);
			}
		});
		
		this.setControl(this.container);
		this.setPageComplete(false);
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
