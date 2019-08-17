package com.sun.jmx.snmp.tasks;

import java.util.ArrayList;

public class ThreadService implements TaskServer {
  private ArrayList<Runnable> jobList = new ArrayList(0);
  
  private ExecutorThread[] threadList;
  
  private int minThreads = 1;
  
  private int currThreds = 0;
  
  private int idle = 0;
  
  private boolean terminated = false;
  
  private int priority;
  
  private ThreadGroup threadGroup = new ThreadGroup("ThreadService");
  
  private ClassLoader cloader;
  
  private static long counter = 0L;
  
  private int addedJobs = 1;
  
  private int doneJobs = 1;
  
  public ThreadService(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("The thread number should bigger than zero."); 
    this.minThreads = paramInt;
    this.threadList = new ExecutorThread[paramInt];
    this.priority = Thread.currentThread().getPriority();
    this.cloader = Thread.currentThread().getContextClassLoader();
  }
  
  public void submitTask(Task paramTask) throws IllegalArgumentException { submitTask(paramTask); }
  
  public void submitTask(Runnable paramRunnable) throws IllegalArgumentException {
    stateCheck();
    if (paramRunnable == null)
      throw new IllegalArgumentException("No task specified."); 
    synchronized (this.jobList) {
      this.jobList.add(this.jobList.size(), paramRunnable);
      this.jobList.notify();
    } 
    createThread();
  }
  
  public Runnable removeTask(Runnable paramRunnable) {
    stateCheck();
    Runnable runnable = null;
    synchronized (this.jobList) {
      int i = this.jobList.indexOf(paramRunnable);
      if (i >= 0)
        runnable = (Runnable)this.jobList.remove(i); 
    } 
    if (runnable != null && runnable instanceof Task)
      ((Task)runnable).cancel(); 
    return runnable;
  }
  
  public void removeAll() {
    Object[] arrayOfObject;
    stateCheck();
    synchronized (this.jobList) {
      arrayOfObject = this.jobList.toArray();
      this.jobList.clear();
    } 
    int i = arrayOfObject.length;
    for (byte b = 0; b < i; b++) {
      Object object = arrayOfObject[b];
      if (object != null && object instanceof Task)
        ((Task)object).cancel(); 
    } 
  }
  
  public void terminate() {
    if (this.terminated == true)
      return; 
    this.terminated = true;
    synchronized (this.jobList) {
      this.jobList.notifyAll();
    } 
    removeAll();
    for (byte b = 0; b < this.currThreds; b++) {
      try {
        this.threadList[b].interrupt();
      } catch (Exception exception) {}
    } 
    this.threadList = null;
  }
  
  private void stateCheck() {
    if (this.terminated)
      throw new IllegalStateException("The thread service has been terminated."); 
  }
  
  private void createThread() {
    if (this.idle < 1)
      synchronized (this.threadList) {
        if (this.jobList.size() > 0 && this.currThreds < this.minThreads) {
          ExecutorThread executorThread = new ExecutorThread(this);
          executorThread.start();
          this.threadList[this.currThreds++] = executorThread;
        } 
      }  
  }
  
  private class ExecutorThread extends Thread {
    public ExecutorThread(ThreadService this$0) {
      super(this$0.threadGroup, "ThreadService-" + counter++);
      setDaemon(true);
      setPriority(this$0.priority);
      setContextClassLoader(this$0.cloader);
      this$0.idle++;
    }
    
    public void run() {
      while (!this.this$0.terminated) {
        Runnable runnable = null;
        synchronized (this.this$0.jobList) {
          if (this.this$0.jobList.size() > 0) {
            runnable = (Runnable)this.this$0.jobList.remove(0);
            if (this.this$0.jobList.size() > 0)
              this.this$0.jobList.notify(); 
          } else {
            try {
              this.this$0.jobList.wait();
            } catch (InterruptedException interruptedException) {
            
            } finally {}
            continue;
          } 
        } 
        if (runnable != null)
          try {
            this.this$0.idle--;
            runnable.run();
          } catch (Exception exception) {
            exception.printStackTrace();
          } finally {
            this.this$0.idle++;
          }  
        setPriority(this.this$0.priority);
        Thread.interrupted();
        setContextClassLoader(this.this$0.cloader);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\tasks\ThreadService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */