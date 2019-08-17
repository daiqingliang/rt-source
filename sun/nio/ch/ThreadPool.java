package sun.nio.ch;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import sun.misc.InnocuousThread;
import sun.security.action.GetPropertyAction;

public class ThreadPool {
  private static final String DEFAULT_THREAD_POOL_THREAD_FACTORY = "java.nio.channels.DefaultThreadPool.threadFactory";
  
  private static final String DEFAULT_THREAD_POOL_INITIAL_SIZE = "java.nio.channels.DefaultThreadPool.initialSize";
  
  private final ExecutorService executor;
  
  private final boolean isFixed;
  
  private final int poolSize;
  
  private ThreadPool(ExecutorService paramExecutorService, boolean paramBoolean, int paramInt) {
    this.executor = paramExecutorService;
    this.isFixed = paramBoolean;
    this.poolSize = paramInt;
  }
  
  ExecutorService executor() { return this.executor; }
  
  boolean isFixedThreadPool() { return this.isFixed; }
  
  int poolSize() { return this.poolSize; }
  
  static ThreadFactory defaultThreadFactory() { return (System.getSecurityManager() == null) ? (paramRunnable -> {
        Thread thread = new Thread(paramRunnable);
        thread.setDaemon(true);
        return thread;
      }) : (paramRunnable -> {
        PrivilegedAction privilegedAction = ();
        return (Thread)AccessController.doPrivileged(privilegedAction);
      }); }
  
  static ThreadPool getDefault() { return DefaultThreadPoolHolder.defaultThreadPool; }
  
  static ThreadPool createDefault() {
    int i = getDefaultThreadPoolInitialSize();
    if (i < 0)
      i = Runtime.getRuntime().availableProcessors(); 
    ThreadFactory threadFactory = getDefaultThreadPoolThreadFactory();
    if (threadFactory == null)
      threadFactory = defaultThreadFactory(); 
    ExecutorService executorService = Executors.newCachedThreadPool(threadFactory);
    return new ThreadPool(executorService, false, i);
  }
  
  static ThreadPool create(int paramInt, ThreadFactory paramThreadFactory) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("'nThreads' must be > 0"); 
    ExecutorService executorService = Executors.newFixedThreadPool(paramInt, paramThreadFactory);
    return new ThreadPool(executorService, true, paramInt);
  }
  
  public static ThreadPool wrap(ExecutorService paramExecutorService, int paramInt) {
    if (paramExecutorService == null)
      throw new NullPointerException("'executor' is null"); 
    if (paramExecutorService instanceof ThreadPoolExecutor) {
      int i = ((ThreadPoolExecutor)paramExecutorService).getMaximumPoolSize();
      if (i == Integer.MAX_VALUE)
        if (paramInt < 0) {
          paramInt = Runtime.getRuntime().availableProcessors();
        } else {
          paramInt = 0;
        }  
    } else if (paramInt < 0) {
      paramInt = 0;
    } 
    return new ThreadPool(paramExecutorService, false, paramInt);
  }
  
  private static int getDefaultThreadPoolInitialSize() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.nio.channels.DefaultThreadPool.initialSize"));
    if (str != null)
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        throw new Error("Value of property 'java.nio.channels.DefaultThreadPool.initialSize' is invalid: " + numberFormatException);
      }  
    return -1;
  }
  
  private static ThreadFactory getDefaultThreadPoolThreadFactory() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.nio.channels.DefaultThreadPool.threadFactory"));
    if (str != null)
      try {
        Class clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
        return (ThreadFactory)clazz.newInstance();
      } catch (ClassNotFoundException classNotFoundException) {
        throw new Error(classNotFoundException);
      } catch (InstantiationException instantiationException) {
        throw new Error(instantiationException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new Error(illegalAccessException);
      }  
    return null;
  }
  
  private static class DefaultThreadPoolHolder {
    static final ThreadPool defaultThreadPool = ThreadPool.createDefault();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\ThreadPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */