package sun.misc;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Cleaner extends PhantomReference<Object> {
  private static final ReferenceQueue<Object> dummyQueue = new ReferenceQueue();
  
  private static Cleaner first = null;
  
  private Cleaner next = null;
  
  private Cleaner prev = null;
  
  private final Runnable thunk;
  
  private static Cleaner add(Cleaner paramCleaner) {
    if (first != null) {
      paramCleaner.next = first;
      first.prev = paramCleaner;
    } 
    first = paramCleaner;
    return paramCleaner;
  }
  
  private static boolean remove(Cleaner paramCleaner) {
    if (paramCleaner.next == paramCleaner)
      return false; 
    if (first == paramCleaner)
      if (paramCleaner.next != null) {
        first = paramCleaner.next;
      } else {
        first = paramCleaner.prev;
      }  
    if (paramCleaner.next != null)
      paramCleaner.next.prev = paramCleaner.prev; 
    if (paramCleaner.prev != null)
      paramCleaner.prev.next = paramCleaner.next; 
    paramCleaner.next = paramCleaner;
    paramCleaner.prev = paramCleaner;
    return true;
  }
  
  private Cleaner(Object paramObject, Runnable paramRunnable) {
    super(paramObject, dummyQueue);
    this.thunk = paramRunnable;
  }
  
  public static Cleaner create(Object paramObject, Runnable paramRunnable) { return (paramRunnable == null) ? null : add(new Cleaner(paramObject, paramRunnable)); }
  
  public void clean() {
    if (!remove(this))
      return; 
    try {
      this.thunk.run();
    } catch (Throwable throwable) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              if (System.err != null)
                (new Error("Cleaner terminated abnormally", x)).printStackTrace(); 
              System.exit(1);
              return null;
            }
          });
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Cleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */