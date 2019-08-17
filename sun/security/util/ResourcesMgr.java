package sun.security.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;

public class ResourcesMgr {
  private static ResourceBundle bundle;
  
  private static ResourceBundle altBundle;
  
  public static String getString(String paramString) {
    if (bundle == null)
      bundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
            public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.Resources"); }
          }); 
    return bundle.getString(paramString);
  }
  
  public static String getString(String paramString1, final String altBundleName) {
    if (altBundle == null)
      altBundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
            public ResourceBundle run() { return ResourceBundle.getBundle(altBundleName); }
          }); 
    return altBundle.getString(paramString1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ResourcesMgr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */