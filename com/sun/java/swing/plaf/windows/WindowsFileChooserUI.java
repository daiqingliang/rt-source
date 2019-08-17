package com.sun.java.swing.plaf.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;
import sun.swing.WindowsPlacesBar;

public class WindowsFileChooserUI extends BasicFileChooserUI {
  private JPanel centerPanel;
  
  private JLabel lookInLabel;
  
  private JComboBox<File> directoryComboBox;
  
  private DirectoryComboBoxModel directoryComboBoxModel;
  
  private ActionListener directoryComboBoxAction = new DirectoryComboBoxAction();
  
  private FilterComboBoxModel filterComboBoxModel;
  
  private JTextField filenameTextField;
  
  private FilePane filePane;
  
  private WindowsPlacesBar placesBar;
  
  private JButton approveButton;
  
  private JButton cancelButton;
  
  private JPanel buttonPanel;
  
  private JPanel bottomPanel;
  
  private JComboBox<FileFilter> filterComboBox;
  
  private static final Dimension hstrut10 = new Dimension(10, 1);
  
  private static final Dimension vstrut4 = new Dimension(1, 4);
  
  private static final Dimension vstrut6 = new Dimension(1, 6);
  
  private static final Dimension vstrut8 = new Dimension(1, 8);
  
  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
  
  private static int PREF_WIDTH = 425;
  
  private static int PREF_HEIGHT = 245;
  
  private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
  
  private static int MIN_WIDTH = 425;
  
  private static int MIN_HEIGHT = 245;
  
  private static int LIST_PREF_WIDTH = 444;
  
  private static int LIST_PREF_HEIGHT = 138;
  
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
  
  private String newFolderToolTipText = null;
  
  private String newFolderAccessibleName = null;
  
  private String viewMenuButtonToolTipText = null;
  
  private String viewMenuButtonAccessibleName = null;
  
  private BasicFileChooserUI.BasicFileView fileView = new WindowsFileView();
  
  private JLabel fileNameLabel;
  
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
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsFileChooserUI((JFileChooser)paramJComponent); }
  
  public WindowsFileChooserUI(JFileChooser paramJFileChooser) { super(paramJFileChooser); }
  
  public void installUI(JComponent paramJComponent) { super.installUI(paramJComponent); }
  
  public void uninstallComponents(JFileChooser paramJFileChooser) { paramJFileChooser.removeAll(); }
  
  public void installComponents(JFileChooser paramJFileChooser) {
    this.filePane = new FilePane(new WindowsFileChooserUIAccessor(this, null));
    paramJFileChooser.addPropertyChangeListener(this.filePane);
    FileSystemView fileSystemView = paramJFileChooser.getFileSystemView();
    paramJFileChooser.setBorder(new EmptyBorder(4, 10, 10, 10));
    paramJFileChooser.setLayout(new BorderLayout(8, 8));
    updateUseShellFolder();
    JToolBar jToolBar = new JToolBar();
    jToolBar.setFloatable(false);
    jToolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    paramJFileChooser.add(jToolBar, "North");
    this.lookInLabel = new JLabel(this.lookInLabelText, 11) {
        public Dimension getPreferredSize() { return getMinimumSize(); }
        
        public Dimension getMinimumSize() {
          Dimension dimension = super.getPreferredSize();
          if (WindowsFileChooserUI.this.placesBar != null)
            dimension.width = Math.max(dimension.width, WindowsFileChooserUI.this.placesBar.getWidth()); 
          return dimension;
        }
      };
    this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
    this.lookInLabel.setAlignmentX(0.0F);
    this.lookInLabel.setAlignmentY(0.5F);
    jToolBar.add(this.lookInLabel);
    jToolBar.add(Box.createRigidArea(new Dimension(8, 0)));
    this.directoryComboBox = new JComboBox<File>() {
        public Dimension getMinimumSize() {
          Dimension dimension = super.getMinimumSize();
          dimension.width = 60;
          return dimension;
        }
        
        public Dimension getPreferredSize() {
          Dimension dimension = super.getPreferredSize();
          dimension.width = 150;
          return dimension;
        }
      };
    this.directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
    this.lookInLabel.setLabelFor(this.directoryComboBox);
    this.directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
    this.directoryComboBox.setModel(this.directoryComboBoxModel);
    this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
    this.directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
    this.directoryComboBox.setAlignmentX(0.0F);
    this.directoryComboBox.setAlignmentY(0.5F);
    this.directoryComboBox.setMaximumRowCount(8);
    jToolBar.add(this.directoryComboBox);
    jToolBar.add(Box.createRigidArea(hstrut10));
    JButton jButton1 = createToolButton(getChangeToParentDirectoryAction(), this.upFolderIcon, this.upFolderToolTipText, this.upFolderAccessibleName);
    jToolBar.add(jButton1);
    if (!UIManager.getBoolean("FileChooser.readOnly")) {
      JButton jButton = createToolButton(this.filePane.getNewFolderAction(), this.newFolderIcon, this.newFolderToolTipText, this.newFolderAccessibleName);
      jToolBar.add(jButton);
    } 
    ButtonGroup buttonGroup = new ButtonGroup();
    final JPopupMenu viewTypePopupMenu = new JPopupMenu();
    final JRadioButtonMenuItem listViewMenuItem = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(0));
    jRadioButtonMenuItem1.setSelected((this.filePane.getViewType() == 0));
    jPopupMenu.add(jRadioButtonMenuItem1);
    buttonGroup.add(jRadioButtonMenuItem1);
    final JRadioButtonMenuItem detailsViewMenuItem = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(1));
    jRadioButtonMenuItem2.setSelected((this.filePane.getViewType() == 1));
    jPopupMenu.add(jRadioButtonMenuItem2);
    buttonGroup.add(jRadioButtonMenuItem2);
    BufferedImage bufferedImage = new BufferedImage(this.viewMenuIcon.getIconWidth() + 7, this.viewMenuIcon.getIconHeight(), 2);
    Graphics graphics = bufferedImage.getGraphics();
    this.viewMenuIcon.paintIcon(this.filePane, graphics, 0, 0);
    int i = bufferedImage.getWidth() - 5;
    int j = bufferedImage.getHeight() / 2 - 1;
    graphics.setColor(Color.BLACK);
    graphics.fillPolygon(new int[] { i, i + 5, i + 2 }, new int[] { j, j, j + 3 }, 3);
    final JButton viewMenuButton = createToolButton(null, new ImageIcon(bufferedImage), this.viewMenuButtonToolTipText, this.viewMenuButtonAccessibleName);
    jButton2.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent param1MouseEvent) {
            if (SwingUtilities.isLeftMouseButton(param1MouseEvent) && !viewMenuButton.isSelected()) {
              viewMenuButton.setSelected(true);
              viewTypePopupMenu.show(viewMenuButton, 0, viewMenuButton.getHeight());
            } 
          }
        });
    jButton2.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent param1KeyEvent) {
            if (param1KeyEvent.getKeyCode() == 32 && viewMenuButton.getModel().isRollover()) {
              viewMenuButton.setSelected(true);
              viewTypePopupMenu.show(viewMenuButton, 0, viewMenuButton.getHeight());
            } 
          }
        });
    jPopupMenu.addPopupMenuListener(new PopupMenuListener() {
          public void popupMenuWillBecomeVisible(PopupMenuEvent param1PopupMenuEvent) {}
          
          public void popupMenuWillBecomeInvisible(PopupMenuEvent param1PopupMenuEvent) { SwingUtilities.invokeLater(new Runnable() {
                  public void run() { viewMenuButton.setSelected(false); }
                }); }
          
          public void popupMenuCanceled(PopupMenuEvent param1PopupMenuEvent) {}
        });
    jToolBar.add(jButton2);
    jToolBar.add(Box.createRigidArea(new Dimension(80, 0)));
    this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            if ("viewType".equals(param1PropertyChangeEvent.getPropertyName()))
              switch (WindowsFileChooserUI.this.filePane.getViewType()) {
                case 0:
                  listViewMenuItem.setSelected(true);
                  break;
                case 1:
                  detailsViewMenuItem.setSelected(true);
                  break;
              }  
          }
        });
    this.centerPanel = new JPanel(new BorderLayout());
    this.centerPanel.add(getAccessoryPanel(), "After");
    JComponent jComponent = paramJFileChooser.getAccessory();
    if (jComponent != null)
      getAccessoryPanel().add(jComponent); 
    this.filePane.setPreferredSize(LIST_PREF_SIZE);
    this.centerPanel.add(this.filePane, "Center");
    paramJFileChooser.add(this.centerPanel, "Center");
    getBottomPanel().setLayout(new BoxLayout(getBottomPanel(), 2));
    this.centerPanel.add(getBottomPanel(), "South");
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new BoxLayout(jPanel1, 3));
    jPanel1.add(Box.createRigidArea(vstrut4));
    this.fileNameLabel = new JLabel();
    populateFileNameLabel();
    this.fileNameLabel.setAlignmentY(0.0F);
    jPanel1.add(this.fileNameLabel);
    jPanel1.add(Box.createRigidArea(new Dimension(1, 12)));
    JLabel jLabel = new JLabel(this.filesOfTypeLabelText);
    jLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
    jPanel1.add(jLabel);
    getBottomPanel().add(jPanel1);
    getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));
    JPanel jPanel2 = new JPanel();
    jPanel2.add(Box.createRigidArea(vstrut8));
    jPanel2.setLayout(new BoxLayout(jPanel2, 1));
    this.filenameTextField = new JTextField(35) {
        public Dimension getMaximumSize() { return new Dimension(32767, (getPreferredSize()).height); }
      };
    this.fileNameLabel.setLabelFor(this.filenameTextField);
    this.filenameTextField.addFocusListener(new FocusAdapter() {
          public void focusGained(FocusEvent param1FocusEvent) {
            if (!WindowsFileChooserUI.this.getFileChooser().isMultiSelectionEnabled())
              WindowsFileChooserUI.this.filePane.clearSelection(); 
          }
        });
    if (paramJFileChooser.isMultiSelectionEnabled()) {
      setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
    } else {
      setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
    } 
    jPanel2.add(this.filenameTextField);
    jPanel2.add(Box.createRigidArea(vstrut8));
    this.filterComboBoxModel = createFilterComboBoxModel();
    paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
    this.filterComboBox = new JComboBox(this.filterComboBoxModel);
    jLabel.setLabelFor(this.filterComboBox);
    this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
    jPanel2.add(this.filterComboBox);
    getBottomPanel().add(jPanel2);
    getBottomPanel().add(Box.createRigidArea(new Dimension(30, 0)));
    getButtonPanel().setLayout(new BoxLayout(getButtonPanel(), 1));
    this.approveButton = new JButton(getApproveButtonText(paramJFileChooser)) {
        public Dimension getMaximumSize() { return ((this.this$0.approveButton.getPreferredSize()).width > (this.this$0.cancelButton.getPreferredSize()).width) ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize(); }
      };
    Insets insets = this.approveButton.getMargin();
    insets = new InsetsUIResource(insets.top, insets.left + 5, insets.bottom, insets.right + 5);
    this.approveButton.setMargin(insets);
    this.approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
    this.approveButton.addActionListener(getApproveSelectionAction());
    this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    getButtonPanel().add(Box.createRigidArea(vstrut6));
    getButtonPanel().add(this.approveButton);
    getButtonPanel().add(Box.createRigidArea(vstrut4));
    this.cancelButton = new JButton(this.cancelButtonText) {
        public Dimension getMaximumSize() { return ((this.this$0.approveButton.getPreferredSize()).width > (this.this$0.cancelButton.getPreferredSize()).width) ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize(); }
      };
    this.cancelButton.setMargin(insets);
    this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
    this.cancelButton.addActionListener(getCancelSelectionAction());
    getButtonPanel().add(this.cancelButton);
    if (paramJFileChooser.getControlButtonsAreShown())
      addControlButtons(); 
  }
  
  private void updateUseShellFolder() {
    JFileChooser jFileChooser = getFileChooser();
    if (FilePane.usesShellFolder(jFileChooser)) {
      if (this.placesBar == null && !UIManager.getBoolean("FileChooser.noPlacesBar")) {
        this.placesBar = new WindowsPlacesBar(jFileChooser, (XPStyle.getXP() != null));
        jFileChooser.add(this.placesBar, "Before");
        jFileChooser.addPropertyChangeListener(this.placesBar);
      } 
    } else if (this.placesBar != null) {
      jFileChooser.remove(this.placesBar);
      jFileChooser.removePropertyChangeListener(this.placesBar);
      this.placesBar = null;
    } 
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
    this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", locale);
    this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", locale);
    this.viewMenuButtonToolTipText = UIManager.getString("FileChooser.viewMenuButtonToolTipText", locale);
    this.viewMenuButtonAccessibleName = UIManager.getString("FileChooser.viewMenuButtonAccessibleName", locale);
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
    if (this.placesBar != null)
      paramJComponent.removePropertyChangeListener(this.placesBar); 
    this.cancelButton.removeActionListener(getCancelSelectionAction());
    this.approveButton.removeActionListener(getApproveSelectionAction());
    this.filenameTextField.removeActionListener(getApproveSelectionAction());
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
    this.approveButton.setMnemonic(getApproveButtonMnemonic(jFileChooser));
  }
  
  private void doDialogTypeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    JFileChooser jFileChooser = getFileChooser();
    this.approveButton.setText(getApproveButtonText(jFileChooser));
    this.approveButton.setToolTipText(getApproveButtonToolTipText(jFileChooser));
    this.approveButton.setMnemonic(getApproveButtonMnemonic(jFileChooser));
    if (jFileChooser.getDialogType() == 1) {
      this.lookInLabel.setText(this.saveInLabelText);
    } else {
      this.lookInLabel.setText(this.lookInLabelText);
    } 
  }
  
  private void doApproveButtonMnemonicChanged(PropertyChangeEvent paramPropertyChangeEvent) { this.approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser())); }
  
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
            WindowsFileChooserUI.this.doSelectedFileChanged(param1PropertyChangeEvent);
          } else if (str.equals("SelectedFilesChangedProperty")) {
            WindowsFileChooserUI.this.doSelectedFilesChanged(param1PropertyChangeEvent);
          } else if (str.equals("directoryChanged")) {
            WindowsFileChooserUI.this.doDirectoryChanged(param1PropertyChangeEvent);
          } else if (str.equals("fileFilterChanged")) {
            WindowsFileChooserUI.this.doFilterChanged(param1PropertyChangeEvent);
          } else if (str.equals("fileSelectionChanged")) {
            WindowsFileChooserUI.this.doFileSelectionModeChanged(param1PropertyChangeEvent);
          } else if (str.equals("AccessoryChangedProperty")) {
            WindowsFileChooserUI.this.doAccessoryChanged(param1PropertyChangeEvent);
          } else if (str.equals("ApproveButtonTextChangedProperty") || str.equals("ApproveButtonToolTipTextChangedProperty")) {
            WindowsFileChooserUI.this.doApproveButtonTextChanged(param1PropertyChangeEvent);
          } else if (str.equals("DialogTypeChangedProperty")) {
            WindowsFileChooserUI.this.doDialogTypeChanged(param1PropertyChangeEvent);
          } else if (str.equals("ApproveButtonMnemonicChangedProperty")) {
            WindowsFileChooserUI.this.doApproveButtonMnemonicChanged(param1PropertyChangeEvent);
          } else if (str.equals("ControlButtonsAreShownChangedProperty")) {
            WindowsFileChooserUI.this.doControlButtonsChanged(param1PropertyChangeEvent);
          } else if (str == "FileChooser.useShellFolder") {
            WindowsFileChooserUI.this.updateUseShellFolder();
            WindowsFileChooserUI.this.doDirectoryChanged(param1PropertyChangeEvent);
          } else if (str.equals("componentOrientation")) {
            ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
            JFileChooser jFileChooser = (JFileChooser)param1PropertyChangeEvent.getSource();
            if (componentOrientation != param1PropertyChangeEvent.getOldValue())
              jFileChooser.applyComponentOrientation(componentOrientation); 
          } else if (str.equals("ancestor") && param1PropertyChangeEvent.getOldValue() == null && param1PropertyChangeEvent.getNewValue() != null) {
            WindowsFileChooserUI.this.filenameTextField.selectAll();
            WindowsFileChooserUI.this.filenameTextField.requestFocus();
          } 
        }
      }; }
  
  protected void removeControlButtons() { getBottomPanel().remove(getButtonPanel()); }
  
  protected void addControlButtons() { getBottomPanel().add(getButtonPanel()); }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) { this.filePane.ensureFileIsVisible(paramJFileChooser, paramFile); }
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) { this.filePane.rescanCurrentDirectory(); }
  
  public String getFileName() { return (this.filenameTextField != null) ? this.filenameTextField.getText() : null; }
  
  public void setFileName(String paramString) {
    if (this.filenameTextField != null)
      this.filenameTextField.setText(paramString); 
  }
  
  protected void setDirectorySelected(boolean paramBoolean) {
    super.setDirectorySelected(paramBoolean);
    JFileChooser jFileChooser = getFileChooser();
    if (paramBoolean) {
      this.approveButton.setText(this.directoryOpenButtonText);
      this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
      this.approveButton.setMnemonic(this.directoryOpenButtonMnemonic);
    } else {
      this.approveButton.setText(getApproveButtonText(jFileChooser));
      this.approveButton.setToolTipText(getApproveButtonToolTipText(jFileChooser));
      this.approveButton.setMnemonic(getApproveButtonMnemonic(jFileChooser));
    } 
  }
  
  public String getDirectoryName() { return null; }
  
  public void setDirectoryName(String paramString) {}
  
  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser) { return new DirectoryComboBoxRenderer(); }
  
  private static JButton createToolButton(Action paramAction, Icon paramIcon, String paramString1, String paramString2) {
    final JButton result = new JButton(paramAction);
    jButton.setText(null);
    jButton.setIcon(paramIcon);
    jButton.setToolTipText(paramString1);
    jButton.setRequestFocusEnabled(false);
    jButton.putClientProperty("AccessibleName", paramString2);
    jButton.putClientProperty(WindowsLookAndFeel.HI_RES_DISABLED_ICON_CLIENT_KEY, Boolean.TRUE);
    jButton.setAlignmentX(0.0F);
    jButton.setAlignmentY(0.5F);
    jButton.setMargin(shrinkwrap);
    jButton.setFocusPainted(false);
    jButton.setModel(new DefaultButtonModel() {
          public void setPressed(boolean param1Boolean) {
            if (!param1Boolean || isRollover())
              super.setPressed(param1Boolean); 
          }
          
          public void setRollover(boolean param1Boolean) {
            if (param1Boolean && !isRollover())
              for (Component component : result.getParent().getComponents()) {
                if (component instanceof JButton && component != result)
                  ((JButton)component).getModel().setRollover(false); 
              }  
            super.setRollover(param1Boolean);
          }
          
          public void setSelected(boolean param1Boolean) {
            super.setSelected(param1Boolean);
            if (param1Boolean) {
              this.stateMask |= 0x5;
            } else {
              this.stateMask &= 0xFFFFFFFA;
            } 
          }
        });
    jButton.addFocusListener(new FocusAdapter() {
          public void focusGained(FocusEvent param1FocusEvent) { result.getModel().setRollover(true); }
          
          public void focusLost(FocusEvent param1FocusEvent) { result.getModel().setRollover(false); }
        });
    return jButton;
  }
  
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
  
  public FileView getFileView(JFileChooser paramJFileChooser) { return this.fileView; }
  
  protected class DirectoryComboBoxAction implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      File file = (File)WindowsFileChooserUI.this.directoryComboBox.getSelectedItem();
      WindowsFileChooserUI.this.getFileChooser().setCurrentDirectory(file);
    }
  }
  
  protected class DirectoryComboBoxModel extends AbstractListModel<File> implements ComboBoxModel<File> {
    Vector<File> directories = new Vector();
    
    int[] depths = null;
    
    File selectedDirectory = null;
    
    JFileChooser chooser = WindowsFileChooserUI.this.getFileChooser();
    
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
        file = param1File.getCanonicalFile();
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
    
    public File getElementAt(int param1Int) { return (File)this.directories.elementAt(param1Int); }
  }
  
  class DirectoryComboBoxRenderer extends DefaultListCellRenderer {
    WindowsFileChooserUI.IndentIcon ii = new WindowsFileChooserUI.IndentIcon(WindowsFileChooserUI.this);
    
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      if (param1Object == null) {
        setText("");
        return this;
      } 
      File file = (File)param1Object;
      setText(WindowsFileChooserUI.this.getFileChooser().getName(file));
      Icon icon = WindowsFileChooserUI.this.getFileChooser().getIcon(file);
      this.ii.icon = icon;
      this.ii.depth = WindowsFileChooserUI.this.directoryComboBoxModel.getDepth(param1Int);
      setIcon(this.ii);
      return this;
    }
  }
  
  protected class FileRenderer extends DefaultListCellRenderer {}
  
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
        WindowsFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)param1Object);
        fireContentsChanged(this, -1, -1);
      } 
    }
    
    public Object getSelectedItem() {
      FileFilter fileFilter = WindowsFileChooserUI.this.getFileChooser().getFileFilter();
      boolean bool = false;
      if (fileFilter != null) {
        for (FileFilter fileFilter1 : this.filters) {
          if (fileFilter1 == fileFilter)
            bool = true; 
        } 
        if (!bool)
          WindowsFileChooserUI.this.getFileChooser().addChoosableFileFilter(fileFilter); 
      } 
      return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
    }
    
    public int getSize() { return (this.filters != null) ? this.filters.length : 0; }
    
    public FileFilter getElementAt(int param1Int) { return (param1Int > getSize() - 1) ? WindowsFileChooserUI.this.getFileChooser().getFileFilter() : ((this.filters != null) ? this.filters[param1Int] : null); }
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
  
  protected class SingleClickListener extends MouseAdapter {}
  
  private class WindowsFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
    private WindowsFileChooserUIAccessor() {}
    
    public JFileChooser getFileChooser() { return WindowsFileChooserUI.this.getFileChooser(); }
    
    public BasicDirectoryModel getModel() { return WindowsFileChooserUI.this.getModel(); }
    
    public JPanel createList() { return WindowsFileChooserUI.this.createList(getFileChooser()); }
    
    public JPanel createDetailsView() { return WindowsFileChooserUI.this.createDetailsView(getFileChooser()); }
    
    public boolean isDirectorySelected() { return WindowsFileChooserUI.this.isDirectorySelected(); }
    
    public File getDirectory() { return WindowsFileChooserUI.this.getDirectory(); }
    
    public Action getChangeToParentDirectoryAction() { return WindowsFileChooserUI.this.getChangeToParentDirectoryAction(); }
    
    public Action getApproveSelectionAction() { return WindowsFileChooserUI.this.getApproveSelectionAction(); }
    
    public Action getNewFolderAction() { return WindowsFileChooserUI.this.getNewFolderAction(); }
    
    public MouseListener createDoubleClickListener(JList param1JList) { return WindowsFileChooserUI.this.createDoubleClickListener(getFileChooser(), param1JList); }
    
    public ListSelectionListener createListSelectionListener() { return WindowsFileChooserUI.this.createListSelectionListener(getFileChooser()); }
  }
  
  protected class WindowsFileView extends BasicFileChooserUI.BasicFileView {
    protected WindowsFileView() { super(WindowsFileChooserUI.this); }
    
    public Icon getIcon(File param1File) {
      Icon icon = getCachedIcon(param1File);
      if (icon != null)
        return icon; 
      if (param1File != null)
        icon = WindowsFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(param1File); 
      if (icon == null)
        icon = super.getIcon(param1File); 
      cacheIcon(param1File, icon);
      return icon;
    }
  }
  
  protected class WindowsNewFolderAction extends BasicFileChooserUI.NewFolderAction {
    protected WindowsNewFolderAction() { super(WindowsFileChooserUI.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */