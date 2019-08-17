package java.net;

import java.security.AccessControlContext;

final class FactoryURLClassLoader extends URLClassLoader {
  FactoryURLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, AccessControlContext paramAccessControlContext) { super(paramArrayOfURL, paramClassLoader, paramAccessControlContext); }
  
  FactoryURLClassLoader(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext) { super(paramArrayOfURL, paramAccessControlContext); }
  
  public final Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      int i = paramString.lastIndexOf('.');
      if (i != -1)
        securityManager.checkPackageAccess(paramString.substring(0, i)); 
    } 
    return super.loadClass(paramString, paramBoolean);
  }
  
  static  {
    ClassLoader.registerAsParallelCapable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\FactoryURLClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */