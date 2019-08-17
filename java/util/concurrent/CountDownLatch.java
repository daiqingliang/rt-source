package java.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CountDownLatch {
  private final Sync sync;
  
  public CountDownLatch(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("count < 0"); 
    this.sync = new Sync(paramInt);
  }
  
  public void await() throws InterruptedException { this.sync.acquireSharedInterruptibly(1); }
  
  public boolean await(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException { return this.sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong)); }
  
  public void countDown() throws InterruptedException { this.sync.releaseShared(1); }
  
  public long getCount() { return this.sync.getCount(); }
  
  public String toString() { return super.toString() + "[Count = " + this.sync.getCount() + "]"; }
  
  private static final class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 4982264981922014374L;
    
    Sync(int param1Int) { setState(param1Int); }
    
    int getCount() { return getState(); }
    
    protected int tryAcquireShared(int param1Int) { return (getState() == 0) ? 1 : -1; }
    
    protected boolean tryReleaseShared(int param1Int) {
      int j;
      int i;
      do {
        i = getState();
        if (i == 0)
          return false; 
        j = i - 1;
      } while (!compareAndSetState(i, j));
      return (j == 0);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CountDownLatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */