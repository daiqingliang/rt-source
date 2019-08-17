package java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;

public abstract class CharsetProvider {
  protected CharsetProvider() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("charsetProvider")); 
  }
  
  public abstract Iterator<Charset> charsets();
  
  public abstract Charset charsetForName(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\spi\CharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */