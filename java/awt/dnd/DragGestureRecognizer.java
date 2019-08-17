package java.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TooManyListenersException;

public abstract class DragGestureRecognizer implements Serializable {
  private static final long serialVersionUID = 8996673345831063337L;
  
  protected DragSource dragSource;
  
  protected Component component;
  
  protected DragGestureListener dragGestureListener;
  
  protected int sourceActions;
  
  protected ArrayList<InputEvent> events = new ArrayList(1);
  
  protected DragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) {
    if (paramDragSource == null)
      throw new IllegalArgumentException("null DragSource"); 
    this.dragSource = paramDragSource;
    this.component = paramComponent;
    this.sourceActions = paramInt & 0x40000003;
    try {
      if (paramDragGestureListener != null)
        addDragGestureListener(paramDragGestureListener); 
    } catch (TooManyListenersException tooManyListenersException) {}
  }
  
  protected DragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt) { this(paramDragSource, paramComponent, paramInt, null); }
  
  protected DragGestureRecognizer(DragSource paramDragSource, Component paramComponent) { this(paramDragSource, paramComponent, 0); }
  
  protected DragGestureRecognizer(DragSource paramDragSource) { this(paramDragSource, null); }
  
  protected abstract void registerListeners();
  
  protected abstract void unregisterListeners();
  
  public DragSource getDragSource() { return this.dragSource; }
  
  public Component getComponent() { return this.component; }
  
  public void setComponent(Component paramComponent) {
    if (this.component != null && this.dragGestureListener != null)
      unregisterListeners(); 
    this.component = paramComponent;
    if (this.component != null && this.dragGestureListener != null)
      registerListeners(); 
  }
  
  public int getSourceActions() { return this.sourceActions; }
  
  public void setSourceActions(int paramInt) { this.sourceActions = paramInt & 0x40000003; }
  
  public InputEvent getTriggerEvent() { return this.events.isEmpty() ? null : (InputEvent)this.events.get(0); }
  
  public void resetRecognizer() { this.events.clear(); }
  
  public void addDragGestureListener(DragGestureListener paramDragGestureListener) throws TooManyListenersException {
    if (this.dragGestureListener != null)
      throw new TooManyListenersException(); 
    this.dragGestureListener = paramDragGestureListener;
    if (this.component != null)
      registerListeners(); 
  }
  
  public void removeDragGestureListener(DragGestureListener paramDragGestureListener) throws TooManyListenersException {
    if (this.dragGestureListener == null || !this.dragGestureListener.equals(paramDragGestureListener))
      throw new IllegalArgumentException(); 
    this.dragGestureListener = null;
    if (this.component != null)
      unregisterListeners(); 
  }
  
  protected void fireDragGestureRecognized(int paramInt, Point paramPoint) {
    try {
      if (this.dragGestureListener != null)
        this.dragGestureListener.dragGestureRecognized(new DragGestureEvent(this, paramInt, paramPoint, this.events)); 
    } finally {
      this.events.clear();
    } 
  }
  
  protected void appendEvent(InputEvent paramInputEvent) { this.events.add(paramInputEvent); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(this.dragGestureListener) ? this.dragGestureListener : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    DragSource dragSource1 = (DragSource)getField.get("dragSource", null);
    if (dragSource1 == null)
      throw new InvalidObjectException("null DragSource"); 
    this.dragSource = dragSource1;
    this.component = (Component)getField.get("component", null);
    this.sourceActions = getField.get("sourceActions", 0) & 0x40000003;
    this.events = (ArrayList)getField.get("events", new ArrayList(1));
    this.dragGestureListener = (DragGestureListener)paramObjectInputStream.readObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DragGestureRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */