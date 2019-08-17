package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.INTERNAL;

public class ReentrantMutex implements Sync {
  protected Thread holder_ = null;
  
  protected int counter_ = 0;
  
  protected boolean debug = false;
  
  public ReentrantMutex() { this(false); }
  
  public ReentrantMutex(boolean paramBoolean) { this.debug = paramBoolean; }
  
  public void acquire() {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      try {
        if (this.debug)
          ORBUtility.dprintTrace(this, "acquire enter: holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
        Thread thread = Thread.currentThread();
        if (this.holder_ != thread)
          try {
            while (this.counter_ > 0)
              wait(); 
            if (this.counter_ != 0)
              throw new INTERNAL("counter not 0 when first acquiring mutex"); 
            this.holder_ = thread;
          } catch (InterruptedException interruptedException) {
            notify();
            throw interruptedException;
          }  
        this.counter_++;
      } finally {
        if (this.debug)
          ORBUtility.dprintTrace(this, "acquire exit: holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
      } 
    } 
  }
  
  void acquireAll(int paramInt) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      try {
        if (this.debug)
          ORBUtility.dprintTrace(this, "acquireAll enter: count=" + paramInt + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
        Thread thread = Thread.currentThread();
        if (this.holder_ == thread)
          throw new INTERNAL("Cannot acquireAll while holding the mutex"); 
        try {
          while (this.counter_ > 0)
            wait(); 
          if (this.counter_ != 0)
            throw new INTERNAL("counter not 0 when first acquiring mutex"); 
          this.holder_ = thread;
        } catch (InterruptedException interruptedException) {
          notify();
          throw interruptedException;
        } 
        this.counter_ = paramInt;
      } finally {
        if (this.debug)
          ORBUtility.dprintTrace(this, "acquireAll exit: count=" + paramInt + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
      } 
    } 
  }
  
  public void release() {
    try {
      if (this.debug)
        ORBUtility.dprintTrace(this, "release enter:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
      Thread thread = Thread.currentThread();
      if (thread != this.holder_)
        throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex"); 
      this.counter_--;
      if (this.counter_ == 0) {
        this.holder_ = null;
        notify();
      } 
    } finally {
      if (this.debug)
        ORBUtility.dprintTrace(this, "release exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
    } 
  }
  
  int releaseAll() {
    try {
      if (this.debug)
        ORBUtility.dprintTrace(this, "releaseAll enter:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
      Thread thread = Thread.currentThread();
      if (thread != this.holder_)
        throw new INTERNAL("Attempt to releaseAll Mutex by thread not holding the Mutex"); 
      int i = this.counter_;
      this.counter_ = 0;
      this.holder_ = null;
      notify();
      return i;
    } finally {
      if (this.debug)
        ORBUtility.dprintTrace(this, "releaseAll exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
    } 
  }
  
  public boolean attempt(long paramLong) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    synchronized (this) {
      try {
        if (this.debug)
          ORBUtility.dprintTrace(this, "attempt enter: msecs=" + paramLong + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
        thread = Thread.currentThread();
        if (this.counter_ == 0) {
          this.holder_ = thread;
          this.counter_ = 1;
          return true;
        } 
        if (paramLong <= 0L)
          return false; 
        l1 = paramLong;
        l2 = System.currentTimeMillis();
      } finally {
        if (this.debug)
          ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\ReentrantMutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */