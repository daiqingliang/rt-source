package sun.awt;

import java.awt.event.InvocationEvent;

public class PeerEvent extends InvocationEvent {
  public static final long PRIORITY_EVENT = 1L;
  
  public static final long ULTIMATE_PRIORITY_EVENT = 2L;
  
  public static final long LOW_PRIORITY_EVENT = 4L;
  
  private long flags;
  
  public PeerEvent(Object paramObject, Runnable paramRunnable, long paramLong) { this(paramObject, paramRunnable, null, false, paramLong); }
  
  public PeerEvent(Object paramObject1, Runnable paramRunnable, Object paramObject2, boolean paramBoolean, long paramLong) {
    super(paramObject1, paramRunnable, paramObject2, paramBoolean);
    this.flags = paramLong;
  }
  
  public long getFlags() { return this.flags; }
  
  public PeerEvent coalesceEvents(PeerEvent paramPeerEvent) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\PeerEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */