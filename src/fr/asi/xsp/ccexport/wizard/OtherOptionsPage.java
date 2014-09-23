package fr.asi.xsp.ccexport.wizard;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.asi.xsp.ccexport.util.Utils;

/**
 * Page de wizard pour sélectionner les autres options
 * @author Lionel HERVIER
 */
public class OtherOptionsPage extends WizardPage {

	/**
	 * Le control principal
	 */
	private Composite container;
	
	/**
	 * La combo pour choisir le nom du répertoire source
	 */
	private Combo sourceCombo;
	
	/**
	 * La zone de texte pour saisir le nom du package pour les fichiers java
	 */
	private Text javaText;
	
	/**
	 * La zone de texte pour saisir le nom du package pour les fichiers xsp-config
	 */
	private Text xspConfigText;
	
	/**
	 * La zone de texte pour saisir le nom du fichier où exporter la liste des xsp-config
	 */
	private Text xspConfigListText;
	
	/**
	 * La zone de text qui contient le préfixe pour les cc à exporter
	 */
	private Text ccPrefixText;
	
	/**
	 * Constructeur
	 */
	public OtherOptionsPage() {
		super("Other options");
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
		return false;
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		// Quand on affiche la page, on doit mettre à jour la liste des valeurs pour la combo
		if( visible ) {
			List<String> lstNewItems = this.getWizard().getSourceFolders();
			String[] newItems = new String[lstNewItems.size() + 1];
			newItems[0] = "";
			int i=0;
			for( String item : lstNewItems)
				newItems[i + 1] = item;
			
			String[] prevItems = this.sourceCombo.getItems();
			
			if( !Utils.equals(prevItems, newItems) ) {
				this.sourceCombo.setItems(newItems);
				this.sourceCombo.setText(newItems[0]);		// On a au moins la valeur vide dedans
			}
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		final SetupWizard wizard = this.getWizard();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		
		this.container = new Composite(parent, SWT.NONE);
		this.container.setLayout(layout);
		
		new Label(this.container, SWT.NONE).setText("Source folder : ");
		this.sourceCombo = new Combo(this.container, SWT.BORDER | SWT.SINGLE);
		this.sourceCombo.setLayoutData(layoutData);
		this.sourceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wizard.setSourceFolder(OtherOptionsPage.this.sourceCombo.getText());
				wizard.getContainer().updateButtons();
			}
		});
		
		new Label(this.container, SWT.NONE).setText("Package for java files : ");
		this.javaText = new Text(this.container, SWT.BORDER | SWT.SINGLE);
		this.javaText.setText(this.getWizard().getJavaExportPackage());
		this.javaText.setLayoutData(layoutData);
		this.javaText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {}
			@Override
			public void keyReleased(KeyEvent event) {
				String text = OtherOptionsPage.this.javaText.getText();
				wizard.setJavaExportPackage(text);
				wizard.getContainer().updateButtons();
			}
		});
		
		new Label(this.container, SWT.NONE).setText("Package for xsp-config files : ");
		this.xspConfigText = new Text(this.container, SWT.BORDER | SWT.SINGLE);
		this.xspConfigText.setText(this.getWizard().getXspConfigExportPackage());
		this.xspConfigText.setLayoutData(layoutData);
		this.xspConfigText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {}
			@Override
			public void keyReleased(KeyEvent event) {
				String text = OtherOptionsPage.this.xspConfigText.getText();
				wizard.setXspConfigExportPackage(text);
				wizard.getContainer().updateButtons();
			}
		});
		
		new Label(this.container, SWT.NONE).setText("File to export the xsp-config classpath references into : ");
		this.xspConfigListText = new Text(this.container, SWT.BORDER | SWT.SINGLE);
		this.xspConfigListText.setText(this.getWizard().getXspConfigList());
		this.xspConfigListText.setLayoutData(layoutData);
		this.xspConfigListText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {}
			@Override
			public void keyReleased(KeyEvent event) {
				String text = OtherOptionsPage.this.xspConfigListText.getText();
				wizard.setXspConfigList(text);
				wizard.getContainer().updateButtons();
			}
		});
		
		new Label(this.container, SWT.NONE).setText("Only custom controls with this prefix will be exported : ");
		this.ccPrefixText = new Text(this.container, SWT.BORDER | SWT.SINGLE);
		this.ccPrefixText.setText(this.getWizard().getCcPrefix());
		this.ccPrefixText.setLayoutData(layoutData);
		this.ccPrefixText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {}
			@Override
			public void keyReleased(KeyEvent event) {
				String text = OtherOptionsPage.this.ccPrefixText.getText();
				wizard.setCcPrefix(text);
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
