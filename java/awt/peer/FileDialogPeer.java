package java.awt.peer;

import java.io.FilenameFilter;

public interface FileDialogPeer extends DialogPeer {
  void setFile(String paramString);
  
  void setDirectory(String paramString);
  
  void setFilenameFilter(FilenameFilter paramFilenameFilter);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\FileDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */