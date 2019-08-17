package com.sun.java.swing.plaf.motif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.SwingUtilities2;

public class MotifFileChooserUI extends BasicFileChooserUI {
  private FilterComboBoxModel filterComboBoxModel;
  
  protected JList<File> directoryList = null;
  
  protected JList<File> fileList = null;
  
  protected JTextField pathField = null;
  
  protected JComboBox<FileFilter> filterComboBox = null;
  
  protected JTextField filenameTextField = null;
  
  private static final Dimension hstrut10 = new Dimension(10, 1);
  
  private static final Dimension vstrut10 = new Dimension(1, 10);
  
  private static final Insets insets = new Insets(10, 10, 10, 10);
  
  private static Dimension prefListSize = new Dimension(75, 150);
  
  private static Dimension WITH_ACCELERATOR_PREF_SIZE = new Dimension(650, 450);
  
  private static Dimension PREF_SIZE = new Dimension(350, 450);
  
  private static final int MIN_WIDTH = 200;
  
  private static final int MIN_HEIGHT = 300;
  
  private static Dimension PREF_ACC_SIZE = new Dimension(10, 10);
  
  private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);
  
  private static Dimension MAX_SIZE = new Dimension(32767, 32767);
  
  private static final Insets buttonMargin = new Insets(3, 3, 3, 3);
  
  private JPanel bottomPanel;
  
  protected JButton approveButton;
  
  private String enterFolderNameLabelText = null;
  
  private int enterFolderNameLabelMnemonic = 0;
  
  private String enterFileNameLabelText = null;
  
  private int enterFileNameLabelMnemonic = 0;
  
  private String filesLabelText = null;
  
  private int filesLabelMnemonic = 0;
  
  private String foldersLabelText = null;
  
  private int foldersLabelMnemonic = 0;
  
  private String pathLabelText = null;
  
  private int pathLabelMnemonic = 0;
  
  private String filterLabelText = null;
  
  private int filterLabelMnemonic = 0;
  
  private JLabel fileNameLabel;
  
  private void populateFileNameLabel() {
    if (getFileChooser().getFileSelectionMode() == 1) {
      this.fileNameLabel.setText(this.enterFolderNameLabelText);
      this.fileNameLabel.setDisplayedMnemonic(this.enterFolderNameLabelMnemonic);
    } else {
      this.fileNameLabel.setText(this.enterFileNameLabelText);
      this.fileNameLabel.setDisplayedMnemonic(this.enterFileNameLabelMnemonic);
    } 
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
  
  public MotifFileChooserUI(JFileChooser paramJFileChooser) { super(paramJFileChooser); }
  
  public String getFileName() { return (this.filenameTextField != null) ? this.filenameTextField.getText() : null; }
  
  public void setFileName(String paramString) {
    if (this.filenameTextField != null)
      this.filenameTextField.setText(paramString); 
  }
  
  public String getDirectoryName() { return this.pathField.getText(); }
  
  public void setDirectoryName(String paramString) { this.pathField.setText(paramString); }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {}
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) { getModel().validateFileCache(); }
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser) { return new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
          String str = param1PropertyChangeEvent.getPropertyName();
          if (str.equals("SelectedFileChangedProperty")) {
            File file = (File)param1PropertyChangeEvent.getNewValue();
            if (file != null)
              MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getFileChooser().getName(file)); 
          } else if (str.equals("SelectedFilesChangedProperty")) {
            File[] arrayOfFile = (File[])param1PropertyChangeEvent.getNewValue();
            JFileChooser jFileChooser = MotifFileChooserUI.this.getFileChooser();
            if (arrayOfFile != null && arrayOfFile.length > 0 && (arrayOfFile.length > 1 || jFileChooser.isDirectorySelectionEnabled() || !arrayOfFile[0].isDirectory()))
              MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.fileNameString(arrayOfFile)); 
          } else if (str.equals("fileFilterChanged")) {
            MotifFileChooserUI.this.fileList.clearSelection();
          } else if (str.equals("directoryChanged")) {
            MotifFileChooserUI.this.directoryList.clearSelection();
            ListSelectionModel listSelectionModel = MotifFileChooserUI.this.directoryList.getSelectionModel();
            if (listSelectionModel instanceof DefaultListSelectionModel) {
              ((DefaultListSelectionModel)listSelectionModel).moveLeadSelectionIndex(0);
              listSelectionModel.setAnchorSelectionIndex(0);
            } 
            MotifFileChooserUI.this.fileList.clearSelection();
            listSelectionModel = MotifFileChooserUI.this.fileList.getSelectionModel();
            if (listSelectionModel instanceof DefaultListSelectionModel) {
              ((DefaultListSelectionModel)listSelectionModel).moveLeadSelectionIndex(0);
              listSelectionModel.setAnchorSelectionIndex(0);
            } 
            File file = MotifFileChooserUI.this.getFileChooser().getCurrentDirectory();
            if (file != null) {
              try {
                MotifFileChooserUI.this.setDirectoryName(ShellFolder.getNormalizedFile((File)param1PropertyChangeEvent.getNewValue()).getPath());
              } catch (IOException iOException) {
                MotifFileChooserUI.this.setDirectoryName(((File)param1PropertyChangeEvent.getNewValue()).getAbsolutePath());
              } 
              if (MotifFileChooserUI.this.getFileChooser().getFileSelectionMode() == 1 && !MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled())
                MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getDirectoryName()); 
            } 
          } else if (str.equals("fileSelectionChanged")) {
            if (MotifFileChooserUI.this.fileNameLabel != null)
              MotifFileChooserUI.this.populateFileNameLabel(); 
            MotifFileChooserUI.this.directoryList.clearSelection();
          } else if (str.equals("MultiSelectionEnabledChangedProperty")) {
            if (MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
              MotifFileChooserUI.this.fileList.setSelectionMode(2);
            } else {
              MotifFileChooserUI.this.fileList.setSelectionMode(0);
              MotifFileChooserUI.this.fileList.clearSelection();
              MotifFileChooserUI.this.getFileChooser().setSelectedFiles(null);
            } 
          } else if (str.equals("AccessoryChangedProperty")) {
            if (MotifFileChooserUI.this.getAccessoryPanel() != null) {
              if (param1PropertyChangeEvent.getOldValue() != null)
                MotifFileChooserUI.this.getAccessoryPanel().remove((JComponent)param1PropertyChangeEvent.getOldValue()); 
              JComponent jComponent = (JComponent)param1PropertyChangeEvent.getNewValue();
              if (jComponent != null) {
                MotifFileChooserUI.this.getAccessoryPanel().add(jComponent, "Center");
                MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(PREF_ACC_SIZE);
                MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MAX_SIZE);
              } else {
                MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(ZERO_ACC_SIZE);
                MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(ZERO_ACC_SIZE);
              } 
            } 
          } else if (str.equals("ApproveButtonTextChangedProperty") || str.equals("ApproveButtonToolTipTextChangedProperty") || str.equals("DialogTypeChangedProperty")) {
            MotifFileChooserUI.this.approveButton.setText(MotifFileChooserUI.this.getApproveButtonText(MotifFileChooserUI.this.getFileChooser()));
            MotifFileChooserUI.this.approveButton.setToolTipText(MotifFileChooserUI.this.getApproveButtonToolTipText(MotifFileChooserUI.this.getFileChooser()));
          } else if (str.equals("ControlButtonsAreShownChangedProperty")) {
            MotifFileChooserUI.this.doControlButtonsChanged(param1PropertyChangeEvent);
          } else if (str.equals("componentOrientation")) {
            ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
            JFileChooser jFileChooser = (JFileChooser)param1PropertyChangeEvent.getSource();
            if (componentOrientation != (ComponentOrientation)param1PropertyChangeEvent.getOldValue())
              jFileChooser.applyComponentOrientation(componentOrientation); 
          } 
        }
      }; }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifFileChooserUI((JFileChooser)paramJComponent); }
  
  public void installUI(JComponent paramJComponent) { super.installUI(paramJComponent); }
  
  public void uninstallUI(JComponent paramJComponent) {
    paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
    this.approveButton.removeActionListener(getApproveSelectionAction());
    this.filenameTextField.removeActionListener(getApproveSelectionAction());
    super.uninstallUI(paramJComponent);
  }
  
  public void installComponents(JFileChooser paramJFileChooser) {
    paramJFileChooser.setLayout(new BorderLayout(10, 10));
    paramJFileChooser.setAlignmentX(0.5F);
    JPanel jPanel1 = new JPanel() {
        public Insets getInsets() { return insets; }
      };
    jPanel1.setInheritsPopupMenu(true);
    align(jPanel1);
    jPanel1.setLayout(new BoxLayout(jPanel1, 3));
    paramJFileChooser.add(jPanel1, "Center");
    JLabel jLabel = new JLabel(this.pathLabelText);
    jLabel.setDisplayedMnemonic(this.pathLabelMnemonic);
    align(jLabel);
    jPanel1.add(jLabel);
    File file = paramJFileChooser.getCurrentDirectory();
    String str = null;
    if (file != null)
      str = file.getPath(); 
    this.pathField = new JTextField(str) {
        public Dimension getMaximumSize() {
          Dimension dimension = super.getMaximumSize();
          dimension.height = (getPreferredSize()).height;
          return dimension;
        }
      };
    this.pathField.setInheritsPopupMenu(true);
    jLabel.setLabelFor(this.pathField);
    align(this.pathField);
    this.pathField.addActionListener(getUpdateAction());
    jPanel1.add(this.pathField);
    jPanel1.add(Box.createRigidArea(vstrut10));
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BoxLayout(jPanel2, 2));
    align(jPanel2);
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BoxLayout(jPanel3, 3));
    align(jPanel3);
    jLabel = new JLabel(this.filterLabelText);
    jLabel.setDisplayedMnemonic(this.filterLabelMnemonic);
    align(jLabel);
    jPanel3.add(jLabel);
    this.filterComboBox = new JComboBox<FileFilter>() {
        public Dimension getMaximumSize() {
          Dimension dimension = super.getMaximumSize();
          dimension.height = (getPreferredSize()).height;
          return dimension;
        }
      };
    this.filterComboBox.setInheritsPopupMenu(true);
    jLabel.setLabelFor(this.filterComboBox);
    this.filterComboBoxModel = createFilterComboBoxModel();
    this.filterComboBox.setModel(this.filterComboBoxModel);
    this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
    paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
    align(this.filterComboBox);
    jPanel3.add(this.filterComboBox);
    jLabel = new JLabel(this.foldersLabelText);
    jLabel.setDisplayedMnemonic(this.foldersLabelMnemonic);
    align(jLabel);
    jPanel3.add(jLabel);
    JScrollPane jScrollPane = createDirectoryList();
    jScrollPane.getVerticalScrollBar().setFocusable(false);
    jScrollPane.getHorizontalScrollBar().setFocusable(false);
    jScrollPane.setInheritsPopupMenu(true);
    jLabel.setLabelFor(jScrollPane.getViewport().getView());
    jPanel3.add(jScrollPane);
    jPanel3.setInheritsPopupMenu(true);
    JPanel jPanel4 = new JPanel();
    align(jPanel4);
    jPanel4.setLayout(new BoxLayout(jPanel4, 3));
    jPanel4.setInheritsPopupMenu(true);
    jLabel = new JLabel(this.filesLabelText);
    jLabel.setDisplayedMnemonic(this.filesLabelMnemonic);
    align(jLabel);
    jPanel4.add(jLabel);
    jScrollPane = createFilesList();
    jLabel.setLabelFor(jScrollPane.getViewport().getView());
    jPanel4.add(jScrollPane);
    jScrollPane.setInheritsPopupMenu(true);
    jPanel2.add(jPanel3);
    jPanel2.add(Box.createRigidArea(hstrut10));
    jPanel2.add(jPanel4);
    jPanel2.setInheritsPopupMenu(true);
    JPanel jPanel5 = getAccessoryPanel();
    JComponent jComponent = paramJFileChooser.getAccessory();
    if (jPanel5 != null) {
      if (jComponent == null) {
        jPanel5.setPreferredSize(ZERO_ACC_SIZE);
        jPanel5.setMaximumSize(ZERO_ACC_SIZE);
      } else {
        getAccessoryPanel().add(jComponent, "Center");
        jPanel5.setPreferredSize(PREF_ACC_SIZE);
        jPanel5.setMaximumSize(MAX_SIZE);
      } 
      align(jPanel5);
      jPanel2.add(jPanel5);
      jPanel5.setInheritsPopupMenu(true);
    } 
    jPanel1.add(jPanel2);
    jPanel1.add(Box.createRigidArea(vstrut10));
    this.fileNameLabel = new JLabel();
    populateFileNameLabel();
    align(this.fileNameLabel);
    jPanel1.add(this.fileNameLabel);
    this.filenameTextField = new JTextField() {
        public Dimension getMaximumSize() {
          Dimension dimension = super.getMaximumSize();
          dimension.height = (getPreferredSize()).height;
          return dimension;
        }
      };
    this.filenameTextField.setInheritsPopupMenu(true);
    this.fileNameLabel.setLabelFor(this.filenameTextField);
    this.filenameTextField.addActionListener(getApproveSelectionAction());
    align(this.filenameTextField);
    this.filenameTextField.setAlignmentX(0.0F);
    jPanel1.add(this.filenameTextField);
    this.bottomPanel = getBottomPanel();
    this.bottomPanel.add(new JSeparator(), "North");
    JPanel jPanel6 = new JPanel();
    align(jPanel6);
    jPanel6.setLayout(new BoxLayout(jPanel6, 2));
    jPanel6.add(Box.createGlue());
    this.approveButton = new JButton(getApproveButtonText(paramJFileChooser)) {
        public Dimension getMaximumSize() { return new Dimension(MAX_SIZE.width, (getPreferredSize()).height); }
      };
    this.approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
    this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    this.approveButton.setInheritsPopupMenu(true);
    align(this.approveButton);
    this.approveButton.setMargin(buttonMargin);
    this.approveButton.addActionListener(getApproveSelectionAction());
    jPanel6.add(this.approveButton);
    jPanel6.add(Box.createGlue());
    JButton jButton1 = new JButton(this.updateButtonText) {
        public Dimension getMaximumSize() { return new Dimension(MAX_SIZE.width, (getPreferredSize()).height); }
      };
    jButton1.setMnemonic(this.updateButtonMnemonic);
    jButton1.setToolTipText(this.updateButtonToolTipText);
    jButton1.setInheritsPopupMenu(true);
    align(jButton1);
    jButton1.setMargin(buttonMargin);
    jButton1.addActionListener(getUpdateAction());
    jPanel6.add(jButton1);
    jPanel6.add(Box.createGlue());
    JButton jButton2 = new JButton(this.cancelButtonText) {
        public Dimension getMaximumSize() { return new Dimension(MAX_SIZE.width, (getPreferredSize()).height); }
      };
    jButton2.setMnemonic(this.cancelButtonMnemonic);
    jButton2.setToolTipText(this.cancelButtonToolTipText);
    jButton2.setInheritsPopupMenu(true);
    align(jButton2);
    jButton2.setMargin(buttonMargin);
    jButton2.addActionListener(getCancelSelectionAction());
    jPanel6.add(jButton2);
    jPanel6.add(Box.createGlue());
    JButton jButton3 = new JButton(this.helpButtonText) {
        public Dimension getMaximumSize() { return new Dimension(MAX_SIZE.width, (getPreferredSize()).height); }
      };
    jButton3.setMnemonic(this.helpButtonMnemonic);
    jButton3.setToolTipText(this.helpButtonToolTipText);
    align(jButton3);
    jButton3.setMargin(buttonMargin);
    jButton3.setEnabled(false);
    jButton3.setInheritsPopupMenu(true);
    jPanel6.add(jButton3);
    jPanel6.add(Box.createGlue());
    jPanel6.setInheritsPopupMenu(true);
    this.bottomPanel.add(jPanel6, "South");
    this.bottomPanel.setInheritsPopupMenu(true);
    if (paramJFileChooser.getControlButtonsAreShown())
      paramJFileChooser.add(this.bottomPanel, "South"); 
  }
  
  protected JPanel getBottomPanel() {
    if (this.bottomPanel == null)
      this.bottomPanel = new JPanel(new BorderLayout(0, 4)); 
    return this.bottomPanel;
  }
  
  private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getFileChooser().getControlButtonsAreShown()) {
      getFileChooser().add(this.bottomPanel, "South");
    } else {
      getFileChooser().remove(getBottomPanel());
    } 
  }
  
  public void uninstallComponents(JFileChooser paramJFileChooser) {
    paramJFileChooser.removeAll();
    this.bottomPanel = null;
    if (this.filterComboBoxModel != null)
      paramJFileChooser.removePropertyChangeListener(this.filterComboBoxModel); 
  }
  
  protected void installStrings(JFileChooser paramJFileChooser) {
    super.installStrings(paramJFileChooser);
    Locale locale = paramJFileChooser.getLocale();
    this.enterFolderNameLabelText = UIManager.getString("FileChooser.enterFolderNameLabelText", locale);
    this.enterFolderNameLabelMnemonic = getMnemonic("FileChooser.enterFolderNameLabelMnemonic", locale).intValue();
    this.enterFileNameLabelText = UIManager.getString("FileChooser.enterFileNameLabelText", locale);
    this.enterFileNameLabelMnemonic = getMnemonic("FileChooser.enterFileNameLabelMnemonic", locale).intValue();
    this.filesLabelText = UIManager.getString("FileChooser.filesLabelText", locale);
    this.filesLabelMnemonic = getMnemonic("FileChooser.filesLabelMnemonic", locale).intValue();
    this.foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", locale);
    this.foldersLabelMnemonic = getMnemonic("FileChooser.foldersLabelMnemonic", locale).intValue();
    this.pathLabelText = UIManager.getString("FileChooser.pathLabelText", locale);
    this.pathLabelMnemonic = getMnemonic("FileChooser.pathLabelMnemonic", locale).intValue();
    this.filterLabelText = UIManager.getString("FileChooser.filterLabelText", locale);
    this.filterLabelMnemonic = getMnemonic("FileChooser.filterLabelMnemonic", locale).intValue();
  }
  
  private Integer getMnemonic(String paramString, Locale paramLocale) { return Integer.valueOf(SwingUtilities2.getUIDefaultsInt(paramString, paramLocale)); }
  
  protected void installIcons(JFileChooser paramJFileChooser) {}
  
  protected void uninstallIcons(JFileChooser paramJFileChooser) {}
  
  protected JScrollPane createFilesList() {
    this.fileList = new JList();
    if (getFileChooser().isMultiSelectionEnabled()) {
      this.fileList.setSelectionMode(2);
    } else {
      this.fileList.setSelectionMode(0);
    } 
    this.fileList.setModel(new MotifFileListModel());
    this.fileList.getSelectionModel().removeSelectionInterval(0, 0);
    this.fileList.setCellRenderer(new FileCellRenderer());
    this.fileList.addListSelectionListener(createListSelectionListener(getFileChooser()));
    this.fileList.addMouseListener(createDoubleClickListener(getFileChooser(), this.fileList));
    this.fileList.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent param1MouseEvent) {
            JFileChooser jFileChooser = MotifFileChooserUI.this.getFileChooser();
            if (SwingUtilities.isLeftMouseButton(param1MouseEvent) && !jFileChooser.isMultiSelectionEnabled()) {
              int i = SwingUtilities2.loc2IndexFileList(MotifFileChooserUI.this.fileList, param1MouseEvent.getPoint());
              if (i >= 0) {
                File file = (File)MotifFileChooserUI.this.fileList.getModel().getElementAt(i);
                MotifFileChooserUI.this.setFileName(jFileChooser.getName(file));
              } 
            } 
          }
        });
    align(this.fileList);
    JScrollPane jScrollPane = new JScrollPane(this.fileList);
    jScrollPane.setPreferredSize(prefListSize);
    jScrollPane.setMaximumSize(MAX_SIZE);
    align(jScrollPane);
    this.fileList.setInheritsPopupMenu(true);
    jScrollPane.setInheritsPopupMenu(true);
    return jScrollPane;
  }
  
  protected JScrollPane createDirectoryList() {
    this.directoryList = new JList();
    align(this.directoryList);
    this.directoryList.setCellRenderer(new DirectoryCellRenderer());
    this.directoryList.setModel(new MotifDirectoryListModel());
    this.directoryList.getSelectionModel().removeSelectionInterval(0, 0);
    this.directoryList.addMouseListener(createDoubleClickListener(getFileChooser(), this.directoryList));
    this.directoryList.addListSelectionListener(createListSelectionListener(getFileChooser()));
    this.directoryList.setInheritsPopupMenu(true);
    JScrollPane jScrollPane = new JScrollPane(this.directoryList);
    jScrollPane.setMaximumSize(MAX_SIZE);
    jScrollPane.setPreferredSize(prefListSize);
    jScrollPane.setInheritsPopupMenu(true);
    align(jScrollPane);
    return jScrollPane;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension1 = (getFileChooser().getAccessory() != null) ? WITH_ACCELERATOR_PREF_SIZE : PREF_SIZE;
    Dimension dimension2 = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
    return (dimension2 != null) ? new Dimension((dimension2.width < dimension1.width) ? dimension1.width : dimension2.width, (dimension2.height < dimension1.height) ? dimension1.height : dimension2.height) : dimension1;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return new Dimension(200, 300); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(2147483647, 2147483647); }
  
  protected void align(JComponent paramJComponent) {
    paramJComponent.setAlignmentX(0.0F);
    paramJComponent.setAlignmentY(0.0F);
  }
  
  protected FilterComboBoxModel createFilterComboBoxModel() { return new FilterComboBoxModel(); }
  
  protected FilterComboBoxRenderer createFilterComboBoxRenderer() { return new FilterComboBoxRenderer(); }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser) { return this.approveButton; }
  
  protected class DirectoryCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      setText(MotifFileChooserUI.this.getFileChooser().getName((File)param1Object));
      setInheritsPopupMenu(true);
      return this;
    }
  }
  
  protected class FileCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      setText(MotifFileChooserUI.this.getFileChooser().getName((File)param1Object));
      setInheritsPopupMenu(true);
      return this;
    }
  }
  
  protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener {
    protected FileFilter[] filters;
    
    protected FilterComboBoxModel() { this.filters = this$0.getFileChooser().getChoosableFileFilters(); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str.equals("ChoosableFileFilterChangedProperty")) {
        this.filters = (FileFilter[])param1PropertyChangeEvent.getNewValue();
        fireContentsChanged(this, -1, -1);
      } else if (str.equals("fileFilterChanged")) {
        fireContentsChanged(this, -1, -1);
      } 
    }
    
    public void setSelectedItem(Object param1Object) {
      if (param1Object != null) {
        MotifFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)param1Object);
        fireContentsChanged(this, -1, -1);
      } 
    }
    
    public Object getSelectedItem() {
      FileFilter fileFilter = MotifFileChooserUI.this.getFileChooser().getFileFilter();
      boolean bool = false;
      if (fileFilter != null) {
        for (FileFilter fileFilter1 : this.filters) {
          if (fileFilter1 == fileFilter)
            bool = true; 
        } 
        if (!bool)
          MotifFileChooserUI.this.getFileChooser().addChoosableFileFilter(fileFilter); 
      } 
      return MotifFileChooserUI.this.getFileChooser().getFileFilter();
    }
    
    public int getSize() { return (this.filters != null) ? this.filters.length : 0; }
    
    public FileFilter getElementAt(int param1Int) { return (param1Int > getSize() - 1) ? MotifFileChooserUI.this.getFileChooser().getFileFilter() : ((this.filters != null) ? this.filters[param1Int] : null); }
  }
  
  public class FilterComboBoxRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      if (param1Object != null && param1Object instanceof FileFilter)
        setText(((FileFilter)param1Object).getDescription()); 
      return this;
    }
  }
  
  protected class MotifDirectoryListModel extends AbstractListModel<File> implements ListDataListener {
    public MotifDirectoryListModel() { this$0.getModel().addListDataListener(this); }
    
    public int getSize() { return MotifFileChooserUI.this.getModel().getDirectories().size(); }
    
    public File getElementAt(int param1Int) { return (File)MotifFileChooserUI.this.getModel().getDirectories().elementAt(param1Int); }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) { fireIntervalAdded(this, param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1()); }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { fireIntervalRemoved(this, param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1()); }
    
    public void fireContentsChanged() { fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getDirectories().size() - 1); }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) { fireContentsChanged(); }
  }
  
  protected class MotifFileListModel extends AbstractListModel<File> implements ListDataListener {
    public MotifFileListModel() { this$0.getModel().addListDataListener(this); }
    
    public int getSize() { return MotifFileChooserUI.this.getModel().getFiles().size(); }
    
    public boolean contains(Object param1Object) { return MotifFileChooserUI.this.getModel().getFiles().contains(param1Object); }
    
    public int indexOf(Object param1Object) { return MotifFileChooserUI.this.getModel().getFiles().indexOf(param1Object); }
    
    public File getElementAt(int param1Int) { return (File)MotifFileChooserUI.this.getModel().getFiles().elementAt(param1Int); }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) { fireIntervalAdded(this, param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1()); }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { fireIntervalRemoved(this, param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1()); }
    
    public void fireContentsChanged() { fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getFiles().size() - 1); }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) { fireContentsChanged(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */