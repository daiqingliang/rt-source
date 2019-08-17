package sun.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public abstract class SunDropTargetContextPeer implements DropTargetContextPeer, Transferable {
  public static final boolean DISPATCH_SYNC = true;
  
  private DropTarget currentDT;
  
  private DropTargetContext currentDTC;
  
  private long[] currentT;
  
  private int currentA;
  
  private int currentSA;
  
  private int currentDA;
  
  private int previousDA;
  
  private long nativeDragContext;
  
  private Transferable local;
  
  private boolean dragRejected = false;
  
  protected int dropStatus = 0;
  
  protected boolean dropComplete = false;
  
  boolean dropInProcess = false;
  
  protected static final Object _globalLock = new Object();
  
  private static final PlatformLogger dndLog = PlatformLogger.getLogger("sun.awt.dnd.SunDropTargetContextPeer");
  
  protected static Transferable currentJVMLocalSourceTransferable = null;
  
  protected static final int STATUS_NONE = 0;
  
  protected static final int STATUS_WAIT = 1;
  
  protected static final int STATUS_ACCEPT = 2;
  
  protected static final int STATUS_REJECT = -1;
  
  public static void setCurrentJVMLocalSourceTransferable(Transferable paramTransferable) throws InvalidDnDOperationException {
    synchronized (_globalLock) {
      if (paramTransferable != null && currentJVMLocalSourceTransferable != null)
        throw new InvalidDnDOperationException(); 
      currentJVMLocalSourceTransferable = paramTransferable;
    } 
  }
  
  private static Transferable getJVMLocalSourceTransferable() { return currentJVMLocalSourceTransferable; }
  
  public DropTarget getDropTarget() { return this.currentDT; }
  
  public void setTargetActions(int paramInt) { this.currentA = paramInt & 0x40000003; }
  
  public int getTargetActions() { return this.currentA; }
  
  public Transferable getTransferable() { return this; }
  
  public DataFlavor[] getTransferDataFlavors() {
    Transferable transferable = this.local;
    return (transferable != null) ? transferable.getTransferDataFlavors() : DataTransferer.getInstance().getFlavorsForFormatsAsArray(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) {
    Transferable transferable = this.local;
    return (transferable != null) ? transferable.isDataFlavorSupported(paramDataFlavor) : DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap())).containsKey(paramDataFlavor);
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException, InvalidDnDOperationException {
    SecurityManager securityManager = System.getSecurityManager();
    try {
      if (!this.dropInProcess && securityManager != null)
        securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION); 
    } catch (Exception exception) {
      Thread thread = Thread.currentThread();
      thread.getUncaughtExceptionHandler().uncaughtException(thread, exception);
      return null;
    } 
    Long long = null;
    Transferable transferable = this.local;
    if (transferable != null)
      return transferable.getTransferData(paramDataFlavor); 
    if (this.dropStatus != 2 || this.dropComplete)
      throw new InvalidDnDOperationException("No drop current"); 
    Map map = DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
    long = (Long)map.get(paramDataFlavor);
    if (long == null)
      throw new UnsupportedFlavorException(paramDataFlavor); 
    if (paramDataFlavor.isRepresentationClassRemote() && this.currentDA != 1073741824)
      throw new InvalidDnDOperationException("only ACTION_LINK is permissable for transfer of java.rmi.Remote objects"); 
    long l = long.longValue();
    Object object = getNativeData(l);
    if (object instanceof byte[])
      try {
        return DataTransferer.getInstance().translateBytes((byte[])object, paramDataFlavor, l, this);
      } catch (IOException iOException) {
        throw new InvalidDnDOperationException(iOException.getMessage());
      }  
    if (object instanceof InputStream)
      try {
        return DataTransferer.getInstance().translateStream((InputStream)object, paramDataFlavor, l, this);
      } catch (IOException iOException) {
        throw new InvalidDnDOperationException(iOException.getMessage());
      }  
    throw new IOException("no native data was transfered");
  }
  
  protected abstract Object getNativeData(long paramLong) throws IOException;
  
  public boolean isTransferableJVMLocal() { return (this.local != null || getJVMLocalSourceTransferable() != null); }
  
  private int handleEnterMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong) { return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 504, true); }
  
  protected void processEnterMessage(SunDropTargetEvent paramSunDropTargetEvent) {
    Component component = (Component)paramSunDropTargetEvent.getSource();
    DropTarget dropTarget = component.getDropTarget();
    Point point = paramSunDropTargetEvent.getPoint();
    this.local = getJVMLocalSourceTransferable();
    if (this.currentDTC != null) {
      this.currentDTC.removeNotify();
      this.currentDTC = null;
    } 
    if (component.isShowing() && dropTarget != null && dropTarget.isActive()) {
      this.currentDT = dropTarget;
      this.currentDTC = this.currentDT.getDropTargetContext();
      this.currentDTC.addNotify(this);
      this.currentA = dropTarget.getDefaultActions();
      try {
        dropTarget.dragEnter(new DropTargetDragEvent(this.currentDTC, point, this.currentDA, this.currentSA));
      } catch (Exception exception) {
        exception.printStackTrace();
        this.currentDA = 0;
      } 
    } else {
      this.currentDT = null;
      this.currentDTC = null;
      this.currentDA = 0;
      this.currentSA = 0;
      this.currentA = 0;
    } 
  }
  
  private void handleExitMessage(Component paramComponent, long paramLong) { postDropTargetEvent(paramComponent, 0, 0, 0, 0, null, paramLong, 505, true); }
  
  protected void processExitMessage(SunDropTargetEvent paramSunDropTargetEvent) {
    Component component = (Component)paramSunDropTargetEvent.getSource();
    DropTarget dropTarget = component.getDropTarget();
    DropTargetContext dropTargetContext = null;
    if (dropTarget == null) {
      this.currentDT = null;
      this.currentT = null;
      if (this.currentDTC != null)
        this.currentDTC.removeNotify(); 
      this.currentDTC = null;
      return;
    } 
    if (dropTarget != this.currentDT) {
      if (this.currentDTC != null)
        this.currentDTC.removeNotify(); 
      this.currentDT = dropTarget;
      this.currentDTC = dropTarget.getDropTargetContext();
      this.currentDTC.addNotify(this);
    } 
    dropTargetContext = this.currentDTC;
    if (dropTarget.isActive())
      try {
        dropTarget.dragExit(new DropTargetEvent(dropTargetContext));
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        this.currentA = 0;
        this.currentSA = 0;
        this.currentDA = 0;
        this.currentDT = null;
        this.currentT = null;
        this.currentDTC.removeNotify();
        this.currentDTC = null;
        this.local = null;
        this.dragRejected = false;
      }  
  }
  
  private int handleMotionMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong) { return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 506, true); }
  
  protected void processMotionMessage(SunDropTargetEvent paramSunDropTargetEvent, boolean paramBoolean) {
    Component component = (Component)paramSunDropTargetEvent.getSource();
    Point point = paramSunDropTargetEvent.getPoint();
    int i = paramSunDropTargetEvent.getID();
    DropTarget dropTarget = component.getDropTarget();
    DropTargetContext dropTargetContext = null;
    if (component.isShowing() && dropTarget != null && dropTarget.isActive()) {
      if (this.currentDT != dropTarget) {
        if (this.currentDTC != null)
          this.currentDTC.removeNotify(); 
        this.currentDT = dropTarget;
        this.currentDTC = null;
      } 
      dropTargetContext = this.currentDT.getDropTargetContext();
      if (dropTargetContext != this.currentDTC) {
        if (this.currentDTC != null)
          this.currentDTC.removeNotify(); 
        this.currentDTC = dropTargetContext;
        this.currentDTC.addNotify(this);
      } 
      this.currentA = this.currentDT.getDefaultActions();
      try {
        DropTargetDragEvent dropTargetDragEvent = new DropTargetDragEvent(dropTargetContext, point, this.currentDA, this.currentSA);
        DropTarget dropTarget1 = dropTarget;
        if (paramBoolean) {
          dropTarget1.dropActionChanged(dropTargetDragEvent);
        } else {
          dropTarget1.dragOver(dropTargetDragEvent);
        } 
        if (this.dragRejected)
          this.currentDA = 0; 
      } catch (Exception exception) {
        exception.printStackTrace();
        this.currentDA = 0;
      } 
    } else {
      this.currentDA = 0;
    } 
  }
  
  private void handleDropMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong) { postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 502, false); }
  
  protected void processDropMessage(SunDropTargetEvent paramSunDropTargetEvent) {
    Component component = (Component)paramSunDropTargetEvent.getSource();
    Point point = paramSunDropTargetEvent.getPoint();
    DropTarget dropTarget = component.getDropTarget();
    this.dropStatus = 1;
    this.dropComplete = false;
    if (component.isShowing() && dropTarget != null && dropTarget.isActive()) {
      DropTargetContext dropTargetContext = dropTarget.getDropTargetContext();
      this.currentDT = dropTarget;
      if (this.currentDTC != null)
        this.currentDTC.removeNotify(); 
      this.currentDTC = dropTargetContext;
      this.currentDTC.addNotify(this);
      this.currentA = dropTarget.getDefaultActions();
      synchronized (_globalLock) {
        if ((this.local = getJVMLocalSourceTransferable()) != null)
          setCurrentJVMLocalSourceTransferable(null); 
      } 
      this.dropInProcess = true;
      try {
        dropTarget.drop(new DropTargetDropEvent(dropTargetContext, point, this.currentDA, this.currentSA, (this.local != null)));
      } finally {
        if (this.dropStatus == 1) {
          rejectDrop();
        } else if (!this.dropComplete) {
          dropComplete(false);
        } 
        this.dropInProcess = false;
      } 
    } else {
      rejectDrop();
    } 
  }
  
  protected int postDropTargetEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong, int paramInt5, boolean paramBoolean) {
    AppContext appContext = SunToolkit.targetToAppContext(paramComponent);
    EventDispatcher eventDispatcher = new EventDispatcher(this, paramInt3, paramInt4, paramArrayOfLong, paramLong, paramBoolean);
    SunDropTargetEvent sunDropTargetEvent = new SunDropTargetEvent(paramComponent, paramInt5, paramInt1, paramInt2, eventDispatcher);
    if (paramBoolean == true)
      DataTransferer.getInstance().getToolkitThreadBlockedHandler().lock(); 
    SunToolkit.postEvent(appContext, sunDropTargetEvent);
    eventPosted(sunDropTargetEvent);
    if (paramBoolean == true) {
      while (!eventDispatcher.isDone())
        DataTransferer.getInstance().getToolkitThreadBlockedHandler().enter(); 
      DataTransferer.getInstance().getToolkitThreadBlockedHandler().unlock();
      return eventDispatcher.getReturnValue();
    } 
    return 0;
  }
  
  public void acceptDrag(int paramInt) {
    if (this.currentDT == null)
      throw new InvalidDnDOperationException("No Drag pending"); 
    this.currentDA = mapOperation(paramInt);
    if (this.currentDA != 0)
      this.dragRejected = false; 
  }
  
  public void rejectDrag() {
    if (this.currentDT == null)
      throw new InvalidDnDOperationException("No Drag pending"); 
    this.currentDA = 0;
    this.dragRejected = true;
  }
  
  public void acceptDrop(int paramInt) {
    if (paramInt == 0)
      throw new IllegalArgumentException("invalid acceptDrop() action"); 
    if (this.dropStatus == 1 || this.dropStatus == 2) {
      this.currentDA = this.currentA = mapOperation(paramInt & this.currentSA);
      this.dropStatus = 2;
      this.dropComplete = false;
    } else {
      throw new InvalidDnDOperationException("invalid acceptDrop()");
    } 
  }
  
  public void rejectDrop() {
    if (this.dropStatus != 1)
      throw new InvalidDnDOperationException("invalid rejectDrop()"); 
    this.dropStatus = -1;
    this.currentDA = 0;
    dropComplete(false);
  }
  
  private int mapOperation(int paramInt) {
    int[] arrayOfInt = { 2, 1, 1073741824 };
    int i = 0;
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if ((paramInt & arrayOfInt[b]) == arrayOfInt[b]) {
        i = arrayOfInt[b];
        break;
      } 
    } 
    return i;
  }
  
  public void dropComplete(boolean paramBoolean) {
    if (this.dropStatus == 0)
      throw new InvalidDnDOperationException("No Drop pending"); 
    if (this.currentDTC != null)
      this.currentDTC.removeNotify(); 
    this.currentDT = null;
    this.currentDTC = null;
    this.currentT = null;
    this.currentA = 0;
    synchronized (_globalLock) {
      currentJVMLocalSourceTransferable = null;
    } 
    this.dropStatus = 0;
    this.dropComplete = true;
    try {
      doDropDone(paramBoolean, this.currentDA, (this.local != null));
    } finally {
      this.currentDA = 0;
      this.nativeDragContext = 0L;
    } 
  }
  
  protected abstract void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2);
  
  protected long getNativeDragContext() { return this.nativeDragContext; }
  
  protected void eventPosted(SunDropTargetEvent paramSunDropTargetEvent) {}
  
  protected void eventProcessed(SunDropTargetEvent paramSunDropTargetEvent, int paramInt, boolean paramBoolean) {}
  
  protected static class EventDispatcher {
    private final SunDropTargetContextPeer peer;
    
    private final int dropAction;
    
    private final int actions;
    
    private final long[] formats;
    
    private long nativeCtxt;
    
    private final boolean dispatchType;
    
    private boolean dispatcherDone = false;
    
    private int returnValue = 0;
    
    private final HashSet eventSet = new HashSet(3);
    
    static final ToolkitThreadBlockedHandler handler = DataTransferer.getInstance().getToolkitThreadBlockedHandler();
    
    EventDispatcher(SunDropTargetContextPeer param1SunDropTargetContextPeer, int param1Int1, int param1Int2, long[] param1ArrayOfLong, long param1Long, boolean param1Boolean) {
      this.peer = param1SunDropTargetContextPeer;
      this.nativeCtxt = param1Long;
      this.dropAction = param1Int1;
      this.actions = param1Int2;
      this.formats = (null == param1ArrayOfLong) ? null : Arrays.copyOf(param1ArrayOfLong, param1ArrayOfLong.length);
      this.dispatchType = param1Boolean;
    }
    
    void dispatchEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      int i = param1SunDropTargetEvent.getID();
      switch (i) {
        case 504:
          dispatchEnterEvent(param1SunDropTargetEvent);
          return;
        case 506:
          dispatchMotionEvent(param1SunDropTargetEvent);
          return;
        case 505:
          dispatchExitEvent(param1SunDropTargetEvent);
          return;
        case 502:
          dispatchDropEvent(param1SunDropTargetEvent);
          return;
      } 
      throw new InvalidDnDOperationException();
    }
    
    private void dispatchEnterEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      synchronized (this.peer) {
        this.peer.previousDA = this.dropAction;
        this.peer.nativeDragContext = this.nativeCtxt;
        this.peer.currentT = this.formats;
        this.peer.currentSA = this.actions;
        this.peer.currentDA = this.dropAction;
        this.peer.dropStatus = 2;
        this.peer.dropComplete = false;
        try {
          this.peer.processEnterMessage(param1SunDropTargetEvent);
        } finally {
          this.peer.dropStatus = 0;
        } 
        setReturnValue(this.peer.currentDA);
      } 
    }
    
    private void dispatchMotionEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      synchronized (this.peer) {
        boolean bool = (this.peer.previousDA != this.dropAction);
        this.peer.previousDA = this.dropAction;
        this.peer.nativeDragContext = this.nativeCtxt;
        this.peer.currentT = this.formats;
        this.peer.currentSA = this.actions;
        this.peer.currentDA = this.dropAction;
        this.peer.dropStatus = 2;
        this.peer.dropComplete = false;
        try {
          this.peer.processMotionMessage(param1SunDropTargetEvent, bool);
        } finally {
          this.peer.dropStatus = 0;
        } 
        setReturnValue(this.peer.currentDA);
      } 
    }
    
    private void dispatchExitEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      synchronized (this.peer) {
        this.peer.nativeDragContext = this.nativeCtxt;
        this.peer.processExitMessage(param1SunDropTargetEvent);
      } 
    }
    
    private void dispatchDropEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      synchronized (this.peer) {
        this.peer.nativeDragContext = this.nativeCtxt;
        this.peer.currentT = this.formats;
        this.peer.currentSA = this.actions;
        this.peer.currentDA = this.dropAction;
        this.peer.processDropMessage(param1SunDropTargetEvent);
      } 
    }
    
    void setReturnValue(int param1Int) { this.returnValue = param1Int; }
    
    int getReturnValue() { return this.returnValue; }
    
    boolean isDone() { return this.eventSet.isEmpty(); }
    
    void registerEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      handler.lock();
      if (!this.eventSet.add(param1SunDropTargetEvent) && dndLog.isLoggable(PlatformLogger.Level.FINE))
        dndLog.fine("Event is already registered: " + param1SunDropTargetEvent); 
      handler.unlock();
    }
    
    void unregisterEvent(SunDropTargetEvent param1SunDropTargetEvent) {
      handler.lock();
      try {
        if (!this.eventSet.remove(param1SunDropTargetEvent))
          return; 
        if (this.eventSet.isEmpty()) {
          if (!this.dispatcherDone && this.dispatchType == true)
            handler.exit(); 
          this.dispatcherDone = true;
        } 
      } finally {
        handler.unlock();
      } 
      try {
        this.peer.eventProcessed(param1SunDropTargetEvent, this.returnValue, this.dispatcherDone);
      } finally {
        if (this.dispatcherDone) {
          this.nativeCtxt = 0L;
          this.peer.nativeDragContext = 0L;
        } 
      } 
    }
    
    public void unregisterAllEvents() {
      Object[] arrayOfObject = null;
      handler.lock();
      try {
        arrayOfObject = this.eventSet.toArray();
      } finally {
        handler.unlock();
      } 
      if (arrayOfObject != null)
        for (byte b = 0; b < arrayOfObject.length; b++)
          unregisterEvent((SunDropTargetEvent)arrayOfObject[b]);  
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\dnd\SunDropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */