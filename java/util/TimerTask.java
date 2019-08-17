package java.util;

public abstract class TimerTask implements Runnable {
  final Object lock = new Object();
  
  int state = 0;
  
  static final int VIRGIN = 0;
  
  static final int SCHEDULED = 1;
  
  static final int EXECUTED = 2;
  
  static final int CANCELLED = 3;
  
  long nextExecutionTime;
  
  long period = 0L;
  
  public abstract void run();
  
  public boolean cancel() {
    synchronized (this.lock) {
      boolean bool = (this.state == 1);
      this.state = 3;
      return bool;
    } 
  }
  
  public long scheduledExecutionTime() {
    synchronized (this.lock) {
      return (this.period < 0L) ? (this.nextExecutionTime + this.period) : (this.nextExecutionTime - this.period);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\TimerTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */