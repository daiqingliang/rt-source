package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.ProtectionDomain;
import sun.misc.Unsafe;

public class ForkJoinWorkerThread extends Thread {
  final ForkJoinPool pool;
  
  final ForkJoinPool.WorkQueue workQueue;
  
  private static final Unsafe U;
  
  private static final long THREADLOCALS;
  
  private static final long INHERITABLETHREADLOCALS;
  
  private static final long INHERITEDACCESSCONTROLCONTEXT;
  
  protected ForkJoinWorkerThread(ForkJoinPool paramForkJoinPool) {
    super("aForkJoinWorkerThread");
    this.pool = paramForkJoinPool;
    this.workQueue = paramForkJoinPool.registerWorker(this);
  }
  
  ForkJoinWorkerThread(ForkJoinPool paramForkJoinPool, ThreadGroup paramThreadGroup, AccessControlContext paramAccessControlContext) {
    super(paramThreadGroup, null, "aForkJoinWorkerThread");
    U.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, paramAccessControlContext);
    eraseThreadLocals();
    this.pool = paramForkJoinPool;
    this.workQueue = paramForkJoinPool.registerWorker(this);
  }
  
  public ForkJoinPool getPool() { return this.pool; }
  
  public int getPoolIndex() { return this.workQueue.getPoolIndex(); }
  
  protected void onStart() {}
  
  protected void onTermination(Throwable paramThrowable) {}
  
  public void run() {
    if (this.workQueue.array == null) {
      throwable = null;
      try {
        onStart();
        this.pool.runWorker(this.workQueue);
      } catch (Throwable throwable1) {
        throwable = throwable1;
      } finally {
        try {
          onTermination(throwable);
        } catch (Throwable throwable1) {
          if (throwable == null)
            throwable = throwable1; 
        } finally {
          this.pool.deregisterWorker(this, throwable);
        } 
      } 
    } 
  }
  
  final void eraseThreadLocals() {
    U.putObject(this, THREADLOCALS, null);
    U.putObject(this, INHERITABLETHREADLOCALS, null);
  }
  
  void afterTopLevelExec() {}
  
  static  {
    try {
      U = Unsafe.getUnsafe();
      Class clazz = Thread.class;
      THREADLOCALS = U.objectFieldOffset(clazz.getDeclaredField("threadLocals"));
      INHERITABLETHREADLOCALS = U.objectFieldOffset(clazz.getDeclaredField("inheritableThreadLocals"));
      INHERITEDACCESSCONTROLCONTEXT = U.objectFieldOffset(clazz.getDeclaredField("inheritedAccessControlContext"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class InnocuousForkJoinWorkerThread extends ForkJoinWorkerThread {
    private static final ThreadGroup innocuousThreadGroup = createThreadGroup();
    
    private static final AccessControlContext INNOCUOUS_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
    
    InnocuousForkJoinWorkerThread(ForkJoinPool param1ForkJoinPool) { super(param1ForkJoinPool, innocuousThreadGroup, INNOCUOUS_ACC); }
    
    void afterTopLevelExec() { eraseThreadLocals(); }
    
    public ClassLoader getContextClassLoader() { return ClassLoader.getSystemClassLoader(); }
    
    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler param1UncaughtExceptionHandler) {}
    
    public void setContextClassLoader(ClassLoader param1ClassLoader) { throw new SecurityException("setContextClassLoader"); }
    
    private static ThreadGroup createThreadGroup() {
      try {
        Unsafe unsafe = Unsafe.getUnsafe();
        Class clazz1 = Thread.class;
        Class clazz2 = ThreadGroup.class;
        long l1 = unsafe.objectFieldOffset(clazz1.getDeclaredField("group"));
        long l2 = unsafe.objectFieldOffset(clazz2.getDeclaredField("parent"));
        for (ThreadGroup threadGroup = (ThreadGroup)unsafe.getObject(Thread.currentThread(), l1); threadGroup != null; threadGroup = threadGroup1) {
          ThreadGroup threadGroup1 = (ThreadGroup)unsafe.getObject(threadGroup, l2);
          if (threadGroup1 == null)
            return new ThreadGroup(threadGroup, "InnocuousForkJoinWorkerThreadGroup"); 
        } 
      } catch (Exception exception) {
        throw new Error(exception);
      } 
      throw new Error("Cannot create ThreadGroup");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ForkJoinWorkerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */