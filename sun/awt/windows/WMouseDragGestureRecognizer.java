package sun.awt.windows;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseEvent;
import sun.awt.dnd.SunDragSourceContextPeer;

final class WMouseDragGestureRecognizer extends MouseDragGestureRecognizer {
  private static final long serialVersionUID = -3527844310018033570L;
  
  protected static int motionThreshold;
  
  protected static final int ButtonMask = 7168;
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) { super(paramDragSource, paramComponent, paramInt, paramDragGestureListener); }
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt) { this(paramDragSource, paramComponent, paramInt, null); }
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent) { this(paramDragSource, paramComponent, 0); }
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource) { this(paramDragSource, null); }
  
  protected int mapDragOperationFromModifiers(MouseEvent paramMouseEvent) {
    int i = paramMouseEvent.getModifiersEx();
    int j = i & 0x1C00;
    return (j != 1024 && j != 2048 && j != 4096) ? 0 : SunDragSourceContextPeer.convertModifiersToDropAction(i, getSourceActions());
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    this.events.clear();
    if (mapDragOperationFromModifiers(paramMouseEvent) != 0) {
      try {
        motionThreshold = DragSource.getDragThreshold();
      } catch (Exception exception) {
        motionThreshold = 5;
      } 
      appendEvent(paramMouseEvent);
    } 
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) { this.events.clear(); }
  
  public void mouseEntered(MouseEvent paramMouseEvent) { this.events.clear(); }
  
  public void mouseExited(MouseEvent paramMouseEvent) {
    if (!this.events.isEmpty()) {
      int i = mapDragOperationFromModifiers(paramMouseEvent);
      if (i == 0)
        this.events.clear(); 
    } 
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    if (!this.events.isEmpty()) {
      int i = mapDragOperationFromModifiers(paramMouseEvent);
      if (i == 0)
        return; 
      MouseEvent mouseEvent = (MouseEvent)this.events.get(0);
      Point point1 = mouseEvent.getPoint();
      Point point2 = paramMouseEvent.getPoint();
      int j = Math.abs(point1.x - point2.x);
      int k = Math.abs(point1.y - point2.y);
      if (j > motionThreshold || k > motionThreshold) {
        fireDragGestureRecognized(i, ((MouseEvent)getTriggerEvent()).getPoint());
      } else {
        appendEvent(paramMouseEvent);
      } 
    } 
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WMouseDragGestureRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */