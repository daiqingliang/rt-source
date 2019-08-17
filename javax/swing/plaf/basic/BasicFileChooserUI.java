package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.UIResource;
import sun.awt.shell.ShellFolder;
import sun.swing.DefaultLookup;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicFileChooserUI extends FileChooserUI {
  protected Icon directoryIcon = null;
  
  protected Icon fileIcon = null;
  
  protected Icon computerIcon = null;
  
  protected Icon hardDriveIcon = null;
  
  protected Icon floppyDriveIcon = null;
  
  protected Icon newFolderIcon = null;
  
  protected Icon upFolderIcon = null;
  
  protected Icon homeFolderIcon = null;
  
  protected Icon listViewIcon = null;
  
  protected Icon detailsViewIcon = null;
  
  protected Icon viewMenuIcon = null;
  
  protected int saveButtonMnemonic = 0;
  
  protected int openButtonMnemonic = 0;
  
  protected int cancelButtonMnemonic = 0;
  
  protected int updateButtonMnemonic = 0;
  
  protected int helpButtonMnemonic = 0;
  
  protected int directoryOpenButtonMnemonic = 0;
  
  protected String saveButtonText = null;
  
  protected String openButtonText = null;
  
  protected String cancelButtonText = null;
  
  protected String updateButtonText = null;
  
  protected String helpButtonText = null;
  
  protected String directoryOpenButtonText = null;
  
  private String openDialogTitleText = null;
  
  private String saveDialogTitleText = null;
  
  protected String saveButtonToolTipText = null;
  
  protected String openButtonToolTipText = null;
  
  protected String cancelButtonToolTipText = null;
  
  protected String updateButtonToolTipText = null;
  
  protected String helpButtonToolTipText = null;
  
  protected String directoryOpenButtonToolTipText = null;
  
  private Action approveSelectionAction = new ApproveSelectionAction();
  
  private Action cancelSelectionAction = new CancelSelectionAction();
  
  private Action updateAction = new UpdateAction();
  
  private Action newFolderAction;
  
  private Action goHomeAction = new GoHomeAction();
  
  private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();
  
  private String newFolderErrorSeparator = null;
  
  private String newFolderErrorText = null;
  
  private String newFolderParentDoesntExistTitleText = null;
  
  private String newFolderParentDoesntExistText = null;
  
  private String fileDescriptionText = null;
  
  private String directoryDescriptionText = null;
  
  private JFileChooser filechooser = null;
  
  private boolean directorySelected = false;
  
  private File directory = null;
  
  private PropertyChangeListener propertyChangeListener = null;
  
  private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();
  
  private FileFilter actualFileFilter = null;
  
  private GlobFilter globFilter = null;
  
  private BasicDirectoryModel model = null;
  
  private BasicFileView fileView = new BasicFileView();
  
  private boolean usesSingleFilePane;
  
  private boolean readOnly;
  
  private JPanel accessoryPanel = null;
  
  private Handler handler;
  
  private static final TransferHandler defaultTransferHandler = new FileTransferHandler();
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicFileChooserUI((JFileChooser)paramJComponent); }
  
  public BasicFileChooserUI(JFileChooser paramJFileChooser) {}
  
  public void installUI(JComponent paramJComponent) {
    this.accessoryPanel = new JPanel(new BorderLayout());
    this.filechooser = (JFileChooser)paramJComponent;
    createModel();
    clearIconCache();
    installDefaults(this.filechooser);
    installComponents(this.filechooser);
    installListeners(this.filechooser);
    this.filechooser.applyComponentOrientation(this.filechooser.getComponentOrientation());
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallListeners(this.filechooser);
    uninstallComponents(this.filechooser);
    uninstallDefaults(this.filechooser);
    if (this.accessoryPanel != null)
      this.accessoryPanel.removeAll(); 
    this.accessoryPanel = null;
    getFileChooser().removeAll();
    this.handler = null;
  }
  
  public void installComponents(JFileChooser paramJFileChooser) {}
  
  public void uninstallComponents(JFileChooser paramJFileChooser) {}
  
  protected void installListeners(JFileChooser paramJFileChooser) {
    this.propertyChangeListener = createPropertyChangeListener(paramJFileChooser);
    if (this.propertyChangeListener != null)
      paramJFileChooser.addPropertyChangeListener(this.propertyChangeListener); 
    paramJFileChooser.addPropertyChangeListener(getModel());
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(paramJFileChooser, 1, inputMap);
    ActionMap actionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(paramJFileChooser, actionMap);
  }
  
  InputMap getInputMap(int paramInt) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(getFileChooser(), this, "FileChooser.ancestorInputMap") : null; }
  
  ActionMap getActionMap() { return createActionMap(); }
  
  ActionMap createActionMap() {
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    UIAction uIAction = new UIAction("refresh") {
        public void actionPerformed(ActionEvent param1ActionEvent) { BasicFileChooserUI.this.getFileChooser().rescanCurrentDirectory(); }
      };
    actionMapUIResource.put("approveSelection", getApproveSelectionAction());
    actionMapUIResource.put("cancelSelection", getCancelSelectionAction());
    actionMapUIResource.put("refresh", uIAction);
    actionMapUIResource.put("Go Up", getChangeToParentDirectoryAction());
    return actionMapUIResource;
  }
  
  protected void uninstallListeners(JFileChooser paramJFileChooser) {
    if (this.propertyChangeListener != null)
      paramJFileChooser.removePropertyChangeListener(this.propertyChangeListener); 
    paramJFileChooser.removePropertyChangeListener(getModel());
    SwingUtilities.replaceUIInputMap(paramJFileChooser, 1, null);
    SwingUtilities.replaceUIActionMap(paramJFileChooser, null);
  }
  
  protected void installDefaults(JFileChooser paramJFileChooser) {
    installIcons(paramJFileChooser);
    installStrings(paramJFileChooser);
    this.usesSingleFilePane = UIManager.getBoolean("FileChooser.usesSingleFilePane");
    this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
    TransferHandler transferHandler = paramJFileChooser.getTransferHandler();
    if (transferHandler == null || transferHandler instanceof UIResource)
      paramJFileChooser.setTransferHandler(defaultTransferHandler); 
    LookAndFeel.installProperty(paramJFileChooser, "opaque", Boolean.FALSE);
  }
  
  protected void installIcons(JFileChooser paramJFileChooser) {
    this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    this.fileIcon = UIManager.getIcon("FileView.fileIcon");
    this.computerIcon = UIManager.getIcon("FileView.computerIcon");
    this.hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
    this.floppyDriveIcon = UIManager.getIcon("FileView.floppyDriveIcon");
    this.newFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
    this.upFolderIcon = UIManager.getIcon("FileChooser.upFolderIcon");
    this.homeFolderIcon = UIManager.getIcon("FileChooser.homeFolderIcon");
    this.detailsViewIcon = UIManager.getIcon("FileChooser.detailsViewIcon");
    this.listViewIcon = UIManager.getIcon("FileChooser.listViewIcon");
    this.viewMenuIcon = UIManager.getIcon("FileChooser.viewMenuIcon");
  }
  
  protected void installStrings(JFileChooser paramJFileChooser) {
    Locale locale = paramJFileChooser.getLocale();
    this.newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", locale);
    this.newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", locale);
    this.newFolderParentDoesntExistTitleText = UIManager.getString("FileChooser.newFolderParentDoesntExistTitleText", locale);
    this.newFolderParentDoesntExistText = UIManager.getString("FileChooser.newFolderParentDoesntExistText", locale);
    this.fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText", locale);
    this.directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText", locale);
    this.saveButtonText = UIManager.getString("FileChooser.saveButtonText", locale);
    this.openButtonText = UIManager.getString("FileChooser.openButtonText", locale);
    this.saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText", locale);
    this.openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText", locale);
    this.cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", locale);
    this.updateButtonText = UIManager.getString("FileChooser.updateButtonText", locale);
    this.helpButtonText = UIManager.getString("FileChooser.helpButtonText", locale);
    this.directoryOpenButtonText = UIManager.getString("FileChooser.directoryOpenButtonText", locale);
    this.saveButtonMnemonic = getMnemonic("FileChooser.saveButtonMnemonic", locale);
    this.openButtonMnemonic = getMnemonic("FileChooser.openButtonMnemonic", locale);
    this.cancelButtonMnemonic = getMnemonic("FileChooser.cancelButtonMnemonic", locale);
    this.updateButtonMnemonic = getMnemonic("FileChooser.updateButtonMnemonic", locale);
    this.helpButtonMnemonic = getMnemonic("FileChooser.helpButtonMnemonic", locale);
    this.directoryOpenButtonMnemonic = getMnemonic("FileChooser.directoryOpenButtonMnemonic", locale);
    this.saveButtonToolTipText = UIManager.getString("FileChooser.saveButtonToolTipText", locale);
    this.openButtonToolTipText = UIManager.getString("FileChooser.openButtonToolTipText", locale);
    this.cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText", locale);
    this.updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText", locale);
    this.helpButtonToolTipText = UIManager.getString("FileChooser.helpButtonToolTipText", locale);
    this.directoryOpenButtonToolTipText = UIManager.getString("FileChooser.directoryOpenButtonToolTipText", locale);
  }
  
  protected void uninstallDefaults(JFileChooser paramJFileChooser) {
    uninstallIcons(paramJFileChooser);
    uninstallStrings(paramJFileChooser);
    if (paramJFileChooser.getTransferHandler() instanceof UIResource)
      paramJFileChooser.setTransferHandler(null); 
  }
  
  protected void uninstallIcons(JFileChooser paramJFileChooser) {
    this.directoryIcon = null;
    this.fileIcon = null;
    this.computerIcon = null;
    this.hardDriveIcon = null;
    this.floppyDriveIcon = null;
    this.newFolderIcon = null;
    this.upFolderIcon = null;
    this.homeFolderIcon = null;
    this.detailsViewIcon = null;
    this.listViewIcon = null;
    this.viewMenuIcon = null;
  }
  
  protected void uninstallStrings(JFileChooser paramJFileChooser) {
    this.saveButtonText = null;
    this.openButtonText = null;
    this.cancelButtonText = null;
    this.updateButtonText = null;
    this.helpButtonText = null;
    this.directoryOpenButtonText = null;
    this.saveButtonToolTipText = null;
    this.openButtonToolTipText = null;
    this.cancelButtonToolTipText = null;
    this.updateButtonToolTipText = null;
    this.helpButtonToolTipText = null;
    this.directoryOpenButtonToolTipText = null;
  }
  
  protected void createModel() {
    if (this.model != null)
      this.model.invalidateFileCache(); 
    this.model = new BasicDirectoryModel(getFileChooser());
  }
  
  public BasicDirectoryModel getModel() { return this.model; }
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser) { return null; }
  
  public String getFileName() { return null; }
  
  public String getDirectoryName() { return null; }
  
  public void setFileName(String paramString) {}
  
  public void setDirectoryName(String paramString) {}
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {}
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {}
  
  public JFileChooser getFileChooser() { return this.filechooser; }
  
  public JPanel getAccessoryPanel() { return this.accessoryPanel; }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser) { return null; }
  
  public JButton getDefaultButton(JFileChooser paramJFileChooser) { return getApproveButton(paramJFileChooser); }
  
  public String getApproveButtonToolTipText(JFileChooser paramJFileChooser) {
    String str = paramJFileChooser.getApproveButtonToolTipText();
    return (str != null) ? str : ((paramJFileChooser.getDialogType() == 0) ? this.openButtonToolTipText : ((paramJFileChooser.getDialogType() == 1) ? this.saveButtonToolTipText : null));
  }
  
  public void clearIconCache() { this.fileView.clearIconCache(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(); 
    return this.handler;
  }
  
  protected MouseListener createDoubleClickListener(JFileChooser paramJFileChooser, JList paramJList) { return new Handler(paramJList); }
  
  public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser) { return getHandler(); }
  
  protected boolean isDirectorySelected() { return this.directorySelected; }
  
  protected void setDirectorySelected(boolean paramBoolean) { this.directorySelected = paramBoolean; }
  
  protected File getDirectory() { return this.directory; }
  
  protected void setDirectory(File paramFile) { this.directory = paramFile; }
  
  private int getMnemonic(String paramString, Locale paramLocale) { return SwingUtilities2.getUIDefaultsInt(paramString, paramLocale); }
  
  public FileFilter getAcceptAllFileFilter(JFileChooser paramJFileChooser) { return this.acceptAllFileFilter; }
  
  public FileView getFileView(JFileChooser paramJFileChooser) { return this.fileView; }
  
  public String getDialogTitle(JFileChooser paramJFileChooser) {
    String str = paramJFileChooser.getDialogTitle();
    return (str != null) ? str : ((paramJFileChooser.getDialogType() == 0) ? this.openDialogTitleText : ((paramJFileChooser.getDialogType() == 1) ? this.saveDialogTitleText : getApproveButtonText(paramJFileChooser)));
  }
  
  public int getApproveButtonMnemonic(JFileChooser paramJFileChooser) {
    int i = paramJFileChooser.getApproveButtonMnemonic();
    return (i > 0) ? i : ((paramJFileChooser.getDialogType() == 0) ? this.openButtonMnemonic : ((paramJFileChooser.getDialogType() == 1) ? this.saveButtonMnemonic : i));
  }
  
  public String getApproveButtonText(JFileChooser paramJFileChooser) {
    String str = paramJFileChooser.getApproveButtonText();
    return (str != null) ? str : ((paramJFileChooser.getDialogType() == 0) ? this.openButtonText : ((paramJFileChooser.getDialogType() == 1) ? this.saveButtonText : null));
  }
  
  public Action getNewFolderAction() {
    if (this.newFolderAction == null) {
      this.newFolderAction = new NewFolderAction();
      if (this.readOnly)
        this.newFolderAction.setEnabled(false); 
    } 
    return this.newFolderAction;
  }
  
  public Action getGoHomeAction() { return this.goHomeAction; }
  
  public Action getChangeToParentDirectoryAction() { return this.changeToParentDirectoryAction; }
  
  public Action getApproveSelectionAction() { return this.approveSelectionAction; }
  
  public Action getCancelSelectionAction() { return this.cancelSelectionAction; }
  
  public Action getUpdateAction() { return this.updateAction; }
  
  private void resetGlobFilter() {
    if (this.actualFileFilter != null) {
      JFileChooser jFileChooser = getFileChooser();
      FileFilter fileFilter = jFileChooser.getFileFilter();
      if (fileFilter != null && fileFilter.equals(this.globFilter)) {
        jFileChooser.setFileFilter(this.actualFileFilter);
        jFileChooser.removeChoosableFileFilter(this.globFilter);
      } 
      this.actualFileFilter = null;
    } 
  }
  
  private static boolean isGlobPattern(String paramString) { return ((File.separatorChar == '\\' && (paramString.indexOf('*') >= 0 || paramString.indexOf('?') >= 0)) || (File.separatorChar == '/' && (paramString.indexOf('*') >= 0 || paramString.indexOf('?') >= 0 || paramString.indexOf('[') >= 0))); }
  
  private void changeDirectory(File paramFile) {
    JFileChooser jFileChooser = getFileChooser();
    if (paramFile != null && FilePane.usesShellFolder(jFileChooser))
      try {
        ShellFolder shellFolder = ShellFolder.getShellFolder(paramFile);
        if (shellFolder.isLink()) {
          ShellFolder shellFolder1 = shellFolder.getLinkLocation();
          if (shellFolder1 != null) {
            if (jFileChooser.isTraversable(shellFolder1)) {
              paramFile = shellFolder1;
            } else {
              return;
            } 
          } else {
            paramFile = shellFolder;
          } 
        } 
      } catch (FileNotFoundException fileNotFoundException) {
        return;
      }  
    jFileChooser.setCurrentDirectory(paramFile);
    if (jFileChooser.getFileSelectionMode() == 2 && jFileChooser.getFileSystemView().isFileSystem(paramFile))
      setFileName(paramFile.getAbsolutePath()); 
  }
  
  protected class AcceptAllFileFilter extends FileFilter {
    public boolean accept(File param1File) { return true; }
    
    public String getDescription() { return UIManager.getString("FileChooser.acceptAllFileFilterText"); }
  }
  
  protected class ApproveSelectionAction extends AbstractAction {
    protected ApproveSelectionAction() { super("approveSelection"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicFileChooserUI.this.isDirectorySelected()) {
        File file = BasicFileChooserUI.this.getDirectory();
        if (file != null) {
          try {
            file = ShellFolder.getNormalizedFile(file);
          } catch (IOException iOException) {}
          BasicFileChooserUI.this.changeDirectory(file);
          return;
        } 
      } 
      JFileChooser jFileChooser = BasicFileChooserUI.this.getFileChooser();
      String str = BasicFileChooserUI.this.getFileName();
      FileSystemView fileSystemView = jFileChooser.getFileSystemView();
      File file1 = jFileChooser.getCurrentDirectory();
      if (str != null) {
        int i;
        for (i = str.length() - 1; i >= 0 && str.charAt(i) <= ' '; i--);
        str = str.substring(0, i + 1);
      } 
      if (str == null || str.length() == 0) {
        BasicFileChooserUI.this.resetGlobFilter();
        return;
      } 
      File file2 = null;
      File[] arrayOfFile = null;
      if (File.separatorChar == '/')
        if (str.startsWith("~/")) {
          str = System.getProperty("user.home") + str.substring(1);
        } else if (str.equals("~")) {
          str = System.getProperty("user.home");
        }  
      if (jFileChooser.isMultiSelectionEnabled() && str.length() > 1 && str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
        ArrayList arrayList = new ArrayList();
        String[] arrayOfString = str.substring(1, str.length() - 1).split("\" \"");
        Arrays.sort(arrayOfString);
        File[] arrayOfFile1 = null;
        int i = 0;
        for (String str1 : arrayOfString) {
          File file = fileSystemView.createFileObject(str1);
          if (!file.isAbsolute()) {
            if (arrayOfFile1 == null) {
              arrayOfFile1 = fileSystemView.getFiles(file1, false);
              Arrays.sort(arrayOfFile1);
            } 
            for (byte b = 0; b < arrayOfFile1.length; b++) {
              int j = (i + b) % arrayOfFile1.length;
              if (arrayOfFile1[j].getName().equals(str1)) {
                file = arrayOfFile1[j];
                i = j + 1;
                break;
              } 
            } 
          } 
          arrayList.add(file);
        } 
        if (!arrayList.isEmpty())
          arrayOfFile = (File[])arrayList.toArray(new File[arrayList.size()]); 
        BasicFileChooserUI.this.resetGlobFilter();
      } else {
        file2 = fileSystemView.createFileObject(str);
        if (!file2.isAbsolute())
          file2 = fileSystemView.getChild(file1, str); 
        FileFilter fileFilter = jFileChooser.getFileFilter();
        if (!file2.exists() && BasicFileChooserUI.isGlobPattern(str)) {
          BasicFileChooserUI.this.changeDirectory(file2.getParentFile());
          if (BasicFileChooserUI.this.globFilter == null)
            BasicFileChooserUI.this.globFilter = new BasicFileChooserUI.GlobFilter(BasicFileChooserUI.this); 
          try {
            BasicFileChooserUI.this.globFilter.setPattern(file2.getName());
            if (!(fileFilter instanceof BasicFileChooserUI.GlobFilter))
              BasicFileChooserUI.this.actualFileFilter = fileFilter; 
            jFileChooser.setFileFilter(null);
            jFileChooser.setFileFilter(BasicFileChooserUI.this.globFilter);
            return;
          } catch (PatternSyntaxException patternSyntaxException) {}
        } 
        BasicFileChooserUI.this.resetGlobFilter();
        boolean bool1 = (file2 != null && file2.isDirectory()) ? 1 : 0;
        boolean bool2 = (file2 != null && jFileChooser.isTraversable(file2)) ? 1 : 0;
        boolean bool3 = jFileChooser.isDirectorySelectionEnabled();
        boolean bool4 = jFileChooser.isFileSelectionEnabled();
        boolean bool5 = (param1ActionEvent != null && (param1ActionEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0) ? 1 : 0;
        if (bool1 && bool2 && (bool5 || !bool3)) {
          BasicFileChooserUI.this.changeDirectory(file2);
          return;
        } 
        if ((bool1 || !bool4) && (!bool1 || !bool3) && (!bool3 || file2.exists()))
          file2 = null; 
      } 
      if (arrayOfFile != null || file2 != null) {
        if (arrayOfFile != null || jFileChooser.isMultiSelectionEnabled()) {
          if (arrayOfFile == null)
            arrayOfFile = new File[] { file2 }; 
          jFileChooser.setSelectedFiles(arrayOfFile);
          jFileChooser.setSelectedFiles(arrayOfFile);
        } else {
          jFileChooser.setSelectedFile(file2);
        } 
        jFileChooser.approveSelection();
      } else {
        if (jFileChooser.isMultiSelectionEnabled()) {
          jFileChooser.setSelectedFiles(null);
        } else {
          jFileChooser.setSelectedFile(null);
        } 
        jFileChooser.cancelSelection();
      } 
    }
  }
  
  protected class BasicFileView extends FileView {
    protected Hashtable<File, Icon> iconCache = new Hashtable();
    
    public void clearIconCache() { this.iconCache = new Hashtable(); }
    
    public String getName(File param1File) {
      String str = null;
      if (param1File != null)
        str = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemDisplayName(param1File); 
      return str;
    }
    
    public String getDescription(File param1File) { return param1File.getName(); }
    
    public String getTypeDescription(File param1File) {
      String str = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemTypeDescription(param1File);
      if (str == null)
        if (param1File.isDirectory()) {
          str = BasicFileChooserUI.this.directoryDescriptionText;
        } else {
          str = BasicFileChooserUI.this.fileDescriptionText;
        }  
      return str;
    }
    
    public Icon getCachedIcon(File param1File) { return (Icon)this.iconCache.get(param1File); }
    
    public void cacheIcon(File param1File, Icon param1Icon) {
      if (param1File == null || param1Icon == null)
        return; 
      this.iconCache.put(param1File, param1Icon);
    }
    
    public Icon getIcon(File param1File) {
      Icon icon = getCachedIcon(param1File);
      if (icon != null)
        return icon; 
      icon = BasicFileChooserUI.this.fileIcon;
      if (param1File != null) {
        FileSystemView fileSystemView = BasicFileChooserUI.this.getFileChooser().getFileSystemView();
        if (fileSystemView.isFloppyDrive(param1File)) {
          icon = BasicFileChooserUI.this.floppyDriveIcon;
        } else if (fileSystemView.isDrive(param1File)) {
          icon = BasicFileChooserUI.this.hardDriveIcon;
        } else if (fileSystemView.isComputerNode(param1File)) {
          icon = BasicFileChooserUI.this.computerIcon;
        } else if (param1File.isDirectory()) {
          icon = BasicFileChooserUI.this.directoryIcon;
        } 
      } 
      cacheIcon(param1File, icon);
      return icon;
    }
    
    public Boolean isHidden(File param1File) {
      String str = param1File.getName();
      return (str != null && str.charAt(0) == '.') ? Boolean.TRUE : Boolean.FALSE;
    }
  }
  
  protected class CancelSelectionAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) { BasicFileChooserUI.this.getFileChooser().cancelSelection(); }
  }
  
  protected class ChangeToParentDirectoryAction extends AbstractAction {
    protected ChangeToParentDirectoryAction() {
      super("Go Up");
      putValue("ActionCommandKey", "Go Up");
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { BasicFileChooserUI.this.getFileChooser().changeToParentDirectory(); }
  }
  
  protected class DoubleClickListener extends MouseAdapter {
    BasicFileChooserUI.Handler handler;
    
    public DoubleClickListener(JList param1JList) { this.handler = new BasicFileChooserUI.Handler(this$0, param1JList); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { this.handler.mouseEntered(param1MouseEvent); }
    
    public void mouseClicked(MouseEvent param1MouseEvent) { this.handler.mouseClicked(param1MouseEvent); }
  }
  
  static class FileTransferHandler extends TransferHandler implements UIResource {
    protected Transferable createTransferable(JComponent param1JComponent) {
      Object[] arrayOfObject = null;
      if (param1JComponent instanceof JList) {
        arrayOfObject = ((JList)param1JComponent).getSelectedValues();
      } else if (param1JComponent instanceof JTable) {
        JTable jTable = (JTable)param1JComponent;
        int[] arrayOfInt = jTable.getSelectedRows();
        if (arrayOfInt != null) {
          arrayOfObject = new Object[arrayOfInt.length];
          for (byte b = 0; b < arrayOfInt.length; b++)
            arrayOfObject[b] = jTable.getValueAt(arrayOfInt[b], 0); 
        } 
      } 
      if (arrayOfObject == null || arrayOfObject.length == 0)
        return null; 
      StringBuffer stringBuffer1 = new StringBuffer();
      StringBuffer stringBuffer2 = new StringBuffer();
      stringBuffer2.append("<html>\n<body>\n<ul>\n");
      for (Object object : arrayOfObject) {
        String str = (object == null) ? "" : object.toString();
        stringBuffer1.append(str + "\n");
        stringBuffer2.append("  <li>" + str + "\n");
      } 
      stringBuffer1.deleteCharAt(stringBuffer1.length() - 1);
      stringBuffer2.append("</ul>\n</body>\n</html>");
      return new FileTransferable(stringBuffer1.toString(), stringBuffer2.toString(), arrayOfObject);
    }
    
    public int getSourceActions(JComponent param1JComponent) { return 1; }
    
    static class FileTransferable extends BasicTransferable {
      Object[] fileData;
      
      FileTransferable(String param2String1, String param2String2, Object[] param2ArrayOfObject) {
        super(param2String1, param2String2);
        this.fileData = param2ArrayOfObject;
      }
      
      protected DataFlavor[] getRicherFlavors() {
        DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
        arrayOfDataFlavor[0] = DataFlavor.javaFileListFlavor;
        return arrayOfDataFlavor;
      }
      
      protected Object getRicherData(DataFlavor param2DataFlavor) {
        if (DataFlavor.javaFileListFlavor.equals(param2DataFlavor)) {
          ArrayList arrayList = new ArrayList();
          for (Object object : this.fileData)
            arrayList.add(object); 
          return arrayList;
        } 
        return null;
      }
    }
  }
  
  class GlobFilter extends FileFilter {
    Pattern pattern;
    
    String globPattern;
    
    public void setPattern(String param1String) {
      char[] arrayOfChar1 = param1String.toCharArray();
      char[] arrayOfChar2 = new char[arrayOfChar1.length * 2];
      boolean bool1 = (File.separatorChar == '\\') ? 1 : 0;
      boolean bool2 = false;
      byte b = 0;
      this.globPattern = param1String;
      if (bool1) {
        int i = arrayOfChar1.length;
        if (param1String.endsWith("*.*"))
          i -= 2; 
        for (byte b1 = 0; b1 < i; b1++) {
          switch (arrayOfChar1[b1]) {
            case '*':
              arrayOfChar2[b++] = '.';
              arrayOfChar2[b++] = '*';
              break;
            case '?':
              arrayOfChar2[b++] = '.';
              break;
            case '\\':
              arrayOfChar2[b++] = '\\';
              arrayOfChar2[b++] = '\\';
              break;
            default:
              if ("+()^$.{}[]".indexOf(arrayOfChar1[b1]) >= 0)
                arrayOfChar2[b++] = '\\'; 
              arrayOfChar2[b++] = arrayOfChar1[b1];
              break;
          } 
        } 
      } else {
        for (byte b1 = 0; b1 < arrayOfChar1.length; b1++) {
          switch (arrayOfChar1[b1]) {
            case '*':
              if (!bool2)
                arrayOfChar2[b++] = '.'; 
              arrayOfChar2[b++] = '*';
              break;
            case '?':
              arrayOfChar2[b++] = bool2 ? '?' : '.';
              break;
            case '[':
              bool2 = true;
              arrayOfChar2[b++] = arrayOfChar1[b1];
              if (b1 < arrayOfChar1.length - 1)
                switch (arrayOfChar1[b1 + true]) {
                  case '!':
                  case '^':
                    arrayOfChar2[b++] = '^';
                    b1++;
                    break;
                  case ']':
                    arrayOfChar2[b++] = arrayOfChar1[++b1];
                    break;
                }  
              break;
            case ']':
              arrayOfChar2[b++] = arrayOfChar1[b1];
              bool2 = false;
              break;
            case '\\':
              if (b1 == 0 && arrayOfChar1.length > 1 && arrayOfChar1[1] == '~') {
                arrayOfChar2[b++] = arrayOfChar1[++b1];
                break;
              } 
              arrayOfChar2[b++] = '\\';
              if (b1 < arrayOfChar1.length - 1 && "*?[]".indexOf(arrayOfChar1[b1 + 1]) >= 0) {
                arrayOfChar2[b++] = arrayOfChar1[++b1];
                break;
              } 
              arrayOfChar2[b++] = '\\';
              break;
            default:
              if (!Character.isLetterOrDigit(arrayOfChar1[b1]))
                arrayOfChar2[b++] = '\\'; 
              arrayOfChar2[b++] = arrayOfChar1[b1];
              break;
          } 
        } 
      } 
      this.pattern = Pattern.compile(new String(arrayOfChar2, 0, b), 2);
    }
    
    public boolean accept(File param1File) { return (param1File == null) ? false : (param1File.isDirectory() ? true : this.pattern.matcher(param1File.getName()).matches()); }
    
    public String getDescription() { return this.globPattern; }
  }
  
  protected class GoHomeAction extends AbstractAction {
    protected GoHomeAction() { super("Go Home"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JFileChooser jFileChooser = BasicFileChooserUI.this.getFileChooser();
      BasicFileChooserUI.this.changeDirectory(jFileChooser.getFileSystemView().getHomeDirectory());
    }
  }
  
  private class Handler implements MouseListener, ListSelectionListener {
    JList list;
    
    Handler() {}
    
    Handler(JList param1JList) { this.list = param1JList; }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      if (this.list != null && SwingUtilities.isLeftMouseButton(param1MouseEvent) && param1MouseEvent.getClickCount() % 2 == 0) {
        int i = SwingUtilities2.loc2IndexFileList(this.list, param1MouseEvent.getPoint());
        if (i >= 0) {
          File file = (File)this.list.getModel().getElementAt(i);
          try {
            file = ShellFolder.getNormalizedFile(file);
          } catch (IOException iOException) {}
          if (BasicFileChooserUI.this.getFileChooser().isTraversable(file)) {
            this.list.clearSelection();
            BasicFileChooserUI.this.changeDirectory(file);
          } else {
            BasicFileChooserUI.this.getFileChooser().approveSelection();
          } 
        } 
      } 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      if (this.list != null) {
        TransferHandler transferHandler1 = BasicFileChooserUI.this.getFileChooser().getTransferHandler();
        TransferHandler transferHandler2 = this.list.getTransferHandler();
        if (transferHandler1 != transferHandler2)
          this.list.setTransferHandler(transferHandler1); 
        if (BasicFileChooserUI.this.getFileChooser().getDragEnabled() != this.list.getDragEnabled())
          this.list.setDragEnabled(BasicFileChooserUI.this.getFileChooser().getDragEnabled()); 
      } 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {}
    
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
      if (!param1ListSelectionEvent.getValueIsAdjusting()) {
        JFileChooser jFileChooser = BasicFileChooserUI.this.getFileChooser();
        FileSystemView fileSystemView = jFileChooser.getFileSystemView();
        JList jList = (JList)param1ListSelectionEvent.getSource();
        int i = jFileChooser.getFileSelectionMode();
        boolean bool = (BasicFileChooserUI.this.usesSingleFilePane && i == 0) ? 1 : 0;
        if (jFileChooser.isMultiSelectionEnabled()) {
          File[] arrayOfFile = null;
          Object[] arrayOfObject = jList.getSelectedValues();
          if (arrayOfObject != null)
            if (arrayOfObject.length == 1 && ((File)arrayOfObject[0]).isDirectory() && jFileChooser.isTraversable((File)arrayOfObject[0]) && (bool || !fileSystemView.isFileSystem((File)arrayOfObject[0]))) {
              BasicFileChooserUI.this.setDirectorySelected(true);
              BasicFileChooserUI.this.setDirectory((File)arrayOfObject[0]);
            } else {
              ArrayList arrayList = new ArrayList(arrayOfObject.length);
              for (Object object : arrayOfObject) {
                File file = (File)object;
                boolean bool1 = file.isDirectory();
                if ((jFileChooser.isFileSelectionEnabled() && !bool1) || (jFileChooser.isDirectorySelectionEnabled() && fileSystemView.isFileSystem(file) && bool1))
                  arrayList.add(file); 
              } 
              if (arrayList.size() > 0)
                arrayOfFile = (File[])arrayList.toArray(new File[arrayList.size()]); 
              BasicFileChooserUI.this.setDirectorySelected(false);
            }  
          jFileChooser.setSelectedFiles(arrayOfFile);
        } else {
          File file = (File)jList.getSelectedValue();
          if (file != null && file.isDirectory() && jFileChooser.isTraversable(file) && (bool || !fileSystemView.isFileSystem(file))) {
            BasicFileChooserUI.this.setDirectorySelected(true);
            BasicFileChooserUI.this.setDirectory(file);
            if (BasicFileChooserUI.this.usesSingleFilePane)
              jFileChooser.setSelectedFile(null); 
          } else {
            BasicFileChooserUI.this.setDirectorySelected(false);
            if (file != null)
              jFileChooser.setSelectedFile(file); 
          } 
        } 
      } 
    }
  }
  
  protected class NewFolderAction extends AbstractAction {
    protected NewFolderAction() { super("New Folder"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicFileChooserUI.this.readOnly)
        return; 
      JFileChooser jFileChooser = BasicFileChooserUI.this.getFileChooser();
      File file = jFileChooser.getCurrentDirectory();
      if (!file.exists()) {
        JOptionPane.showMessageDialog(jFileChooser, BasicFileChooserUI.this.newFolderParentDoesntExistText, BasicFileChooserUI.this.newFolderParentDoesntExistTitleText, 2);
        return;
      } 
      try {
        File file1 = jFileChooser.getFileSystemView().createNewFolder(file);
        if (jFileChooser.isMultiSelectionEnabled()) {
          jFileChooser.setSelectedFiles(new File[] { file1 });
        } else {
          jFileChooser.setSelectedFile(file1);
        } 
      } catch (IOException iOException) {
        JOptionPane.showMessageDialog(jFileChooser, BasicFileChooserUI.this.newFolderErrorText + BasicFileChooserUI.this.newFolderErrorSeparator + iOException, BasicFileChooserUI.this.newFolderErrorText, 0);
        return;
      } 
      jFileChooser.rescanCurrentDirectory();
    }
  }
  
  protected class SelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) { BasicFileChooserUI.this.getHandler().valueChanged(param1ListSelectionEvent); }
  }
  
  protected class UpdateAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JFileChooser jFileChooser = BasicFileChooserUI.this.getFileChooser();
      jFileChooser.setCurrentDirectory(jFileChooser.getFileSystemView().createFileObject(BasicFileChooserUI.this.getDirectoryName()));
      jFileChooser.rescanCurrentDirectory();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */