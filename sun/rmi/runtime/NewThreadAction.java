package sun.rmi.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.util.SecurityConstants;

public final class NewThreadAction extends Object implements PrivilegedAction<Thread> {
  static final ThreadGroup systemThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {
        public ThreadGroup run() {
          ThreadGroup threadGroup1;
          ThreadGroup threadGroup2;
          for (threadGroup1 = Thread.currentThread().getThreadGroup(); (threadGroup2 = threadGroup1.getParent()) != null; threadGroup1 = threadGroup2);
          return threadGroup1;
        }
      });
  
  static final ThreadGroup userThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {
        public ThreadGroup run() { return new ThreadGroup(NewThreadAction.systemThreadGroup, "RMI Runtime"); }
      });
  
  private final ThreadGroup group;
  
  private final Runnable runnable;
  
  private final String name;
  
  private final boolean daemon;
  
  NewThreadAction(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, boolean paramBoolean) {
    this.group = paramThreadGroup;
    this.runnable = paramRunnable;
    this.name = paramString;
    this.daemon = paramBoolean;
  }
  
  public NewThreadAction(Runnable paramRunnable, String paramString, boolean paramBoolean) { this(systemThreadGroup, paramRunnable, paramString, paramBoolean); }
  
  public NewThreadAction(Runnable paramRunnable, String paramString, boolean paramBoolean1, boolean paramBoolean2) { this(paramBoolean2 ? userThreadGroup : systemThreadGroup, paramRunnable, paramString, paramBoolean1); }
  
  public Thread run() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION); 
    Thread thread = new Thread(this.group, this.runnable, "RMI " + this.name);
    thread.setContextClassLoader(ClassLoader.getSystemClassLoader());
    thread.setDaemon(this.daemon);
    return thread;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\runtime\NewThreadAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */