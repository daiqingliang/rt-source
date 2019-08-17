package sun.swing;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.UIDefaults;
import sun.reflect.misc.ReflectUtil;

public class SwingLazyValue implements UIDefaults.LazyValue {
  private String className;
  
  private String methodName;
  
  private Object[] args;
  
  public SwingLazyValue(String paramString) { this(paramString, (String)null); }
  
  public SwingLazyValue(String paramString1, String paramString2) { this(paramString1, paramString2, null); }
  
  public SwingLazyValue(String paramString, Object[] paramArrayOfObject) { this(paramString, null, paramArrayOfObject); }
  
  public SwingLazyValue(String paramString1, String paramString2, Object[] paramArrayOfObject) {
    this.className = paramString1;
    this.methodName = paramString2;
    if (paramArrayOfObject != null)
      this.args = (Object[])paramArrayOfObject.clone(); 
  }
  
  public Object createValue(UIDefaults paramUIDefaults) {
    try {
      ReflectUtil.checkPackageAccess(this.className);
      Class clazz = Class.forName(this.className, true, null);
      if (this.methodName != null) {
        Class[] arrayOfClass1 = getClassArray(this.args);
        Method method = clazz.getMethod(this.methodName, arrayOfClass1);
        makeAccessible(method);
        return method.invoke(clazz, this.args);
      } 
      Class[] arrayOfClass = getClassArray(this.args);
      Constructor constructor = clazz.getConstructor(arrayOfClass);
      makeAccessible(constructor);
      return constructor.newInstance(this.args);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private void makeAccessible(final AccessibleObject object) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            object.setAccessible(true);
            return null;
          }
        }); }
  
  private Class[] getClassArray(Object[] paramArrayOfObject) {
    Class[] arrayOfClass = null;
    if (paramArrayOfObject != null) {
      arrayOfClass = new Class[paramArrayOfObject.length];
      for (byte b = 0; b < paramArrayOfObject.length; b++) {
        if (paramArrayOfObject[b] instanceof Integer) {
          arrayOfClass[b] = int.class;
        } else if (paramArrayOfObject[b] instanceof Boolean) {
          arrayOfClass[b] = boolean.class;
        } else if (paramArrayOfObject[b] instanceof javax.swing.plaf.ColorUIResource) {
          arrayOfClass[b] = java.awt.Color.class;
        } else {
          arrayOfClass[b] = paramArrayOfObject[b].getClass();
        } 
      } 
    } 
    return arrayOfClass;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\SwingLazyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */