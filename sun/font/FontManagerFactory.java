package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class FontManagerFactory {
  private static FontManager instance = null;
  
  private static final String DEFAULT_CLASS;
  
  public static FontManager getInstance() {
    if (instance != null)
      return instance; 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              String str = System.getProperty("sun.font.fontmanager", DEFAULT_CLASS);
              ClassLoader classLoader = ClassLoader.getSystemClassLoader();
              Class clazz = Class.forName(str, true, classLoader);
              instance = (FontManager)clazz.newInstance();
            } catch (ClassNotFoundException|InstantiationException|IllegalAccessException classNotFoundException) {
              throw new InternalError(classNotFoundException);
            } 
            return null;
          }
        });
    return instance;
  }
  
  static  {
    if (FontUtilities.isWindows) {
      DEFAULT_CLASS = "sun.awt.Win32FontManager";
    } else if (FontUtilities.isMacOSX) {
      DEFAULT_CLASS = "sun.font.CFontManager";
    } else {
      DEFAULT_CLASS = "sun.awt.X11FontManager";
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */