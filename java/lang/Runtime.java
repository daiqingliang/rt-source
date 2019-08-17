package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Runtime {
  private static Runtime currentRuntime = new Runtime();
  
  public static Runtime getRuntime() { return currentRuntime; }
  
  public void exit(int paramInt) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkExit(paramInt); 
    Shutdown.exit(paramInt);
  }
  
  public void addShutdownHook(Thread paramThread) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("shutdownHooks")); 
    ApplicationShutdownHooks.add(paramThread);
  }
  
  public boolean removeShutdownHook(Thread paramThread) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("shutdownHooks")); 
    return ApplicationShutdownHooks.remove(paramThread);
  }
  
  public void halt(int paramInt) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkExit(paramInt); 
    Shutdown.halt(paramInt);
  }
  
  @Deprecated
  public static void runFinalizersOnExit(boolean paramBoolean) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkExit(0);
      } catch (SecurityException securityException) {
        throw new SecurityException("runFinalizersOnExit");
      }  
    Shutdown.setRunFinalizersOnExit(paramBoolean);
  }
  
  public Process exec(String paramString) throws IOException { return exec(paramString, null, null); }
  
  public Process exec(String paramString, String[] paramArrayOfString) throws IOException { return exec(paramString, paramArrayOfString, null); }
  
  public Process exec(String paramString, String[] paramArrayOfString, File paramFile) throws IOException {
    if (paramString.length() == 0)
      throw new IllegalArgumentException("Empty command"); 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    String[] arrayOfString = new String[stringTokenizer.countTokens()];
    for (byte b = 0; stringTokenizer.hasMoreTokens(); b++)
      arrayOfString[b] = stringTokenizer.nextToken(); 
    return exec(arrayOfString, paramArrayOfString, paramFile);
  }
  
  public Process exec(String[] paramArrayOfString) throws IOException { return exec(paramArrayOfString, null, null); }
  
  public Process exec(String[] paramArrayOfString1, String[] paramArrayOfString2) throws IOException { return exec(paramArrayOfString1, paramArrayOfString2, null); }
  
  public Process exec(String[] paramArrayOfString1, String[] paramArrayOfString2, File paramFile) throws IOException { return (new ProcessBuilder(paramArrayOfString1)).environment(paramArrayOfString2).directory(paramFile).start(); }
  
  public native int availableProcessors();
  
  public native long freeMemory();
  
  public native long totalMemory();
  
  public native long maxMemory();
  
  public native void gc();
  
  private static native void runFinalization0();
  
  public void runFinalization() { runFinalization0(); }
  
  public native void traceInstructions(boolean paramBoolean);
  
  public native void traceMethodCalls(boolean paramBoolean);
  
  @CallerSensitive
  public void load(String paramString) { load0(Reflection.getCallerClass(), paramString); }
  
  void load0(Class<?> paramClass, String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkLink(paramString); 
    if (!(new File(paramString)).isAbsolute())
      throw new UnsatisfiedLinkError("Expecting an absolute path of the library: " + paramString); 
    ClassLoader.loadLibrary(paramClass, paramString, true);
  }
  
  @CallerSensitive
  public void loadLibrary(String paramString) { loadLibrary0(Reflection.getCallerClass(), paramString); }
  
  void loadLibrary0(Class<?> paramClass, String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkLink(paramString); 
    if (paramString.indexOf(File.separatorChar) != -1)
      throw new UnsatisfiedLinkError("Directory separator should not appear in library name: " + paramString); 
    ClassLoader.loadLibrary(paramClass, paramString, false);
  }
  
  @Deprecated
  public InputStream getLocalizedInputStream(InputStream paramInputStream) { return paramInputStream; }
  
  @Deprecated
  public OutputStream getLocalizedOutputStream(OutputStream paramOutputStream) { return paramOutputStream; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Runtime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */