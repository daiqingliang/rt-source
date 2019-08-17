package sun.awt.dnd;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.SortedMap;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;

public abstract class SunDragSourceContextPeer implements DragSourceContextPeer {
  private DragGestureEvent trigger;
  
  private Component component;
  
  private Cursor cursor;
  
  private Image dragImage;
  
  private Point dragImageOffset;
  
  private long nativeCtxt;
  
  private DragSourceContext dragSourceContext;
  
  private int sourceActions;
  
  private static boolean dragDropInProgress = false;
  
  private static boolean discardingMouseEvents = false;
  
  protected static final int DISPATCH_ENTER = 1;
  
  protected static final int DISPATCH_MOTION = 2;
  
  protected static final int DISPATCH_CHANGED = 3;
  
  protected static final int DISPATCH_EXIT = 4;
  
  protected static final int DISPATCH_FINISH = 5;
  
  protected static final int DISPATCH_MOUSE_MOVED = 6;
  
  public SunDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) {
    this.trigger = paramDragGestureEvent;
    if (this.trigger != null) {
      this.component = this.trigger.getComponent();
    } else {
      this.component = null;
    } 
  }
  
  public void startSecondaryEventLoop() {}
  
  public void quitSecondaryEventLoop() {}
  
  public void startDrag(DragSourceContext paramDragSourceContext, Cursor paramCursor, Image paramImage, Point paramPoint) throws InvalidDnDOperationException {
    if (getTrigger().getTriggerEvent() == null)
      throw new InvalidDnDOperationException("DragGestureEvent has a null trigger"); 
    this.dragSourceContext = paramDragSourceContext;
    this.cursor = paramCursor;
    this.sourceActions = getDragSourceContext().getSourceActions();
    this.dragImage = paramImage;
    this.dragImageOffset = paramPoint;
    Transferable transferable = getDragSourceContext().getTransferable();
    SortedMap sortedMap = DataTransferer.getInstance().getFormatsForTransferable(transferable, DataTransferer.adaptFlavorMap(getTrigger().getDragSource().getFlavorMap()));
    long[] arrayOfLong = DataTransferer.getInstance().keysToLongArray(sortedMap);
    startDrag(transferable, arrayOfLong, sortedMap);
    discardingMouseEvents = true;
    EventQueue.invokeLater(new Runnable() {
          public void run() { discardingMouseEvents = false; }
        });
  }
  
  protected abstract void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap);
  
  public void setCursor(Cursor paramCursor) throws InvalidDnDOperationException {
    synchronized (this) {
      if (this.cursor == null || !this.cursor.equals(paramCursor)) {
        this.cursor = paramCursor;
        setNativeCursor(getNativeContext(), paramCursor, (paramCursor != null) ? paramCursor.getType() : 0);
      } 
    } 
  }
  
  public Cursor getCursor() { return this.cursor; }
  
  public Image getDragImage() { return this.dragImage; }
  
  public Point getDragImageOffset() { return (this.dragImageOffset == null) ? new Point(0, 0) : new Point(this.dragImageOffset); }
  
  protected abstract void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt);
  
  protected void setTrigger(DragGestureEvent paramDragGestureEvent) {
    this.trigger = paramDragGestureEvent;
    if (this.trigger != null) {
      this.component = this.trigger.getComponent();
    } else {
      this.component = null;
    } 
  }
  
  protected DragGestureEvent getTrigger() { return this.trigger; }
  
  protected Component getComponent() { return this.component; }
  
  protected void setNativeContext(long paramLong) { this.nativeCtxt = paramLong; }
  
  protected long getNativeContext() { return this.nativeCtxt; }
  
  protected DragSourceContext getDragSourceContext() { return this.dragSourceContext; }
  
  public void transferablesFlavorsChanged() {}
  
  protected final void postDragSourceDragEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = convertModifiersToDropAction(paramInt2, this.sourceActions);
    DragSourceDragEvent dragSourceDragEvent = new DragSourceDragEvent(getDragSourceContext(), i, paramInt1 & this.sourceActions, paramInt2, paramInt3, paramInt4);
    EventDispatcher eventDispatcher = new EventDispatcher(paramInt5, dragSourceDragEvent);
    SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(getComponent()), eventDispatcher);
    startSecondaryEventLoop();
  }
  
  protected void dragEnter(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 1); }
  
  private void dragMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 2); }
  
  private void operationChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 3); }
  
  protected final void dragExit(int paramInt1, int paramInt2) {
    DragSourceEvent dragSourceEvent = new DragSourceEvent(getDragSourceContext(), paramInt1, paramInt2);
    EventDispatcher eventDispatcher = new EventDispatcher(4, dragSourceEvent);
    SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(getComponent()), eventDispatcher);
    startSecondaryEventLoop();
  }
  
  private void dragMouseMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 6); }
  
  protected final void dragDropFinished(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3) {
    DragSourceDropEvent dragSourceDropEvent = new DragSourceDropEvent(getDragSourceContext(), paramInt1 & this.sourceActions, paramBoolean, paramInt2, paramInt3);
    EventDispatcher eventDispatcher = new EventDispatcher(5, dragSourceDropEvent);
    SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(getComponent()), eventDispatcher);
    startSecondaryEventLoop();
    setNativeContext(0L);
    this.dragImage = null;
    this.dragImageOffset = null;
  }
  
  public static void setDragDropInProgress(boolean paramBoolean) throws InvalidDnDOperationException {
    synchronized (SunDragSourceContextPeer.class) {
      if (dragDropInProgress == paramBoolean)
        throw new InvalidDnDOperationException(getExceptionMessage(paramBoolean)); 
      dragDropInProgress = paramBoolean;
    } 
  }
  
  public static boolean checkEvent(AWTEvent paramAWTEvent) {
    if (discardingMouseEvents && paramAWTEvent instanceof MouseEvent) {
      MouseEvent mouseEvent = (MouseEvent)paramAWTEvent;
      if (!(mouseEvent instanceof SunDropTargetEvent))
        return false; 
    } 
    return true;
  }
  
  public static void checkDragDropInProgress() {
    if (dragDropInProgress)
      throw new InvalidDnDOperationException(getExceptionMessage(true)); 
  }
  
  private static String getExceptionMessage(boolean paramBoolean) { return paramBoolean ? "Drag and drop in progress" : "No drag in progress"; }
  
  public static int convertModifiersToDropAction(int paramInt1, int paramInt2) {
    int i = 0;
    switch (paramInt1 & 0xC0) {
      case 192:
        i = 1073741824;
        return i & paramInt2;
      case 128:
        i = 1;
        return i & paramInt2;
      case 64:
        i = 2;
        return i & paramInt2;
    } 
    if ((paramInt2 & 0x2) != 0) {
      i = 2;
    } else if ((paramInt2 & true) != 0) {
      i = 1;
    } else if ((paramInt2 & 0x40000000) != 0) {
      i = 1073741824;
    } 
    return i & paramInt2;
  }
  
  private void cleanup() {
    this.trigger = null;
    this.component = null;
    this.cursor = null;
    this.dragSourceContext = null;
    SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(null);
    setDragDropInProgress(false);
  }
  
  private class EventDispatcher implements Runnable {
    private final int dispatchType;
    
    private final DragSourceEvent event;
    
    EventDispatcher(int param1Int, DragSourceEvent param1DragSourceEvent) {
      switch (param1Int) {
        case 1:
        case 2:
        case 3:
        case 6:
          if (!(param1DragSourceEvent instanceof DragSourceDragEvent))
            throw new IllegalArgumentException("Event: " + param1DragSourceEvent); 
          break;
        case 4:
          break;
        case 5:
          if (!(param1DragSourceEvent instanceof DragSourceDropEvent))
            throw new IllegalArgumentException("Event: " + param1DragSourceEvent); 
          break;
        default:
          throw new IllegalArgumentException("Dispatch type: " + param1Int);
      } 
      this.dispatchType = param1Int;
      this.event = param1DragSourceEvent;
    }
    
    public void run() {
      DragSourceContext dragSourceContext = SunDragSourceContextPeer.this.getDragSourceContext();
      try {
        switch (this.dispatchType) {
          case 1:
            dragSourceContext.dragEnter((DragSourceDragEvent)this.event);
            break;
          case 2:
            dragSourceContext.dragOver((DragSourceDragEvent)this.event);
            break;
          case 3:
            dragSourceContext.dropActionChanged((DragSourceDragEvent)this.event);
            break;
          case 4:
            dragSourceContext.dragExit(this.event);
            break;
          case 6:
            dragSourceContext.dragMouseMoved((DragSourceDragEvent)this.event);
            break;
          case 5:
            try {
              dragSourceContext.dragDropEnd((DragSourceDropEvent)this.event);
            } finally {
              SunDragSourceContextPeer.this.cleanup();
            } 
            break;
          default:
            throw new IllegalStateException("Dispatch type: " + this.dispatchType);
        } 
      } finally {
        SunDragSourceContextPeer.this.quitSecondaryEventLoop();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\dnd\SunDragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */