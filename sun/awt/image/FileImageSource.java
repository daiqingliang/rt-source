package sun.awt.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileImageSource extends InputStreamImageSource {
  String imagefile;
  
  public FileImageSource(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(paramString); 
    this.imagefile = paramString;
  }
  
  final boolean checkSecurity(Object paramObject, boolean paramBoolean) { return true; }
  
  protected ImageDecoder getDecoder() {
    BufferedInputStream bufferedInputStream;
    if (this.imagefile == null)
      return null; 
    try {
      bufferedInputStream = new BufferedInputStream(new FileInputStream(this.imagefile));
    } catch (FileNotFoundException fileNotFoundException) {
      return null;
    } 
    return getDecoder(bufferedInputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\FileImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */