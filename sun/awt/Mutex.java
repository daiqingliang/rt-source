package sun.awt;

public class Mutex {
  private boolean locked;
  
  private Thread owner;
  
  public void lock() {
    if (this.locked && Thread.currentThread() == this.owner)
      throw new IllegalMonitorStateException(); 
    do {
      if (!this.locked) {
        this.locked = true;
        this.owner = Thread.currentThread();
      } else {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } while (this.owner != Thread.currentThread());
  }
  
  public void unlock() {
    if (Thread.currentThread() != this.owner)
      throw new IllegalMonitorStateException(); 
    this.owner = null;
    this.locked = false;
    notify();
  }
  
  protected boolean isOwned() { return (this.locked && Thread.currentThread() == this.owner); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\Mutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */