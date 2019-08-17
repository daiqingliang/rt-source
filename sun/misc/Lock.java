package sun.misc;

public class Lock {
  private boolean locked = false;
  
  public final void lock() {
    while (this.locked)
      wait(); 
    this.locked = true;
  }
  
  public final void unlock() {
    this.locked = false;
    notifyAll();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Lock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */