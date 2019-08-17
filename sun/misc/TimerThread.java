package sun.misc;

class TimerThread extends Thread {
  public static boolean debug = false;
  
  static TimerThread timerThread;
  
  static boolean notified = false;
  
  static Timer timerQueue = null;
  
  protected TimerThread() {
    super("TimerThread");
    timerThread = this;
    start();
  }
  
  public void run() {
    while (true) {
      while (timerQueue == null) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
      notified = false;
      long l = timerQueue.sleepUntil - System.currentTimeMillis();
      if (l > 0L)
        try {
          wait(l);
        } catch (InterruptedException interruptedException) {} 
      if (!notified) {
        Timer timer = timerQueue;
        timerQueue = timerQueue.next;
        TimerTickThread timerTickThread = TimerTickThread.call(timer, timer.sleepUntil);
        if (debug) {
          long l1 = System.currentTimeMillis() - timer.sleepUntil;
          System.out.println("tick(" + timerTickThread.getName() + "," + timer.interval + "," + l1 + ")");
          if (l1 > 250L)
            System.out.println("*** BIG DELAY ***"); 
        } 
      } 
    } 
  }
  
  protected static void enqueue(Timer paramTimer) {
    Timer timer1 = null;
    Timer timer2 = timerQueue;
    if (timer2 == null || paramTimer.sleepUntil <= timer2.sleepUntil) {
      paramTimer.next = timerQueue;
      timerQueue = paramTimer;
      notified = true;
      timerThread.notify();
    } else {
      do {
        timer1 = timer2;
        timer2 = timer2.next;
      } while (timer2 != null && paramTimer.sleepUntil > timer2.sleepUntil);
      paramTimer.next = timer2;
      timer1.next = paramTimer;
    } 
    if (debug) {
      long l = System.currentTimeMillis();
      System.out.print(Thread.currentThread().getName() + ": enqueue " + paramTimer.interval + ": ");
      for (timer2 = timerQueue; timer2 != null; timer2 = timer2.next) {
        long l1 = timer2.sleepUntil - l;
        System.out.print(timer2.interval + "(" + l1 + ") ");
      } 
      System.out.println();
    } 
  }
  
  protected static boolean dequeue(Timer paramTimer) {
    Timer timer1 = null;
    Timer timer2;
    for (timer2 = timerQueue; timer2 != null && timer2 != paramTimer; timer2 = timer2.next)
      timer1 = timer2; 
    if (timer2 == null) {
      if (debug)
        System.out.println(Thread.currentThread().getName() + ": dequeue " + paramTimer.interval + ": no-op"); 
      return false;
    } 
    if (timer1 == null) {
      timerQueue = paramTimer.next;
      notified = true;
      timerThread.notify();
    } else {
      timer1.next = paramTimer.next;
    } 
    paramTimer.next = null;
    if (debug) {
      long l = System.currentTimeMillis();
      System.out.print(Thread.currentThread().getName() + ": dequeue " + paramTimer.interval + ": ");
      for (timer2 = timerQueue; timer2 != null; timer2 = timer2.next) {
        long l1 = timer2.sleepUntil - l;
        System.out.print(timer2.interval + "(" + l1 + ") ");
      } 
      System.out.println();
    } 
    return true;
  }
  
  protected static void requeue(Timer paramTimer) {
    if (!paramTimer.stopped) {
      long l = System.currentTimeMillis();
      if (paramTimer.regular) {
        paramTimer.sleepUntil += paramTimer.interval;
      } else {
        paramTimer.sleepUntil = l + paramTimer.interval;
      } 
      enqueue(paramTimer);
    } else if (debug) {
      System.out.println(Thread.currentThread().getName() + ": requeue " + paramTimer.interval + ": no-op");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\TimerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */