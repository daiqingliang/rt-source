package javax.swing.filechooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import javax.swing.UIManager;

class WindowsFileSystemView extends FileSystemView {
  private static final String newFolderString = UIManager.getString("FileChooser.win32.newFolder");
  
  private static final String newFolderNextString = UIManager.getString("FileChooser.win32.newFolder.subsequent");
  
  public Boolean isTraversable(File paramFile) { return Boolean.valueOf((isFileSystemRoot(paramFile) || isComputerNode(paramFile) || paramFile.isDirectory())); }
  
  public File getChild(File paramFile, String paramString) {
    if (paramString.startsWith("\\") && !paramString.startsWith("\\\\") && isFileSystem(paramFile)) {
      String str = paramFile.getAbsolutePath();
      if (str.length() >= 2 && str.charAt(1) == ':' && Character.isLetter(str.charAt(0)))
        return createFileObject(str.substring(0, 2) + paramString); 
    } 
    return super.getChild(paramFile, paramString);
  }
  
  public String getSystemTypeDescription(File paramFile) {
    if (paramFile == null)
      return null; 
    try {
      return getShellFolder(paramFile).getFolderType();
    } catch (FileNotFoundException fileNotFoundException) {
      return null;
    } 
  }
  
  public File getHomeDirectory() {
    File[] arrayOfFile = getRoots();
    return (arrayOfFile.length == 0) ? null : arrayOfFile[0];
  }
  
  public File createNewFolder(File paramFile) throws IOException {
    if (paramFile == null)
      throw new IOException("Containing directory is null:"); 
    File file = createFileObject(paramFile, newFolderString);
    for (byte b = 2; file.exists() && b < 100; b++) {
      file = createFileObject(paramFile, MessageFormat.format(newFolderNextString, new Object[] { new Integer(b) }));
    } 
    if (file.exists())
      throw new IOException("Directory already exists:" + file.getAbsolutePath()); 
    file.mkdirs();
    return file;
  }
  
  public boolean isDrive(File paramFile) { return isFileSystemRoot(paramFile); }
  
  public boolean isFloppyDrive(final File dir) {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return dir.getAbsolutePath(); }
        });
    return (str != null && (str.equals("A:\\") || str.equals("B:\\")));
  }
  
  public File createFileObject(String paramString) {
    if (paramString.length() >= 2 && paramString.charAt(1) == ':' && Character.isLetter(paramString.charAt(0)))
      if (paramString.length() == 2) {
        paramString = paramString + "\\";
      } else if (paramString.charAt(2) != '\\') {
        paramString = paramString.substring(0, 2) + "\\" + paramString.substring(2);
      }  
    return super.createFileObject(paramString);
  }
  
  protected File createFileSystemRoot(File paramFile) throws IOException { return new FileSystemView.FileSystemRoot(paramFile) {
        public boolean exists() { return true; }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\WindowsFileSystemView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */