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
	 * La zone de texte pour saisir le nom du répertoire source
	 */
	private Text sourceText;
	
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
		
		new Label(this.container, SWT.NONE).setText("Source folder : ");
		this.sourceText = new Text(this.container, SWT.BORDER | SWT.SINGLE);
		this.sourceText.setText(this.getWizard().getSourceFolder());
		this.sourceText.setLayoutData(layoutData);
		this.sourceText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {}
			@Override
			public void keyReleased(KeyEvent event) {
				String text = OtherOptionsPage.this.sourceText.getText();
				OtherOptionsPage.this.getWizard().setSourceFolder(text);
				OtherOptionsPage.this.getWizard().getContainer().updateButtons();
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
				OtherOptionsPage.this.getWizard().setJavaExportPackage(text);
				OtherOptionsPage.this.getWizard().getContainer().updateButtons();
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
				OtherOptionsPage.this.getWizard().setXspConfigExportPackage(text);
				OtherOptionsPage.this.getWizard().getContainer().updateButtons();
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
				OtherOptionsPage.this.getWizard().setXspConfigList(text);
				OtherOptionsPage.this.getWizard().getContainer().updateButtons();
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
