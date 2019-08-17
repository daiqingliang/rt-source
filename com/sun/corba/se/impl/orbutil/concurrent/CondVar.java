package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;

public class CondVar {
  protected boolean debug_;
  
  protected final Sync mutex_;
  
  protected final ReentrantMutex remutex_;
  
  private int releaseMutex() {
    int i = 1;
    if (this.remutex_ != null) {
      i = this.remutex_.releaseAll();
    } else {
      this.mutex_.release();
    } 
    return i;
  }
  
  private void acquireMutex(int paramInt) throws InterruptedException {
    if (this.remutex_ != null) {
      this.remutex_.acquireAll(paramInt);
    } else {
      this.mutex_.acquire();
    } 
  }
  
  public CondVar(Sync paramSync, boolean paramBoolean) {
    this.debug_ = paramBoolean;
    this.mutex_ = paramSync;
    if (paramSync instanceof ReentrantMutex) {
      this.remutex_ = (ReentrantMutex)paramSync;
    } else {
      this.remutex_ = null;
    } 
  }
  
  public CondVar(Sync paramSync) { this(paramSync, false); }
  
  public void await() throws InterruptedException {
    i = 0;
    if (Thread.interrupted())
      throw new InterruptedException(); 
    try {
      if (this.debug_)
        ORBUtility.dprintTrace(this, "await enter"); 
      synchronized (this) {
        i = releaseMutex();
        try {
          wait();
        } catch (InterruptedException interruptedException) {
          notify();
          throw interruptedException;
        } 
      } 
    } finally {
      boolean bool = false;
      while (true) {
        try {
          acquireMutex(i);
          break;
        } catch (InterruptedException interruptedException) {
          bool = true;
        } 
      } 
      if (bool)
        Thread.currentThread().interrupt(); 
      if (this.debug_)
        ORBUtility.dprintTrace(this, "await exit"); 
    } 
  }
  
  public boolean timedwait(long paramLong) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    boolean bool = false;
    i = 0;
    try {
      if (this.debug_)
        ORBUtility.dprintTrace(this, "timedwait enter"); 
      synchronized (this) {
        i = releaseMutex();
        try {
          if (paramLong > 0L) {
            long l = System.currentTimeMillis();
            wait(paramLong);
            bool = (System.currentTimeMillis() - l <= paramLong);
          } 
        } catch (InterruptedException interruptedException) {
          notify();
          throw interruptedException;
        } 
      } 
    } finally {
      boolean bool1 = false;
      while (true) {
        try {
          acquireMutex(i);
          break;
        } catch (InterruptedException interruptedException) {
          bool1 = true;
        } 
      } 
      if (bool1)
        Thread.currentThread().interrupt(); 
      if (this.debug_)
        ORBUtility.dprintTrace(this, "timedwait exit"); 
    } 
    return bool;
  }
  
  public void signal() throws InterruptedException { notify(); }
  
  public void broadcast() throws InterruptedException { notifyAll(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\CondVar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */