package sun.security.action;

import java.io.File;
import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;

public class OpenFileInputStreamAction extends Object implements PrivilegedExceptionAction<FileInputStream> {
  private final File file;
  
  public OpenFileInputStreamAction(File paramFile) { this.file = paramFile; }
  
  public OpenFileInputStreamAction(String paramString) { this.file = new File(paramString); }
  
  public FileInputStream run() throws Exception { return new FileInputStream(this.file); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\OpenFileInputStreamAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */