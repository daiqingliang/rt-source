package sun.misc;

class TimerTickThread extends Thread {
  static final int MAX_POOL_SIZE = 3;
  
  static int curPoolSize = 0;
  
  static TimerTickThread pool = null;
  
  TimerTickThread next = null;
  
  Timer timer;
  
  long lastSleepUntil;
  
  protected static TimerTickThread call(Timer paramTimer, long paramLong) {
    TimerTickThread timerTickThread = pool;
    if (timerTickThread == null) {
      timerTickThread = new TimerTickThread();
      timerTickThread.timer = paramTimer;
      timerTickThread.lastSleepUntil = paramLong;
      timerTickThread.start();
    } else {
      pool = pool.next;
      timerTickThread.timer = paramTimer;
      timerTickThread.lastSleepUntil = paramLong;
      synchronized (timerTickThread) {
        timerTickThread.notify();
      } 
    } 
    return timerTickThread;
  }
  
  private boolean returnToPool() {
    synchronized (getClass()) {
      if (curPoolSize >= 3)
        return false; 
      this.next = pool;
      pool = this;
      curPoolSize++;
      this.timer = null;
    } 
    while (this.timer == null) {
      synchronized (this) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
    synchronized (getClass()) {
      curPoolSize--;
    } 
    return true;
  }
  
  public void run() {
    do {
      this.timer.owner.tick(this.timer);
      synchronized (TimerThread.timerThread) {
        synchronized (this.timer) {
          if (this.lastSleepUntil == this.timer.sleepUntil)
            TimerThread.requeue(this.timer); 
        } 
      } 
    } while (returnToPool());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\TimerTickThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */