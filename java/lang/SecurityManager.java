package java.lang;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FilePermission;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import sun.reflect.CallerSensitive;
import sun.security.util.SecurityConstants;

public class SecurityManager {
  @Deprecated
  protected boolean inCheck;
  
  private boolean initialized = false;
  
  private static ThreadGroup rootGroup = getRootGroup();
  
  private static boolean packageAccessValid = false;
  
  private static String[] packageAccess;
  
  private static final Object packageAccessLock = new Object();
  
  private static boolean packageDefinitionValid = false;
  
  private static String[] packageDefinition;
  
  private static final Object packageDefinitionLock = new Object();
  
  private boolean hasAllPermission() {
    try {
      checkPermission(SecurityConstants.ALL_PERMISSION);
      return true;
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  @Deprecated
  public boolean getInCheck() { return this.inCheck; }
  
  public SecurityManager() {
    synchronized (SecurityManager.class) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(new RuntimePermission("createSecurityManager")); 
      this.initialized = true;
    } 
  }
  
  protected native Class[] getClassContext();
  
  @Deprecated
  protected ClassLoader currentClassLoader() {
    ClassLoader classLoader = currentClassLoader0();
    if (classLoader != null && hasAllPermission())
      classLoader = null; 
    return classLoader;
  }
  
  private native ClassLoader currentClassLoader0();
  
  @Deprecated
  protected Class<?> currentLoadedClass() {
    Class clazz = currentLoadedClass0();
    if (clazz != null && hasAllPermission())
      clazz = null; 
    return clazz;
  }
  
  @Deprecated
  protected native int classDepth(String paramString);
  
  @Deprecated
  protected int classLoaderDepth() {
    int i = classLoaderDepth0();
    if (i != -1)
      if (hasAllPermission()) {
        i = -1;
      } else {
        i--;
      }  
    return i;
  }
  
  private native int classLoaderDepth0();
  
  @Deprecated
  protected boolean inClass(String paramString) { return (classDepth(paramString) >= 0); }
  
  @Deprecated
  protected boolean inClassLoader() { return (currentClassLoader() != null); }
  
  public Object getSecurityContext() { return AccessController.getContext(); }
  
  public void checkPermission(Permission paramPermission) { AccessController.checkPermission(paramPermission); }
  
  public void checkPermission(Permission paramPermission, Object paramObject) {
    if (paramObject instanceof AccessControlContext) {
      ((AccessControlContext)paramObject).checkPermission(paramPermission);
    } else {
      throw new SecurityException();
    } 
  }
  
  public void checkCreateClassLoader() { checkPermission(SecurityConstants.CREATE_CLASSLOADER_PERMISSION); }
  
  private static ThreadGroup getRootGroup() {
    ThreadGroup threadGroup;
    for (threadGroup = Thread.currentThread().getThreadGroup(); threadGroup.getParent() != null; threadGroup = threadGroup.getParent());
    return threadGroup;
  }
  
  public void checkAccess(Thread paramThread) {
    if (paramThread == null)
      throw new NullPointerException("thread can't be null"); 
    if (paramThread.getThreadGroup() == rootGroup)
      checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION); 
  }
  
  public void checkAccess(ThreadGroup paramThreadGroup) {
    if (paramThreadGroup == null)
      throw new NullPointerException("thread group can't be null"); 
    if (paramThreadGroup == rootGroup)
      checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION); 
  }
  
  public void checkExit(int paramInt) { checkPermission(new RuntimePermission("exitVM." + paramInt)); }
  
  public void checkExec(String paramString) {
    File file = new File(paramString);
    if (file.isAbsolute()) {
      checkPermission(new FilePermission(paramString, "execute"));
    } else {
      checkPermission(new FilePermission("<<ALL FILES>>", "execute"));
    } 
  }
  
  public void checkLink(String paramString) {
    if (paramString == null)
      throw new NullPointerException("library can't be null"); 
    checkPermission(new RuntimePermission("loadLibrary." + paramString));
  }
  
  public void checkRead(FileDescriptor paramFileDescriptor) {
    if (paramFileDescriptor == null)
      throw new NullPointerException("file descriptor can't be null"); 
    checkPermission(new RuntimePermission("readFileDescriptor"));
  }
  
  public void checkRead(String paramString) { checkPermission(new FilePermission(paramString, "read")); }
  
  public void checkRead(String paramString, Object paramObject) { checkPermission(new FilePermission(paramString, "read"), paramObject); }
  
  public void checkWrite(FileDescriptor paramFileDescriptor) {
    if (paramFileDescriptor == null)
      throw new NullPointerException("file descriptor can't be null"); 
    checkPermission(new RuntimePermission("writeFileDescriptor"));
  }
  
  public void checkWrite(String paramString) { checkPermission(new FilePermission(paramString, "write")); }
  
  public void checkDelete(String paramString) { checkPermission(new FilePermission(paramString, "delete")); }
  
  public void checkConnect(String paramString, int paramInt) {
    if (paramString == null)
      throw new NullPointerException("host can't be null"); 
    if (!paramString.startsWith("[") && paramString.indexOf(':') != -1)
      paramString = "[" + paramString + "]"; 
    if (paramInt == -1) {
      checkPermission(new SocketPermission(paramString, "resolve"));
    } else {
      checkPermission(new SocketPermission(paramString + ":" + paramInt, "connect"));
    } 
  }
  
  public void checkConnect(String paramString, int paramInt, Object paramObject) {
    if (paramString == null)
      throw new NullPointerException("host can't be null"); 
    if (!paramString.startsWith("[") && paramString.indexOf(':') != -1)
      paramString = "[" + paramString + "]"; 
    if (paramInt == -1) {
      checkPermission(new SocketPermission(paramString, "resolve"), paramObject);
    } else {
      checkPermission(new SocketPermission(paramString + ":" + paramInt, "connect"), paramObject);
    } 
  }
  
  public void checkListen(int paramInt) { checkPermission(new SocketPermission("localhost:" + paramInt, "listen")); }
  
  public void checkAccept(String paramString, int paramInt) {
    if (paramString == null)
      throw new NullPointerException("host can't be null"); 
    if (!paramString.startsWith("[") && paramString.indexOf(':') != -1)
      paramString = "[" + paramString + "]"; 
    checkPermission(new SocketPermission(paramString + ":" + paramInt, "accept"));
  }
  
  public void checkMulticast(InetAddress paramInetAddress) {
    String str = paramInetAddress.getHostAddress();
    if (!str.startsWith("[") && str.indexOf(':') != -1)
      str = "[" + str + "]"; 
    checkPermission(new SocketPermission(str, "connect,accept"));
  }
  
  @Deprecated
  public void checkMulticast(InetAddress paramInetAddress, byte paramByte) {
    String str = paramInetAddress.getHostAddress();
    if (!str.startsWith("[") && str.indexOf(':') != -1)
      str = "[" + str + "]"; 
    checkPermission(new SocketPermission(str, "connect,accept"));
  }
  
  public void checkPropertiesAccess() { checkPermission(new PropertyPermission("*", "read,write")); }
  
  public void checkPropertyAccess(String paramString) { checkPermission(new PropertyPermission(paramString, "read")); }
  
  @Deprecated
  public boolean checkTopLevelWindow(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException("window can't be null"); 
    Permission permission = SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION;
    if (permission == null)
      permission = SecurityConstants.ALL_PERMISSION; 
    try {
      checkPermission(permission);
      return true;
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  public void checkPrintJobAccess() { checkPermission(new RuntimePermission("queuePrintJob")); }
  
  @Deprecated
  public void checkSystemClipboardAccess() {
    Permission permission = SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION;
    if (permission == null)
      permission = SecurityConstants.ALL_PERMISSION; 
    checkPermission(permission);
  }
  
  @Deprecated
  public void checkAwtEventQueueAccess() {
    Permission permission = SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION;
    if (permission == null)
      permission = SecurityConstants.ALL_PERMISSION; 
    checkPermission(permission);
  }
  
  private static String[] getPackages(String paramString) {
    String[] arrayOfString = null;
    if (paramString != null && !paramString.equals("")) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
      int i = stringTokenizer.countTokens();
      if (i > 0) {
        arrayOfString = new String[i];
        byte b = 0;
        while (stringTokenizer.hasMoreElements()) {
          String str = stringTokenizer.nextToken().trim();
          arrayOfString[b++] = str;
        } 
      } 
    } 
    if (arrayOfString == null)
      arrayOfString = new String[0]; 
    return arrayOfString;
  }
  
  public void checkPackageAccess(String paramString) {
    String[] arrayOfString;
    if (paramString == null)
      throw new NullPointerException("package name can't be null"); 
    synchronized (packageAccessLock) {
      if (!packageAccessValid) {
        String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
              public String run() { return Security.getProperty("package.access"); }
            });
        packageAccess = getPackages(str);
        packageAccessValid = true;
      } 
      arrayOfString = packageAccess;
    } 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.startsWith(arrayOfString[b]) || arrayOfString[b].equals(paramString + ".")) {
        checkPermission(new RuntimePermission("accessClassInPackage." + paramString));
        break;
      } 
    } 
  }
  
  public void checkPackageDefinition(String paramString) {
    String[] arrayOfString;
    if (paramString == null)
      throw new NullPointerException("package name can't be null"); 
    synchronized (packageDefinitionLock) {
      if (!packageDefinitionValid) {
        String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
              public String run() { return Security.getProperty("package.definition"); }
            });
        packageDefinition = getPackages(str);
        packageDefinitionValid = true;
      } 
      arrayOfString = packageDefinition;
    } 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.startsWith(arrayOfString[b]) || arrayOfString[b].equals(paramString + ".")) {
        checkPermission(new RuntimePermission("defineClassInPackage." + paramString));
        break;
      } 
    } 
  }
  
  public void checkSetFactory() { checkPermission(new RuntimePermission("setFactory")); }
  
  @Deprecated
  @CallerSensitive
  public void checkMemberAccess(Class<?> paramClass, int paramInt) {
    if (paramClass == null)
      throw new NullPointerException("class can't be null"); 
    if (paramInt != 0) {
      Class[] arrayOfClass = getClassContext();
      if (arrayOfClass.length < 4 || arrayOfClass[3].getClassLoader() != paramClass.getClassLoader())
        checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION); 
    } 
  }
  
  public void checkSecurityAccess(String paramString) { checkPermission(new SecurityPermission(paramString)); }
  
  private native Class<?> currentLoadedClass0();
  
  public ThreadGroup getThreadGroup() { return Thread.currentThread().getThreadGroup(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\SecurityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */