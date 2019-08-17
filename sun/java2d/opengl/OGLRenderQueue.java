package sun.java2d.opengl;

import java.security.AccessController;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.misc.ThreadGroupUtils;

public class OGLRenderQueue extends RenderQueue {
  private static OGLRenderQueue theInstance;
  
  private final QueueFlusher flusher = (QueueFlusher)AccessController.doPrivileged(() -> new QueueFlusher(ThreadGroupUtils.getRootThreadGroup()));
  
  public static OGLRenderQueue getInstance() {
    if (theInstance == null)
      theInstance = new OGLRenderQueue(); 
    return theInstance;
  }
  
  public static void sync() {
    if (theInstance != null) {
      theInstance.lock();
      try {
        theInstance.ensureCapacity(4);
        theInstance.getBuffer().putInt(76);
        theInstance.flushNow();
      } finally {
        theInstance.unlock();
      } 
    } 
  }
  
  public static void disposeGraphicsConfig(long paramLong) {
    oGLRenderQueue = getInstance();
    oGLRenderQueue.lock();
    try {
      OGLContext.setScratchSurface(paramLong);
      RenderBuffer renderBuffer = oGLRenderQueue.getBuffer();
      oGLRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(74);
      renderBuffer.putLong(paramLong);
      oGLRenderQueue.flushNow();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  public static boolean isQueueFlusherThread() { return (Thread.currentThread() == (getInstance()).flusher); }
  
  public void flushNow() {
    try {
      this.flusher.flushNow();
    } catch (Exception exception) {
      System.err.println("exception in flushNow:");
      exception.printStackTrace();
    } 
  }
  
  public void flushAndInvokeNow(Runnable paramRunnable) {
    try {
      this.flusher.flushAndInvokeNow(paramRunnable);
    } catch (Exception exception) {
      System.err.println("exception in flushAndInvokeNow:");
      exception.printStackTrace();
    } 
  }
  
  private native void flushBuffer(long paramLong, int paramInt);
  
  private void flushBuffer() {
    int i = this.buf.position();
    if (i > 0)
      flushBuffer(this.buf.getAddress(), i); 
    this.buf.clear();
    this.refSet.clear();
  }
  
  private class QueueFlusher extends Thread {
    private boolean needsFlush;
    
    private Runnable task;
    
    private Error error;
    
    public QueueFlusher(ThreadGroup param1ThreadGroup) {
      super(param1ThreadGroup, "Java2D Queue Flusher");
      setDaemon(true);
      setPriority(10);
      start();
    }
    
    public void flushNow() {
      this.needsFlush = true;
      notify();
      while (this.needsFlush) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
      if (this.error != null)
        throw this.error; 
    }
    
    public void flushAndInvokeNow(Runnable param1Runnable) {
      this.task = param1Runnable;
      flushNow();
    }
    
    public void run() {
      bool = false;
      while (true) {
        while (!this.needsFlush) {
          try {
            bool = false;
            wait(100L);
            if (!this.needsFlush && (bool = OGLRenderQueue.this.tryLock())) {
              if (OGLRenderQueue.this.buf.position() > 0) {
                this.needsFlush = true;
                continue;
              } 
              OGLRenderQueue.this.unlock();
            } 
          } catch (InterruptedException interruptedException) {}
        } 
        try {
          this.error = null;
          OGLRenderQueue.this.flushBuffer();
          if (this.task != null)
            this.task.run(); 
        } catch (Error error1) {
          this.error = error1;
        } catch (Exception exception) {
          System.err.println("exception in QueueFlusher:");
          exception.printStackTrace();
        } finally {
          if (bool)
            OGLRenderQueue.this.unlock(); 
          this.task = null;
          this.needsFlush = false;
          notify();
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLRenderQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */