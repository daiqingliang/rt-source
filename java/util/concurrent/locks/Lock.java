package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public interface Lock {
  void lock();
  
  void lockInterruptibly();
  
  boolean tryLock();
  
  boolean tryLock(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  void unlock();
  
  Condition newCondition();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\Lock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */