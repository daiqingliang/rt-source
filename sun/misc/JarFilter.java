package sun.misc;

import java.io.File;
import java.io.FilenameFilter;

public class JarFilter implements FilenameFilter {
  public boolean accept(File paramFile, String paramString) {
    String str = paramString.toLowerCase();
    return (str.endsWith(".jar") || str.endsWith(".zip"));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JarFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */