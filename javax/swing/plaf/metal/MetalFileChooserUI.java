package javax.swing.plaf.metal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;

public class MetalFileChooserUI extends BasicFileChooserUI {
  private JLabel lookInLabel;
  
  private JComboBox directoryComboBox;
  
  private DirectoryComboBoxModel directoryComboBoxModel;
  
  private Action directoryComboBoxAction = new DirectoryComboBoxAction();
  
  private FilterComboBoxModel filterComboBoxModel;
  
  private JTextField fileNameTextField;
  
  private FilePane filePane;
  
  private JToggleButton listViewButton;
  
  private JToggleButton detailsViewButton;
  
  private JButton approveButton;
  
  private JButton cancelButton;
  
  private JPanel buttonPanel;
  
  private JPanel bottomPanel;
  
  private JComboBox filterComboBox;
  
  private static final Dimension hstrut5 = new Dimension(5, 1);
  
  private static final Dimension hstrut11 = new Dimension(11, 1);
  
  private static final Dimension vstrut5 = new Dimension(1, 5);
  
  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
  
  private static int PREF_WIDTH = 500;
  
  private static int PREF_HEIGHT = 326;
  
  private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
  
  private static int MIN_WIDTH = 500;
  
  private static int MIN_HEIGHT = 326;
  
  private static int LIST_PREF_WIDTH = 405;
  
  private static int LIST_PREF_HEIGHT = 135;
  
  private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
  
  private int lookInLabelMnemonic = 0;
  
  private String lookInLabelText = null;
  
  private String saveInLabelText = null;
  
  private int fileNameLabelMnemonic = 0;
  
  private String fileNameLabelText = null;
  
  private int folderNameLabelMnemonic = 0;
  
  private String folderNameLabelText = null;
  
  private int filesOfTypeLabelMnemonic = 0;
  
  private String filesOfTypeLabelText = null;
  
  private String upFolderToolTipText = null;
  
  private String upFolderAccessibleName = null;
  
  private String homeFolderToolTipText = null;
  
  private String homeFolderAccessibleName = null;
  
  private String newFolderToolTipText = null;
  
  private String newFolderAccessibleName = null;
  
  private String listViewButtonToolTipText = null;
  
  private String listViewButtonAccessibleName = null;
  
  private String detailsViewButtonToolTipText = null;
  
  private String detailsViewButtonAccessibleName = null;
  
  private AlignedLabel fileNameLabel;
  
  static final int space = 10;
  
  private void populateFileNameLabel() {
    if (getFileChooser().getFileSelectionMode() == 1) {
      this.fileNameLabel.setText(this.folderNameLabelText);
      this.fileNameLabel.setDisplayedMnemonic(this.folderNameLabelMnemonic);
    } else {
      this.fileNameLabel.setText(this.fileNameLabelText);
      this.fileNameLabel.setDisplayedMnemonic(this.fileNameLabelMnemonic);
    } 
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalFileChooserUI((JFileChooser)paramJComponent); }
  
  public MetalFileChooserUI(JFileChooser paramJFileChooser) { super(paramJFileChooser); }
  
  public void installUI(JComponent paramJComponent) { super.installUI(paramJComponent); }
  
  public void uninstallComponents(JFileChooser paramJFileChooser) {
    paramJFileChooser.removeAll();
    this.bottomPanel = null;
    this.buttonPanel = null;
  }
  
  public void installComponents(JFileChooser paramJFileChooser) {
    FileSystemView fileSystemView = paramJFileChooser.getFileSystemView();
    paramJFileChooser.setBorder(new EmptyBorder(12, 12, 11, 11));
    paramJFileChooser.setLayout(new BorderLayout(0, 11));
    this.filePane = new FilePane(new MetalFileChooserUIAccessor(this, null));
    paramJFileChooser.addPropertyChangeListener(this.filePane);
    JPanel jPanel1 = new JPanel(new BorderLayout(11, 0));
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BoxLayout(jPanel2, 2));
    jPanel1.add(jPanel2, "After");
    paramJFileChooser.add(jPanel1, "North");
    this.lookInLabel = new JLabel(this.lookInLabelText);
    this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
    jPanel1.add(this.lookInLabel, "Before");
    this.directoryComboBox = new JComboBox() {
        public Dimension getPreferredSize() {
          Dimension dimension = super.getPreferredSize();
          dimension.width = 150;
          return dimension;
        }
      };
    this.directoryComboBox.putClientProperty("AccessibleDescription", this.lookInLabelText);
    this.directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    this.lookInLabel.setLabelFor(this.directoryComboBox);
    this.directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
    this.directoryComboBox.setModel(this.directoryComboBoxModel);
    this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
    this.directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
    this.directoryComboBox.setAlignmentX(0.0F);
    this.directoryComboBox.setAlignmentY(0.0F);
    this.directoryComboBox.setMaximumRowCount(8);
    jPanel1.add(this.directoryComboBox, "Center");
    JButton jButton1 = new JButton(getChangeToParentDirectoryAction());
    jButton1.setText(null);
    jButton1.setIcon(this.upFolderIcon);
    jButton1.setToolTipText(this.upFolderToolTipText);
    jButton1.putClientProperty("AccessibleName", this.upFolderAccessibleName);
    jButton1.setAlignmentX(0.0F);
    jButton1.setAlignmentY(0.5F);
    jButton1.setMargin(shrinkwrap);
    jPanel2.add(jButton1);
    jPanel2.add(Box.createRigidArea(hstrut5));
    File file = fileSystemView.getHomeDirectory();
    String str = this.homeFolderToolTipText;
    JButton jButton2 = new JButton(this.homeFolderIcon);
    jButton2.setToolTipText(str);
    jButton2.putClientProperty("AccessibleName", this.homeFolderAccessibleName);
    jButton2.setAlignmentX(0.0F);
    jButton2.setAlignmentY(0.5F);
    jButton2.setMargin(shrinkwrap);
    jButton2.addActionListener(getGoHomeAction());
    jPanel2.add(jButton2);
    jPanel2.add(Box.createRigidArea(hstrut5));
    if (!UIManager.getBoolean("FileChooser.readOnly")) {
      jButton2 = new JButton(this.filePane.getNewFolderAction());
      jButton2.setText(null);
      jButton2.setIcon(this.newFolderIcon);
      jButton2.setToolTipText(this.newFolderToolTipText);
      jButton2.putClientProperty("AccessibleName", this.newFolderAccessibleName);
      jButton2.setAlignmentX(0.0F);
      jButton2.setAlignmentY(0.5F);
      jButton2.setMargin(shrinkwrap);
    } 
    jPanel2.add(jButton2);
    jPanel2.add(Box.createRigidArea(hstrut5));
    ButtonGroup buttonGroup = new ButtonGroup();
    this.listViewButton = new JToggleButton(this.listViewIcon);
    this.listViewButton.setToolTipText(this.listViewButtonToolTipText);
    this.listViewButton.putClientProperty("AccessibleName", this.listViewButtonAccessibleName);
    this.listViewButton.setSelected(true);
    this.listViewButton.setAlignmentX(0.0F);
    this.listViewButton.setAlignmentY(0.5F);
    this.listViewButton.setMargin(shrinkwrap);
    this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
    jPanel2.add(this.listViewButton);
    buttonGroup.add(this.listViewButton);
    this.detailsViewButton = new JToggleButton(this.detailsViewIcon);
    this.detailsViewButton.setToolTipText(this.detailsViewButtonToolTipText);
    this.detailsViewButton.putClientProperty("AccessibleName", this.detailsViewButtonAccessibleName);
    this.detailsViewButton.setAlignmentX(0.0F);
    this.detailsViewButton.setAlignmentY(0.5F);
    this.detailsViewButton.setMargin(shrinkwrap);
    this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
    jPanel2.add(this.detailsViewButton);
    buttonGroup.add(this.detailsViewButton);
    this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            if ("viewType".equals(param1PropertyChangeEvent.getPropertyName())) {
              int i = MetalFileChooserUI.this.filePane.getViewType();
              switch (i) {
                case 0:
                  MetalFileChooserUI.this.listViewButton.setSelected(true);
                  break;
                case 1:
                  MetalFileChooserUI.this.detailsViewButton.setSelected(true);
                  break;
              } 
            } 
          }
        });
    paramJFileChooser.add(getAccessoryPanel(), "After");
    JComponent jComponent = paramJFileChooser.getAccessory();
    if (jComponent != null)
      getAccessoryPanel().add(jComponent); 
    this.filePane.setPreferredSize(LIST_PREF_SIZE);
    paramJFileChooser.add(this.filePane, "Center");
    JPanel jPanel3 = getBottomPanel();
    jPanel3.setLayout(new BoxLayout(jPanel3, 1));
    paramJFileChooser.add(jPanel3, "South");
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new BoxLayout(jPanel4, 2));
    jPanel3.add(jPanel4);
    jPanel3.add(Box.createRigidArea(vstrut5));
    this.fileNameLabel = new AlignedLabel();
    populateFileNameLabel();
    jPanel4.add(this.fileNameLabel);
    this.fileNameTextField = new JTextField(35) {
        public Dimension getMaximumSize() { return new Dimension(32767, (getPreferredSize()).height); }
      };
    jPanel4.add(this.fileNameTextField);
    this.fileNameLabel.setLabelFor(this.fileNameTextField);
    this.fileNameTextField.addFocusListener(new FocusAdapter() {
          public void focusGained(FocusEvent param1FocusEvent) {
            if (!MetalFileChooserUI.this.getFileChooser().isMultiSelectionEnabled())
              MetalFileChooserUI.this.filePane.clearSelection(); 
          }
        });
    if (paramJFileChooser.isMultiSelectionEnabled()) {
      setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
    } else {
      setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
    } 
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new BoxLayout(jPanel5, 2));
    jPanel3.add(jPanel5);
    AlignedLabel alignedLabel = new AlignedLabel(this.filesOfTypeLabelText);
    alignedLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
    jPanel5.add(alignedLabel);
    this.filterComboBoxModel = createFilterComboBoxModel();
    paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
    this.filterComboBox = new JComboBox(this.filterComboBoxModel);
    this.filterComboBox.putClientProperty("AccessibleDescription", this.filesOfTypeLabelText);
    alignedLabel.setLabelFor(this.filterComboBox);
    this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
    jPanel5.add(this.filterComboBox);
    getButtonPanel().setLayout(new ButtonAreaLayout(null));
    this.approveButton = new JButton(getApproveButtonText(paramJFileChooser));
    this.approveButton.addActionListener(getApproveSelectionAction());
    this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    getButtonPanel().add(this.approveButton);
    this.cancelButton = new JButton(this.cancelButtonText);
    this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
    this.cancelButton.addActionListener(getCancelSelectionAction());
    getButtonPanel().add(this.cancelButton);
    if (paramJFileChooser.getControlButtonsAreShown())
      addControlButtons(); 
    groupLabels(new AlignedLabel[] { this.fileNameLabel, alignedLabel });
  }
  
  protected JPanel getButtonPanel() {
    if (this.buttonPanel == null)
      this.buttonPanel = new JPanel(); 
    return this.buttonPanel;
  }
  
  protected JPanel getBottomPanel() {
    if (this.bottomPanel == null)
      this.bottomPanel = new JPanel(); 
    return this.bottomPanel;
  }
  
  protected void installStrings(JFileChooser paramJFileChooser) {
    super.installStrings(paramJFileChooser);
    Locale locale = paramJFileChooser.getLocale();
    this.lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", locale).intValue();
    this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", locale);
    this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", locale);
    this.fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", locale).intValue();
    this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", locale);
    this.folderNameLabelMnemonic = getMnemonic("FileChooser.folderNameLabelMnemonic", locale).intValue();
    this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", locale);
    this.filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", locale).intValue();
    this.filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", locale);
    this.upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", locale);
    this.upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", locale);
    this.homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", locale);
    this.homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", locale);
    this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", locale);
    this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", locale);
    this.listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", locale);
    this.listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", locale);
    this.detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", locale);
    this.detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", locale);
  }
  
  private Integer getMnemonic(String paramString, Locale paramLocale) { return Integer.valueOf(SwingUtilities2.getUIDefaultsInt(paramString, paramLocale)); }
  
  protected void installListeners(JFileChooser paramJFileChooser) {
    super.installListeners(paramJFileChooser);
    ActionMap actionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(paramJFileChooser, actionMap);
  }
  
  protected ActionMap getActionMap() { return createActionMap(); }
  
  protected ActionMap createActionMap() {
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    FilePane.addActionsToMap(actionMapUIResource, this.filePane.getActions());
    return actionMapUIResource;
  }
  
  protected JPanel createList(JFileChooser paramJFileChooser) { return this.filePane.createList(); }
  
  protected JPanel createDetailsView(JFileChooser paramJFileChooser) { return this.filePane.createDetailsView(); }
  
  public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser) { return super.createListSelectionListener(paramJFileChooser); }
  
  public void uninstallUI(JComponent paramJComponent) {
    paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
    paramJComponent.removePropertyChangeListener(this.filePane);
    this.cancelButton.removeActionListener(getCancelSelectionAction());
    this.approveButton.removeActionListener(getApproveSelectionAction());
    this.fileNameTextField.removeActionListener(getApproveSelectionAction());
    if (this.filePane != null) {
      this.filePane.uninstallUI();
      this.filePane = null;
    } 
    super.uninstallUI(paramJComponent);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    int i = PREF_SIZE.width;
    Dimension dimension = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
    return (dimension != null) ? new Dimension((dimension.width < i) ? i : dimension.width, (dimension.height < PREF_SIZE.height) ? PREF_SIZE.height : dimension.height) : new Dimension(i, PREF_SIZE.height);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return new Dimension(MIN_WIDTH, MIN_HEIGHT); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(2147483647, 2147483647); }
  
  private String fileNameString(File paramFile) {
    if (paramFile == null)
      return null; 
    JFileChooser jFileChooser = getFileChooser();
    return ((jFileChooser.isDirectorySelectionEnabled() && !jFileChooser.isFileSelectionEnabled()) || (jFileChooser.isDirectorySelectionEnabled() && jFileChooser.isFileSelectionEnabled() && jFileChooser.getFileSystemView().isFileSystemRoot(paramFile))) ? paramFile.getPath() : paramFile.getName();
  }
  
  private String fileNameString(File[] paramArrayOfFile) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; paramArrayOfFile != null && b < paramArrayOfFile.length; b++) {
      if (b)
        stringBuffer.append(" "); 
      if (paramArrayOfFile.length > 1)
        stringBuffer.append("\""); 
      stringBuffer.append(fileNameString(paramArrayOfFile[b]));
      if (paramArrayOfFile.length > 1)
        stringBuffer.append("\""); 
    } 
    return stringBuffer.toString();
  }
  
  private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    File file = (File)paramPropertyChangeEvent.getNewValue();
    JFileChooser jFileChooser = getFileChooser();
    if (file != null && ((jFileChooser.isFileSelectionEnabled() && !file.isDirectory()) || (file.isDirectory() && jFileChooser.isDirectorySelectionEnabled())))
      setFileName(fileNameString(file)); 
  }
  
  private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
    JFileChooser jFileChooser = getFileChooser();
    if (arrayOfFile != null && arrayOfFile.length > 0 && (arrayOfFile.length > 1 || jFileChooser.isDirectorySelectionEnabled() || !arrayOfFile[0].isDirectory()))
      setFileName(fileNameString(arrayOfFile)); 
  }
  
  private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    JFileChooser jFileChooser = getFileChooser();
    FileSystemView fileSystemView = jFileChooser.getFileSystemView();
    clearIconCache();
    File file = jFileChooser.getCurrentDirectory();
    if (file != null) {
      this.directoryComboBoxModel.addItem(file);
      if (jFileChooser.isDirectorySelectionEnabled() && !jFileChooser.isFileSelectionEnabled())
        if (fileSystemView.isFileSystem(file)) {
          setFileName(file.getPath());
        } else {
          setFileName(null);
        }  
    } 
  }
  
  private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent) { clearIconCache(); }
  
  private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (this.fileNameLabel != null)
      populateFileNameLabel(); 
    clearIconCache();
    JFileChooser jFileChooser = getFileChooser();
    File file = jFileChooser.getCurrentDirectory();
    if (file != null && jFileChooser.isDirectorySelectionEnabled() && !jFileChooser.isFileSelectionEnabled() && jFileChooser.getFileSystemView().isFileSystem(file)) {
      setFileName(file.getPath());
    } else {
      setFileName(null);
    } 
  }
  
  private void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getAccessoryPanel() != null) {
      if (paramPropertyChangeEvent.getOldValue() != null)
        getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue()); 
      JComponent jComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
      if (jComponent != null)
        getAccessoryPanel().add(jComponent, "Center"); 
    } 
  }
  
  private void doApproveButtonTextChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    JFileChooser jFileChooser = getFileChooser();
    this.approveButton.setText(getApproveButtonText(jFileChooser));
    this.approveButton.setToolTipText(getApproveButtonToolTipText(jFileChooser));
  }
  
  private void doDialogTypeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    JFileChooser jFileChooser = getFileChooser();
    this.approveButton.setText(getApproveButtonText(jFileChooser));
    this.approveButton.setToolTipText(getApproveButtonToolTipText(jFileChooser));
    if (jFileChooser.getDialogType() == 1) {
      this.lookInLabel.setText(this.saveInLabelText);
    } else {
      this.lookInLabel.setText(this.lookInLabelText);
    } 
  }
  
  private void doApproveButtonMnemonicChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getFileChooser().getControlButtonsAreShown()) {
      addControlButtons();
    } else {
      removeControlButtons();
    } 
  }
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser) { return new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
          String str = param1PropertyChangeEvent.getPropertyName();
          if (str.equals("SelectedFileChangedProperty")) {
            MetalFileChooserUI.this.doSelectedFileChanged(param1PropertyChangeEvent);
          } else if (str.equals("SelectedFilesChangedProperty")) {
            MetalFileChooserUI.this.doSelectedFilesChanged(param1PropertyChangeEvent);
          } else if (str.equals("directoryChanged")) {
            MetalFileChooserUI.this.doDirectoryChanged(param1PropertyChangeEvent);
          } else if (str.equals("fileFilterChanged")) {
            MetalFileChooserUI.this.doFilterChanged(param1PropertyChangeEvent);
          } else if (str.equals("fileSelectionChanged")) {
            MetalFileChooserUI.this.doFileSelectionModeChanged(param1PropertyChangeEvent);
          } else if (str.equals("AccessoryChangedProperty")) {
            MetalFileChooserUI.this.doAccessoryChanged(param1PropertyChangeEvent);
          } else if (str.equals("ApproveButtonTextChangedProperty") || str.equals("ApproveButtonToolTipTextChangedProperty")) {
            MetalFileChooserUI.this.doApproveButtonTextChanged(param1PropertyChangeEvent);
          } else if (str.equals("DialogTypeChangedProperty")) {
            MetalFileChooserUI.this.doDialogTypeChanged(param1PropertyChangeEvent);
          } else if (str.equals("ApproveButtonMnemonicChangedProperty")) {
            MetalFileChooserUI.this.doApproveButtonMnemonicChanged(param1PropertyChangeEvent);
          } else if (str.equals("ControlButtonsAreShownChangedProperty")) {
            MetalFileChooserUI.this.doControlButtonsChanged(param1PropertyChangeEvent);
          } else if (str.equals("componentOrientation")) {
            ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
            JFileChooser jFileChooser = (JFileChooser)param1PropertyChangeEvent.getSource();
            if (componentOrientation != param1PropertyChangeEvent.getOldValue())
              jFileChooser.applyComponentOrientation(componentOrientation); 
          } else if (str == "FileChooser.useShellFolder") {
            MetalFileChooserUI.this.doDirectoryChanged(param1PropertyChangeEvent);
          } else if (str.equals("ancestor") && param1PropertyChangeEvent.getOldValue() == null && param1PropertyChangeEvent.getNewValue() != null) {
            MetalFileChooserUI.this.fileNameTextField.selectAll();
            MetalFileChooserUI.this.fileNameTextField.requestFocus();
          } 
        }
      }; }
  
  protected void removeControlButtons() { getBottomPanel().remove(getButtonPanel()); }
  
  protected void addControlButtons() { getBottomPanel().add(getButtonPanel()); }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) { this.filePane.ensureFileIsVisible(paramJFileChooser, paramFile); }
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) { this.filePane.rescanCurrentDirectory(); }
  
  public String getFileName() { return (this.fileNameTextField != null) ? this.fileNameTextField.getText() : null; }
  
  public void setFileName(String paramString) {
    if (this.fileNameTextField != null)
      this.fileNameTextField.setText(paramString); 
  }
  
  protected void setDirectorySelected(boolean paramBoolean) {
    super.setDirectorySelected(paramBoolean);
    JFileChooser jFileChooser = getFileChooser();
    if (paramBoolean) {
      if (this.approveButton != null) {
        this.approveButton.setText(this.directoryOpenButtonText);
        this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
      } 
    } else if (this.approveButton != null) {
      this.approveButton.setText(getApproveButtonText(jFileChooser));
      this.approveButton.setToolTipText(getApproveButtonToolTipText(jFileChooser));
    } 
  }
  
  public String getDirectoryName() { return null; }
  
  public void setDirectoryName(String paramString) {}
  
  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser) { return new DirectoryComboBoxRenderer(); }
  
  protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser) { return new DirectoryComboBoxModel(); }
  
  protected FilterComboBoxRenderer createFilterComboBoxRenderer() { return new FilterComboBoxRenderer(); }
  
  protected FilterComboBoxModel createFilterComboBoxModel() { return new FilterComboBoxModel(); }
  
  public void valueChanged(ListSelectionEvent paramListSelectionEvent) {
    JFileChooser jFileChooser = getFileChooser();
    File file = jFileChooser.getSelectedFile();
    if (!paramListSelectionEvent.getValueIsAdjusting() && file != null && !getFileChooser().isTraversable(file))
      setFileName(fileNameString(file)); 
  }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser) { return this.approveButton; }
  
  private static void groupLabels(AlignedLabel[] paramArrayOfAlignedLabel) {
    for (byte b = 0; b < paramArrayOfAlignedLabel.length; b++)
      (paramArrayOfAlignedLabel[b]).group = paramArrayOfAlignedLabel; 
  }
  
  private class AlignedLabel extends JLabel {
    private AlignedLabel[] group;
    
    private int maxWidth = 0;
    
    AlignedLabel() { setAlignmentX(0.0F); }
    
    AlignedLabel(String param1String) {
      super(param1String);
      setAlignmentX(0.0F);
    }
    
    public Dimension getPreferredSize() {
      Dimension dimension = super.getPreferredSize();
      return new Dimension(getMaxWidth() + 11, dimension.height);
    }
    
    private int getMaxWidth() {
      if (this.maxWidth == 0 && this.group != null) {
        int i = 0;
        byte b;
        for (b = 0; b < this.group.length; b++)
          i = Math.max(this.group[b].getSuperPreferredWidth(), i); 
        for (b = 0; b < this.group.length; b++)
          (this.group[b]).maxWidth = i; 
      } 
      return this.maxWidth;
    }
    
    private int getSuperPreferredWidth() { return (super.getPreferredSize()).width; }
  }
  
  private static class ButtonAreaLayout implements LayoutManager {
    private int hGap = 5;
    
    private int topMargin = 17;
    
    private ButtonAreaLayout() {}
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void layoutContainer(Container param1Container) {
      Component[] arrayOfComponent = param1Container.getComponents();
      if (arrayOfComponent != null && arrayOfComponent.length > 0) {
        int n;
        int i = arrayOfComponent.length;
        Dimension[] arrayOfDimension = new Dimension[i];
        Insets insets = param1Container.getInsets();
        int j = insets.top + this.topMargin;
        int k = 0;
        int m;
        for (m = 0; m < i; m++) {
          arrayOfDimension[m] = arrayOfComponent[m].getPreferredSize();
          k = Math.max(k, (arrayOfDimension[m]).width);
        } 
        if (param1Container.getComponentOrientation().isLeftToRight()) {
          m = (param1Container.getSize()).width - insets.left - k;
          n = this.hGap + k;
        } else {
          m = insets.left;
          n = -(this.hGap + k);
        } 
        for (int i1 = i - 1; i1 >= 0; i1--) {
          arrayOfComponent[i1].setBounds(m, j, k, (arrayOfDimension[i1]).height);
          m -= n;
        } 
      } 
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      if (param1Container != null) {
        Component[] arrayOfComponent = param1Container.getComponents();
        if (arrayOfComponent != null && arrayOfComponent.length > 0) {
          int i = arrayOfComponent.length;
          int j = 0;
          Insets insets = param1Container.getInsets();
          int k = this.topMargin + insets.top + insets.bottom;
          int m = insets.left + insets.right;
          int n = 0;
          for (byte b = 0; b < i; b++) {
            Dimension dimension = arrayOfComponent[b].getPreferredSize();
            j = Math.max(j, dimension.height);
            n = Math.max(n, dimension.width);
          } 
          return new Dimension(m + i * n + (i - 1) * this.hGap, k + j);
        } 
      } 
      return new Dimension(0, 0);
    }
    
    public Dimension preferredLayoutSize(Container param1Container) { return minimumLayoutSize(param1Container); }
    
    public void removeLayoutComponent(Component param1Component) {}
  }
  
  protected class DirectoryComboBoxAction extends AbstractAction {
    protected DirectoryComboBoxAction() { super("DirectoryComboBoxAction"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      MetalFileChooserUI.this.directoryComboBox.hidePopup();
      File file = (File)MetalFileChooserUI.this.directoryComboBox.getSelectedItem();
      if (!MetalFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(file))
        MetalFileChooserUI.this.getFileChooser().setCurrentDirectory(file); 
    }
  }
  
  protected class DirectoryComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object> {
    Vector<File> directories = new Vector();
    
    int[] depths = null;
    
    File selectedDirectory = null;
    
    JFileChooser chooser = MetalFileChooserUI.this.getFileChooser();
    
    FileSystemView fsv = this.chooser.getFileSystemView();
    
    public DirectoryComboBoxModel() {
      File file = this$0.getFileChooser().getCurrentDirectory();
      if (file != null)
        addItem(file); 
    }
    
    private void addItem(File param1File) {
      File file;
      if (param1File == null)
        return; 
      boolean bool = FilePane.usesShellFolder(this.chooser);
      this.directories.clear();
      File[] arrayOfFile = bool ? (File[])ShellFolder.get("fileChooserComboBoxFolders") : this.fsv.getRoots();
      this.directories.addAll(Arrays.asList(arrayOfFile));
      try {
        file = ShellFolder.getNormalizedFile(param1File);
      } catch (IOException iOException) {
        file = param1File;
      } 
      try {
        ShellFolder shellFolder1 = bool ? ShellFolder.getShellFolder(file) : file;
        ShellFolder shellFolder2 = shellFolder1;
        Vector vector = new Vector(10);
        File file1;
        do {
          vector.addElement(shellFolder2);
        } while ((file1 = shellFolder2.getParentFile()) != null);
        int i = vector.size();
        for (int j = 0; j < i; j++) {
          file1 = (File)vector.get(j);
          if (this.directories.contains(file1)) {
            int k = this.directories.indexOf(file1);
            for (int m = j - 1; m >= 0; m--)
              this.directories.insertElementAt(vector.get(m), k + j - m); 
            break;
          } 
        } 
        calculateDepths();
        setSelectedItem(shellFolder1);
      } catch (FileNotFoundException fileNotFoundException) {
        calculateDepths();
      } 
    }
    
    private void calculateDepths() {
      this.depths = new int[this.directories.size()];
      for (byte b = 0; b < this.depths.length; b++) {
        File file1 = (File)this.directories.get(b);
        File file2 = file1.getParentFile();
        this.depths[b] = 0;
        if (file2 != null)
          for (byte b1 = b - 1; b1 >= 0; b1--) {
            if (file2.equals(this.directories.get(b1))) {
              this.depths[b] = this.depths[b1] + 1;
              break;
            } 
          }  
      } 
    }
    
    public int getDepth(int param1Int) { return (this.depths != null && param1Int >= 0 && param1Int < this.depths.length) ? this.depths[param1Int] : 0; }
    
    public void setSelectedItem(Object param1Object) {
      this.selectedDirectory = (File)param1Object;
      fireContentsChanged(this, -1, -1);
    }
    
    public Object getSelectedItem() { return this.selectedDirectory; }
    
    public int getSize() { return this.directories.size(); }
    
    public Object getElementAt(int param1Int) { return this.directories.elementAt(param1Int); }
  }
  
  class DirectoryComboBoxRenderer extends DefaultListCellRenderer {
    MetalFileChooserUI.IndentIcon ii = new MetalFileChooserUI.IndentIcon(MetalFileChooserUI.this);
    
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      if (param1Object == null) {
        setText("");
        return this;
      } 
      File file = (File)param1Object;
      setText(MetalFileChooserUI.this.getFileChooser().getName(file));
      Icon icon = MetalFileChooserUI.this.getFileChooser().getIcon(file);
      this.ii.icon = icon;
      this.ii.depth = MetalFileChooserUI.this.directoryComboBoxModel.getDepth(param1Int);
      setIcon(this.ii);
      return this;
    }
  }
  
  protected class FileRenderer extends DefaultListCellRenderer {}
  
  protected class FilterComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object>, PropertyChangeListener {
    protected FileFilter[] filters;
    
    protected FilterComboBoxModel() { this.filters = this$0.getFileChooser().getChoosableFileFilters(); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "ChoosableFileFilterChangedProperty") {
        this.filters = (FileFilter[])param1PropertyChangeEvent.getNewValue();
        fireContentsChanged(this, -1, -1);
      } else if (str == "fileFilterChanged") {
        fireContentsChanged(this, -1, -1);
      } 
    }
    
    public void setSelectedItem(Object param1Object) {
      if (param1Object != null) {
        MetalFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)param1Object);
        fireContentsChanged(this, -1, -1);
      } 
    }
    
    public Object getSelectedItem() {
      FileFilter fileFilter = MetalFileChooserUI.this.getFileChooser().getFileFilter();
      boolean bool = false;
      if (fileFilter != null) {
        for (FileFilter fileFilter1 : this.filters) {
          if (fileFilter1 == fileFilter)
            bool = true; 
        } 
        if (!bool)
          MetalFileChooserUI.this.getFileChooser().addChoosableFileFilter(fileFilter); 
      } 
      return MetalFileChooserUI.this.getFileChooser().getFileFilter();
    }
    
    public int getSize() { return (this.filters != null) ? this.filters.length : 0; }
    
    public Object getElementAt(int param1Int) { return (param1Int > getSize() - 1) ? MetalFileChooserUI.this.getFileChooser().getFileFilter() : ((this.filters != null) ? this.filters[param1Int] : null); }
  }
  
  public class FilterComboBoxRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      if (param1Object != null && param1Object instanceof FileFilter)
        setText(((FileFilter)param1Object).getDescription()); 
      return this;
    }
  }
  
  class IndentIcon implements Icon {
    Icon icon = null;
    
    int depth = 0;
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (param1Component.getComponentOrientation().isLeftToRight()) {
        this.icon.paintIcon(param1Component, param1Graphics, param1Int1 + this.depth * 10, param1Int2);
      } else {
        this.icon.paintIcon(param1Component, param1Graphics, param1Int1, param1Int2);
      } 
    }
    
    public int getIconWidth() { return this.icon.getIconWidth() + this.depth * 10; }
    
    public int getIconHeight() { return this.icon.getIconHeight(); }
  }
  
  private class MetalFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
    private MetalFileChooserUIAccessor() {}
    
    public JFileChooser getFileChooser() { return MetalFileChooserUI.this.getFileChooser(); }
    
    public BasicDirectoryModel getModel() { return MetalFileChooserUI.this.getModel(); }
    
    public JPanel createList() { return MetalFileChooserUI.this.createList(getFileChooser()); }
    
    public JPanel createDetailsView() { return MetalFileChooserUI.this.createDetailsView(getFileChooser()); }
    
    public boolean isDirectorySelected() { return MetalFileChooserUI.this.isDirectorySelected(); }
    
    public File getDirectory() { return MetalFileChooserUI.this.getDirectory(); }
    
    public Action getChangeToParentDirectoryAction() { return MetalFileChooserUI.this.getChangeToParentDirectoryAction(); }
    
    public Action getApproveSelectionAction() { return MetalFileChooserUI.this.getApproveSelectionAction(); }
    
    public Action getNewFolderAction() { return MetalFileChooserUI.this.getNewFolderAction(); }
    
    public MouseListener createDoubleClickListener(JList param1JList) { return MetalFileChooserUI.this.createDoubleClickListener(getFileChooser(), param1JList); }
    
    public ListSelectionListener createListSelectionListener() { return MetalFileChooserUI.this.createListSelectionListener(getFileChooser()); }
  }
  
  protected class SingleClickListener extends MouseAdapter {
    public SingleClickListener(JList param1JList) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */