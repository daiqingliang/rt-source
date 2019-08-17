package jdk.management.resource.internal;

import java.security.AccessController;
import java.security.Permission;
import java.util.Arrays;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceMeter;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

class HeapMetrics implements Runnable {
  private static final HeapMetrics instance = new HeapMetrics();
  
  private static ResourceIdImpl[] idWithAccuracy = { null, null, null, (new ResourceIdImpl[4][2] = (new ResourceIdImpl[4][1] = (new ResourceIdImpl[4][0] = ResourceIdImpl.of("Heap", ResourceAccuracy.LOW)).of("Heap", ResourceAccuracy.MEDIUM)).of("Heap", ResourceAccuracy.HIGH)).of("Heap", ResourceAccuracy.HIGHEST) };
  
  private static ResourceIdImpl[] idWithAccuracyForced = { null, null, null, (new ResourceIdImpl[4][2] = (new ResourceIdImpl[4][1] = (new ResourceIdImpl[4][0] = ResourceIdImpl.of("Heap", ResourceAccuracy.LOW, true)).of("Heap", ResourceAccuracy.MEDIUM, true)).of("Heap", ResourceAccuracy.HIGH, true)).of("Heap", ResourceAccuracy.HIGHEST, true) };
  
  static void init() {
    synchronized (instance) {
      if (thread != null) {
        if (thread.isAlive())
          return; 
        terminate();
      } 
      thread = (Thread)AccessController.doPrivileged(() -> {
            ThreadGroup threadGroup;
            for (threadGroup = Thread.currentThread().getThreadGroup(); threadGroup.getParent() != null; threadGroup = threadGroup.getParent());
            ResourceContext resourceContext = SimpleResourceContext.getThreadContext(Thread.currentThread());
            UnassignedContext.getSystemContext().bindThreadContext();
            thread = new Thread(threadGroup, instance, "HeapMetrics");
            thread.setDaemon(true);
            resourceContext.bindThreadContext();
            return thread;
          }null, new Permission[] { new RuntimePermission("modifyThreadGroup"), new RuntimePermission("modifyThread") });
      thread.start();
    } 
  }
  
  static void terminate() {
    synchronized (instance) {
      if (thread != null) {
        Thread thread1 = thread;
        thread = null;
        thread1.interrupt();
      } 
    } 
  }
  
  private ResourceId selectId(int paramInt, boolean paramBoolean) { return paramBoolean ? idWithAccuracyForced[paramInt] : idWithAccuracy[paramInt]; }
  
  public void run() {
    Object object = new Object();
    ResourceNatives.setRetainedMemoryNotificationEnabled(object);
    UnassignedContext.getSystemContext().bindThreadContext();
    UnassignedContext unassignedContext = UnassignedContext.getUnassignedContext();
    long[] arrayOfLong = new long[1];
    int[] arrayOfInt = new int[1];
    byte[] arrayOfByte = new byte[1];
    synchronized (object) {
      while (Thread.currentThread().equals(thread)) {
        try {
          boolean bool = false;
          do {
            SimpleResourceContext[] arrayOfSimpleResourceContext = (SimpleResourceContext[])SimpleResourceContext.getContexts().values().toArray(new SimpleResourceContext[0]);
            int i = arrayOfSimpleResourceContext.length;
            if (i + 1 != arrayOfLong.length) {
              arrayOfLong = new long[i + 1];
              arrayOfInt = new int[i + 1];
              arrayOfByte = new byte[i + 1];
            } 
            byte b;
            for (b = 0; b < i; b++)
              arrayOfInt[b] = arrayOfSimpleResourceContext[b].nativeThreadContext(); 
            arrayOfInt[i] = unassignedContext.nativeThreadContext();
            Arrays.fill(arrayOfLong, -1L);
            bool = ResourceNatives.getContextsRetainedMemory(arrayOfInt, arrayOfLong, arrayOfByte);
            for (b = 0; b <= i; b++) {
              if (arrayOfLong[b] != -1L) {
                SimpleResourceContext simpleResourceContext = (b < i) ? arrayOfSimpleResourceContext[b] : unassignedContext;
                ResourceRequest resourceRequest = simpleResourceContext.getResourceRequest(ResourceType.HEAP_RETAINED);
                if (resourceRequest != null) {
                  long l = arrayOfLong[b] - ((ResourceMeter)resourceRequest).getValue();
                  boolean bool1 = (arrayOfByte[b] >= ResourceAccuracy.HIGH.ordinal());
                  if (l != 0L || bool1)
                    resourceRequest.request(l, selectId(arrayOfByte[b], bool1)); 
                } 
              } 
            } 
          } while (bool);
          object.wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\HeapMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */