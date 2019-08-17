package sun.awt.windows;

import java.awt.Dimension;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.peer.ScrollbarPeer;

final class WScrollbarPeer extends WComponentPeer implements ScrollbarPeer {
  private boolean dragInProgress = false;
  
  static native int getScrollbarSize(int paramInt);
  
  public Dimension getMinimumSize() { return (((Scrollbar)this.target).getOrientation() == 1) ? new Dimension(getScrollbarSize(1), 50) : new Dimension(50, getScrollbarSize(0)); }
  
  public native void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void setLineIncrement(int paramInt);
  
  public native void setPageIncrement(int paramInt);
  
  WScrollbarPeer(Scrollbar paramScrollbar) { super(paramScrollbar); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize() {
    Scrollbar scrollbar = (Scrollbar)this.target;
    setValues(scrollbar.getValue(), scrollbar.getVisibleAmount(), scrollbar.getMinimum(), scrollbar.getMaximum());
    super.initialize();
  }
  
  private void postAdjustmentEvent(final int type, final int value, final boolean isAdjusting) {
    final Scrollbar sb = (Scrollbar)this.target;
    WToolkit.executeOnEventHandlerThread(scrollbar, new Runnable() {
          public void run() {
            sb.setValueIsAdjusting(isAdjusting);
            sb.setValue(value);
            WScrollbarPeer.this.postEvent(new AdjustmentEvent(sb, 601, type, value, isAdjusting));
          }
        });
  }
  
  void lineUp(int paramInt) { postAdjustmentEvent(2, paramInt, false); }
  
  void lineDown(int paramInt) { postAdjustmentEvent(1, paramInt, false); }
  
  void pageUp(int paramInt) { postAdjustmentEvent(3, paramInt, false); }
  
  void pageDown(int paramInt) { postAdjustmentEvent(4, paramInt, false); }
  
  void warp(int paramInt) { postAdjustmentEvent(5, paramInt, false); }
  
  void drag(int paramInt) {
    if (!this.dragInProgress)
      this.dragInProgress = true; 
    postAdjustmentEvent(5, paramInt, true);
  }
  
  void dragEnd(final int value) {
    final Scrollbar sb = (Scrollbar)this.target;
    if (!this.dragInProgress)
      return; 
    this.dragInProgress = false;
    WToolkit.executeOnEventHandlerThread(scrollbar, new Runnable() {
          public void run() {
            sb.setValueIsAdjusting(false);
            WScrollbarPeer.this.postEvent(new AdjustmentEvent(sb, 601, 5, value, false));
          }
        });
  }
  
  public boolean shouldClearRectBeforePaint() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WScrollbarPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */