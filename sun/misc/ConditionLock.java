package sun.misc;

public final class ConditionLock extends Lock {
  private int state = 0;
  
  public ConditionLock() {}
  
  public ConditionLock(int paramInt) { this.state = paramInt; }
  
  public void lockWhen(int paramInt) {
    while (this.state != paramInt)
      wait(); 
    lock();
  }
  
  public void unlockWith(int paramInt) {
    this.state = paramInt;
    unlock();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ConditionLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */