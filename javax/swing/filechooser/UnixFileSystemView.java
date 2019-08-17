package javax.swing.filechooser;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.UIManager;

class UnixFileSystemView extends FileSystemView {
  private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
  
  private static final String newFolderNextString = UIManager.getString("FileChooser.other.newFolder.subsequent");
  
  public File createNewFolder(File paramFile) throws IOException {
    if (paramFile == null)
      throw new IOException("Containing directory is null:"); 
    File file = createFileObject(paramFile, newFolderString);
    for (byte b = 1; file.exists() && b < 100; b++) {
      file = createFileObject(paramFile, MessageFormat.format(newFolderNextString, new Object[] { new Integer(b) }));
    } 
    if (file.exists())
      throw new IOException("Directory already exists:" + file.getAbsolutePath()); 
    file.mkdirs();
    return file;
  }
  
  public boolean isFileSystemRoot(File paramFile) { return (paramFile != null && paramFile.getAbsolutePath().equals("/")); }
  
  public boolean isDrive(File paramFile) { return isFloppyDrive(paramFile); }
  
  public boolean isFloppyDrive(File paramFile) { return false; }
  
  public boolean isComputerNode(File paramFile) {
    if (paramFile != null) {
      String str = paramFile.getParent();
      if (str != null && str.equals("/net"))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\UnixFileSystemView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */