package com.sun.istack.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface Pool<T> {
  @NotNull
  T take();
  
  void recycle(@NotNull T paramT);
  
  public static abstract class Impl<T> extends Object implements Pool<T> {
    @NotNull
    public final T take() {
      Object object = getQueue().poll();
      return (object == null) ? (T)create() : (T)object;
    }
    
    public final void recycle(T param1T) { getQueue().offer(param1T); }
    
    private ConcurrentLinkedQueue<T> getQueue() {
      WeakReference weakReference = this.queue;
      if (weakReference != null) {
        ConcurrentLinkedQueue concurrentLinkedQueue1 = (ConcurrentLinkedQueue)weakReference.get();
        if (concurrentLinkedQueue1 != null)
          return concurrentLinkedQueue1; 
      } 
      ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
      this.queue = new WeakReference(concurrentLinkedQueue);
      return concurrentLinkedQueue;
    }
    
    @NotNull
    protected abstract T create();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */