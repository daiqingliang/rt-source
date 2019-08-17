package sun.awt.shell;

import java.io.File;
import java.io.ObjectStreamException;

class DefaultShellFolder extends ShellFolder {
  DefaultShellFolder(ShellFolder paramShellFolder, File paramFile) { super(paramShellFolder, paramFile.getAbsolutePath()); }
  
  protected Object writeReplace() throws ObjectStreamException { return new File(getPath()); }
  
  public File[] listFiles() {
    File[] arrayOfFile = super.listFiles();
    if (arrayOfFile != null)
      for (byte b = 0; b < arrayOfFile.length; b++)
        arrayOfFile[b] = new DefaultShellFolder(this, arrayOfFile[b]);  
    return arrayOfFile;
  }
  
  public boolean isLink() { return false; }
  
  public boolean isHidden() {
    String str = getName();
    return (str.length() > 0) ? ((str.charAt(0) == '.')) : false;
  }
  
  public ShellFolder getLinkLocation() { return null; }
  
  public String getDisplayName() { return getName(); }
  
  public String getFolderType() { return isDirectory() ? "File Folder" : "File"; }
  
  public String getExecutableType() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\shell\DefaultShellFolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */