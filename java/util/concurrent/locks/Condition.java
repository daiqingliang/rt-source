package java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Condition {
  void await() throws InterruptedException;
  
  void awaitUninterruptibly() throws InterruptedException;
  
  long awaitNanos(long paramLong) throws InterruptedException;
  
  boolean await(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  boolean awaitUntil(Date paramDate) throws InterruptedException;
  
  void signal() throws InterruptedException;
  
  void signalAll() throws InterruptedException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\Condition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */