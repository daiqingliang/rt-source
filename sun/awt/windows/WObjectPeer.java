package sun.awt.windows;

import java.util.Map;
import java.util.WeakHashMap;

abstract class WObjectPeer {
  private final Object stateLock = new Object();
  
  public static WObjectPeer getPeerForTarget(Object paramObject) { return (WObjectPeer)WToolkit.targetToPeer(paramObject); }
  
  public long getData() { return this.pData; }
  
  public Object getTarget() { return this.target; }
  
  public final Object getStateLock() { return this.stateLock; }
  
  protected abstract void disposeImpl();
  
  public final void dispose() {
    boolean bool = false;
    synchronized (this) {
      if (!this.disposed)
        this.disposed = bool = true; 
    } 
    if (bool) {
      if (this.childPeers != null)
        disposeChildPeers(); 
      disposeImpl();
    } 
  }
  
  protected final boolean isDisposed() { return this.disposed; }
  
  private static native void initIDs();
  
  final void addChildPeer(WObjectPeer paramWObjectPeer) {
    synchronized (getStateLock()) {
      if (this.childPeers == null)
        this.childPeers = new WeakHashMap(); 
      if (isDisposed())
        throw new IllegalStateException("Parent peer is disposed"); 
      this.childPeers.put(paramWObjectPeer, this);
    } 
  }
  
  private void disposeChildPeers() {
    synchronized (getStateLock()) {
      for (WObjectPeer wObjectPeer : this.childPeers.keySet()) {
        if (wObjectPeer != null)
          try {
            wObjectPeer.dispose();
          } catch (Exception exception) {} 
      } 
    } 
  }
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WObjectPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */