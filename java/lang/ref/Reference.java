package java.lang.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import sun.misc.Cleaner;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;

public abstract class Reference<T> extends Object {
  private T referent;
  
  private Reference<T> discovered;
  
  private static Lock lock = new Lock(null);
  
  private static Reference<Object> pending = null;
  
  static boolean tryHandlePending(boolean paramBoolean) {
    Cleaner cleaner;
    Reference reference;
    try {
      synchronized (lock) {
        if (pending != null) {
          reference = pending;
          cleaner = (reference instanceof Cleaner) ? (Cleaner)reference : null;
          pending = reference.discovered;
          reference.discovered = null;
        } else {
          if (paramBoolean)
            lock.wait(); 
          return paramBoolean;
        } 
      } 
    } catch (OutOfMemoryError outOfMemoryError) {
      Thread.yield();
      return true;
    } catch (InterruptedException interruptedException) {
      return true;
    } 
    if (cleaner != null) {
      cleaner.clean();
      return true;
    } 
    ReferenceQueue referenceQueue = reference.queue;
    if (referenceQueue != ReferenceQueue.NULL)
      referenceQueue.enqueue(reference); 
    return true;
  }
  
  public T get() { return (T)this.referent; }
  
  public void clear() { this.referent = null; }
  
  public boolean isEnqueued() { return (this.queue == ReferenceQueue.ENQUEUED); }
  
  public boolean enqueue() { return this.queue.enqueue(this); }
  
  Reference(T paramT) { this(paramT, null); }
  
  Reference(T paramT, ReferenceQueue<? super T> paramReferenceQueue) {
    this.referent = paramT;
    this.queue = (paramReferenceQueue == null) ? ReferenceQueue.NULL : paramReferenceQueue;
  }
  
  static  {
    ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
    for (ThreadGroup threadGroup2 = threadGroup1; threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
      threadGroup1 = threadGroup2; 
    ReferenceHandler referenceHandler = new ReferenceHandler(threadGroup1, "Reference Handler");
    referenceHandler.setPriority(10);
    referenceHandler.setDaemon(true);
    referenceHandler.start();
    SharedSecrets.setJavaLangRefAccess(new JavaLangRefAccess() {
          public boolean tryHandlePendingReference() { return Reference.tryHandlePending(false); }
        });
  }
  
  private static class Lock {
    private Lock() {}
  }
  
  private static class ReferenceHandler extends Thread {
    private static void ensureClassInitialized(Class<?> param1Class) {
      try {
        Class.forName(param1Class.getName(), true, param1Class.getClassLoader());
      } catch (ClassNotFoundException classNotFoundException) {
        throw (Error)(new NoClassDefFoundError(classNotFoundException.getMessage())).initCause(classNotFoundException);
      } 
    }
    
    ReferenceHandler(ThreadGroup param1ThreadGroup, String param1String) { super(param1ThreadGroup, param1String); }
    
    public void run() {
      while (true)
        Reference.tryHandlePending(true); 
    }
    
    static  {
      ensureClassInitialized(InterruptedException.class);
      ensureClassInitialized(Cleaner.class);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ref\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */