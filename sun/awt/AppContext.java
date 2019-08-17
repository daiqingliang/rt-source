package sun.awt;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import sun.misc.JavaAWTAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public final class AppContext {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.AppContext");
  
  public static final Object EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
  
  public static final Object EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
  
  public static final Object EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
  
  private static final Map<ThreadGroup, AppContext> threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap());
  
  private static final Object getAppContextLock = new GetAppContextLock(null);
  
  private final Map<Object, Object> table = new HashMap();
  
  private final ThreadGroup threadGroup;
  
  private PropertyChangeSupport changeSupport = null;
  
  public static final String DISPOSED_PROPERTY_NAME = "disposed";
  
  public static final String GUI_DISPOSED = "guidisposed";
  
  private static final AtomicInteger numAppContexts = new AtomicInteger(0);
  
  private final ClassLoader contextClassLoader;
  
  private static final ThreadLocal<AppContext> threadAppContext = new ThreadLocal();
  
  private long DISPOSAL_TIMEOUT = 5000L;
  
  private long THREAD_INTERRUPT_TIMEOUT = 1000L;
  
  private MostRecentKeyValue mostRecentKeyValue = null;
  
  private MostRecentKeyValue shadowMostRecentKeyValue = null;
  
  public static Set<AppContext> getAppContexts() {
    synchronized (threadGroup2appContext) {
      return new HashSet(threadGroup2appContext.values());
    } 
  }
  
  public boolean isDisposed() { return (this.state == State.DISPOSED); }
  
  AppContext(ThreadGroup paramThreadGroup) {
    numAppContexts.incrementAndGet();
    this.threadGroup = paramThreadGroup;
    threadGroup2appContext.put(paramThreadGroup, this);
    this.contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return Thread.currentThread().getContextClassLoader(); }
        });
    ReentrantLock reentrantLock = new ReentrantLock();
    put(EVENT_QUEUE_LOCK_KEY, reentrantLock);
    Condition condition = reentrantLock.newCondition();
    put(EVENT_QUEUE_COND_KEY, condition);
  }
  
  private static final void initMainAppContext() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
            for (ThreadGroup threadGroup2 = threadGroup1.getParent(); threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
              threadGroup1 = threadGroup2; 
            mainAppContext = SunToolkit.createNewAppContext(threadGroup1);
            return null;
          }
        }); }
  
  public static final AppContext getAppContext() {
    if (numAppContexts.get() == 1 && mainAppContext != null)
      return mainAppContext; 
    AppContext appContext = (AppContext)threadAppContext.get();
    if (null == appContext)
      appContext = (AppContext)AccessController.doPrivileged(new PrivilegedAction<AppContext>() {
            public AppContext run() {
              ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
              ThreadGroup threadGroup2 = threadGroup1;
              synchronized (getAppContextLock) {
                if (numAppContexts.get() == 0)
                  if (System.getProperty("javaplugin.version") == null && System.getProperty("javawebstart.version") == null) {
                    AppContext.initMainAppContext();
                  } else if (System.getProperty("javafx.version") != null && threadGroup2.getParent() != null) {
                    SunToolkit.createNewAppContext();
                  }  
              } 
              AppContext appContext;
              for (appContext = (AppContext)threadGroup2appContext.get(threadGroup2); appContext == null; appContext = (AppContext)threadGroup2appContext.get(threadGroup2)) {
                threadGroup2 = threadGroup2.getParent();
                if (threadGroup2 == null) {
                  SecurityManager securityManager = System.getSecurityManager();
                  if (securityManager != null) {
                    ThreadGroup threadGroup = securityManager.getThreadGroup();
                    if (threadGroup != null)
                      return (AppContext)threadGroup2appContext.get(threadGroup); 
                  } 
                  return null;
                } 
              } 
              for (ThreadGroup threadGroup3 = threadGroup1; threadGroup3 != threadGroup2; threadGroup3 = threadGroup3.getParent())
                threadGroup2appContext.put(threadGroup3, appContext); 
              threadAppContext.set(appContext);
              return appContext;
            }
          }); 
    return appContext;
  }
  
  public static final boolean isMainContext(AppContext paramAppContext) { return (paramAppContext != null && paramAppContext == mainAppContext); }
  
  private static final AppContext getExecutionAppContext() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && securityManager instanceof AWTSecurityManager) {
      AWTSecurityManager aWTSecurityManager = (AWTSecurityManager)securityManager;
      return aWTSecurityManager.getAppContext();
    } 
    return null;
  }
  
  public void dispose() {
    if (this.threadGroup.parentOf(Thread.currentThread().getThreadGroup()))
      throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed."); 
    synchronized (this) {
      if (this.state != State.VALID)
        return; 
      this.state = State.BEING_DISPOSED;
    } 
    final PropertyChangeSupport changeSupport = this.changeSupport;
    if (propertyChangeSupport != null)
      propertyChangeSupport.firePropertyChange("disposed", false, true); 
    final Object notificationLock = new Object();
    Runnable runnable = new Runnable() {
        public void run() {
          Window[] arrayOfWindow = Window.getOwnerlessWindows();
          for (Window window : arrayOfWindow) {
            try {
              window.dispose();
            } catch (Throwable throwable) {
              log.finer("exception occurred while disposing app context", throwable);
            } 
          } 
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                  if (!GraphicsEnvironment.isHeadless() && SystemTray.isSupported()) {
                    SystemTray systemTray = SystemTray.getSystemTray();
                    TrayIcon[] arrayOfTrayIcon = systemTray.getTrayIcons();
                    for (TrayIcon trayIcon : arrayOfTrayIcon)
                      systemTray.remove(trayIcon); 
                  } 
                  return null;
                }
              });
          if (changeSupport != null)
            changeSupport.firePropertyChange("guidisposed", false, true); 
          synchronized (notificationLock) {
            notificationLock.notifyAll();
          } 
        }
      };
    synchronized (object) {
      SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), runnable));
      try {
        object.wait(this.DISPOSAL_TIMEOUT);
      } catch (InterruptedException interruptedException) {}
    } 
    runnable = new Runnable() {
        public void run() {
          synchronized (notificationLock) {
            notificationLock.notifyAll();
          } 
        }
      };
    synchronized (object) {
      SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), runnable));
      try {
        object.wait(this.DISPOSAL_TIMEOUT);
      } catch (InterruptedException interruptedException) {}
    } 
    synchronized (this) {
      this.state = State.DISPOSED;
    } 
    this.threadGroup.interrupt();
    long l1 = System.currentTimeMillis();
    long l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
    while (this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < l2) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException interruptedException) {}
    } 
    this.threadGroup.stop();
    l1 = System.currentTimeMillis();
    l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
    while (this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < l2) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException interruptedException) {}
    } 
    int i = this.threadGroup.activeGroupCount();
    if (i > 0) {
      ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[i];
      i = this.threadGroup.enumerate(arrayOfThreadGroup);
      for (byte b = 0; b < i; b++)
        threadGroup2appContext.remove(arrayOfThreadGroup[b]); 
    } 
    threadGroup2appContext.remove(this.threadGroup);
    threadAppContext.set(null);
    try {
      this.threadGroup.destroy();
    } catch (IllegalThreadStateException illegalThreadStateException) {}
    synchronized (this.table) {
      this.table.clear();
    } 
    numAppContexts.decrementAndGet();
    this.mostRecentKeyValue = null;
  }
  
  static void stopEventDispatchThreads() {
    for (AppContext appContext : getAppContexts()) {
      if (appContext.isDisposed())
        continue; 
      PostShutdownEventRunnable postShutdownEventRunnable = new PostShutdownEventRunnable(appContext);
      if (appContext != getAppContext()) {
        CreateThreadAction createThreadAction = new CreateThreadAction(appContext, postShutdownEventRunnable);
        Thread thread = (Thread)AccessController.doPrivileged(createThreadAction);
        thread.start();
        continue;
      } 
      postShutdownEventRunnable.run();
    } 
  }
  
  public Object get(Object paramObject) {
    synchronized (this.table) {
      MostRecentKeyValue mostRecentKeyValue1 = this.mostRecentKeyValue;
      if (mostRecentKeyValue1 != null && mostRecentKeyValue1.key == paramObject)
        return mostRecentKeyValue1.value; 
      Object object = this.table.get(paramObject);
      if (this.mostRecentKeyValue == null) {
        this.mostRecentKeyValue = new MostRecentKeyValue(paramObject, object);
        this.shadowMostRecentKeyValue = new MostRecentKeyValue(paramObject, object);
      } else {
        MostRecentKeyValue mostRecentKeyValue2 = this.mostRecentKeyValue;
        this.shadowMostRecentKeyValue.setPair(paramObject, object);
        this.mostRecentKeyValue = this.shadowMostRecentKeyValue;
        this.shadowMostRecentKeyValue = mostRecentKeyValue2;
      } 
      return object;
    } 
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    synchronized (this.table) {
      MostRecentKeyValue mostRecentKeyValue1 = this.mostRecentKeyValue;
      if (mostRecentKeyValue1 != null && mostRecentKeyValue1.key == paramObject1)
        mostRecentKeyValue1.value = paramObject2; 
      return this.table.put(paramObject1, paramObject2);
    } 
  }
  
  public Object remove(Object paramObject) {
    synchronized (this.table) {
      MostRecentKeyValue mostRecentKeyValue1 = this.mostRecentKeyValue;
      if (mostRecentKeyValue1 != null && mostRecentKeyValue1.key == paramObject)
        mostRecentKeyValue1.value = null; 
      return this.table.remove(paramObject);
    } 
  }
  
  public ThreadGroup getThreadGroup() { return this.threadGroup; }
  
  public ClassLoader getContextClassLoader() { return this.contextClassLoader; }
  
  public String toString() { return getClass().getName() + "[threadGroup=" + this.threadGroup.getName() + "]"; }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null)
      return; 
    if (this.changeSupport == null)
      this.changeSupport = new PropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null || this.changeSupport == null)
      return; 
    this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(paramString); }
  
  public static <T> T getSoftReferenceValue(Object paramObject, Supplier<T> paramSupplier) {
    AppContext appContext = getAppContext();
    SoftReference softReference = (SoftReference)appContext.get(paramObject);
    if (softReference != null) {
      Object object1 = softReference.get();
      if (object1 != null)
        return (T)object1; 
    } 
    Object object = paramSupplier.get();
    softReference = new SoftReference(object);
    appContext.put(paramObject, softReference);
    return (T)object;
  }
  
  static  {
    SharedSecrets.setJavaAWTAccess(new JavaAWTAccess() {
          private boolean hasRootThreadGroup(final AppContext ecx) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                  public Boolean run() { return Boolean.valueOf((ecx.threadGroup.getParent() == null)); }
                })).booleanValue(); }
          
          public Object getAppletContext() {
            if (numAppContexts.get() == 0)
              return null; 
            AppContext appContext;
            if (numAppContexts.get() > 0)
              appContext = (appContext != null) ? appContext : AppContext.getAppContext(); 
            boolean bool = (appContext == null || mainAppContext == appContext || (mainAppContext == null && hasRootThreadGroup(appContext))) ? 1 : 0;
            return bool ? null : appContext;
          }
        });
  }
  
  static final class CreateThreadAction extends Object implements PrivilegedAction<Thread> {
    private final AppContext appContext;
    
    private final Runnable runnable;
    
    public CreateThreadAction(AppContext param1AppContext, Runnable param1Runnable) {
      this.appContext = param1AppContext;
      this.runnable = param1Runnable;
    }
    
    public Thread run() {
      Thread thread = new Thread(this.appContext.getThreadGroup(), this.runnable);
      thread.setContextClassLoader(this.appContext.getContextClassLoader());
      thread.setPriority(6);
      thread.setDaemon(true);
      return thread;
    }
  }
  
  private static class GetAppContextLock {
    private GetAppContextLock() {}
  }
  
  static final class PostShutdownEventRunnable implements Runnable {
    private final AppContext appContext;
    
    public PostShutdownEventRunnable(AppContext param1AppContext) { this.appContext = param1AppContext; }
    
    public void run() {
      EventQueue eventQueue = (EventQueue)this.appContext.get(AppContext.EVENT_QUEUE_KEY);
      if (eventQueue != null)
        eventQueue.postEvent(AWTAutoShutdown.getShutdownEvent()); 
    }
  }
  
  private enum State {
    VALID, BEING_DISPOSED, DISPOSED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\AppContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */