package sun.text.normalizer;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;

public final class ICUData {
  private static InputStream getStream(final Class<ICUData> root, final String resourceName, boolean paramBoolean) {
    InputStream inputStream = null;
    if (System.getSecurityManager() != null) {
      inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() { return root.getResourceAsStream(resourceName); }
          });
    } else {
      inputStream = paramClass.getResourceAsStream(paramString);
    } 
    if (inputStream == null && paramBoolean)
      throw new MissingResourceException("could not locate data", paramClass.getPackage().getName(), paramString); 
    return inputStream;
  }
  
  public static InputStream getStream(String paramString) { return getStream(ICUData.class, paramString, false); }
  
  public static InputStream getRequiredStream(String paramString) { return getStream(ICUData.class, paramString, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\ICUData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */