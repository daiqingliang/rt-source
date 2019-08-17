package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Font;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.util.List;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.CausedFocusEvent;
import sun.java2d.pipe.Region;

class WPrintDialogPeer extends WWindowPeer implements DialogPeer {
  private WComponentPeer parent;
  
  private Vector<WWindowPeer> blockedWindows = new Vector();
  
  WPrintDialogPeer(WPrintDialog paramWPrintDialog) { super(paramWPrintDialog); }
  
  void create(WComponentPeer paramWComponentPeer) { this.parent = paramWComponentPeer; }
  
  protected void checkCreation() {}
  
  protected void disposeImpl() { WToolkit.targetDisposedPeer(this.target, this); }
  
  private native boolean _show();
  
  public void show() { (new Thread(new Runnable(this) {
          public void run() {
            try {
              ((WPrintDialog)WPrintDialogPeer.this.target).setRetVal(WPrintDialogPeer.this._show());
            } catch (Exception exception) {}
            ((WPrintDialog)WPrintDialogPeer.this.target).setVisible(false);
          }
        })).start(); }
  
  void setHWnd(long paramLong) {
    this.hwnd = paramLong;
    for (WWindowPeer wWindowPeer : this.blockedWindows) {
      if (paramLong != 0L) {
        wWindowPeer.modalDisable((Dialog)this.target, paramLong);
        continue;
      } 
      wWindowPeer.modalEnable((Dialog)this.target);
    } 
  }
  
  void blockWindow(WWindowPeer paramWWindowPeer) {
    this.blockedWindows.add(paramWWindowPeer);
    if (this.hwnd != 0L)
      paramWWindowPeer.modalDisable((Dialog)this.target, this.hwnd); 
  }
  
  void unblockWindow(WWindowPeer paramWWindowPeer) {
    this.blockedWindows.remove(paramWWindowPeer);
    if (this.hwnd != 0L)
      paramWWindowPeer.modalEnable((Dialog)this.target); 
  }
  
  public void blockWindows(List<Window> paramList) {
    for (Window window : paramList) {
      WWindowPeer wWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(window);
      if (wWindowPeer != null)
        blockWindow(wWindowPeer); 
    } 
  }
  
  public native void toFront();
  
  public native void toBack();
  
  void initialize() {}
  
  public void updateAlwaysOnTopState() {}
  
  public void setResizable(boolean paramBoolean) {}
  
  void hide() {}
  
  void enable() {}
  
  void disable() {}
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public boolean handleEvent(Event paramEvent) { return false; }
  
  public void setForeground(Color paramColor) {}
  
  public void setBackground(Color paramColor) {}
  
  public void setFont(Font paramFont) {}
  
  public void updateMinimumSize() {}
  
  public void updateIconImages() {}
  
  public boolean requestFocus(boolean paramBoolean1, boolean paramBoolean2) { return false; }
  
  public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause) { return false; }
  
  public void updateFocusableWindowState() {}
  
  void start() {}
  
  public void beginValidate() {}
  
  public void endValidate() {}
  
  void invalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void addDropTarget(DropTarget paramDropTarget) {}
  
  public void removeDropTarget(DropTarget paramDropTarget) {}
  
  public void setZOrder(ComponentPeer paramComponentPeer) {}
  
  private static native void initIDs();
  
  public void applyShape(Region paramRegion) {}
  
  public void setOpacity(float paramFloat) {}
  
  public void setOpaque(boolean paramBoolean) {}
  
  public void updateWindow(BufferedImage paramBufferedImage) {}
  
  public void createScreenSurface(boolean paramBoolean) {}
  
  public void replaceSurfaceData() {}
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPrintDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */