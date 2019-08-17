package java.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyPermission;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.misc.Version;
import sun.nio.ch.Interruptible;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstantPool;
import sun.reflect.Reflection;
import sun.reflect.annotation.AnnotationType;
import sun.security.util.SecurityConstants;

public final class System {
  public static final InputStream in;
  
  public static final PrintStream out;
  
  public static final PrintStream err;
  
  private static Properties props;
  
  private static String lineSeparator;
  
  private static native void registerNatives();
  
  public static void setIn(InputStream paramInputStream) {
    checkIO();
    setIn0(paramInputStream);
  }
  
  public static void setOut(PrintStream paramPrintStream) {
    checkIO();
    setOut0(paramPrintStream);
  }
  
  public static void setErr(PrintStream paramPrintStream) {
    checkIO();
    setErr0(paramPrintStream);
  }
  
  public static Console console() {
    if (cons == null)
      synchronized (System.class) {
        cons = SharedSecrets.getJavaIOAccess().console();
      }  
    return cons;
  }
  
  public static Channel inheritedChannel() throws IOException { return SelectorProvider.provider().inheritedChannel(); }
  
  private static void checkIO() {
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("setIO")); 
  }
  
  private static native void setIn0(InputStream paramInputStream);
  
  private static native void setOut0(PrintStream paramPrintStream);
  
  private static native void setErr0(PrintStream paramPrintStream);
  
  public static void setSecurityManager(SecurityManager paramSecurityManager) {
    try {
      paramSecurityManager.checkPackageAccess("java.lang");
    } catch (Exception exception) {}
    setSecurityManager0(paramSecurityManager);
  }
  
  private static void setSecurityManager0(final SecurityManager s) {
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("setSecurityManager")); 
    if (paramSecurityManager != null && paramSecurityManager.getClass().getClassLoader() != null)
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
              s.getClass().getProtectionDomain().implies(SecurityConstants.ALL_PERMISSION);
              return null;
            }
          }); 
    security = paramSecurityManager;
  }
  
  public static SecurityManager getSecurityManager() { return security; }
  
  public static native long currentTimeMillis();
  
  public static native long nanoTime();
  
  public static native void arraycopy(Object paramObject1, int paramInt1, Object paramObject2, int paramInt2, int paramInt3);
  
  public static native int identityHashCode(Object paramObject);
  
  private static native Properties initProperties(Properties paramProperties);
  
  public static Properties getProperties() {
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    return props;
  }
  
  public static String lineSeparator() { return lineSeparator; }
  
  public static void setProperties(Properties paramProperties) {
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    if (paramProperties == null) {
      paramProperties = new Properties();
      initProperties(paramProperties);
    } 
    props = paramProperties;
  }
  
  public static String getProperty(String paramString) {
    checkKey(paramString);
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertyAccess(paramString); 
    return props.getProperty(paramString);
  }
  
  public static String getProperty(String paramString1, String paramString2) {
    checkKey(paramString1);
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertyAccess(paramString1); 
    return props.getProperty(paramString1, paramString2);
  }
  
  public static String setProperty(String paramString1, String paramString2) {
    checkKey(paramString1);
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new PropertyPermission(paramString1, "write")); 
    return (String)props.setProperty(paramString1, paramString2);
  }
  
  public static String clearProperty(String paramString) {
    checkKey(paramString);
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new PropertyPermission(paramString, "write")); 
    return (String)props.remove(paramString);
  }
  
  private static void checkKey(String paramString) {
    if (paramString == null)
      throw new NullPointerException("key can't be null"); 
    if (paramString.equals(""))
      throw new IllegalArgumentException("key can't be empty"); 
  }
  
  public static String getenv(String paramString) {
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("getenv." + paramString)); 
    return ProcessEnvironment.getenv(paramString);
  }
  
  public static Map<String, String> getenv() {
    SecurityManager securityManager = getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("getenv.*")); 
    return ProcessEnvironment.getenv();
  }
  
  public static void exit(int paramInt) { Runtime.getRuntime().exit(paramInt); }
  
  public static void gc() { Runtime.getRuntime().gc(); }
  
  public static void runFinalization() { Runtime.getRuntime().runFinalization(); }
  
  @Deprecated
  public static void runFinalizersOnExit(boolean paramBoolean) { Runtime.runFinalizersOnExit(paramBoolean); }
  
  @CallerSensitive
  public static void load(String paramString) { Runtime.getRuntime().load0(Reflection.getCallerClass(), paramString); }
  
  @CallerSensitive
  public static void loadLibrary(String paramString) { Runtime.getRuntime().loadLibrary0(Reflection.getCallerClass(), paramString); }
  
  public static native String mapLibraryName(String paramString);
  
  private static PrintStream newPrintStream(FileOutputStream paramFileOutputStream, String paramString) {
    if (paramString != null)
      try {
        return new PrintStream(new BufferedOutputStream(paramFileOutputStream, 128), true, paramString);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {} 
    return new PrintStream(new BufferedOutputStream(paramFileOutputStream, 128), true);
  }
  
  private static void initializeSystemClass() {
    props = new Properties();
    initProperties(props);
    VM.saveAndRemoveProperties(props);
    lineSeparator = props.getProperty("line.separator");
    Version.init();
    FileInputStream fileInputStream = new FileInputStream(FileDescriptor.in);
    FileOutputStream fileOutputStream1 = new FileOutputStream(FileDescriptor.out);
    FileOutputStream fileOutputStream2 = new FileOutputStream(FileDescriptor.err);
    setIn0(new BufferedInputStream(fileInputStream));
    setOut0(newPrintStream(fileOutputStream1, props.getProperty("sun.stdout.encoding")));
    setErr0(newPrintStream(fileOutputStream2, props.getProperty("sun.stderr.encoding")));
    loadLibrary("zip");
    Terminator.setup();
    VM.initializeOSEnvironment();
    Thread thread = Thread.currentThread();
    thread.getThreadGroup().add(thread);
    setJavaLangAccess();
    VM.booted();
  }
  
  private static void setJavaLangAccess() { SharedSecrets.setJavaLangAccess(new JavaLangAccess() {
          public ConstantPool getConstantPool(Class<?> param1Class) { return param1Class.getConstantPool(); }
          
          public boolean casAnnotationType(Class<?> param1Class, AnnotationType param1AnnotationType1, AnnotationType param1AnnotationType2) { return param1Class.casAnnotationType(param1AnnotationType1, param1AnnotationType2); }
          
          public AnnotationType getAnnotationType(Class<?> param1Class) { return param1Class.getAnnotationType(); }
          
          public Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap(Class<?> param1Class) { return param1Class.getDeclaredAnnotationMap(); }
          
          public byte[] getRawClassAnnotations(Class<?> param1Class) { return param1Class.getRawAnnotations(); }
          
          public byte[] getRawClassTypeAnnotations(Class<?> param1Class) { return param1Class.getRawTypeAnnotations(); }
          
          public byte[] getRawExecutableTypeAnnotations(Executable param1Executable) { return Class.getExecutableTypeAnnotationBytes(param1Executable); }
          
          public <E extends Enum<E>> E[] getEnumConstantsShared(Class<E> param1Class) { return (E[])(Enum[])param1Class.getEnumConstantsShared(); }
          
          public void blockedOn(Thread param1Thread, Interruptible param1Interruptible) { param1Thread.blockedOn(param1Interruptible); }
          
          public void registerShutdownHook(int param1Int, boolean param1Boolean, Runnable param1Runnable) { Shutdown.add(param1Int, param1Boolean, param1Runnable); }
          
          public int getStackTraceDepth(Throwable param1Throwable) { return param1Throwable.getStackTraceDepth(); }
          
          public StackTraceElement getStackTraceElement(Throwable param1Throwable, int param1Int) { return param1Throwable.getStackTraceElement(param1Int); }
          
          public String newStringUnsafe(char[] param1ArrayOfChar) { return new String(param1ArrayOfChar, true); }
          
          public Thread newThreadWithAcc(Runnable param1Runnable, AccessControlContext param1AccessControlContext) { return new Thread(param1Runnable, param1AccessControlContext); }
          
          public void invokeFinalize(Object param1Object) throws Throwable { param1Object.finalize(); }
        }); }
  
  static  {
    registerNatives();
    in = null;
    out = null;
    err = null;
    security = null;
    cons = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\System.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */