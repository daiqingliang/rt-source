package java.util.concurrent.locks;

import java.io.Serializable;

public abstract class AbstractOwnableSynchronizer implements Serializable {
  private static final long serialVersionUID = 3737899427754241961L;
  
  private Thread exclusiveOwnerThread;
  
  protected final void setExclusiveOwnerThread(Thread paramThread) { this.exclusiveOwnerThread = paramThread; }
  
  protected final Thread getExclusiveOwnerThread() { return this.exclusiveOwnerThread; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\AbstractOwnableSynchronizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */