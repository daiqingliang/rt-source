package java.nio.file.spi;

import java.io.IOException;
import java.nio.file.Path;

public abstract class FileTypeDetector {
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("fileTypeDetector")); 
    return null;
  }
  
  private FileTypeDetector(Void paramVoid) {}
  
  protected FileTypeDetector() { this(checkPermission()); }
  
  public abstract String probeContentType(Path paramPath) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\spi\FileTypeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */