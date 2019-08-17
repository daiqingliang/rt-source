package sun.nio.ch;

import java.nio.channels.AsynchronousCloseException;
import java.util.HashMap;
import java.util.Map;
import sun.misc.Unsafe;

class PendingIoCache {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final int addressSize = unsafe.addressSize();
  
  private static final int SIZEOF_OVERLAPPED = dependsArch(20, 32);
  
  private boolean closed;
  
  private boolean closePending;
  
  private final Map<Long, PendingFuture> pendingIoMap = new HashMap();
  
  private long[] overlappedCache = new long[4];
  
  private int overlappedCacheCount = 0;
  
  private static int dependsArch(int paramInt1, int paramInt2) { return (addressSize == 4) ? paramInt1 : paramInt2; }
  
  long add(PendingFuture<?, ?> paramPendingFuture) {
    synchronized (this) {
      long l;
      if (this.closed)
        throw new AssertionError("Should not get here"); 
      if (this.overlappedCacheCount > 0) {
        l = this.overlappedCache[--this.overlappedCacheCount];
      } else {
        l = unsafe.allocateMemory(SIZEOF_OVERLAPPED);
      } 
      this.pendingIoMap.put(Long.valueOf(l), paramPendingFuture);
      return l;
    } 
  }
  
  <V, A> PendingFuture<V, A> remove(long paramLong) {
    synchronized (this) {
      PendingFuture pendingFuture = (PendingFuture)this.pendingIoMap.remove(Long.valueOf(paramLong));
      if (pendingFuture != null) {
        if (this.overlappedCacheCount < this.overlappedCache.length) {
          this.overlappedCache[this.overlappedCacheCount++] = paramLong;
        } else {
          unsafe.freeMemory(paramLong);
        } 
        if (this.closePending)
          notifyAll(); 
      } 
      return pendingFuture;
    } 
  }
  
  void close() {
    synchronized (this) {
      if (this.closed)
        return; 
      if (!this.pendingIoMap.isEmpty())
        clearPendingIoMap(); 
      while (this.overlappedCacheCount > 0)
        unsafe.freeMemory(this.overlappedCache[--this.overlappedCacheCount]); 
      this.closed = true;
    } 
  }
  
  private void clearPendingIoMap() {
    assert Thread.holdsLock(this);
    this.closePending = true;
    try {
      wait(50L);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    } 
    this.closePending = false;
    if (this.pendingIoMap.isEmpty())
      return; 
    for (Long long : this.pendingIoMap.keySet()) {
      PendingFuture pendingFuture = (PendingFuture)this.pendingIoMap.get(long);
      assert !pendingFuture.isDone();
      Iocp iocp = (Iocp)((Groupable)pendingFuture.channel()).group();
      iocp.makeStale(long);
      final Iocp.ResultHandler rh = (Iocp.ResultHandler)pendingFuture.getContext();
      Runnable runnable = new Runnable() {
          public void run() { rh.failed(-1, new AsynchronousCloseException()); }
        };
      iocp.executeOnPooledThread(runnable);
    } 
    this.pendingIoMap.clear();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\PendingIoCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */