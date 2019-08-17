package com.sun.jmx.remote.util;

import sun.reflect.misc.ReflectUtil;

public class OrderClassLoaders extends ClassLoader {
  private ClassLoader cl2;
  
  public OrderClassLoaders(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) {
    super(paramClassLoader1);
    this.cl2 = paramClassLoader2;
  }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    try {
      return super.loadClass(paramString, paramBoolean);
    } catch (ClassNotFoundException classNotFoundException) {
      if (this.cl2 != null)
        return this.cl2.loadClass(paramString); 
      throw classNotFoundException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remot\\util\OrderClassLoaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */