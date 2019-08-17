package java.lang.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.function.Consumer;
import sun.misc.VM;

public class ReferenceQueue<T> extends Object {
  static ReferenceQueue<Object> NULL = new Null(null);
  
  static ReferenceQueue<Object> ENQUEUED = new Null(null);
  
  private Lock lock = new Lock(null);
  
  private long queueLength = 0L;
  
  boolean enqueue(Reference<? extends T> paramReference) {
    synchronized (this.lock) {
      ReferenceQueue referenceQueue = paramReference.queue;
      if (referenceQueue == NULL || referenceQueue == ENQUEUED)
        return false; 
      assert referenceQueue == this;
      paramReference.queue = ENQUEUED;
      paramReference.next = (this.head == null) ? paramReference : this.head;
      this.head = paramReference;
      this.queueLength++;
      if (paramReference instanceof FinalReference)
        VM.addFinalRefCount(1); 
      this.lock.notifyAll();
      return true;
    } 
  }
  
  private Reference<? extends T> reallyPoll() {
    Reference reference = this.head;
    if (reference != null) {
      Reference reference1 = reference.next;
      this.head = (reference1 == reference) ? null : reference1;
      reference.queue = NULL;
      reference.next = reference;
      this.queueLength--;
      if (reference instanceof FinalReference)
        VM.addFinalRefCount(-1); 
      return reference;
    } 
    return null;
  }
  
  public Reference<? extends T> poll() {
    if (this.head == null)
      return null; 
    synchronized (this.lock) {
      return reallyPoll();
    } 
  }
  
  public Reference<? extends T> remove(long paramLong) throws IllegalArgumentException, InterruptedException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative timeout value"); 
    synchronized (this.lock) {
      Reference reference = reallyPoll();
      if (reference != null)
        return reference; 
      long l = (paramLong == 0L) ? 0L : System.nanoTime();
      while (true) {
        this.lock.wait(paramLong);
        reference = reallyPoll();
        if (reference != null)
          return reference; 
        if (paramLong != 0L) {
          long l1 = System.nanoTime();
          paramLong -= (l1 - l) / 1000000L;
          if (paramLong <= 0L)
            return null; 
          l = l1;
        } 
      } 
    } 
  }
  
  public Reference<? extends T> remove() { return remove(0L); }
  
  void forEach(Consumer<? super Reference<? extends T>> paramConsumer) {
    for (Reference reference = this.head; reference != null; reference = reference1) {
      paramConsumer.accept(reference);
      Reference reference1 = reference.next;
      if (reference1 == reference) {
        if (reference.queue == ENQUEUED) {
          reference = null;
          continue;
        } 
        reference = this.head;
        continue;
      } 
    } 
  }
  
  private static class Lock {
    private Lock() {}
  }
  
  private static class Null<S> extends ReferenceQueue<S> {
    private Null() {}
    
    boolean enqueue(Reference<? extends S> param1Reference) { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ref\ReferenceQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */