package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class DragGestureEvent extends EventObject {
  private static final long serialVersionUID = 9080172649166731306L;
  
  private List events;
  
  private DragSource dragSource;
  
  private Component component;
  
  private Point origin;
  
  private int action;
  
  public DragGestureEvent(DragGestureRecognizer paramDragGestureRecognizer, int paramInt, Point paramPoint, List<? extends InputEvent> paramList) {
    super(paramDragGestureRecognizer);
    if ((this.component = paramDragGestureRecognizer.getComponent()) == null)
      throw new IllegalArgumentException("null component"); 
    if ((this.dragSource = paramDragGestureRecognizer.getDragSource()) == null)
      throw new IllegalArgumentException("null DragSource"); 
    if (paramList == null || paramList.isEmpty())
      throw new IllegalArgumentException("null or empty list of events"); 
    if (paramInt != 1 && paramInt != 2 && paramInt != 1073741824)
      throw new IllegalArgumentException("bad action"); 
    if (paramPoint == null)
      throw new IllegalArgumentException("null origin"); 
    this.events = paramList;
    this.action = paramInt;
    this.origin = paramPoint;
  }
  
  public DragGestureRecognizer getSourceAsDragGestureRecognizer() { return (DragGestureRecognizer)getSource(); }
  
  public Component getComponent() { return this.component; }
  
  public DragSource getDragSource() { return this.dragSource; }
  
  public Point getDragOrigin() { return this.origin; }
  
  public Iterator<InputEvent> iterator() { return this.events.iterator(); }
  
  public Object[] toArray() { return this.events.toArray(); }
  
  public Object[] toArray(Object[] paramArrayOfObject) { return this.events.toArray(paramArrayOfObject); }
  
  public int getDragAction() { return this.action; }
  
  public InputEvent getTriggerEvent() { return getSourceAsDragGestureRecognizer().getTriggerEvent(); }
  
  public void startDrag(Cursor paramCursor, Transferable paramTransferable) throws InvalidDnDOperationException { this.dragSource.startDrag(this, paramCursor, paramTransferable, null); }
  
  public void startDrag(Cursor paramCursor, Transferable paramTransferable, DragSourceListener paramDragSourceListener) throws InvalidDnDOperationException { this.dragSource.startDrag(this, paramCursor, paramTransferable, paramDragSourceListener); }
  
  public void startDrag(Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener) throws InvalidDnDOperationException { this.dragSource.startDrag(this, paramCursor, paramImage, paramPoint, paramTransferable, paramDragSourceListener); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(this.events) ? this.events : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    List list;
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    DragSource dragSource1 = (DragSource)getField.get("dragSource", null);
    if (dragSource1 == null)
      throw new InvalidObjectException("null DragSource"); 
    this.dragSource = dragSource1;
    Component component1 = (Component)getField.get("component", null);
    if (component1 == null)
      throw new InvalidObjectException("null component"); 
    this.component = component1;
    Point point = (Point)getField.get("origin", null);
    if (point == null)
      throw new InvalidObjectException("null origin"); 
    this.origin = point;
    int i = getField.get("action", 0);
    if (i != 1 && i != 2 && i != 1073741824)
      throw new InvalidObjectException("bad action"); 
    this.action = i;
    try {
      list = (List)getField.get("events", null);
    } catch (IllegalArgumentException illegalArgumentException) {
      list = (List)paramObjectInputStream.readObject();
    } 
    if (list != null && list.isEmpty())
      throw new InvalidObjectException("empty list of events"); 
    if (list == null)
      list = Collections.emptyList(); 
    this.events = list;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DragGestureEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */