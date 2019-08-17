package javax.swing.filechooser;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import sun.awt.shell.ShellFolder;

public abstract class FileSystemView {
  static FileSystemView windowsFileSystemView = null;
  
  static FileSystemView unixFileSystemView = null;
  
  static FileSystemView genericFileSystemView = null;
  
  private boolean useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
  
  public static FileSystemView getFileSystemView() {
    if (File.separatorChar == '\\') {
      if (windowsFileSystemView == null)
        windowsFileSystemView = new WindowsFileSystemView(); 
      return windowsFileSystemView;
    } 
    if (File.separatorChar == '/') {
      if (unixFileSystemView == null)
        unixFileSystemView = new UnixFileSystemView(); 
      return unixFileSystemView;
    } 
    if (genericFileSystemView == null)
      genericFileSystemView = new GenericFileSystemView(); 
    return genericFileSystemView;
  }
  
  public FileSystemView() {
    final WeakReference weakReference = new WeakReference(this);
    UIManager.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            FileSystemView fileSystemView = (FileSystemView)weakReference.get();
            if (fileSystemView == null) {
              UIManager.removePropertyChangeListener(this);
            } else if (param1PropertyChangeEvent.getPropertyName().equals("lookAndFeel")) {
              fileSystemView.useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
            } 
          }
        });
  }
  
  public boolean isRoot(File paramFile) {
    if (paramFile == null || !paramFile.isAbsolute())
      return false; 
    File[] arrayOfFile = getRoots();
    for (File file : arrayOfFile) {
      if (file.equals(paramFile))
        return true; 
    } 
    return false;
  }
  
  public Boolean isTraversable(File paramFile) { return Boolean.valueOf(paramFile.isDirectory()); }
  
  public String getSystemDisplayName(File paramFile) {
    if (paramFile == null)
      return null; 
    String str = paramFile.getName();
    if (!str.equals("..") && !str.equals(".") && (this.useSystemExtensionHiding || !isFileSystem(paramFile) || isFileSystemRoot(paramFile)) && (paramFile instanceof ShellFolder || paramFile.exists())) {
      try {
        str = getShellFolder(paramFile).getDisplayName();
      } catch (FileNotFoundException fileNotFoundException) {
        return null;
      } 
      if (str == null || str.length() == 0)
        str = paramFile.getPath(); 
    } 
    return str;
  }
  
  public String getSystemTypeDescription(File paramFile) { return null; }
  
  public Icon getSystemIcon(File paramFile) {
    ShellFolder shellFolder;
    if (paramFile == null)
      return null; 
    try {
      shellFolder = getShellFolder(paramFile);
    } catch (FileNotFoundException fileNotFoundException) {
      return null;
    } 
    Image image = shellFolder.getIcon(false);
    return (image != null) ? new ImageIcon(image, shellFolder.getFolderType()) : UIManager.getIcon(paramFile.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
  }
  
  public boolean isParent(File paramFile1, File paramFile2) {
    if (paramFile1 == null || paramFile2 == null)
      return false; 
    if (paramFile1 instanceof ShellFolder) {
      File file = paramFile2.getParentFile();
      if (file != null && file.equals(paramFile1))
        return true; 
      File[] arrayOfFile = getFiles(paramFile1, false);
      for (File file1 : arrayOfFile) {
        if (paramFile2.equals(file1))
          return true; 
      } 
      return false;
    } 
    return paramFile1.equals(paramFile2.getParentFile());
  }
  
  public File getChild(File paramFile, String paramString) {
    if (paramFile instanceof ShellFolder) {
      File[] arrayOfFile = getFiles(paramFile, false);
      for (File file : arrayOfFile) {
        if (file.getName().equals(paramString))
          return file; 
      } 
    } 
    return createFileObject(paramFile, paramString);
  }
  
  public boolean isFileSystem(File paramFile) {
    if (paramFile instanceof ShellFolder) {
      ShellFolder shellFolder = (ShellFolder)paramFile;
      return (shellFolder.isFileSystem() && (!shellFolder.isLink() || !shellFolder.isDirectory()));
    } 
    return true;
  }
  
  public abstract File createNewFolder(File paramFile) throws IOException;
  
  public boolean isHiddenFile(File paramFile) { return paramFile.isHidden(); }
  
  public boolean isFileSystemRoot(File paramFile) { return ShellFolder.isFileSystemRoot(paramFile); }
  
  public boolean isDrive(File paramFile) { return false; }
  
  public boolean isFloppyDrive(File paramFile) { return false; }
  
  public boolean isComputerNode(File paramFile) { return ShellFolder.isComputerNode(paramFile); }
  
  public File[] getRoots() {
    File[] arrayOfFile = (File[])ShellFolder.get("roots");
    for (byte b = 0; b < arrayOfFile.length; b++) {
      if (isFileSystemRoot(arrayOfFile[b]))
        arrayOfFile[b] = createFileSystemRoot(arrayOfFile[b]); 
    } 
    return arrayOfFile;
  }
  
  public File getHomeDirectory() { return createFileObject(System.getProperty("user.home")); }
  
  public File getDefaultDirectory() {
    File file = (File)ShellFolder.get("fileChooserDefaultFolder");
    if (isFileSystemRoot(file))
      file = createFileSystemRoot(file); 
    return file;
  }
  
  public File createFileObject(File paramFile, String paramString) { return (paramFile == null) ? new File(paramString) : new File(paramFile, paramString); }
  
  public File createFileObject(String paramString) {
    File file = new File(paramString);
    if (isFileSystemRoot(file))
      file = createFileSystemRoot(file); 
    return file;
  }
  
  public File[] getFiles(File paramFile, boolean paramBoolean) {
    ArrayList arrayList = new ArrayList();
    if (!(paramFile instanceof ShellFolder))
      try {
        paramFile = getShellFolder(paramFile);
      } catch (FileNotFoundException fileNotFoundException) {
        return new File[0];
      }  
    File[] arrayOfFile = ((ShellFolder)paramFile).listFiles(!paramBoolean);
    if (arrayOfFile == null)
      return new File[0]; 
    for (File file : arrayOfFile) {
      if (Thread.currentThread().isInterrupted())
        break; 
      if (!(file instanceof ShellFolder)) {
        if (isFileSystemRoot(file))
          file = createFileSystemRoot(file); 
        try {
          file = ShellFolder.getShellFolder(file);
        } catch (FileNotFoundException fileNotFoundException) {
        
        } catch (InternalError internalError) {}
      } 
      if (!paramBoolean || !isHiddenFile(file))
        arrayList.add(file); 
    } 
    return (File[])arrayList.toArray(new File[arrayList.size()]);
  }
  
  public File getParentDirectory(File paramFile) throws IOException {
    ShellFolder shellFolder;
    if (paramFile == null || !paramFile.exists())
      return null; 
    try {
      shellFolder = getShellFolder(paramFile);
    } catch (FileNotFoundException fileNotFoundException) {
      return null;
    } 
    File file = shellFolder.getParentFile();
    if (file == null)
      return null; 
    if (isFileSystem(file)) {
      File file1 = file;
      if (!file1.exists()) {
        File file2 = file.getParentFile();
        if (file2 == null || !isFileSystem(file2))
          file1 = createFileSystemRoot(file1); 
      } 
      return file1;
    } 
    return file;
  }
  
  ShellFolder getShellFolder(File paramFile) throws FileNotFoundException {
    if (!(paramFile instanceof ShellFolder) && !(paramFile instanceof FileSystemRoot) && isFileSystemRoot(paramFile))
      paramFile = createFileSystemRoot(paramFile); 
    try {
      return ShellFolder.getShellFolder(paramFile);
    } catch (InternalError internalError) {
      System.err.println("FileSystemView.getShellFolder: f=" + paramFile);
      internalError.printStackTrace();
      return null;
    } 
  }
  
  protected File createFileSystemRoot(File paramFile) throws IOException { return new FileSystemRoot(paramFile); }
  
  static class FileSystemRoot extends File {
    public FileSystemRoot(File param1File) { super(param1File, ""); }
    
    public FileSystemRoot(String param1String) { super(param1String); }
    
    public boolean isDirectory() { return true; }
    
    public String getName() { return getPath(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\FileSystemView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */