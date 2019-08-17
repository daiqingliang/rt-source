package sun.applet;

import java.lang.reflect.Field;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import sun.awt.AWTSecurityManager;
import sun.awt.AppContext;
import sun.security.util.SecurityConstants;

public class AppletSecurity extends AWTSecurityManager {
  private static Field facc = null;
  
  private static Field fcontext = null;
  
  private HashSet restrictedPackages = new HashSet();
  
  private boolean inThreadGroupCheck = false;
  
  public AppletSecurity() { reset(); }
  
  public void reset() {
    this.restrictedPackages.clear();
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Enumeration enumeration = System.getProperties().propertyNames();
            while (enumeration.hasMoreElements()) {
              String str = (String)enumeration.nextElement();
              if (str != null && str.startsWith("package.restrict.access.")) {
                String str1 = System.getProperty(str);
                if (str1 != null && str1.equalsIgnoreCase("true")) {
                  String str2 = str.substring(24);
                  AppletSecurity.this.restrictedPackages.add(str2);
                } 
              } 
            } 
            return null;
          }
        });
  }
  
  private AppletClassLoader currentAppletClassLoader() {
    ClassLoader classLoader = currentClassLoader();
    if (classLoader == null || classLoader instanceof AppletClassLoader)
      return (AppletClassLoader)classLoader; 
    Class[] arrayOfClass = getClassContext();
    byte b;
    for (b = 0; b < arrayOfClass.length; b++) {
      classLoader = arrayOfClass[b].getClassLoader();
      if (classLoader instanceof AppletClassLoader)
        return (AppletClassLoader)classLoader; 
    } 
    for (b = 0; b < arrayOfClass.length; b++) {
      final ClassLoader currentLoader = arrayOfClass[b].getClassLoader();
      if (classLoader1 instanceof java.net.URLClassLoader) {
        classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() {
                AccessControlContext accessControlContext = null;
                ProtectionDomain[] arrayOfProtectionDomain = null;
                try {
                  accessControlContext = (AccessControlContext)facc.get(currentLoader);
                  if (accessControlContext == null)
                    return null; 
                  arrayOfProtectionDomain = (ProtectionDomain[])fcontext.get(accessControlContext);
                  if (arrayOfProtectionDomain == null)
                    return null; 
                } catch (Exception exception) {
                  throw new UnsupportedOperationException(exception);
                } 
                for (byte b = 0; b < arrayOfProtectionDomain.length; b++) {
                  ClassLoader classLoader = arrayOfProtectionDomain[b].getClassLoader();
                  if (classLoader instanceof AppletClassLoader)
                    return classLoader; 
                } 
                return null;
              }
            });
        if (classLoader != null)
          return (AppletClassLoader)classLoader; 
      } 
    } 
    classLoader = Thread.currentThread().getContextClassLoader();
    return (classLoader instanceof AppletClassLoader) ? (AppletClassLoader)classLoader : (AppletClassLoader)null;
  }
  
  protected boolean inThreadGroup(ThreadGroup paramThreadGroup) { return (currentAppletClassLoader() == null) ? false : getThreadGroup().parentOf(paramThreadGroup); }
  
  protected boolean inThreadGroup(Thread paramThread) { return inThreadGroup(paramThread.getThreadGroup()); }
  
  public void checkAccess(Thread paramThread) {
    if (paramThread.getState() != Thread.State.TERMINATED && !inThreadGroup(paramThread))
      checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION); 
  }
  
  public void checkAccess(ThreadGroup paramThreadGroup) {
    if (this.inThreadGroupCheck) {
      checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
    } else {
      try {
        this.inThreadGroupCheck = true;
        if (!inThreadGroup(paramThreadGroup))
          checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION); 
      } finally {
        this.inThreadGroupCheck = false;
      } 
    } 
  }
  
  public void checkPackageAccess(String paramString) {
    super.checkPackageAccess(paramString);
    for (String str : this.restrictedPackages) {
      if (paramString.equals(str) || paramString.startsWith(str + "."))
        checkPermission(new RuntimePermission("accessClassInPackage." + paramString)); 
    } 
  }
  
  public void checkAwtEventQueueAccess() {
    AppContext appContext = AppContext.getAppContext();
    AppletClassLoader appletClassLoader = currentAppletClassLoader();
    if (AppContext.isMainContext(appContext) && appletClassLoader != null)
      checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION); 
  }
  
  public ThreadGroup getThreadGroup() {
    AppletClassLoader appletClassLoader = currentAppletClassLoader();
    ThreadGroup threadGroup = (appletClassLoader == null) ? null : appletClassLoader.getThreadGroup();
    return (threadGroup != null) ? threadGroup : super.getThreadGroup();
  }
  
  public AppContext getAppContext() {
    AppletClassLoader appletClassLoader = currentAppletClassLoader();
    if (appletClassLoader == null)
      return null; 
    AppContext appContext = appletClassLoader.getAppContext();
    if (appContext == null)
      throw new SecurityException("Applet classloader has invalid AppContext"); 
    return appContext;
  }
  
  static  {
    try {
      facc = java.net.URLClassLoader.class.getDeclaredField("acc");
      facc.setAccessible(true);
      fcontext = AccessControlContext.class.getDeclaredField("context");
      fcontext.setAccessible(true);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new UnsupportedOperationException(noSuchFieldException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */