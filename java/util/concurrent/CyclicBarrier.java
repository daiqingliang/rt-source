package java.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {
  private final ReentrantLock lock = new ReentrantLock();
  
  private final Condition trip = this.lock.newCondition();
  
  private final int parties;
  
  private final Runnable barrierCommand;
  
  private Generation generation = new Generation(null);
  
  private int count;
  
  private void nextGeneration() {
    this.trip.signalAll();
    this.count = this.parties;
    this.generation = new Generation(null);
  }
  
  private void breakBarrier() {
    this.generation.broken = true;
    this.count = this.parties;
    this.trip.signalAll();
  }
  
  private int dowait(boolean paramBoolean, long paramLong) throws InterruptedException, BrokenBarrierException, TimeoutException {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Generation generation1 = this.generation;
      if (generation1.broken)
        throw new BrokenBarrierException(); 
      if (Thread.interrupted()) {
        breakBarrier();
        throw new InterruptedException();
      } 
      int i = --this.count;
      if (i == 0) {
        bool = false;
        try {
          Runnable runnable = this.barrierCommand;
          if (runnable != null)
            runnable.run(); 
          bool = true;
          nextGeneration();
          return 0;
        } finally {
          if (!bool)
            breakBarrier(); 
        } 
      } 
      do {
        try {
          if (!paramBoolean) {
            this.trip.await();
          } else if (paramLong > 0L) {
            paramLong = this.trip.awaitNanos(paramLong);
          } 
        } catch (InterruptedException interruptedException) {
          if (generation1 == this.generation && !generation1.broken) {
            breakBarrier();
            throw interruptedException;
          } 
          Thread.currentThread().interrupt();
        } 
        if (generation1.broken)
          throw new BrokenBarrierException(); 
        if (generation1 != this.generation)
          return i; 
      } while (!paramBoolean || paramLong > 0L);
      breakBarrier();
      throw new TimeoutException();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public CyclicBarrier(int paramInt, Runnable paramRunnable) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.parties = paramInt;
    this.count = paramInt;
    this.barrierCommand = paramRunnable;
  }
  
  public CyclicBarrier(int paramInt) { this(paramInt, null); }
  
  public int getParties() { return this.parties; }
  
  public int await() {
    try {
      return dowait(false, 0L);
    } catch (TimeoutException timeoutException) {
      throw new Error(timeoutException);
    } 
  }
  
  public int await(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, BrokenBarrierException, TimeoutException { return dowait(true, paramTimeUnit.toNanos(paramLong)); }
  
  public boolean isBroken() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.generation.broken;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void reset() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      breakBarrier();
      nextGeneration();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int getNumberWaiting() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.parties - this.count;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private static class Generation {
    boolean broken = false;
    
    private Generation() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CyclicBarrier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */