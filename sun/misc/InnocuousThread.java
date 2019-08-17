package sun.misc;

import java.security.AccessControlContext;
import java.security.ProtectionDomain;

public final class InnocuousThread extends Thread {
  private static final Unsafe UNSAFE;
  
  private static final ThreadGroup THREADGROUP;
  
  private static final AccessControlContext ACC;
  
  private static final long THREADLOCALS;
  
  private static final long INHERITABLETHREADLOCALS;
  
  private static final long INHERITEDACCESSCONTROLCONTEXT;
  
  public InnocuousThread(Runnable paramRunnable) {
    super(THREADGROUP, paramRunnable, "anInnocuousThread");
    UNSAFE.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, ACC);
    eraseThreadLocals();
  }
  
  public ClassLoader getContextClassLoader() { return ClassLoader.getSystemClassLoader(); }
  
  public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler) {}
  
  public void setContextClassLoader(ClassLoader paramClassLoader) { throw new SecurityException("setContextClassLoader"); }
  
  public void run() {
    if (Thread.currentThread() == this && !this.hasRun) {
      this.hasRun = true;
      super.run();
    } 
  }
  
  public void eraseThreadLocals() {
    UNSAFE.putObject(this, THREADLOCALS, null);
    UNSAFE.putObject(this, INHERITABLETHREADLOCALS, null);
  }
  
  static  {
    try {
      ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
      UNSAFE = Unsafe.getUnsafe();
      Class clazz1 = Thread.class;
      Class clazz2 = ThreadGroup.class;
      THREADLOCALS = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("threadLocals"));
      INHERITABLETHREADLOCALS = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("inheritableThreadLocals"));
      INHERITEDACCESSCONTROLCONTEXT = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("inheritedAccessControlContext"));
      long l1 = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("group"));
      long l2 = UNSAFE.objectFieldOffset(clazz2.getDeclaredField("parent"));
      ThreadGroup threadGroup;
      for (threadGroup = (ThreadGroup)UNSAFE.getObject(Thread.currentThread(), l1); threadGroup != null; threadGroup = threadGroup1) {
        ThreadGroup threadGroup1 = (ThreadGroup)UNSAFE.getObject(threadGroup, l2);
        if (threadGroup1 == null)
          break; 
      } 
      THREADGROUP = new ThreadGroup(threadGroup, "InnocuousThreadGroup");
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\InnocuousThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */