package java.awt.dnd.peer;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.InvalidDnDOperationException;

public interface DragSourceContextPeer {
  void startDrag(DragSourceContext paramDragSourceContext, Cursor paramCursor, Image paramImage, Point paramPoint) throws InvalidDnDOperationException;
  
  Cursor getCursor();
  
  void setCursor(Cursor paramCursor) throws InvalidDnDOperationException;
  
  void transferablesFlavorsChanged();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\peer\DragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */