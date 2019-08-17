package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolChooser;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManagerImpl implements ThreadPoolManager {
  private ThreadPool threadPool = new ThreadPoolImpl(this.threadGroup, "default-threadpool");
  
  private ThreadGroup threadGroup = getThreadGroup();
  
  private static final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
  
  private static AtomicInteger tgCount = new AtomicInteger();
  
  private ThreadGroup getThreadGroup() {
    ThreadGroup threadGroup1;
    try {
      threadGroup1 = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {
            public ThreadGroup run() {
              ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
              ThreadGroup threadGroup2 = threadGroup1;
              try {
                while (threadGroup2 != null) {
                  threadGroup1 = threadGroup2;
                  threadGroup2 = threadGroup1.getParent();
                } 
              } catch (SecurityException securityException) {}
              return new ThreadGroup(threadGroup1, "ORB ThreadGroup " + tgCount.getAndIncrement());
            }
          });
    } catch (SecurityException securityException) {
      threadGroup1 = Thread.currentThread().getThreadGroup();
    } 
    return threadGroup1;
  }
  
  public void close() {
    try {
      this.threadPool.close();
    } catch (IOException iOException) {
      wrapper.threadPoolCloseError();
    } 
    try {
      boolean bool = this.threadGroup.isDestroyed();
      int i = this.threadGroup.activeCount();
      int j = this.threadGroup.activeGroupCount();
      if (bool) {
        wrapper.threadGroupIsDestroyed(this.threadGroup);
      } else {
        if (i > 0)
          wrapper.threadGroupHasActiveThreadsInClose(this.threadGroup, Integer.valueOf(i)); 
        if (j > 0)
          wrapper.threadGroupHasSubGroupsInClose(this.threadGroup, Integer.valueOf(j)); 
        this.threadGroup.destroy();
      } 
    } catch (IllegalThreadStateException illegalThreadStateException) {
      wrapper.threadGroupDestroyFailed(illegalThreadStateException, this.threadGroup);
    } 
    this.threadGroup = null;
  }
  
  public ThreadPool getThreadPool(String paramString) throws NoSuchThreadPoolException { return this.threadPool; }
  
  public ThreadPool getThreadPool(int paramInt) throws NoSuchThreadPoolException { return this.threadPool; }
  
  public int getThreadPoolNumericId(String paramString) { return 0; }
  
  public String getThreadPoolStringId(int paramInt) { return ""; }
  
  public ThreadPool getDefaultThreadPool() { return this.threadPool; }
  
  public ThreadPoolChooser getThreadPoolChooser(String paramString) { return null; }
  
  public ThreadPoolChooser getThreadPoolChooser(int paramInt) { return null; }
  
  public void setThreadPoolChooser(String paramString, ThreadPoolChooser paramThreadPoolChooser) {}
  
  public int getThreadPoolChooserNumericId(String paramString) { return 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\threadpool\ThreadPoolManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */