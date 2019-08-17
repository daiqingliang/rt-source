package sun.awt;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;

public class PaintEventDispatcher {
  private static PaintEventDispatcher dispatcher;
  
  public static void setPaintEventDispatcher(PaintEventDispatcher paramPaintEventDispatcher) {
    synchronized (PaintEventDispatcher.class) {
      dispatcher = paramPaintEventDispatcher;
    } 
  }
  
  public static PaintEventDispatcher getPaintEventDispatcher() {
    synchronized (PaintEventDispatcher.class) {
      if (dispatcher == null)
        dispatcher = new PaintEventDispatcher(); 
      return dispatcher;
    } 
  }
  
  public PaintEvent createPaintEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new PaintEvent(paramComponent, 800, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public boolean shouldDoNativeBackgroundErase(Component paramComponent) { return true; }
  
  public boolean queueSurfaceDataReplacing(Component paramComponent, Runnable paramRunnable) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\PaintEventDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */