package javax.swing.filechooser;

import java.io.File;
import javax.swing.Icon;

public abstract class FileView {
  public String getName(File paramFile) { return null; }
  
  public String getDescription(File paramFile) { return null; }
  
  public String getTypeDescription(File paramFile) { return null; }
  
  public Icon getIcon(File paramFile) { return null; }
  
  public Boolean isTraversable(File paramFile) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\FileView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */