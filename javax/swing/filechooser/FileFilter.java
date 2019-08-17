package javax.swing.filechooser;

import java.io.File;

public abstract class FileFilter {
  public abstract boolean accept(File paramFile);
  
  public abstract String getDescription();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\FileFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */