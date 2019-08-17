package java.lang;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class Compiler {
  private static native void initialize();
  
  private static native void registerNatives();
  
  public static native boolean compileClass(Class<?> paramClass);
  
  public static native boolean compileClasses(String paramString);
  
  public static native Object command(Object paramObject);
  
  public static native void enable();
  
  public static native void disable();
  
  static  {
    registerNatives();
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            boolean bool = false;
            String str1 = System.getProperty("java.compiler");
            if (str1 != null && !str1.equals("NONE") && !str1.equals(""))
              try {
                System.loadLibrary(str1);
                Compiler.initialize();
                bool = true;
              } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                System.err.println("Warning: JIT compiler \"" + str1 + "\" not found. Will use interpreter.");
              }  
            String str2 = System.getProperty("java.vm.info");
            if (bool) {
              System.setProperty("java.vm.info", str2 + ", " + str1);
            } else {
              System.setProperty("java.vm.info", str2 + ", nojit");
            } 
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Compiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */