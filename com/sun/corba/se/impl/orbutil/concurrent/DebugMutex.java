package com.sun.corba.se.impl.orbutil.concurrent;

import org.omg.CORBA.INTERNAL;

public class DebugMutex implements Sync {
  protected boolean inuse_ = false;
  
  protected Thread holder_ = null;
  
  public void acquire() {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      Thread thread = Thread.currentThread();
      if (this.holder_ == thread)
        throw new INTERNAL("Attempt to acquire Mutex by thread holding the Mutex"); 
      try {
        while (this.inuse_)
          wait(); 
        this.inuse_ = true;
        this.holder_ = Thread.currentThread();
      } catch (InterruptedException interruptedException) {
        notify();
        throw interruptedException;
      } 
    } 
  }
  
  public void release() {
    Thread thread = Thread.currentThread();
    if (thread != this.holder_)
      throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex"); 
    this.holder_ = null;
    this.inuse_ = false;
    notify();
  }
  
  public boolean attempt(long paramLong) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      Thread thread = Thread.currentThread();
      if (!this.inuse_) {
        this.inuse_ = true;
        this.holder_ = thread;
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
            this.holder_ = thread;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\DebugMutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */