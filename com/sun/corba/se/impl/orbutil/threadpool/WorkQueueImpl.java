package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.util.LinkedList;

public class WorkQueueImpl implements WorkQueue {
  private ThreadPool workerThreadPool;
  
  private LinkedList theWorkQueue = new LinkedList();
  
  private long workItemsAdded = 0L;
  
  private long workItemsDequeued = 1L;
  
  private long totalTimeInQueue = 0L;
  
  private String name;
  
  private MonitoredObject workqueueMonitoredObject;
  
  public WorkQueueImpl() {
    this.name = "default-workqueue";
    initializeMonitoring();
  }
  
  public WorkQueueImpl(ThreadPool paramThreadPool) { this(paramThreadPool, "default-workqueue"); }
  
  public WorkQueueImpl(ThreadPool paramThreadPool, String paramString) {
    this.workerThreadPool = paramThreadPool;
    this.name = paramString;
    initializeMonitoring();
  }
  
  private void initializeMonitoring() {
    this.workqueueMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.name, "Monitoring for a Work Queue");
    LongMonitoredAttributeBase longMonitoredAttributeBase1 = new LongMonitoredAttributeBase("totalWorkItemsAdded", "Total number of Work items added to the Queue") {
        public Object getValue() { return new Long(WorkQueueImpl.this.totalWorkItemsAdded()); }
      };
    this.workqueueMonitoredObject.addAttribute(longMonitoredAttributeBase1);
    LongMonitoredAttributeBase longMonitoredAttributeBase2 = new LongMonitoredAttributeBase("workItemsInQueue", "Number of Work items in the Queue to be processed") {
        public Object getValue() { return new Long(WorkQueueImpl.this.workItemsInQueue()); }
      };
    this.workqueueMonitoredObject.addAttribute(longMonitoredAttributeBase2);
    LongMonitoredAttributeBase longMonitoredAttributeBase3 = new LongMonitoredAttributeBase("averageTimeInQueue", "Average time a work item waits in the work queue") {
        public Object getValue() { return new Long(WorkQueueImpl.this.averageTimeInQueue()); }
      };
    this.workqueueMonitoredObject.addAttribute(longMonitoredAttributeBase3);
  }
  
  MonitoredObject getMonitoredObject() { return this.workqueueMonitoredObject; }
  
  public void addWork(Work paramWork) {
    this.workItemsAdded++;
    paramWork.setEnqueueTime(System.currentTimeMillis());
    this.theWorkQueue.addLast(paramWork);
    ((ThreadPoolImpl)this.workerThreadPool).notifyForAvailableWork(this);
  }
  
  Work requestWork(long paramLong) throws TimeoutException, InterruptedException {
    ((ThreadPoolImpl)this.workerThreadPool).incrementNumberOfAvailableThreads();
    if (this.theWorkQueue.size() != 0) {
      Work work = (Work)this.theWorkQueue.removeFirst();
      this.totalTimeInQueue += System.currentTimeMillis() - work.getEnqueueTime();
      this.workItemsDequeued++;
      ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
      return work;
    } 
    try {
      long l1 = paramLong;
      long l2 = System.currentTimeMillis() + paramLong;
      do {
        wait(l1);
        if (this.theWorkQueue.size() != 0) {
          Work work = (Work)this.theWorkQueue.removeFirst();
          this.totalTimeInQueue += System.currentTimeMillis() - work.getEnqueueTime();
          this.workItemsDequeued++;
          ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
          return work;
        } 
        l1 = l2 - System.currentTimeMillis();
      } while (l1 > 0L);
      ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
      throw new TimeoutException();
    } catch (InterruptedException interruptedException) {
      ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
      throw interruptedException;
    } 
  }
  
  public void setThreadPool(ThreadPool paramThreadPool) { this.workerThreadPool = paramThreadPool; }
  
  public ThreadPool getThreadPool() { return this.workerThreadPool; }
  
  public long totalWorkItemsAdded() { return this.workItemsAdded; }
  
  public int workItemsInQueue() { return this.theWorkQueue.size(); }
  
  public long averageTimeInQueue() { return this.totalTimeInQueue / this.workItemsDequeued; }
  
  public String getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\threadpool\WorkQueueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */