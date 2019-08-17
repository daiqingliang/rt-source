package sun.awt.image;

import java.awt.image.BufferStrategy;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class VSyncedBSManager {
  private static VSyncedBSManager theInstance;
  
  private static final boolean vSyncLimit = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.vsynclimit", "true"))).booleanValue();
  
  private static VSyncedBSManager getInstance(boolean paramBoolean) {
    if (theInstance == null && paramBoolean)
      theInstance = vSyncLimit ? new SingleVSyncedBSMgr(null) : new NoLimitVSyncBSMgr(null); 
    return theInstance;
  }
  
  abstract boolean checkAllowed(BufferStrategy paramBufferStrategy);
  
  abstract void relinquishVsync(BufferStrategy paramBufferStrategy);
  
  public static boolean vsyncAllowed(BufferStrategy paramBufferStrategy) {
    VSyncedBSManager vSyncedBSManager = getInstance(true);
    return vSyncedBSManager.checkAllowed(paramBufferStrategy);
  }
  
  public static void releaseVsync(BufferStrategy paramBufferStrategy) {
    VSyncedBSManager vSyncedBSManager = getInstance(false);
    if (vSyncedBSManager != null)
      vSyncedBSManager.relinquishVsync(paramBufferStrategy); 
  }
  
  private static final class NoLimitVSyncBSMgr extends VSyncedBSManager {
    private NoLimitVSyncBSMgr() {}
    
    boolean checkAllowed(BufferStrategy param1BufferStrategy) { return true; }
    
    void relinquishVsync(BufferStrategy param1BufferStrategy) {}
  }
  
  private static final class SingleVSyncedBSMgr extends VSyncedBSManager {
    private WeakReference<BufferStrategy> strategy;
    
    private SingleVSyncedBSMgr() {}
    
    public boolean checkAllowed(BufferStrategy param1BufferStrategy) {
      if (this.strategy != null) {
        BufferStrategy bufferStrategy = (BufferStrategy)this.strategy.get();
        if (bufferStrategy != null)
          return (bufferStrategy == param1BufferStrategy); 
      } 
      this.strategy = new WeakReference(param1BufferStrategy);
      return true;
    }
    
    public void relinquishVsync(BufferStrategy param1BufferStrategy) {
      if (this.strategy != null) {
        BufferStrategy bufferStrategy = (BufferStrategy)this.strategy.get();
        if (bufferStrategy == param1BufferStrategy) {
          this.strategy.clear();
          this.strategy = null;
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\VSyncedBSManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */