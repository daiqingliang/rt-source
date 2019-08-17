package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TooManyListenersException;

public class DragSourceContext implements DragSourceListener, DragSourceMotionListener, Serializable {
  private static final long serialVersionUID = -115407898692194719L;
  
  protected static final int DEFAULT = 0;
  
  protected static final int ENTER = 1;
  
  protected static final int OVER = 2;
  
  protected static final int CHANGED = 3;
  
  private static Transferable emptyTransferable;
  
  private DragSourceContextPeer peer;
  
  private DragGestureEvent trigger;
  
  private Cursor cursor;
  
  private Transferable transferable;
  
  private DragSourceListener listener;
  
  private boolean useCustomCursor;
  
  private int sourceActions;
  
  public DragSourceContext(DragSourceContextPeer paramDragSourceContextPeer, DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener) {
    if (paramDragSourceContextPeer == null)
      throw new NullPointerException("DragSourceContextPeer"); 
    if (paramDragGestureEvent == null)
      throw new NullPointerException("Trigger"); 
    if (paramDragGestureEvent.getDragSource() == null)
      throw new IllegalArgumentException("DragSource"); 
    if (paramDragGestureEvent.getComponent() == null)
      throw new IllegalArgumentException("Component"); 
    if (paramDragGestureEvent.getSourceAsDragGestureRecognizer().getSourceActions() == 0)
      throw new IllegalArgumentException("source actions"); 
    if (paramDragGestureEvent.getDragAction() == 0)
      throw new IllegalArgumentException("no drag action"); 
    if (paramTransferable == null)
      throw new NullPointerException("Transferable"); 
    if (paramImage != null && paramPoint == null)
      throw new NullPointerException("offset"); 
    this.peer = paramDragSourceContextPeer;
    this.trigger = paramDragGestureEvent;
    this.cursor = paramCursor;
    this.transferable = paramTransferable;
    this.listener = paramDragSourceListener;
    this.sourceActions = paramDragGestureEvent.getSourceAsDragGestureRecognizer().getSourceActions();
    this.useCustomCursor = (paramCursor != null);
    updateCurrentCursor(paramDragGestureEvent.getDragAction(), getSourceActions(), 0);
  }
  
  public DragSource getDragSource() { return this.trigger.getDragSource(); }
  
  public Component getComponent() { return this.trigger.getComponent(); }
  
  public DragGestureEvent getTrigger() { return this.trigger; }
  
  public int getSourceActions() { return this.sourceActions; }
  
  public void setCursor(Cursor paramCursor) {
    this.useCustomCursor = (paramCursor != null);
    setCursorImpl(paramCursor);
  }
  
  public Cursor getCursor() { return this.cursor; }
  
  public void addDragSourceListener(DragSourceListener paramDragSourceListener) throws TooManyListenersException {
    if (paramDragSourceListener == null)
      return; 
    if (equals(paramDragSourceListener))
      throw new IllegalArgumentException("DragSourceContext may not be its own listener"); 
    if (this.listener != null)
      throw new TooManyListenersException(); 
    this.listener = paramDragSourceListener;
  }
  
  public void removeDragSourceListener(DragSourceListener paramDragSourceListener) throws TooManyListenersException {
    if (this.listener != null && this.listener.equals(paramDragSourceListener)) {
      this.listener = null;
    } else {
      throw new IllegalArgumentException();
    } 
  }
  
  public void transferablesFlavorsChanged() {
    if (this.peer != null)
      this.peer.transferablesFlavorsChanged(); 
  }
  
  public void dragEnter(DragSourceDragEvent paramDragSourceDragEvent) {
    DragSourceListener dragSourceListener = this.listener;
    if (dragSourceListener != null)
      dragSourceListener.dragEnter(paramDragSourceDragEvent); 
    getDragSource().processDragEnter(paramDragSourceDragEvent);
    updateCurrentCursor(getSourceActions(), paramDragSourceDragEvent.getTargetActions(), 1);
  }
  
  public void dragOver(DragSourceDragEvent paramDragSourceDragEvent) {
    DragSourceListener dragSourceListener = this.listener;
    if (dragSourceListener != null)
      dragSourceListener.dragOver(paramDragSourceDragEvent); 
    getDragSource().processDragOver(paramDragSourceDragEvent);
    updateCurrentCursor(getSourceActions(), paramDragSourceDragEvent.getTargetActions(), 2);
  }
  
  public void dragExit(DragSourceEvent paramDragSourceEvent) {
    DragSourceListener dragSourceListener = this.listener;
    if (dragSourceListener != null)
      dragSourceListener.dragExit(paramDragSourceEvent); 
    getDragSource().processDragExit(paramDragSourceEvent);
    updateCurrentCursor(0, 0, 0);
  }
  
  public void dropActionChanged(DragSourceDragEvent paramDragSourceDragEvent) {
    DragSourceListener dragSourceListener = this.listener;
    if (dragSourceListener != null)
      dragSourceListener.dropActionChanged(paramDragSourceDragEvent); 
    getDragSource().processDropActionChanged(paramDragSourceDragEvent);
    updateCurrentCursor(getSourceActions(), paramDragSourceDragEvent.getTargetActions(), 3);
  }
  
  public void dragDropEnd(DragSourceDropEvent paramDragSourceDropEvent) {
    DragSourceListener dragSourceListener = this.listener;
    if (dragSourceListener != null)
      dragSourceListener.dragDropEnd(paramDragSourceDropEvent); 
    getDragSource().processDragDropEnd(paramDragSourceDropEvent);
  }
  
  public void dragMouseMoved(DragSourceDragEvent paramDragSourceDragEvent) { getDragSource().processDragMouseMoved(paramDragSourceDragEvent); }
  
  public Transferable getTransferable() { return this.transferable; }
  
  protected void updateCurrentCursor(int paramInt1, int paramInt2, int paramInt3) {
    if (this.useCustomCursor)
      return; 
    Cursor cursor1 = null;
    switch (paramInt3) {
      default:
        paramInt2 = 0;
        break;
      case 1:
      case 2:
      case 3:
        break;
    } 
    int i = paramInt1 & paramInt2;
    if (i == 0) {
      if ((paramInt1 & 0x40000000) == 1073741824) {
        cursor1 = DragSource.DefaultLinkNoDrop;
      } else if ((paramInt1 & 0x2) == 2) {
        cursor1 = DragSource.DefaultMoveNoDrop;
      } else {
        cursor1 = DragSource.DefaultCopyNoDrop;
      } 
    } else if ((i & 0x40000000) == 1073741824) {
      cursor1 = DragSource.DefaultLinkDrop;
    } else if ((i & 0x2) == 2) {
      cursor1 = DragSource.DefaultMoveDrop;
    } else {
      cursor1 = DragSource.DefaultCopyDrop;
    } 
    setCursorImpl(cursor1);
  }
  
  private void setCursorImpl(Cursor paramCursor) {
    if (this.cursor == null || !this.cursor.equals(paramCursor)) {
      this.cursor = paramCursor;
      if (this.peer != null)
        this.peer.setCursor(this.cursor); 
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(this.transferable) ? this.transferable : null);
    paramObjectOutputStream.writeObject(SerializationTester.test(this.listener) ? this.listener : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    DragGestureEvent dragGestureEvent = (DragGestureEvent)getField.get("trigger", null);
    if (dragGestureEvent == null)
      throw new InvalidObjectException("Null trigger"); 
    if (dragGestureEvent.getDragSource() == null)
      throw new InvalidObjectException("Null DragSource"); 
    if (dragGestureEvent.getComponent() == null)
      throw new InvalidObjectException("Null trigger component"); 
    int i = getField.get("sourceActions", 0) & 0x40000003;
    if (i == 0)
      throw new InvalidObjectException("Invalid source actions"); 
    int j = dragGestureEvent.getDragAction();
    if (j != 1 && j != 2 && j != 1073741824)
      throw new InvalidObjectException("No drag action"); 
    this.trigger = dragGestureEvent;
    this.cursor = (Cursor)getField.get("cursor", null);
    this.useCustomCursor = getField.get("useCustomCursor", false);
    this.sourceActions = i;
    this.transferable = (Transferable)paramObjectInputStream.readObject();
    this.listener = (DragSourceListener)paramObjectInputStream.readObject();
    if (this.transferable == null) {
      if (emptyTransferable == null)
        emptyTransferable = new Transferable() {
            public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[0]; }
            
            public boolean isDataFlavorSupported(DataFlavor param1DataFlavor) { return false; }
            
            public Object getTransferData(DataFlavor param1DataFlavor) throws UnsupportedFlavorException { throw new UnsupportedFlavorException(param1DataFlavor); }
          }; 
      this.transferable = emptyTransferable;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DragSourceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */