package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentEvent;
import sun.awt.LightweightFrame;
import sun.awt.OverrideNativeWindowHandle;
import sun.swing.JLightweightFrame;
import sun.swing.SwingAccessor;

public class WLightweightFramePeer extends WFramePeer implements OverrideNativeWindowHandle {
  public WLightweightFramePeer(LightweightFrame paramLightweightFrame) { super(paramLightweightFrame); }
  
  private LightweightFrame getLwTarget() { return (LightweightFrame)this.target; }
  
  public Graphics getGraphics() { return getLwTarget().getGraphics(); }
  
  private native void overrideNativeHandle(long paramLong);
  
  public void overrideWindowHandle(long paramLong) { overrideNativeHandle(paramLong); }
  
  public void show() {
    super.show();
    postEvent(new ComponentEvent((Component)getTarget(), 102));
  }
  
  public void hide() {
    super.hide();
    postEvent(new ComponentEvent((Component)getTarget(), 103));
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    postEvent(new ComponentEvent((Component)getTarget(), 100));
    postEvent(new ComponentEvent((Component)getTarget(), 101));
  }
  
  public void handleEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent.getID() == 501)
      emulateActivation(true); 
    super.handleEvent(paramAWTEvent);
  }
  
  public void grab() { getLwTarget().grabFocus(); }
  
  public void ungrab() { getLwTarget().ungrabFocus(); }
  
  public void updateCursorImmediately() { SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame)getLwTarget()); }
  
  public boolean isLightweightFramePeer() { return true; }
  
  public void addDropTarget(DropTarget paramDropTarget) { getLwTarget().addDropTarget(paramDropTarget); }
  
  public void removeDropTarget(DropTarget paramDropTarget) { getLwTarget().removeDropTarget(paramDropTarget); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WLightweightFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */