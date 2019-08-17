package sun.awt.windows;

import java.io.FileInputStream;
import java.io.IOException;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetContextPeer;
import sun.awt.dnd.SunDropTargetEvent;

final class WDropTargetContextPeer extends SunDropTargetContextPeer {
  static WDropTargetContextPeer getWDropTargetContextPeer() { return new WDropTargetContextPeer(); }
  
  private static FileInputStream getFileStream(String paramString, long paramLong) throws IOException { return new WDropTargetContextPeerFileStream(paramString, paramLong); }
  
  private static Object getIStream(long paramLong) throws IOException { return new WDropTargetContextPeerIStream(paramLong); }
  
  protected Object getNativeData(long paramLong) throws IOException { return getData(getNativeDragContext(), paramLong); }
  
  protected void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2) { dropDone(getNativeDragContext(), paramBoolean1, paramInt); }
  
  protected void eventPosted(final SunDropTargetEvent e) {
    if (paramSunDropTargetEvent.getID() != 502) {
      Runnable runnable = new Runnable() {
          public void run() { e.getDispatcher().unregisterAllEvents(); }
        };
      PeerEvent peerEvent = new PeerEvent(paramSunDropTargetEvent.getSource(), runnable, 0L);
      SunToolkit.executeOnEventHandlerThread(peerEvent);
    } 
  }
  
  private native Object getData(long paramLong1, long paramLong2);
  
  private native void dropDone(long paramLong, boolean paramBoolean, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WDropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */