package sun.java2d.d3d;

import sun.java2d.ScreenUpdateManager;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;

public class D3DRenderQueue extends RenderQueue {
  private static D3DRenderQueue theInstance;
  
  private static Thread rqThread;
  
  public static D3DRenderQueue getInstance() {
    if (theInstance == null) {
      theInstance = new D3DRenderQueue();
      theInstance.flushAndInvokeNow(new Runnable() {
            public void run() { rqThread = Thread.currentThread(); }
          });
    } 
    return theInstance;
  }
  
  public static void sync() {
    if (theInstance != null) {
      D3DScreenUpdateManager d3DScreenUpdateManager = (D3DScreenUpdateManager)ScreenUpdateManager.getInstance();
      d3DScreenUpdateManager.runUpdateNow();
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
  
  public static void restoreDevices() {
    d3DRenderQueue = getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.ensureCapacity(4);
      d3DRenderQueue.getBuffer().putInt(77);
      d3DRenderQueue.flushNow();
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public static boolean isRenderQueueThread() { return (Thread.currentThread() == rqThread); }
  
  public static void disposeGraphicsConfig(long paramLong) {
    d3DRenderQueue = getInstance();
    d3DRenderQueue.lock();
    try {
      RenderBuffer renderBuffer = d3DRenderQueue.getBuffer();
      d3DRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(74);
      renderBuffer.putLong(paramLong);
      d3DRenderQueue.flushNow();
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public void flushNow() { flushBuffer(null); }
  
  public void flushAndInvokeNow(Runnable paramRunnable) { flushBuffer(paramRunnable); }
  
  private native void flushBuffer(long paramLong, int paramInt, Runnable paramRunnable);
  
  private void flushBuffer(Runnable paramRunnable) {
    int i = this.buf.position();
    if (i > 0 || paramRunnable != null)
      flushBuffer(this.buf.getAddress(), i, paramRunnable); 
    this.buf.clear();
    this.refSet.clear();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DRenderQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */