package jdk.management.resource.internal;

import java.security.AccessController;
import java.security.Permission;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

final class ThreadMetrics {
  static final WeakKeyConcurrentHashMap<Thread, ThreadMetrics> threadMetrics = new WeakKeyConcurrentHashMap();
  
  private long cputime = 0L;
  
  private long allocatedHeap = 0L;
  
  private long usedCputime(long paramLong) {
    long l = (this.cputime == 0L) ? 0L : (paramLong - this.cputime);
    this.cputime = paramLong;
    return l;
  }
  
  private long usedAllocatedHeap(long paramLong) {
    long l = (this.allocatedHeap == 0L) ? 0L : (paramLong - this.allocatedHeap);
    this.allocatedHeap = paramLong;
    return l;
  }
  
  static void updateCurrentThreadMetrics(ResourceContext paramResourceContext) {
    ResourceRequest resourceRequest1 = paramResourceContext.getResourceRequest(ResourceType.THREAD_CPU);
    ResourceRequest resourceRequest2 = paramResourceContext.getResourceRequest(ResourceType.HEAP_ALLOCATED);
    if (resourceRequest1 == null && resourceRequest2 == null)
      return; 
    updateCurrentThreadMetrics(resourceRequest1, resourceRequest2);
  }
  
  static void updateThreadMetrics(ResourceContext paramResourceContext) {
    ResourceRequest resourceRequest1 = paramResourceContext.getResourceRequest(ResourceType.THREAD_CPU);
    ResourceRequest resourceRequest2 = paramResourceContext.getResourceRequest(ResourceType.HEAP_ALLOCATED);
    if (resourceRequest1 == null && resourceRequest2 == null)
      return; 
    Thread[] arrayOfThread = (Thread[])paramResourceContext.boundThreads().toArray(paramInt -> new Thread[paramInt]);
    if (arrayOfThread.length > 0)
      updateThreadMetrics(arrayOfThread, resourceRequest1, resourceRequest2); 
  }
  
  static void updateThreadMetrics(ResourceContext paramResourceContext, Thread paramThread) {
    ResourceRequest resourceRequest1 = paramResourceContext.getResourceRequest(ResourceType.THREAD_CPU);
    ResourceRequest resourceRequest2 = paramResourceContext.getResourceRequest(ResourceType.HEAP_ALLOCATED);
    if (resourceRequest1 == null && resourceRequest2 == null)
      return; 
    Thread[] arrayOfThread = { paramThread };
    updateThreadMetrics(arrayOfThread, resourceRequest1, resourceRequest2);
  }
  
  private static void updateCurrentThreadMetrics(ResourceRequest paramResourceRequest1, ResourceRequest paramResourceRequest2) {
    long l1 = ResourceNatives.getCurrentThreadCPUTime();
    long l2 = ResourceNatives.getCurrentThreadAllocatedHeap();
    Thread thread = Thread.currentThread();
    ThreadMetrics threadMetrics1;
    (threadMetrics1 = getThreadMetrics(thread)).updateMetrics(threadMetrics1, thread.getId(), paramResourceRequest1, l1, paramResourceRequest2, l2);
  }
  
  private static void updateThreadMetrics(Thread[] paramArrayOfThread, ResourceRequest paramResourceRequest1, ResourceRequest paramResourceRequest2) {
    long[] arrayOfLong1 = new long[paramArrayOfThread.length];
    long[] arrayOfLong2 = new long[paramArrayOfThread.length];
    long[] arrayOfLong3 = new long[paramArrayOfThread.length];
    byte b;
    for (b = 0; b < paramArrayOfThread.length; b++)
      arrayOfLong3[b] = (paramArrayOfThread[b] != null) ? paramArrayOfThread[b].getId() : Float.MIN_VALUE; 
    ResourceNatives.getThreadStats(arrayOfLong3, arrayOfLong1, arrayOfLong2);
    for (b = 0; b < paramArrayOfThread.length; b++) {
      if (arrayOfLong1[b] != -1L && arrayOfLong2[b] != -1L) {
        ThreadMetrics threadMetrics1;
        (threadMetrics1 = getThreadMetrics(paramArrayOfThread[b])).updateMetrics(threadMetrics1, arrayOfLong3[b], paramResourceRequest1, arrayOfLong1[b], paramResourceRequest2, arrayOfLong2[b]);
      } 
    } 
  }
  
  private static void updateMetrics(ThreadMetrics paramThreadMetrics, long paramLong1, ResourceRequest paramResourceRequest1, long paramLong2, ResourceRequest paramResourceRequest2, long paramLong3) {
    long l1 = paramThreadMetrics.usedCputime(paramLong2);
    ResourceIdImpl resourceIdImpl = null;
    if (l1 > 0L && paramResourceRequest1 != null) {
      resourceIdImpl = ResourceIdImpl.of(Long.valueOf(paramLong1));
      try {
        paramResourceRequest1.request(l1, resourceIdImpl);
      } catch (RuntimeException runtimeException) {}
    } 
    long l2 = paramThreadMetrics.usedAllocatedHeap(paramLong3);
    if (l2 > 0L && paramResourceRequest2 != null) {
      if (resourceIdImpl == null)
        resourceIdImpl = ResourceIdImpl.of(Long.valueOf(paramLong1)); 
      try {
        paramResourceRequest2.request(l2, resourceIdImpl);
      } catch (RuntimeException runtimeException) {}
    } 
  }
  
  private static ThreadMetrics getThreadMetrics(Thread paramThread) { return (ThreadMetrics)threadMetrics.computeIfAbsent(paramThread, paramThread -> new ThreadMetrics()); }
  
  static void init() {
    int i = ResourceNatives.sampleInterval();
    if (i != 0) {
      if (i < 0)
        i = 100; 
      ThreadSampler.init(i);
    } 
  }
  
  private static class ThreadSampler implements Runnable {
    private static ThreadSampler samplerRunnable = null;
    
    private static ScheduledFuture<?> samplerFuture = null;
    
    private final long interval;
    
    private static final ScheduledExecutorService scheduledExecutor = (ScheduledExecutorService)AccessController.doPrivileged(() -> {
          ThreadGroup threadGroup1;
          for (threadGroup1 = Thread.currentThread().getThreadGroup(); threadGroup1.getParent() != null; threadGroup1 = threadGroup1.getParent());
          ThreadGroup threadGroup2 = threadGroup1;
          ThreadFactory threadFactory = ();
          return Executors.newScheduledThreadPool(1, threadFactory);
        }null, new Permission[] { new RuntimePermission("modifyThreadGroup"), new RuntimePermission("modifyThread") });
    
    static void init(long param1Long) {
      if (samplerRunnable == null || param1Long != samplerRunnable.interval) {
        terminate();
        samplerRunnable = new ThreadSampler(param1Long);
        samplerFuture = scheduledExecutor.scheduleAtFixedRate(samplerRunnable, param1Long, param1Long, TimeUnit.MILLISECONDS);
      } 
    }
    
    static void terminate() {
      samplerRunnable = null;
      if (samplerFuture != null)
        samplerFuture.cancel(false); 
    }
    
    private ThreadSampler(long param1Long) { this.interval = param1Long; }
    
    public void run() {
      UnassignedContext.getSystemContext().bindThreadContext();
      try {
        SimpleResourceContext.getContexts().forEachValue(2147483647L, param1SimpleResourceContext -> ThreadMetrics.updateThreadMetrics(param1SimpleResourceContext));
      } catch (RuntimeException runtimeException) {}
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\ThreadMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */