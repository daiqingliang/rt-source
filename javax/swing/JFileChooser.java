package javax.swing;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;

public class JFileChooser extends JComponent implements Accessible {
  private static final String uiClassID = "FileChooserUI";
  
  public static final int OPEN_DIALOG = 0;
  
  public static final int SAVE_DIALOG = 1;
  
  public static final int CUSTOM_DIALOG = 2;
  
  public static final int CANCEL_OPTION = 1;
  
  public static final int APPROVE_OPTION = 0;
  
  public static final int ERROR_OPTION = -1;
  
  public static final int FILES_ONLY = 0;
  
  public static final int DIRECTORIES_ONLY = 1;
  
  public static final int FILES_AND_DIRECTORIES = 2;
  
  public static final String CANCEL_SELECTION = "CancelSelection";
  
  public static final String APPROVE_SELECTION = "ApproveSelection";
  
  public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";
  
  public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";
  
  public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";
  
  public static final String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";
  
  public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";
  
  public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";
  
  public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";
  
  public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
  
  public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";
  
  public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";
  
  public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";
  
  public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";
  
  public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";
  
  public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";
  
  public static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";
  
  public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";
  
  public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";
  
  public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";
  
  private String dialogTitle = null;
  
  private String approveButtonText = null;
  
  private String approveButtonToolTipText = null;
  
  private int approveButtonMnemonic = 0;
  
  private Vector<FileFilter> filters = new Vector(5);
  
  private JDialog dialog = null;
  
  private int dialogType = 0;
  
  private int returnValue = -1;
  
  private JComponent accessory = null;
  
  private FileView fileView = null;
  
  private boolean controlsShown = true;
  
  private boolean useFileHiding = true;
  
  private static final String SHOW_HIDDEN_PROP = "awt.file.showHiddenFiles";
  
  private PropertyChangeListener showFilesListener = null;
  
  private int fileSelectionMode = 0;
  
  private boolean multiSelectionEnabled = false;
  
  private boolean useAcceptAllFileFilter = true;
  
  private boolean dragEnabled = false;
  
  private FileFilter fileFilter = null;
  
  private FileSystemView fileSystemView = null;
  
  private File currentDirectory = null;
  
  private File selectedFile = null;
  
  private File[] selectedFiles;
  
  protected AccessibleContext accessibleContext = null;
  
  public JFileChooser() { this((File)null, (FileSystemView)null); }
  
  public JFileChooser(String paramString) { this(paramString, (FileSystemView)null); }
  
  public JFileChooser(File paramFile) { this(paramFile, (FileSystemView)null); }
  
  public JFileChooser(FileSystemView paramFileSystemView) { this((File)null, paramFileSystemView); }
  
  public JFileChooser(File paramFile, FileSystemView paramFileSystemView) {
    setup(paramFileSystemView);
    setCurrentDirectory(paramFile);
  }
  
  public JFileChooser(String paramString, FileSystemView paramFileSystemView) {
    setup(paramFileSystemView);
    if (paramString == null) {
      setCurrentDirectory(null);
    } else {
      setCurrentDirectory(this.fileSystemView.createFileObject(paramString));
    } 
  }
  
  protected void setup(FileSystemView paramFileSystemView) {
    installShowFilesListener();
    installHierarchyListener();
    if (paramFileSystemView == null)
      paramFileSystemView = FileSystemView.getFileSystemView(); 
    setFileSystemView(paramFileSystemView);
    updateUI();
    if (isAcceptAllFileFilterUsed())
      setFileFilter(getAcceptAllFileFilter()); 
    enableEvents(16L);
  }
  
  private void installHierarchyListener() { addHierarchyListener(new HierarchyListener() {
          public void hierarchyChanged(HierarchyEvent param1HierarchyEvent) {
            if ((param1HierarchyEvent.getChangeFlags() & 0x1L) == 1L) {
              JFileChooser jFileChooser = JFileChooser.this;
              JRootPane jRootPane = SwingUtilities.getRootPane(jFileChooser);
              if (jRootPane != null)
                jRootPane.setDefaultButton(jFileChooser.getUI().getDefaultButton(jFileChooser)); 
            } 
          }
        }); }
  
  private void installShowFilesListener() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Object object = toolkit.getDesktopProperty("awt.file.showHiddenFiles");
    if (object instanceof Boolean) {
      this.useFileHiding = !((Boolean)object).booleanValue();
      this.showFilesListener = new WeakPCL(this);
      toolkit.addPropertyChangeListener("awt.file.showHiddenFiles", this.showFilesListener);
    } 
  }
  
  public void setDragEnabled(boolean paramBoolean) {
    if (paramBoolean && GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    this.dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled() { return this.dragEnabled; }
  
  public File getSelectedFile() { return this.selectedFile; }
  
  public void setSelectedFile(File paramFile) {
    File file = this.selectedFile;
    this.selectedFile = paramFile;
    if (this.selectedFile != null) {
      if (paramFile.isAbsolute() && !getFileSystemView().isParent(getCurrentDirectory(), this.selectedFile))
        setCurrentDirectory(this.selectedFile.getParentFile()); 
      if (!isMultiSelectionEnabled() || this.selectedFiles == null || this.selectedFiles.length == 1)
        ensureFileIsVisible(this.selectedFile); 
    } 
    firePropertyChange("SelectedFileChangedProperty", file, this.selectedFile);
  }
  
  public File[] getSelectedFiles() { return (this.selectedFiles == null) ? new File[0] : (File[])this.selectedFiles.clone(); }
  
  public void setSelectedFiles(File[] paramArrayOfFile) {
    File[] arrayOfFile = this.selectedFiles;
    if (paramArrayOfFile == null || paramArrayOfFile.length == 0) {
      paramArrayOfFile = null;
      this.selectedFiles = null;
      setSelectedFile(null);
    } else {
      this.selectedFiles = (File[])paramArrayOfFile.clone();
      setSelectedFile(this.selectedFiles[0]);
    } 
    firePropertyChange("SelectedFilesChangedProperty", arrayOfFile, paramArrayOfFile);
  }
  
  public File getCurrentDirectory() { return this.currentDirectory; }
  
  public void setCurrentDirectory(File paramFile) {
    File file1 = this.currentDirectory;
    if (paramFile != null && !paramFile.exists())
      paramFile = this.currentDirectory; 
    if (paramFile == null)
      paramFile = getFileSystemView().getDefaultDirectory(); 
    if (this.currentDirectory != null && this.currentDirectory.equals(paramFile))
      return; 
    File file2 = null;
    while (!isTraversable(paramFile) && file2 != paramFile) {
      file2 = paramFile;
      paramFile = getFileSystemView().getParentDirectory(paramFile);
    } 
    this.currentDirectory = paramFile;
    firePropertyChange("directoryChanged", file1, this.currentDirectory);
  }
  
  public void changeToParentDirectory() {
    this.selectedFile = null;
    File file = getCurrentDirectory();
    setCurrentDirectory(getFileSystemView().getParentDirectory(file));
  }
  
  public void rescanCurrentDirectory() { getUI().rescanCurrentDirectory(this); }
  
  public void ensureFileIsVisible(File paramFile) { getUI().ensureFileIsVisible(this, paramFile); }
  
  public int showOpenDialog(Component paramComponent) throws HeadlessException {
    setDialogType(0);
    return showDialog(paramComponent, null);
  }
  
  public int showSaveDialog(Component paramComponent) throws HeadlessException {
    setDialogType(1);
    return showDialog(paramComponent, null);
  }
  
  public int showDialog(Component paramComponent, String paramString) throws HeadlessException {
    if (this.dialog != null)
      return -1; 
    if (paramString != null) {
      setApproveButtonText(paramString);
      setDialogType(2);
    } 
    this.dialog = createDialog(paramComponent);
    this.dialog.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent param1WindowEvent) { JFileChooser.this.returnValue = 1; }
        });
    this.returnValue = -1;
    rescanCurrentDirectory();
    this.dialog.show();
    firePropertyChange("JFileChooserDialogIsClosingProperty", this.dialog, null);
    this.dialog.getContentPane().removeAll();
    this.dialog.dispose();
    this.dialog = null;
    return this.returnValue;
  }
  
  protected JDialog createDialog(Component paramComponent) throws HeadlessException {
    JDialog jDialog;
    FileChooserUI fileChooserUI = getUI();
    String str = fileChooserUI.getDialogTitle(this);
    putClientProperty("AccessibleDescription", str);
    Window window = JOptionPane.getWindowForComponent(paramComponent);
    if (window instanceof Frame) {
      jDialog = new JDialog((Frame)window, str, true);
    } else {
      jDialog = new JDialog((Dialog)window, str, true);
    } 
    jDialog.setComponentOrientation(getComponentOrientation());
    Container container = jDialog.getContentPane();
    container.setLayout(new BorderLayout());
    container.add(this, "Center");
    if (JDialog.isDefaultLookAndFeelDecorated()) {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool)
        jDialog.getRootPane().setWindowDecorationStyle(6); 
    } 
    jDialog.pack();
    jDialog.setLocationRelativeTo(paramComponent);
    return jDialog;
  }
  
  public boolean getControlButtonsAreShown() { return this.controlsShown; }
  
  public void setControlButtonsAreShown(boolean paramBoolean) {
    if (this.controlsShown == paramBoolean)
      return; 
    boolean bool = this.controlsShown;
    this.controlsShown = paramBoolean;
    firePropertyChange("ControlButtonsAreShownChangedProperty", bool, this.controlsShown);
  }
  
  public int getDialogType() { return this.dialogType; }
  
  public void setDialogType(int paramInt) {
    if (this.dialogType == paramInt)
      return; 
    if (paramInt != 0 && paramInt != 1 && paramInt != 2)
      throw new IllegalArgumentException("Incorrect Dialog Type: " + paramInt); 
    int i = this.dialogType;
    this.dialogType = paramInt;
    if (paramInt == 0 || paramInt == 1)
      setApproveButtonText(null); 
    firePropertyChange("DialogTypeChangedProperty", i, paramInt);
  }
  
  public void setDialogTitle(String paramString) {
    String str = this.dialogTitle;
    this.dialogTitle = paramString;
    if (this.dialog != null)
      this.dialog.setTitle(paramString); 
    firePropertyChange("DialogTitleChangedProperty", str, paramString);
  }
  
  public String getDialogTitle() { return this.dialogTitle; }
  
  public void setApproveButtonToolTipText(String paramString) {
    if (this.approveButtonToolTipText == paramString)
      return; 
    String str = this.approveButtonToolTipText;
    this.approveButtonToolTipText = paramString;
    firePropertyChange("ApproveButtonToolTipTextChangedProperty", str, this.approveButtonToolTipText);
  }
  
  public String getApproveButtonToolTipText() { return this.approveButtonToolTipText; }
  
  public int getApproveButtonMnemonic() { return this.approveButtonMnemonic; }
  
  public void setApproveButtonMnemonic(int paramInt) {
    if (this.approveButtonMnemonic == paramInt)
      return; 
    int i = this.approveButtonMnemonic;
    this.approveButtonMnemonic = paramInt;
    firePropertyChange("ApproveButtonMnemonicChangedProperty", i, this.approveButtonMnemonic);
  }
  
  public void setApproveButtonMnemonic(char paramChar) {
    char c = paramChar;
    if (c >= 'a' && c <= 'z')
      c -= ' '; 
    setApproveButtonMnemonic(c);
  }
  
  public void setApproveButtonText(String paramString) {
    if (this.approveButtonText == paramString)
      return; 
    String str = this.approveButtonText;
    this.approveButtonText = paramString;
    firePropertyChange("ApproveButtonTextChangedProperty", str, paramString);
  }
  
  public String getApproveButtonText() { return this.approveButtonText; }
  
  public FileFilter[] getChoosableFileFilters() {
    FileFilter[] arrayOfFileFilter = new FileFilter[this.filters.size()];
    this.filters.copyInto(arrayOfFileFilter);
    return arrayOfFileFilter;
  }
  
  public void addChoosableFileFilter(FileFilter paramFileFilter) {
    if (paramFileFilter != null && !this.filters.contains(paramFileFilter)) {
      FileFilter[] arrayOfFileFilter = getChoosableFileFilters();
      this.filters.addElement(paramFileFilter);
      firePropertyChange("ChoosableFileFilterChangedProperty", arrayOfFileFilter, getChoosableFileFilters());
      if (this.fileFilter == null && this.filters.size() == 1)
        setFileFilter(paramFileFilter); 
    } 
  }
  
  public boolean removeChoosableFileFilter(FileFilter paramFileFilter) {
    int i = this.filters.indexOf(paramFileFilter);
    if (i >= 0) {
      if (getFileFilter() == paramFileFilter) {
        FileFilter fileFilter1 = getAcceptAllFileFilter();
        if (isAcceptAllFileFilterUsed() && fileFilter1 != paramFileFilter) {
          setFileFilter(fileFilter1);
        } else if (i > 0) {
          setFileFilter((FileFilter)this.filters.get(0));
        } else if (this.filters.size() > 1) {
          setFileFilter((FileFilter)this.filters.get(1));
        } else {
          setFileFilter(null);
        } 
      } 
      FileFilter[] arrayOfFileFilter = getChoosableFileFilters();
      this.filters.removeElement(paramFileFilter);
      firePropertyChange("ChoosableFileFilterChangedProperty", arrayOfFileFilter, getChoosableFileFilters());
      return true;
    } 
    return false;
  }
  
  public void resetChoosableFileFilters() {
    FileFilter[] arrayOfFileFilter = getChoosableFileFilters();
    setFileFilter(null);
    this.filters.removeAllElements();
    if (isAcceptAllFileFilterUsed())
      addChoosableFileFilter(getAcceptAllFileFilter()); 
    firePropertyChange("ChoosableFileFilterChangedProperty", arrayOfFileFilter, getChoosableFileFilters());
  }
  
  public FileFilter getAcceptAllFileFilter() {
    FileFilter fileFilter1 = null;
    if (getUI() != null)
      fileFilter1 = getUI().getAcceptAllFileFilter(this); 
    return fileFilter1;
  }
  
  public boolean isAcceptAllFileFilterUsed() { return this.useAcceptAllFileFilter; }
  
  public void setAcceptAllFileFilterUsed(boolean paramBoolean) {
    boolean bool = this.useAcceptAllFileFilter;
    this.useAcceptAllFileFilter = paramBoolean;
    if (!paramBoolean) {
      removeChoosableFileFilter(getAcceptAllFileFilter());
    } else {
      removeChoosableFileFilter(getAcceptAllFileFilter());
      addChoosableFileFilter(getAcceptAllFileFilter());
    } 
    firePropertyChange("acceptAllFileFilterUsedChanged", bool, this.useAcceptAllFileFilter);
  }
  
  public JComponent getAccessory() { return this.accessory; }
  
  public void setAccessory(JComponent paramJComponent) {
    JComponent jComponent = this.accessory;
    this.accessory = paramJComponent;
    firePropertyChange("AccessoryChangedProperty", jComponent, this.accessory);
  }
  
  public void setFileSelectionMode(int paramInt) {
    if (this.fileSelectionMode == paramInt)
      return; 
    if (paramInt == 0 || paramInt == 1 || paramInt == 2) {
      int i = this.fileSelectionMode;
      this.fileSelectionMode = paramInt;
      firePropertyChange("fileSelectionChanged", i, this.fileSelectionMode);
    } else {
      throw new IllegalArgumentException("Incorrect Mode for file selection: " + paramInt);
    } 
  }
  
  public int getFileSelectionMode() { return this.fileSelectionMode; }
  
  public boolean isFileSelectionEnabled() { return (this.fileSelectionMode == 0 || this.fileSelectionMode == 2); }
  
  public boolean isDirectorySelectionEnabled() { return (this.fileSelectionMode == 1 || this.fileSelectionMode == 2); }
  
  public void setMultiSelectionEnabled(boolean paramBoolean) {
    if (this.multiSelectionEnabled == paramBoolean)
      return; 
    boolean bool = this.multiSelectionEnabled;
    this.multiSelectionEnabled = paramBoolean;
    firePropertyChange("MultiSelectionEnabledChangedProperty", bool, this.multiSelectionEnabled);
  }
  
  public boolean isMultiSelectionEnabled() { return this.multiSelectionEnabled; }
  
  public boolean isFileHidingEnabled() { return this.useFileHiding; }
  
  public void setFileHidingEnabled(boolean paramBoolean) {
    if (this.showFilesListener != null) {
      Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this.showFilesListener);
      this.showFilesListener = null;
    } 
    boolean bool = this.useFileHiding;
    this.useFileHiding = paramBoolean;
    firePropertyChange("FileHidingChanged", bool, this.useFileHiding);
  }
  
  public void setFileFilter(FileFilter paramFileFilter) {
    FileFilter fileFilter1 = this.fileFilter;
    this.fileFilter = paramFileFilter;
    if (paramFileFilter != null)
      if (isMultiSelectionEnabled() && this.selectedFiles != null && this.selectedFiles.length > 0) {
        Vector vector = new Vector();
        boolean bool = false;
        for (File file : this.selectedFiles) {
          if (paramFileFilter.accept(file)) {
            vector.add(file);
          } else {
            bool = true;
          } 
        } 
        if (bool)
          setSelectedFiles((vector.size() == 0) ? null : (File[])vector.toArray(new File[vector.size()])); 
      } else if (this.selectedFile != null && !paramFileFilter.accept(this.selectedFile)) {
        setSelectedFile(null);
      }  
    firePropertyChange("fileFilterChanged", fileFilter1, this.fileFilter);
  }
  
  public FileFilter getFileFilter() { return this.fileFilter; }
  
  public void setFileView(FileView paramFileView) {
    FileView fileView1 = this.fileView;
    this.fileView = paramFileView;
    firePropertyChange("fileViewChanged", fileView1, paramFileView);
  }
  
  public FileView getFileView() { return this.fileView; }
  
  public String getName(File paramFile) {
    String str = null;
    if (paramFile != null) {
      if (getFileView() != null)
        str = getFileView().getName(paramFile); 
      FileView fileView1 = getUI().getFileView(this);
      if (str == null && fileView1 != null)
        str = fileView1.getName(paramFile); 
    } 
    return str;
  }
  
  public String getDescription(File paramFile) {
    String str = null;
    if (paramFile != null) {
      if (getFileView() != null)
        str = getFileView().getDescription(paramFile); 
      FileView fileView1 = getUI().getFileView(this);
      if (str == null && fileView1 != null)
        str = fileView1.getDescription(paramFile); 
    } 
    return str;
  }
  
  public String getTypeDescription(File paramFile) {
    String str = null;
    if (paramFile != null) {
      if (getFileView() != null)
        str = getFileView().getTypeDescription(paramFile); 
      FileView fileView1 = getUI().getFileView(this);
      if (str == null && fileView1 != null)
        str = fileView1.getTypeDescription(paramFile); 
    } 
    return str;
  }
  
  public Icon getIcon(File paramFile) {
    Icon icon = null;
    if (paramFile != null) {
      if (getFileView() != null)
        icon = getFileView().getIcon(paramFile); 
      FileView fileView1 = getUI().getFileView(this);
      if (icon == null && fileView1 != null)
        icon = fileView1.getIcon(paramFile); 
    } 
    return icon;
  }
  
  public boolean isTraversable(File paramFile) {
    Boolean bool = null;
    if (paramFile != null) {
      if (getFileView() != null)
        bool = getFileView().isTraversable(paramFile); 
      FileView fileView1 = getUI().getFileView(this);
      if (bool == null && fileView1 != null)
        bool = fileView1.isTraversable(paramFile); 
      if (bool == null)
        bool = getFileSystemView().isTraversable(paramFile); 
    } 
    return (bool != null && bool.booleanValue());
  }
  
  public boolean accept(File paramFile) {
    boolean bool = true;
    if (paramFile != null && this.fileFilter != null)
      bool = this.fileFilter.accept(paramFile); 
    return bool;
  }
  
  public void setFileSystemView(FileSystemView paramFileSystemView) {
    FileSystemView fileSystemView1 = this.fileSystemView;
    this.fileSystemView = paramFileSystemView;
    firePropertyChange("FileSystemViewChanged", fileSystemView1, this.fileSystemView);
  }
  
  public FileSystemView getFileSystemView() { return this.fileSystemView; }
  
  public void approveSelection() {
    this.returnValue = 0;
    if (this.dialog != null)
      this.dialog.setVisible(false); 
    fireActionPerformed("ApproveSelection");
  }
  
  public void cancelSelection() {
    this.returnValue = 1;
    if (this.dialog != null)
      this.dialog.setVisible(false); 
    fireActionPerformed("CancelSelection");
  }
  
  public void addActionListener(ActionListener paramActionListener) { this.listenerList.add(ActionListener.class, paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) { this.listenerList.remove(ActionListener.class, paramActionListener); }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])this.listenerList.getListeners(ActionListener.class); }
  
  protected void fireActionPerformed(String paramString) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    long l = EventQueue.getMostRecentEventTime();
    int i = 0;
    AWTEvent aWTEvent = EventQueue.getCurrentEvent();
    if (aWTEvent instanceof InputEvent) {
      i = ((InputEvent)aWTEvent).getModifiers();
    } else if (aWTEvent instanceof ActionEvent) {
      i = ((ActionEvent)aWTEvent).getModifiers();
    } 
    ActionEvent actionEvent = null;
    for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
      if (arrayOfObject[j] == ActionListener.class) {
        if (actionEvent == null)
          actionEvent = new ActionEvent(this, 1001, paramString, l, i); 
        ((ActionListener)arrayOfObject[j + 1]).actionPerformed(actionEvent);
      } 
    } 
  }
  
  public void updateUI() {
    if (isAcceptAllFileFilterUsed())
      removeChoosableFileFilter(getAcceptAllFileFilter()); 
    FileChooserUI fileChooserUI = (FileChooserUI)UIManager.getUI(this);
    if (this.fileSystemView == null)
      setFileSystemView(FileSystemView.getFileSystemView()); 
    setUI(fileChooserUI);
    if (isAcceptAllFileFilterUsed())
      addChoosableFileFilter(getAcceptAllFileFilter()); 
  }
  
  public String getUIClassID() { return "FileChooserUI"; }
  
  public FileChooserUI getUI() { return (FileChooserUI)this.ui; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    installShowFilesListener();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    FileSystemView fileSystemView1 = null;
    if (isAcceptAllFileFilterUsed())
      removeChoosableFileFilter(getAcceptAllFileFilter()); 
    if (this.fileSystemView.equals(FileSystemView.getFileSystemView())) {
      fileSystemView1 = this.fileSystemView;
      this.fileSystemView = null;
    } 
    paramObjectOutputStream.defaultWriteObject();
    if (fileSystemView1 != null)
      this.fileSystemView = fileSystemView1; 
    if (isAcceptAllFileFilterUsed())
      addChoosableFileFilter(getAcceptAllFileFilter()); 
    if (getUIClassID().equals("FileChooserUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str6;
    String str4;
    String str3;
    String str1 = (this.approveButtonText != null) ? this.approveButtonText : "";
    String str2 = (this.dialogTitle != null) ? this.dialogTitle : "";
    if (this.dialogType == 0) {
      str3 = "OPEN_DIALOG";
    } else if (this.dialogType == 1) {
      str3 = "SAVE_DIALOG";
    } else if (this.dialogType == 2) {
      str3 = "CUSTOM_DIALOG";
    } else {
      str3 = "";
    } 
    if (this.returnValue == 1) {
      str4 = "CANCEL_OPTION";
    } else if (this.returnValue == 0) {
      str4 = "APPROVE_OPTION";
    } else if (this.returnValue == -1) {
      str4 = "ERROR_OPTION";
    } else {
      str4 = "";
    } 
    String str5 = this.useFileHiding ? "true" : "false";
    if (this.fileSelectionMode == 0) {
      str6 = "FILES_ONLY";
    } else if (this.fileSelectionMode == 1) {
      str6 = "DIRECTORIES_ONLY";
    } else if (this.fileSelectionMode == 2) {
      str6 = "FILES_AND_DIRECTORIES";
    } else {
      str6 = "";
    } 
    String str7 = (this.currentDirectory != null) ? this.currentDirectory.toString() : "";
    String str8 = (this.selectedFile != null) ? this.selectedFile.toString() : "";
    return super.paramString() + ",approveButtonText=" + str1 + ",currentDirectory=" + str7 + ",dialogTitle=" + str2 + ",dialogType=" + str3 + ",fileSelectionMode=" + str6 + ",returnValue=" + str4 + ",selectedFile=" + str8 + ",useFileHiding=" + str5;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJFileChooser(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJFileChooser extends JComponent.AccessibleJComponent {
    protected AccessibleJFileChooser() { super(JFileChooser.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.FILE_CHOOSER; }
  }
  
  private static class WeakPCL implements PropertyChangeListener {
    WeakReference<JFileChooser> jfcRef;
    
    public WeakPCL(JFileChooser param1JFileChooser) { this.jfcRef = new WeakReference(param1JFileChooser); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      assert param1PropertyChangeEvent.getPropertyName().equals("awt.file.showHiddenFiles");
      JFileChooser jFileChooser = (JFileChooser)this.jfcRef.get();
      if (jFileChooser == null) {
        Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this);
      } else {
        boolean bool = jFileChooser.useFileHiding;
        jFileChooser.useFileHiding = !((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue();
        jFileChooser.firePropertyChange("FileHidingChanged", bool, jFileChooser.useFileHiding);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JFileChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */