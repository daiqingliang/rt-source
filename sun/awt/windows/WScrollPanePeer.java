package sun.awt.windows;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.ScrollPaneAdjustable;
import java.awt.peer.ScrollPanePeer;
import sun.awt.AWTAccessor;
import sun.awt.PeerEvent;
import sun.util.logging.PlatformLogger;

final class WScrollPanePeer extends WPanelPeer implements ScrollPanePeer {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WScrollPanePeer");
  
  int scrollbarWidth = _getVScrollbarWidth();
  
  int scrollbarHeight = _getHScrollbarHeight();
  
  int prevx;
  
  int prevy;
  
  static native void initIDs();
  
  native void create(WComponentPeer paramWComponentPeer);
  
  native int getOffset(int paramInt);
  
  WScrollPanePeer(Component paramComponent) { super(paramComponent); }
  
  void initialize() {
    super.initialize();
    setInsets();
    Insets insets = getInsets();
    setScrollPosition(-insets.left, -insets.top);
  }
  
  public void setUnitIncrement(Adjustable paramAdjustable, int paramInt) {}
  
  public Insets insets() { return getInsets(); }
  
  private native void setInsets();
  
  public native void setScrollPosition(int paramInt1, int paramInt2);
  
  public int getHScrollbarHeight() { return this.scrollbarHeight; }
  
  private native int _getHScrollbarHeight();
  
  public int getVScrollbarWidth() { return this.scrollbarWidth; }
  
  private native int _getVScrollbarWidth();
  
  public Point getScrollOffset() {
    int i = getOffset(0);
    int j = getOffset(1);
    return new Point(i, j);
  }
  
  public void childResized(int paramInt1, int paramInt2) {
    ScrollPane scrollPane = (ScrollPane)this.target;
    Dimension dimension = scrollPane.getSize();
    setSpans(dimension.width, dimension.height, paramInt1, paramInt2);
    setInsets();
  }
  
  native void setSpans(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public void setValue(Adjustable paramAdjustable, int paramInt) {
    Component component = getScrollChild();
    if (component == null)
      return; 
    Point point = component.getLocation();
    switch (paramAdjustable.getOrientation()) {
      case 1:
        setScrollPosition(-point.x, paramInt);
        break;
      case 0:
        setScrollPosition(paramInt, -point.y);
        break;
    } 
  }
  
  private Component getScrollChild() {
    ScrollPane scrollPane = (ScrollPane)this.target;
    Component component = null;
    try {
      component = scrollPane.getComponent(0);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    return component;
  }
  
  private void postScrollEvent(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    Adjustor adjustor = new Adjustor(paramInt1, paramInt2, paramInt3, paramBoolean);
    WToolkit.executeOnEventHandlerThread(new ScrollEvent(this.target, adjustor));
  }
  
  static  {
    initIDs();
  }
  
  class Adjustor implements Runnable {
    int orient;
    
    int type;
    
    int pos;
    
    boolean isAdjusting;
    
    Adjustor(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      this.orient = param1Int1;
      this.type = param1Int2;
      this.pos = param1Int3;
      this.isAdjusting = param1Boolean;
    }
    
    public void run() {
      if (WScrollPanePeer.this.getScrollChild() == null)
        return; 
      ScrollPane scrollPane = (ScrollPane)WScrollPanePeer.this.target;
      ScrollPaneAdjustable scrollPaneAdjustable = null;
      if (this.orient == 1) {
        scrollPaneAdjustable = (ScrollPaneAdjustable)scrollPane.getVAdjustable();
      } else if (this.orient == 0) {
        scrollPaneAdjustable = (ScrollPaneAdjustable)scrollPane.getHAdjustable();
      } else if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("Assertion failed: unknown orient");
      } 
      if (scrollPaneAdjustable == null)
        return; 
      int i = scrollPaneAdjustable.getValue();
      switch (this.type) {
        case 2:
          i -= scrollPaneAdjustable.getUnitIncrement();
          break;
        case 1:
          i += scrollPaneAdjustable.getUnitIncrement();
          break;
        case 3:
          i -= scrollPaneAdjustable.getBlockIncrement();
          break;
        case 4:
          i += scrollPaneAdjustable.getBlockIncrement();
          break;
        case 5:
          i = this.pos;
          break;
        default:
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("Assertion failed: unknown type"); 
          return;
      } 
      i = Math.max(scrollPaneAdjustable.getMinimum(), i);
      i = Math.min(scrollPaneAdjustable.getMaximum(), i);
      scrollPaneAdjustable.setValueIsAdjusting(this.isAdjusting);
      AWTAccessor.getScrollPaneAdjustableAccessor().setTypedValue(scrollPaneAdjustable, i, this.type);
      Component component;
      for (component = WScrollPanePeer.this.getScrollChild(); component != null && !(component.getPeer() instanceof WComponentPeer); component = component.getParent());
      if (log.isLoggable(PlatformLogger.Level.FINE) && component == null)
        log.fine("Assertion (hwAncestor != null) failed, couldn't find heavyweight ancestor of scroll pane child"); 
      WComponentPeer wComponentPeer = (WComponentPeer)component.getPeer();
      wComponentPeer.paintDamagedAreaImmediately();
    }
  }
  
  class ScrollEvent extends PeerEvent {
    ScrollEvent(Object param1Object, Runnable param1Runnable) { super(param1Object, param1Runnable, 0L); }
    
    public PeerEvent coalesceEvents(PeerEvent param1PeerEvent) {
      if (log.isLoggable(PlatformLogger.Level.FINEST))
        log.finest("ScrollEvent coalesced: " + param1PeerEvent); 
      return (param1PeerEvent instanceof ScrollEvent) ? param1PeerEvent : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WScrollPanePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */