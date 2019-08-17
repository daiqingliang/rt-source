package sun.rmi.runtime;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import sun.security.action.GetIntegerAction;

public final class RuntimeUtil {
  private static final Log runtimeLog = Log.getLog("sun.rmi.runtime", null, false);
  
  private static final int schedulerThreads = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.runtime.schedulerThreads", 1))).intValue();
  
  private static final Permission GET_INSTANCE_PERMISSION = new RuntimePermission("sun.rmi.runtime.RuntimeUtil.getInstance");
  
  private static final RuntimeUtil instance = new RuntimeUtil();
  
  private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(schedulerThreads, new ThreadFactory(this) {
        private final AtomicInteger count = new AtomicInteger(0);
        
        public Thread newThread(Runnable param1Runnable) {
          try {
            return (Thread)AccessController.doPrivileged(new NewThreadAction(param1Runnable, "Scheduler(" + this.count.getAndIncrement() + ")", true));
          } catch (Throwable throwable) {
            runtimeLog.log(Level.WARNING, "scheduler thread factory throws", throwable);
            return null;
          } 
        }
      });
  
  private static RuntimeUtil getInstance() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(GET_INSTANCE_PERMISSION); 
    return instance;
  }
  
  public ScheduledThreadPoolExecutor getScheduler() { return this.scheduler; }
  
  public static class GetInstanceAction extends Object implements PrivilegedAction<RuntimeUtil> {
    public RuntimeUtil run() { return RuntimeUtil.getInstance(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\runtime\RuntimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */