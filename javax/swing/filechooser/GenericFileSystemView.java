package javax.swing.filechooser;

import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;

class GenericFileSystemView extends FileSystemView {
  private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
  
  public File createNewFolder(File paramFile) throws IOException {
    if (paramFile == null)
      throw new IOException("Containing directory is null:"); 
    File file = createFileObject(paramFile, newFolderString);
    if (file.exists())
      throw new IOException("Directory already exists:" + file.getAbsolutePath()); 
    file.mkdirs();
    return file;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\GenericFileSystemView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */