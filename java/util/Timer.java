package java.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Timer {
  private final TaskQueue queue = new TaskQueue();
  
  private final TimerThread thread = new TimerThread(this.queue);
  
  private final Object threadReaper = new Object() {
      protected void finalize() {
        synchronized (Timer.this.queue) {
          this.this$0.thread.newTasksMayBeScheduled = false;
          Timer.this.queue.notify();
        } 
      }
    };
  
  private static final AtomicInteger nextSerialNumber = new AtomicInteger(0);
  
  private static int serialNumber() { return nextSerialNumber.getAndIncrement(); }
  
  public Timer() { this("Timer-" + serialNumber()); }
  
  public Timer(boolean paramBoolean) { this("Timer-" + serialNumber(), paramBoolean); }
  
  public Timer(String paramString) {
    this.thread.setName(paramString);
    this.thread.start();
  }
  
  public Timer(String paramString, boolean paramBoolean) {
    this.thread.setName(paramString);
    this.thread.setDaemon(paramBoolean);
    this.thread.start();
  }
  
  public void schedule(TimerTask paramTimerTask, long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative delay."); 
    sched(paramTimerTask, System.currentTimeMillis() + paramLong, 0L);
  }
  
  public void schedule(TimerTask paramTimerTask, Date paramDate) { sched(paramTimerTask, paramDate.getTime(), 0L); }
  
  public void schedule(TimerTask paramTimerTask, long paramLong1, long paramLong2) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Negative delay."); 
    if (paramLong2 <= 0L)
      throw new IllegalArgumentException("Non-positive period."); 
    sched(paramTimerTask, System.currentTimeMillis() + paramLong1, -paramLong2);
  }
  
  public void schedule(TimerTask paramTimerTask, Date paramDate, long paramLong) {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("Non-positive period."); 
    sched(paramTimerTask, paramDate.getTime(), -paramLong);
  }
  
  public void scheduleAtFixedRate(TimerTask paramTimerTask, long paramLong1, long paramLong2) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Negative delay."); 
    if (paramLong2 <= 0L)
      throw new IllegalArgumentException("Non-positive period."); 
    sched(paramTimerTask, System.currentTimeMillis() + paramLong1, paramLong2);
  }
  
  public void scheduleAtFixedRate(TimerTask paramTimerTask, Date paramDate, long paramLong) {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("Non-positive period."); 
    sched(paramTimerTask, paramDate.getTime(), paramLong);
  }
  
  private void sched(TimerTask paramTimerTask, long paramLong1, long paramLong2) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Illegal execution time."); 
    if (Math.abs(paramLong2) > 4611686018427387903L)
      paramLong2 >>= true; 
    synchronized (this.queue) {
      if (!this.thread.newTasksMayBeScheduled)
        throw new IllegalStateException("Timer already cancelled."); 
      synchronized (paramTimerTask.lock) {
        if (paramTimerTask.state != 0)
          throw new IllegalStateException("Task already scheduled or cancelled"); 
        paramTimerTask.nextExecutionTime = paramLong1;
        paramTimerTask.period = paramLong2;
        paramTimerTask.state = 1;
      } 
      this.queue.add(paramTimerTask);
      if (this.queue.getMin() == paramTimerTask)
        this.queue.notify(); 
    } 
  }
  
  public void cancel() {
    synchronized (this.queue) {
      this.thread.newTasksMayBeScheduled = false;
      this.queue.clear();
      this.queue.notify();
    } 
  }
  
  public int purge() {
    byte b = 0;
    synchronized (this.queue) {
      for (int i = this.queue.size(); i > 0; i--) {
        if ((this.queue.get(i)).state == 3) {
          this.queue.quickRemove(i);
          b++;
        } 
      } 
      if (b != 0)
        this.queue.heapify(); 
    } 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */