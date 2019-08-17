package java.lang;

import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;

class SystemClassLoaderAction extends Object implements PrivilegedExceptionAction<ClassLoader> {
  private ClassLoader parent;
  
  SystemClassLoaderAction(ClassLoader paramClassLoader) { this.parent = paramClassLoader; }
  
  public ClassLoader run() throws Exception {
    String str = System.getProperty("java.system.class.loader");
    if (str == null)
      return this.parent; 
    Constructor constructor = Class.forName(str, true, this.parent).getDeclaredConstructor(new Class[] { ClassLoader.class });
    ClassLoader classLoader = (ClassLoader)constructor.newInstance(new Object[] { this.parent });
    Thread.currentThread().setContextClassLoader(classLoader);
    return classLoader;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\SystemClassLoaderAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */