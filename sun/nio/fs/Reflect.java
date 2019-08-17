package sun.nio.fs;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Reflect {
  private static void setAccessible(final AccessibleObject ao) { AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            ao.setAccessible(true);
            return null;
          }
        }); }
  
  static Field lookupField(String paramString1, String paramString2) {
    try {
      Class clazz = Class.forName(paramString1);
      Field field = clazz.getDeclaredField(paramString2);
      setAccessible(field);
      return field;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new AssertionError(classNotFoundException);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new AssertionError(noSuchFieldException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\Reflect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */