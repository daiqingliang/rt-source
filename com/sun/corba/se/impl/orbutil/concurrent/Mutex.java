package com.sun.corba.se.impl.orbutil.concurrent;

public class Mutex implements Sync {
  protected boolean inuse_ = false;
  
  public void acquire() {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      while (true) {
        try {
          if (this.inuse_) {
            wait();
            continue;
          } 
          this.inuse_ = true;
          break;
        } catch (InterruptedException interruptedException) {
          notify();
          throw interruptedException;
        } 
      } 
    } 
  }
  
  public void release() {
    this.inuse_ = false;
    notify();
  }
  
  public boolean attempt(long paramLong) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      if (!this.inuse_) {
        this.inuse_ = true;
        return true;
      } 
      if (paramLong <= 0L)
        return false; 
      long l1 = paramLong;
      long l2 = System.currentTimeMillis();
      try {
        do {
          wait(l1);
          if (!this.inuse_) {
            this.inuse_ = true;
            return true;
          } 
          l1 = paramLong - System.currentTimeMillis() - l2;
        } while (l1 > 0L);
        return false;
      } catch (InterruptedException interruptedException) {
        notify();
        throw interruptedException;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\Mutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */