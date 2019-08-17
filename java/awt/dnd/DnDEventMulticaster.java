package java.awt.dnd;

import java.awt.AWTEventMulticaster;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventListener;

class DnDEventMulticaster extends AWTEventMulticaster implements DragSourceListener, DragSourceMotionListener {
  protected DnDEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) { super(paramEventListener1, paramEventListener2); }
  
  public void dragEnter(DragSourceDragEvent paramDragSourceDragEvent) {
    ((DragSourceListener)this.a).dragEnter(paramDragSourceDragEvent);
    ((DragSourceListener)this.b).dragEnter(paramDragSourceDragEvent);
  }
  
  public void dragOver(DragSourceDragEvent paramDragSourceDragEvent) {
    ((DragSourceListener)this.a).dragOver(paramDragSourceDragEvent);
    ((DragSourceListener)this.b).dragOver(paramDragSourceDragEvent);
  }
  
  public void dropActionChanged(DragSourceDragEvent paramDragSourceDragEvent) {
    ((DragSourceListener)this.a).dropActionChanged(paramDragSourceDragEvent);
    ((DragSourceListener)this.b).dropActionChanged(paramDragSourceDragEvent);
  }
  
  public void dragExit(DragSourceEvent paramDragSourceEvent) {
    ((DragSourceListener)this.a).dragExit(paramDragSourceEvent);
    ((DragSourceListener)this.b).dragExit(paramDragSourceEvent);
  }
  
  public void dragDropEnd(DragSourceDropEvent paramDragSourceDropEvent) {
    ((DragSourceListener)this.a).dragDropEnd(paramDragSourceDropEvent);
    ((DragSourceListener)this.b).dragDropEnd(paramDragSourceDropEvent);
  }
  
  public void dragMouseMoved(DragSourceDragEvent paramDragSourceDragEvent) {
    ((DragSourceMotionListener)this.a).dragMouseMoved(paramDragSourceDragEvent);
    ((DragSourceMotionListener)this.b).dragMouseMoved(paramDragSourceDragEvent);
  }
  
  public static DragSourceListener add(DragSourceListener paramDragSourceListener1, DragSourceListener paramDragSourceListener2) { return (DragSourceListener)addInternal(paramDragSourceListener1, paramDragSourceListener2); }
  
  public static DragSourceMotionListener add(DragSourceMotionListener paramDragSourceMotionListener1, DragSourceMotionListener paramDragSourceMotionListener2) { return (DragSourceMotionListener)addInternal(paramDragSourceMotionListener1, paramDragSourceMotionListener2); }
  
  public static DragSourceListener remove(DragSourceListener paramDragSourceListener1, DragSourceListener paramDragSourceListener2) { return (DragSourceListener)removeInternal(paramDragSourceListener1, paramDragSourceListener2); }
  
  public static DragSourceMotionListener remove(DragSourceMotionListener paramDragSourceMotionListener1, DragSourceMotionListener paramDragSourceMotionListener2) { return (DragSourceMotionListener)removeInternal(paramDragSourceMotionListener1, paramDragSourceMotionListener2); }
  
  protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) { return (paramEventListener1 == null) ? paramEventListener2 : ((paramEventListener2 == null) ? paramEventListener1 : new DnDEventMulticaster(paramEventListener1, paramEventListener2)); }
  
  protected EventListener remove(EventListener paramEventListener) {
    if (paramEventListener == this.a)
      return this.b; 
    if (paramEventListener == this.b)
      return this.a; 
    EventListener eventListener1 = removeInternal(this.a, paramEventListener);
    EventListener eventListener2 = removeInternal(this.b, paramEventListener);
    return (eventListener1 == this.a && eventListener2 == this.b) ? this : addInternal(eventListener1, eventListener2);
  }
  
  protected static EventListener removeInternal(EventListener paramEventListener1, EventListener paramEventListener2) { return (paramEventListener1 == paramEventListener2 || paramEventListener1 == null) ? null : ((paramEventListener1 instanceof DnDEventMulticaster) ? ((DnDEventMulticaster)paramEventListener1).remove(paramEventListener2) : paramEventListener1); }
  
  protected static void save(ObjectOutputStream paramObjectOutputStream, String paramString, EventListener paramEventListener) throws IOException { AWTEventMulticaster.save(paramObjectOutputStream, paramString, paramEventListener); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DnDEventMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */