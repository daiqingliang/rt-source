package com.sun.org.apache.xml.internal.utils;

public class ThreadControllerWrapper {
  private static ThreadController m_tpool = new ThreadController();
  
  public static Thread runThread(Runnable paramRunnable, int paramInt) { return m_tpool.run(paramRunnable, paramInt); }
  
  public static void waitThread(Thread paramThread, Runnable paramRunnable) throws InterruptedException { m_tpool.waitThread(paramThread, paramRunnable); }
  
  public static class ThreadController {
    public Thread run(Runnable param1Runnable, int param1Int) {
      SafeThread safeThread = new SafeThread(param1Runnable);
      safeThread.start();
      return safeThread;
    }
    
    public void waitThread(Thread param1Thread, Runnable param1Runnable) throws InterruptedException { param1Thread.join(); }
    
    final class SafeThread extends Thread {
      public SafeThread(Runnable param2Runnable) { super(param2Runnable); }
      
      public final void run() {
        if (Thread.currentThread() != this)
          throw new IllegalStateException("The run() method in a SafeThread cannot be called from another thread."); 
        synchronized (this) {
          if (!this.ran) {
            this.ran = true;
          } else {
            throw new IllegalStateException("The run() method in a SafeThread cannot be called more than once.");
          } 
        } 
        super.run();
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\ThreadControllerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */