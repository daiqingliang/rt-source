package java.lang.ref;

import java.lang.ref.FinalReference;
import java.lang.ref.Finalizer;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;

final class Finalizer extends FinalReference<Object> {
  private static ReferenceQueue<Object> queue = new ReferenceQueue();
  
  private static Finalizer unfinalized = null;
  
  private static final Object lock = new Object();
  
  private Finalizer next = null;
  
  private Finalizer prev = null;
  
  private boolean hasBeenFinalized() { return (this.next == this); }
  
  private void add() {
    synchronized (lock) {
      if (unfinalized != null) {
        this.next = unfinalized;
        unfinalized.prev = this;
      } 
      unfinalized = this;
    } 
  }
  
  private void remove() {
    synchronized (lock) {
      if (unfinalized == this)
        if (this.next != null) {
          unfinalized = this.next;
        } else {
          unfinalized = this.prev;
        }  
      if (this.next != null)
        this.next.prev = this.prev; 
      if (this.prev != null)
        this.prev.next = this.next; 
      this.next = this;
      this.prev = this;
    } 
  }
  
  private Finalizer(Object paramObject) {
    super(paramObject, queue);
    add();
  }
  
  static ReferenceQueue<Object> getQueue() { return queue; }
  
  static void register(Object paramObject) { new Finalizer(paramObject); }
  
  private void runFinalizer(JavaLangAccess paramJavaLangAccess) {
    synchronized (this) {
      if (hasBeenFinalized())
        return; 
      remove();
    } 
    try {
      Object object = get();
      if (object != null && !(object instanceof Enum)) {
        paramJavaLangAccess.invokeFinalize(object);
        object = null;
      } 
    } catch (Throwable throwable) {}
    clear();
  }
  
  private static void forkSecondaryFinalizer(final Runnable proc) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
            for (ThreadGroup threadGroup2 = threadGroup1; threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
              threadGroup1 = threadGroup2; 
            Thread thread = new Thread(threadGroup1, proc, "Secondary finalizer");
            thread.start();
            try {
              thread.join();
            } catch (InterruptedException interruptedException) {
              Thread.currentThread().interrupt();
            } 
            return null;
          }
        }); }
  
  static void runFinalization() {
    if (!VM.isBooted())
      return; 
    forkSecondaryFinalizer(new Runnable() {
          public void run() {
            if (this.running)
              return; 
            JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
            this.running = true;
            while (true) {
              Finalizer finalizer = (Finalizer)queue.poll();
              if (finalizer == null)
                break; 
              finalizer.runFinalizer(javaLangAccess);
            } 
          }
        });
  }
  
  static void runAllFinalizers() {
    if (!VM.isBooted())
      return; 
    forkSecondaryFinalizer(new Runnable() {
          public void run() {
            if (this.running)
              return; 
            JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
            this.running = true;
            while (true) {
              Finalizer finalizer;
              synchronized (lock) {
                finalizer = unfinalized;
                if (finalizer == null)
                  break; 
                unfinalized = finalizer.next;
              } 
              finalizer.runFinalizer(javaLangAccess);
            } 
          }
        });
  }
  
  static  {
    ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
    for (ThreadGroup threadGroup2 = threadGroup1; threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
      threadGroup1 = threadGroup2; 
    FinalizerThread finalizerThread = new FinalizerThread(threadGroup1);
    finalizerThread.setPriority(8);
    finalizerThread.setDaemon(true);
    finalizerThread.start();
  }
  
  private static class FinalizerThread extends Thread {
    FinalizerThread(ThreadGroup param1ThreadGroup) { super(param1ThreadGroup, "Finalizer"); }
    
    public void run() {
      if (this.running)
        return; 
      while (!VM.isBooted()) {
        try {
          VM.awaitBooted();
        } catch (InterruptedException interruptedException) {}
      } 
      JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
      this.running = true;
      while (true) {
        try {
          while (true) {
            Finalizer finalizer;
            finalizer.runFinalizer(javaLangAccess);
          } 
          break;
        } catch (InterruptedException interruptedException) {}
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ref\Finalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */