package sun.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.synth.SynthContext;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;

public class SynthFileChooserUIImpl extends SynthFileChooserUI {
  private JLabel lookInLabel;
  
  private JComboBox<File> directoryComboBox;
  
  private DirectoryComboBoxModel directoryComboBoxModel;
  
  private Action directoryComboBoxAction = new DirectoryComboBoxAction();
  
  private FilterComboBoxModel filterComboBoxModel;
  
  private JTextField fileNameTextField;
  
  private FilePane filePane;
  
  private JToggleButton listViewButton;
  
  private JToggleButton detailsViewButton;
  
  private boolean readOnly;
  
  private JPanel buttonPanel;
  
  private JPanel bottomPanel;
  
  private JComboBox<FileFilter> filterComboBox;
  
  private static final Dimension hstrut5 = new Dimension(5, 1);
  
  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
  
  private static Dimension LIST_PREF_SIZE = new Dimension(405, 135);
  
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
  
  private final PropertyChangeListener modeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
        if (SynthFileChooserUIImpl.this.fileNameLabel != null)
          SynthFileChooserUIImpl.this.populateFileNameLabel(); 
      }
    };
  
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
  
  public SynthFileChooserUIImpl(JFileChooser paramJFileChooser) { super(paramJFileChooser); }
  
  protected void installDefaults(JFileChooser paramJFileChooser) {
    super.installDefaults(paramJFileChooser);
    this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
  }
  
  public void installComponents(JFileChooser paramJFileChooser) {
    super.installComponents(paramJFileChooser);
    SynthContext synthContext = getContext(paramJFileChooser, 1);
    paramJFileChooser.setLayout(new BorderLayout(0, 11));
    JPanel jPanel1 = new JPanel(new BorderLayout(11, 0));
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BoxLayout(jPanel2, 2));
    jPanel1.add(jPanel2, "After");
    paramJFileChooser.add(jPanel1, "North");
    this.lookInLabel = new JLabel(this.lookInLabelText);
    this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
    jPanel1.add(this.lookInLabel, "Before");
    this.directoryComboBox = new JComboBox();
    this.directoryComboBox.getAccessibleContext().setAccessibleDescription(this.lookInLabelText);
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
    this.filePane = new FilePane(new SynthFileChooserUIAccessor(this, null));
    paramJFileChooser.addPropertyChangeListener(this.filePane);
    JPopupMenu jPopupMenu = this.filePane.getComponentPopupMenu();
    if (jPopupMenu != null) {
      jPopupMenu.insert(getChangeToParentDirectoryAction(), 0);
      if (File.separatorChar == '/')
        jPopupMenu.insert(getGoHomeAction(), 1); 
    } 
    FileSystemView fileSystemView = paramJFileChooser.getFileSystemView();
    JButton jButton1 = new JButton(getChangeToParentDirectoryAction());
    jButton1.setText(null);
    jButton1.setIcon(this.upFolderIcon);
    jButton1.setToolTipText(this.upFolderToolTipText);
    jButton1.getAccessibleContext().setAccessibleName(this.upFolderAccessibleName);
    jButton1.setAlignmentX(0.0F);
    jButton1.setAlignmentY(0.5F);
    jButton1.setMargin(shrinkwrap);
    jPanel2.add(jButton1);
    jPanel2.add(Box.createRigidArea(hstrut5));
    File file = fileSystemView.getHomeDirectory();
    String str = this.homeFolderToolTipText;
    JButton jButton2 = new JButton(this.homeFolderIcon);
    jButton2.setToolTipText(str);
    jButton2.getAccessibleContext().setAccessibleName(this.homeFolderAccessibleName);
    jButton2.setAlignmentX(0.0F);
    jButton2.setAlignmentY(0.5F);
    jButton2.setMargin(shrinkwrap);
    jButton2.addActionListener(getGoHomeAction());
    jPanel2.add(jButton2);
    jPanel2.add(Box.createRigidArea(hstrut5));
    if (!this.readOnly) {
      jButton2 = new JButton(this.filePane.getNewFolderAction());
      jButton2.setText(null);
      jButton2.setIcon(this.newFolderIcon);
      jButton2.setToolTipText(this.newFolderToolTipText);
      jButton2.getAccessibleContext().setAccessibleName(this.newFolderAccessibleName);
      jButton2.setAlignmentX(0.0F);
      jButton2.setAlignmentY(0.5F);
      jButton2.setMargin(shrinkwrap);
      jPanel2.add(jButton2);
      jPanel2.add(Box.createRigidArea(hstrut5));
    } 
    ButtonGroup buttonGroup = new ButtonGroup();
    this.listViewButton = new JToggleButton(this.listViewIcon);
    this.listViewButton.setToolTipText(this.listViewButtonToolTipText);
    this.listViewButton.getAccessibleContext().setAccessibleName(this.listViewButtonAccessibleName);
    this.listViewButton.setSelected(true);
    this.listViewButton.setAlignmentX(0.0F);
    this.listViewButton.setAlignmentY(0.5F);
    this.listViewButton.setMargin(shrinkwrap);
    this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
    jPanel2.add(this.listViewButton);
    buttonGroup.add(this.listViewButton);
    this.detailsViewButton = new JToggleButton(this.detailsViewIcon);
    this.detailsViewButton.setToolTipText(this.detailsViewButtonToolTipText);
    this.detailsViewButton.getAccessibleContext().setAccessibleName(this.detailsViewButtonAccessibleName);
    this.detailsViewButton.setAlignmentX(0.0F);
    this.detailsViewButton.setAlignmentY(0.5F);
    this.detailsViewButton.setMargin(shrinkwrap);
    this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
    jPanel2.add(this.detailsViewButton);
    buttonGroup.add(this.detailsViewButton);
    this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            if ("viewType".equals(param1PropertyChangeEvent.getPropertyName())) {
              int i = SynthFileChooserUIImpl.this.filePane.getViewType();
              switch (i) {
                case 0:
                  SynthFileChooserUIImpl.this.listViewButton.setSelected(true);
                  break;
                case 1:
                  SynthFileChooserUIImpl.this.detailsViewButton.setSelected(true);
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
    this.bottomPanel = new JPanel();
    this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, 1));
    paramJFileChooser.add(this.bottomPanel, "South");
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BoxLayout(jPanel3, 2));
    this.bottomPanel.add(jPanel3);
    this.bottomPanel.add(Box.createRigidArea(new Dimension(1, 5)));
    this.fileNameLabel = new AlignedLabel();
    populateFileNameLabel();
    jPanel3.add(this.fileNameLabel);
    this.fileNameTextField = new JTextField(35) {
        public Dimension getMaximumSize() { return new Dimension(32767, (getPreferredSize()).height); }
      };
    jPanel3.add(this.fileNameTextField);
    this.fileNameLabel.setLabelFor(this.fileNameTextField);
    this.fileNameTextField.addFocusListener(new FocusAdapter() {
          public void focusGained(FocusEvent param1FocusEvent) {
            if (!SynthFileChooserUIImpl.this.getFileChooser().isMultiSelectionEnabled())
              SynthFileChooserUIImpl.this.filePane.clearSelection(); 
          }
        });
    if (paramJFileChooser.isMultiSelectionEnabled()) {
      setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
    } else {
      setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
    } 
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new BoxLayout(jPanel4, 2));
    this.bottomPanel.add(jPanel4);
    AlignedLabel alignedLabel = new AlignedLabel(this.filesOfTypeLabelText);
    alignedLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
    jPanel4.add(alignedLabel);
    this.filterComboBoxModel = createFilterComboBoxModel();
    paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
    this.filterComboBox = new JComboBox(this.filterComboBoxModel);
    this.filterComboBox.getAccessibleContext().setAccessibleDescription(this.filesOfTypeLabelText);
    alignedLabel.setLabelFor(this.filterComboBox);
    this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
    jPanel4.add(this.filterComboBox);
    this.buttonPanel = new JPanel();
    this.buttonPanel.setLayout(new ButtonAreaLayout(null));
    this.buttonPanel.add(getApproveButton(paramJFileChooser));
    this.buttonPanel.add(getCancelButton(paramJFileChooser));
    if (paramJFileChooser.getControlButtonsAreShown())
      addControlButtons(); 
    groupLabels(new AlignedLabel[] { this.fileNameLabel, alignedLabel });
  }
  
  protected void installListeners(JFileChooser paramJFileChooser) {
    super.installListeners(paramJFileChooser);
    paramJFileChooser.addPropertyChangeListener("fileSelectionChanged", this.modeListener);
  }
  
  protected void uninstallListeners(JFileChooser paramJFileChooser) {
    paramJFileChooser.removePropertyChangeListener("fileSelectionChanged", this.modeListener);
    super.uninstallListeners(paramJFileChooser);
  }
  
  private String fileNameString(File paramFile) {
    if (paramFile == null)
      return null; 
    JFileChooser jFileChooser = getFileChooser();
    return (jFileChooser.isDirectorySelectionEnabled() && !jFileChooser.isFileSelectionEnabled()) ? paramFile.getPath() : paramFile.getName();
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
  
  public void uninstallUI(JComponent paramJComponent) {
    paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
    paramJComponent.removePropertyChangeListener(this.filePane);
    if (this.filePane != null) {
      this.filePane.uninstallUI();
      this.filePane = null;
    } 
    super.uninstallUI(paramJComponent);
  }
  
  protected void installStrings(JFileChooser paramJFileChooser) {
    super.installStrings(paramJFileChooser);
    Locale locale = paramJFileChooser.getLocale();
    this.lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", locale);
    this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", locale);
    this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", locale);
    this.fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", locale);
    this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", locale);
    this.folderNameLabelMnemonic = getMnemonic("FileChooser.folderNameLabelMnemonic", locale);
    this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", locale);
    this.filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", locale);
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
  
  private int getMnemonic(String paramString, Locale paramLocale) { return SwingUtilities2.getUIDefaultsInt(paramString, paramLocale); }
  
  public String getFileName() { return (this.fileNameTextField != null) ? this.fileNameTextField.getText() : null; }
  
  public void setFileName(String paramString) {
    if (this.fileNameTextField != null)
      this.fileNameTextField.setText(paramString); 
  }
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) { this.filePane.rescanCurrentDirectory(); }
  
  protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    super.doSelectedFileChanged(paramPropertyChangeEvent);
    File file = (File)paramPropertyChangeEvent.getNewValue();
    JFileChooser jFileChooser = getFileChooser();
    if (file != null && ((jFileChooser.isFileSelectionEnabled() && !file.isDirectory()) || (file.isDirectory() && jFileChooser.isDirectorySelectionEnabled())))
      setFileName(fileNameString(file)); 
  }
  
  protected void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    super.doSelectedFilesChanged(paramPropertyChangeEvent);
    File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
    JFileChooser jFileChooser = getFileChooser();
    if (arrayOfFile != null && arrayOfFile.length > 0 && (arrayOfFile.length > 1 || jFileChooser.isDirectorySelectionEnabled() || !arrayOfFile[0].isDirectory()))
      setFileName(fileNameString(arrayOfFile)); 
  }
  
  protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    super.doDirectoryChanged(paramPropertyChangeEvent);
    JFileChooser jFileChooser = getFileChooser();
    FileSystemView fileSystemView = jFileChooser.getFileSystemView();
    File file = jFileChooser.getCurrentDirectory();
    if (!this.readOnly && file != null)
      getNewFolderAction().setEnabled(this.filePane.canWrite(file)); 
    if (file != null) {
      JComponent jComponent = getDirectoryComboBox();
      if (jComponent instanceof JComboBox) {
        ComboBoxModel comboBoxModel = ((JComboBox)jComponent).getModel();
        if (comboBoxModel instanceof DirectoryComboBoxModel)
          ((DirectoryComboBoxModel)comboBoxModel).addItem(file); 
      } 
      if (jFileChooser.isDirectorySelectionEnabled() && !jFileChooser.isFileSelectionEnabled())
        if (fileSystemView.isFileSystem(file)) {
          setFileName(file.getPath());
        } else {
          setFileName(null);
        }  
    } 
  }
  
  protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    super.doFileSelectionModeChanged(paramPropertyChangeEvent);
    JFileChooser jFileChooser = getFileChooser();
    File file = jFileChooser.getCurrentDirectory();
    if (file != null && jFileChooser.isDirectorySelectionEnabled() && !jFileChooser.isFileSelectionEnabled() && jFileChooser.getFileSystemView().isFileSystem(file)) {
      setFileName(file.getPath());
    } else {
      setFileName(null);
    } 
  }
  
  protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getAccessoryPanel() != null) {
      if (paramPropertyChangeEvent.getOldValue() != null)
        getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue()); 
      JComponent jComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
      if (jComponent != null)
        getAccessoryPanel().add(jComponent, "Center"); 
    } 
  }
  
  protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    super.doControlButtonsChanged(paramPropertyChangeEvent);
    if (getFileChooser().getControlButtonsAreShown()) {
      addControlButtons();
    } else {
      removeControlButtons();
    } 
  }
  
  protected void addControlButtons() {
    if (this.bottomPanel != null)
      this.bottomPanel.add(this.buttonPanel); 
  }
  
  protected void removeControlButtons() {
    if (this.bottomPanel != null)
      this.bottomPanel.remove(this.buttonPanel); 
  }
  
  protected ActionMap createActionMap() {
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    FilePane.addActionsToMap(actionMapUIResource, this.filePane.getActions());
    actionMapUIResource.put("fileNameCompletion", getFileNameCompletionAction());
    return actionMapUIResource;
  }
  
  protected JComponent getDirectoryComboBox() { return this.directoryComboBox; }
  
  protected Action getDirectoryComboBoxAction() { return this.directoryComboBoxAction; }
  
  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser) { return new DirectoryComboBoxRenderer(this.directoryComboBox.getRenderer(), null); }
  
  protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser) { return new DirectoryComboBoxModel(); }
  
  protected FilterComboBoxRenderer createFilterComboBoxRenderer() { return new FilterComboBoxRenderer(this.filterComboBox.getRenderer(), null); }
  
  protected FilterComboBoxModel createFilterComboBoxModel() { return new FilterComboBoxModel(); }
  
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
      SynthFileChooserUIImpl.this.directoryComboBox.hidePopup();
      JComponent jComponent = SynthFileChooserUIImpl.this.getDirectoryComboBox();
      if (jComponent instanceof JComboBox) {
        File file = (File)((JComboBox)jComponent).getSelectedItem();
        SynthFileChooserUIImpl.this.getFileChooser().setCurrentDirectory(file);
      } 
    }
  }
  
  protected class DirectoryComboBoxModel extends AbstractListModel<File> implements ComboBoxModel<File> {
    Vector<File> directories = new Vector();
    
    int[] depths = null;
    
    File selectedDirectory = null;
    
    JFileChooser chooser = SynthFileChooserUIImpl.this.getFileChooser();
    
    FileSystemView fsv = this.chooser.getFileSystemView();
    
    public DirectoryComboBoxModel() {
      File file = this$0.getFileChooser().getCurrentDirectory();
      if (file != null)
        addItem(file); 
    }
    
    public void addItem(File param1File) {
      File file;
      if (param1File == null)
        return; 
      boolean bool = FilePane.usesShellFolder(this.chooser);
      int i = this.directories.size();
      this.directories.clear();
      if (i > 0)
        fireIntervalRemoved(this, 0, i); 
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
        int j = vector.size();
        for (int k = 0; k < j; k++) {
          file1 = (File)vector.get(k);
          if (this.directories.contains(file1)) {
            int m = this.directories.indexOf(file1);
            for (int n = k - 1; n >= 0; n--)
              this.directories.insertElementAt(vector.get(n), m + k - n); 
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
    
    public File getElementAt(int param1Int) { return (File)this.directories.elementAt(param1Int); }
  }
  
  private class DirectoryComboBoxRenderer extends Object implements ListCellRenderer<File> {
    private ListCellRenderer<? super File> delegate;
    
    SynthFileChooserUIImpl.IndentIcon ii = new SynthFileChooserUIImpl.IndentIcon(SynthFileChooserUIImpl.this);
    
    private DirectoryComboBoxRenderer(ListCellRenderer<? super File> param1ListCellRenderer) { this.delegate = param1ListCellRenderer; }
    
    public Component getListCellRendererComponent(JList<? extends File> param1JList, File param1File, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      Component component = this.delegate.getListCellRendererComponent(param1JList, param1File, param1Int, param1Boolean1, param1Boolean2);
      assert component instanceof JLabel;
      JLabel jLabel = (JLabel)component;
      if (param1File == null) {
        jLabel.setText("");
        return jLabel;
      } 
      jLabel.setText(SynthFileChooserUIImpl.this.getFileChooser().getName(param1File));
      Icon icon = SynthFileChooserUIImpl.this.getFileChooser().getIcon(param1File);
      this.ii.icon = icon;
      this.ii.depth = SynthFileChooserUIImpl.this.directoryComboBoxModel.getDepth(param1Int);
      jLabel.setIcon(this.ii);
      return jLabel;
    }
  }
  
  protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener {
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
        SynthFileChooserUIImpl.this.getFileChooser().setFileFilter((FileFilter)param1Object);
        fireContentsChanged(this, -1, -1);
      } 
    }
    
    public Object getSelectedItem() {
      FileFilter fileFilter = SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
      boolean bool = false;
      if (fileFilter != null) {
        for (FileFilter fileFilter1 : this.filters) {
          if (fileFilter1 == fileFilter)
            bool = true; 
        } 
        if (!bool)
          SynthFileChooserUIImpl.this.getFileChooser().addChoosableFileFilter(fileFilter); 
      } 
      return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
    }
    
    public int getSize() { return (this.filters != null) ? this.filters.length : 0; }
    
    public FileFilter getElementAt(int param1Int) { return (param1Int > getSize() - 1) ? SynthFileChooserUIImpl.this.getFileChooser().getFileFilter() : ((this.filters != null) ? this.filters[param1Int] : null); }
  }
  
  public class FilterComboBoxRenderer extends Object implements ListCellRenderer<FileFilter> {
    private ListCellRenderer<? super FileFilter> delegate;
    
    private FilterComboBoxRenderer(ListCellRenderer<? super FileFilter> param1ListCellRenderer) { this.delegate = param1ListCellRenderer; }
    
    public Component getListCellRendererComponent(JList<? extends FileFilter> param1JList, FileFilter param1FileFilter, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      Component component = this.delegate.getListCellRendererComponent(param1JList, param1FileFilter, param1Int, param1Boolean1, param1Boolean2);
      String str = null;
      if (param1FileFilter != null)
        str = param1FileFilter.getDescription(); 
      assert component instanceof JLabel;
      if (str != null)
        ((JLabel)component).setText(str); 
      return component;
    }
  }
  
  class IndentIcon implements Icon {
    Icon icon = null;
    
    int depth = 0;
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (this.icon != null)
        if (param1Component.getComponentOrientation().isLeftToRight()) {
          this.icon.paintIcon(param1Component, param1Graphics, param1Int1 + this.depth * 10, param1Int2);
        } else {
          this.icon.paintIcon(param1Component, param1Graphics, param1Int1, param1Int2);
        }  
    }
    
    public int getIconWidth() { return ((this.icon != null) ? this.icon.getIconWidth() : 0) + this.depth * 10; }
    
    public int getIconHeight() { return (this.icon != null) ? this.icon.getIconHeight() : 0; }
  }
  
  private class SynthFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
    private SynthFileChooserUIAccessor() {}
    
    public JFileChooser getFileChooser() { return SynthFileChooserUIImpl.this.getFileChooser(); }
    
    public BasicDirectoryModel getModel() { return SynthFileChooserUIImpl.this.getModel(); }
    
    public JPanel createList() { return null; }
    
    public JPanel createDetailsView() { return null; }
    
    public boolean isDirectorySelected() { return SynthFileChooserUIImpl.this.isDirectorySelected(); }
    
    public File getDirectory() { return SynthFileChooserUIImpl.this.getDirectory(); }
    
    public Action getChangeToParentDirectoryAction() { return SynthFileChooserUIImpl.this.getChangeToParentDirectoryAction(); }
    
    public Action getApproveSelectionAction() { return SynthFileChooserUIImpl.this.getApproveSelectionAction(); }
    
    public Action getNewFolderAction() { return SynthFileChooserUIImpl.this.getNewFolderAction(); }
    
    public MouseListener createDoubleClickListener(JList param1JList) { return SynthFileChooserUIImpl.this.createDoubleClickListener(getFileChooser(), param1JList); }
    
    public ListSelectionListener createListSelectionListener() { return SynthFileChooserUIImpl.this.createListSelectionListener(getFileChooser()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\plaf\synth\SynthFileChooserUIImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */