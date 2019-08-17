package java.awt.dnd;

import java.awt.Point;
import java.util.EventObject;

public class DragSourceEvent extends EventObject {
  private static final long serialVersionUID = -763287114604032641L;
  
  private final boolean locationSpecified = false;
  
  private final int x = 0;
  
  private final int y = 0;
  
  public DragSourceEvent(DragSourceContext paramDragSourceContext) { super(paramDragSourceContext); }
  
  public DragSourceEvent(DragSourceContext paramDragSourceContext, int paramInt1, int paramInt2) { super(paramDragSourceContext); }
  
  public DragSourceContext getDragSourceContext() { return (DragSourceContext)getSource(); }
  
  public Point getLocation() { return this.locationSpecified ? new Point(this.x, this.y) : null; }
  
  public int getX() { return this.x; }
  
  public int getY() { return this.y; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DragSourceEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */