package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.io.Closeable;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolImpl implements ThreadPool {
  private static AtomicInteger threadCounter = new AtomicInteger(0);
  
  private static final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
  
  private WorkQueue workQueue;
  
  private int availableWorkerThreads = 0;
  
  private int currentThreadCount = 0;
  
  private int minWorkerThreads = 0;
  
  private int maxWorkerThreads = 0;
  
  private long inactivityTimeout;
  
  private boolean boundedThreadPool = false;
  
  private AtomicLong processedCount = new AtomicLong(1L);
  
  private AtomicLong totalTimeTaken = new AtomicLong(0L);
  
  private String name;
  
  private MonitoredObject threadpoolMonitoredObject;
  
  private ThreadGroup threadGroup;
  
  Object workersLock = new Object();
  
  List<WorkerThread> workers = new ArrayList();
  
  public ThreadPoolImpl(ThreadGroup paramThreadGroup, String paramString) {
    this.inactivityTimeout = 120000L;
    this.maxWorkerThreads = Integer.MAX_VALUE;
    this.workQueue = new WorkQueueImpl(this);
    this.threadGroup = paramThreadGroup;
    this.name = paramString;
    initializeMonitoring();
  }
  
  public ThreadPoolImpl(String paramString) { this(Thread.currentThread().getThreadGroup(), paramString); }
  
  public ThreadPoolImpl(int paramInt1, int paramInt2, long paramLong, String paramString) {
    this.minWorkerThreads = paramInt1;
    this.maxWorkerThreads = paramInt2;
    this.inactivityTimeout = paramLong;
    this.boundedThreadPool = true;
    this.workQueue = new WorkQueueImpl(this);
    this.name = paramString;
    for (byte b = 0; b < this.minWorkerThreads; b++)
      createWorkerThread(); 
    initializeMonitoring();
  }
  
  public void close() throws IOException {
    ArrayList arrayList = null;
    synchronized (this.workersLock) {
      arrayList = new ArrayList(this.workers);
    } 
    for (WorkerThread workerThread : arrayList) {
      workerThread.close();
      while (workerThread.getState() != Thread.State.TERMINATED) {
        try {
          workerThread.join();
        } catch (InterruptedException interruptedException) {
          wrapper.interruptedJoinCallWhileClosingThreadPool(interruptedException, workerThread, this);
        } 
      } 
    } 
    this.threadGroup = null;
  }
  
  private void initializeMonitoring() throws IOException {
    MonitoredObject monitoredObject1 = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", null).getRootMonitoredObject();
    MonitoredObject monitoredObject2 = monitoredObject1.getChild("threadpool");
    if (monitoredObject2 == null) {
      monitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("threadpool", "Monitoring for all ThreadPool instances");
      monitoredObject1.addChild(monitoredObject2);
    } 
    this.threadpoolMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.name, "Monitoring for a ThreadPool");
    monitoredObject2.addChild(this.threadpoolMonitoredObject);
    LongMonitoredAttributeBase longMonitoredAttributeBase1 = new LongMonitoredAttributeBase("currentNumberOfThreads", "Current number of total threads in the ThreadPool") {
        public Object getValue() { return new Long(ThreadPoolImpl.this.currentNumberOfThreads()); }
      };
    this.threadpoolMonitoredObject.addAttribute(longMonitoredAttributeBase1);
    LongMonitoredAttributeBase longMonitoredAttributeBase2 = new LongMonitoredAttributeBase("numberOfAvailableThreads", "Current number of total threads in the ThreadPool") {
        public Object getValue() { return new Long(ThreadPoolImpl.this.numberOfAvailableThreads()); }
      };
    this.threadpoolMonitoredObject.addAttribute(longMonitoredAttributeBase2);
    LongMonitoredAttributeBase longMonitoredAttributeBase3 = new LongMonitoredAttributeBase("numberOfBusyThreads", "Number of busy threads in the ThreadPool") {
        public Object getValue() { return new Long(ThreadPoolImpl.this.numberOfBusyThreads()); }
      };
    this.threadpoolMonitoredObject.addAttribute(longMonitoredAttributeBase3);
    LongMonitoredAttributeBase longMonitoredAttributeBase4 = new LongMonitoredAttributeBase("averageWorkCompletionTime", "Average elapsed time taken to complete a work item by the ThreadPool") {
        public Object getValue() { return new Long(ThreadPoolImpl.this.averageWorkCompletionTime()); }
      };
    this.threadpoolMonitoredObject.addAttribute(longMonitoredAttributeBase4);
    LongMonitoredAttributeBase longMonitoredAttributeBase5 = new LongMonitoredAttributeBase("currentProcessedCount", "Number of Work items processed by the ThreadPool") {
        public Object getValue() { return new Long(ThreadPoolImpl.this.currentProcessedCount()); }
      };
    this.threadpoolMonitoredObject.addAttribute(longMonitoredAttributeBase5);
    this.threadpoolMonitoredObject.addChild(((WorkQueueImpl)this.workQueue).getMonitoredObject());
  }
  
  MonitoredObject getMonitoredObject() { return this.threadpoolMonitoredObject; }
  
  public WorkQueue getAnyWorkQueue() { return this.workQueue; }
  
  public WorkQueue getWorkQueue(int paramInt) throws NoSuchWorkQueueException {
    if (paramInt != 0)
      throw new NoSuchWorkQueueException(); 
    return this.workQueue;
  }
  
  void notifyForAvailableWork(WorkQueue paramWorkQueue) {
    synchronized (paramWorkQueue) {
      if (this.availableWorkerThreads < paramWorkQueue.workItemsInQueue()) {
        createWorkerThread();
      } else {
        paramWorkQueue.notify();
      } 
    } 
  }
  
  private Thread createWorkerThreadHelper(String paramString) {
    WorkerThread workerThread = new WorkerThread(this.threadGroup, paramString);
    synchronized (this.workersLock) {
      this.workers.add(workerThread);
    } 
    workerThread.setDaemon(true);
    wrapper.workerThreadCreated(workerThread, workerThread.getContextClassLoader());
    workerThread.start();
    return null;
  }
  
  void createWorkerThread() throws IOException {
    final String name = getName();
    synchronized (this.workQueue) {
      try {
        if (System.getSecurityManager() == null) {
          createWorkerThreadHelper(str);
        } else {
          AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() { return ThreadPoolImpl.this.createWorkerThreadHelper(name); }
              });
        } 
      } catch (Throwable throwable) {
        decrementCurrentNumberOfThreads();
        wrapper.workerThreadCreationFailure(throwable);
      } finally {
        incrementCurrentNumberOfThreads();
      } 
    } 
  }
  
  public int minimumNumberOfThreads() { return this.minWorkerThreads; }
  
  public int maximumNumberOfThreads() { return this.maxWorkerThreads; }
  
  public long idleTimeoutForThreads() { return this.inactivityTimeout; }
  
  public int currentNumberOfThreads() {
    synchronized (this.workQueue) {
      return this.currentThreadCount;
    } 
  }
  
  void decrementCurrentNumberOfThreads() throws IOException {
    synchronized (this.workQueue) {
      this.currentThreadCount--;
    } 
  }
  
  void incrementCurrentNumberOfThreads() throws IOException {
    synchronized (this.workQueue) {
      this.currentThreadCount++;
    } 
  }
  
  public int numberOfAvailableThreads() {
    synchronized (this.workQueue) {
      return this.availableWorkerThreads;
    } 
  }
  
  public int numberOfBusyThreads() {
    synchronized (this.workQueue) {
      return this.currentThreadCount - this.availableWorkerThreads;
    } 
  }
  
  public long averageWorkCompletionTime() {
    synchronized (this.workQueue) {
      return this.totalTimeTaken.get() / this.processedCount.get();
    } 
  }
  
  public long currentProcessedCount() {
    synchronized (this.workQueue) {
      return this.processedCount.get();
    } 
  }
  
  public String getName() { return this.name; }
  
  public int numberOfWorkQueues() { return 1; }
  
  private static int getUniqueThreadId() { return threadCounter.incrementAndGet(); }
  
  void decrementNumberOfAvailableThreads() throws IOException {
    synchronized (this.workQueue) {
      this.availableWorkerThreads--;
    } 
  }
  
  void incrementNumberOfAvailableThreads() throws IOException {
    synchronized (this.workQueue) {
      this.availableWorkerThreads++;
    } 
  }
  
  private class WorkerThread extends Thread implements Closeable {
    private Work currentWork;
    
    private int threadId = 0;
    
    private String threadPoolName;
    
    private StringBuffer workerThreadName = new StringBuffer();
    
    WorkerThread(ThreadGroup param1ThreadGroup, String param1String) {
      super(param1ThreadGroup, "Idle");
      this.threadId = ThreadPoolImpl.getUniqueThreadId();
      this.threadPoolName = param1String;
      setName(composeWorkerThreadName(param1String, "Idle"));
    }
    
    public void close() throws IOException {
      this.closeCalled = true;
      interrupt();
    }
    
    private void resetClassLoader() throws IOException {}
    
    private void performWork() throws IOException {
      long l1 = System.currentTimeMillis();
      try {
        this.currentWork.doWork();
      } catch (Throwable throwable) {
        wrapper.workerThreadDoWorkThrowable(this, throwable);
      } 
      long l2 = System.currentTimeMillis() - l1;
      ThreadPoolImpl.this.totalTimeTaken.addAndGet(l2);
      ThreadPoolImpl.this.processedCount.incrementAndGet();
    }
    
    public void run() throws IOException {
      try {
        while (!this.closeCalled) {
          try {
            this.currentWork = ((WorkQueueImpl)ThreadPoolImpl.this.workQueue).requestWork(ThreadPoolImpl.this.inactivityTimeout);
            if (this.currentWork == null)
              continue; 
          } catch (InterruptedException interruptedException) {
            wrapper.workQueueThreadInterrupted(interruptedException, getName(), Boolean.valueOf(this.closeCalled));
            continue;
          } catch (Throwable throwable) {
            wrapper.workerThreadThrowableFromRequestWork(this, throwable, ThreadPoolImpl.this.workQueue.getName());
            continue;
          } 
          performWork();
          this.currentWork = null;
          resetClassLoader();
        } 
      } catch (Throwable throwable) {
        wrapper.workerThreadCaughtUnexpectedThrowable(this, throwable);
      } finally {
        synchronized (ThreadPoolImpl.this.workersLock) {
          ThreadPoolImpl.this.workers.remove(this);
        } 
      } 
    }
    
    private String composeWorkerThreadName(String param1String1, String param1String2) {
      this.workerThreadName.setLength(0);
      this.workerThreadName.append("p: ").append(param1String1);
      this.workerThreadName.append("; w: ").append(param1String2);
      return this.workerThreadName.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\threadpool\ThreadPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */