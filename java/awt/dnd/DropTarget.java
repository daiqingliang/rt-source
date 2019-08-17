package java.awt.dnd;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TooManyListenersException;
import javax.swing.Timer;

public class DropTarget implements DropTargetListener, Serializable {
  private static final long serialVersionUID = -6283860791671019047L;
  
  private DropTargetContext dropTargetContext = createDropTargetContext();
  
  private Component component;
  
  private ComponentPeer componentPeer;
  
  private ComponentPeer nativePeer;
  
  int actions = 3;
  
  boolean active = true;
  
  private DropTargetAutoScroller autoScroller;
  
  private DropTargetListener dtListener;
  
  private FlavorMap flavorMap;
  
  private boolean isDraggingInside;
  
  public DropTarget(Component paramComponent, int paramInt, DropTargetListener paramDropTargetListener, boolean paramBoolean, FlavorMap paramFlavorMap) throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    this.component = paramComponent;
    setDefaultActions(paramInt);
    if (paramDropTargetListener != null)
      try {
        addDropTargetListener(paramDropTargetListener);
      } catch (TooManyListenersException tooManyListenersException) {} 
    if (paramComponent != null) {
      paramComponent.setDropTarget(this);
      setActive(paramBoolean);
    } 
    if (paramFlavorMap != null) {
      this.flavorMap = paramFlavorMap;
    } else {
      this.flavorMap = SystemFlavorMap.getDefaultFlavorMap();
    } 
  }
  
  public DropTarget(Component paramComponent, int paramInt, DropTargetListener paramDropTargetListener, boolean paramBoolean) throws HeadlessException { this(paramComponent, paramInt, paramDropTargetListener, paramBoolean, null); }
  
  public DropTarget() throws HeadlessException { this(null, 3, null, true, null); }
  
  public DropTarget(Component paramComponent, DropTargetListener paramDropTargetListener) throws HeadlessException { this(paramComponent, 3, paramDropTargetListener, true, null); }
  
  public DropTarget(Component paramComponent, int paramInt, DropTargetListener paramDropTargetListener) throws HeadlessException { this(paramComponent, paramInt, paramDropTargetListener, true); }
  
  public void setComponent(Component paramComponent) {
    if (this.component == paramComponent || (this.component != null && this.component.equals(paramComponent)))
      return; 
    ComponentPeer componentPeer1 = null;
    Component component1;
    if ((component1 = this.component) != null) {
      clearAutoscroll();
      this.component = null;
      if (this.componentPeer != null) {
        componentPeer1 = this.componentPeer;
        removeNotify(this.componentPeer);
      } 
      component1.setDropTarget(null);
    } 
    if ((this.component = paramComponent) != null)
      try {
        paramComponent.setDropTarget(this);
      } catch (Exception exception) {
        if (component1 != null) {
          component1.setDropTarget(this);
          addNotify(componentPeer1);
        } 
      }  
  }
  
  public Component getComponent() { return this.component; }
  
  public void setDefaultActions(int paramInt) { getDropTargetContext().setTargetActions(paramInt & 0x40000003); }
  
  void doSetDefaultActions(int paramInt) { this.actions = paramInt; }
  
  public int getDefaultActions() { return this.actions; }
  
  public void setActive(boolean paramBoolean) {
    if (paramBoolean != this.active)
      this.active = paramBoolean; 
    if (!this.active)
      clearAutoscroll(); 
  }
  
  public boolean isActive() { return this.active; }
  
  public void addDropTargetListener(DropTargetListener paramDropTargetListener) throws TooManyListenersException {
    if (paramDropTargetListener == null)
      return; 
    if (equals(paramDropTargetListener))
      throw new IllegalArgumentException("DropTarget may not be its own Listener"); 
    if (this.dtListener == null) {
      this.dtListener = paramDropTargetListener;
    } else {
      throw new TooManyListenersException();
    } 
  }
  
  public void removeDropTargetListener(DropTargetListener paramDropTargetListener) throws TooManyListenersException {
    if (paramDropTargetListener != null && this.dtListener != null)
      if (this.dtListener.equals(paramDropTargetListener)) {
        this.dtListener = null;
      } else {
        throw new IllegalArgumentException("listener mismatch");
      }  
  }
  
  public void dragEnter(DropTargetDragEvent paramDropTargetDragEvent) {
    this.isDraggingInside = true;
    if (!this.active)
      return; 
    if (this.dtListener != null) {
      this.dtListener.dragEnter(paramDropTargetDragEvent);
    } else {
      paramDropTargetDragEvent.getDropTargetContext().setTargetActions(0);
    } 
    initializeAutoscrolling(paramDropTargetDragEvent.getLocation());
  }
  
  public void dragOver(DropTargetDragEvent paramDropTargetDragEvent) {
    if (!this.active)
      return; 
    if (this.dtListener != null && this.active)
      this.dtListener.dragOver(paramDropTargetDragEvent); 
    updateAutoscroll(paramDropTargetDragEvent.getLocation());
  }
  
  public void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent) {
    if (!this.active)
      return; 
    if (this.dtListener != null)
      this.dtListener.dropActionChanged(paramDropTargetDragEvent); 
    updateAutoscroll(paramDropTargetDragEvent.getLocation());
  }
  
  public void dragExit(DropTargetEvent paramDropTargetEvent) {
    this.isDraggingInside = false;
    if (!this.active)
      return; 
    if (this.dtListener != null && this.active)
      this.dtListener.dragExit(paramDropTargetEvent); 
    clearAutoscroll();
  }
  
  public void drop(DropTargetDropEvent paramDropTargetDropEvent) {
    this.isDraggingInside = false;
    clearAutoscroll();
    if (this.dtListener != null && this.active) {
      this.dtListener.drop(paramDropTargetDropEvent);
    } else {
      paramDropTargetDropEvent.rejectDrop();
    } 
  }
  
  public FlavorMap getFlavorMap() { return this.flavorMap; }
  
  public void setFlavorMap(FlavorMap paramFlavorMap) { this.flavorMap = (paramFlavorMap == null) ? SystemFlavorMap.getDefaultFlavorMap() : paramFlavorMap; }
  
  public void addNotify(ComponentPeer paramComponentPeer) {
    if (paramComponentPeer == this.componentPeer)
      return; 
    this.componentPeer = paramComponentPeer;
    for (Component component1 = this.component; component1 != null && paramComponentPeer instanceof java.awt.peer.LightweightPeer; component1 = component1.getParent())
      paramComponentPeer = component1.getPeer(); 
    if (paramComponentPeer instanceof DropTargetPeer) {
      this.nativePeer = paramComponentPeer;
      ((DropTargetPeer)paramComponentPeer).addDropTarget(this);
    } else {
      this.nativePeer = null;
    } 
  }
  
  public void removeNotify(ComponentPeer paramComponentPeer) {
    if (this.nativePeer != null)
      ((DropTargetPeer)this.nativePeer).removeDropTarget(this); 
    this.componentPeer = this.nativePeer = null;
    synchronized (this) {
      if (this.isDraggingInside)
        dragExit(new DropTargetEvent(getDropTargetContext())); 
    } 
  }
  
  public DropTargetContext getDropTargetContext() { return this.dropTargetContext; }
  
  protected DropTargetContext createDropTargetContext() { return new DropTargetContext(this); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(this.dtListener) ? this.dtListener : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    try {
      this.dropTargetContext = (DropTargetContext)getField.get("dropTargetContext", null);
    } catch (IllegalArgumentException illegalArgumentException) {}
    if (this.dropTargetContext == null)
      this.dropTargetContext = createDropTargetContext(); 
    this.component = (Component)getField.get("component", null);
    this.actions = getField.get("actions", 3);
    this.active = getField.get("active", true);
    try {
      this.dtListener = (DropTargetListener)getField.get("dtListener", null);
    } catch (IllegalArgumentException illegalArgumentException) {
      this.dtListener = (DropTargetListener)paramObjectInputStream.readObject();
    } 
  }
  
  protected DropTargetAutoScroller createDropTargetAutoScroller(Component paramComponent, Point paramPoint) { return new DropTargetAutoScroller(paramComponent, paramPoint); }
  
  protected void initializeAutoscrolling(Point paramPoint) {
    if (this.component == null || !(this.component instanceof Autoscroll))
      return; 
    this.autoScroller = createDropTargetAutoScroller(this.component, paramPoint);
  }
  
  protected void updateAutoscroll(Point paramPoint) {
    if (this.autoScroller != null)
      this.autoScroller.updateLocation(paramPoint); 
  }
  
  protected void clearAutoscroll() throws HeadlessException {
    if (this.autoScroller != null) {
      this.autoScroller.stop();
      this.autoScroller = null;
    } 
  }
  
  protected static class DropTargetAutoScroller implements ActionListener {
    private Component component;
    
    private Autoscroll autoScroll;
    
    private Timer timer;
    
    private Point locn;
    
    private Point prev;
    
    private Rectangle outer = new Rectangle();
    
    private Rectangle inner = new Rectangle();
    
    private int hysteresis = 10;
    
    protected DropTargetAutoScroller(Component param1Component, Point param1Point) {
      this.component = param1Component;
      this.autoScroll = (Autoscroll)this.component;
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Integer integer1;
      Integer integer2 = (integer1 = Integer.valueOf(100)).valueOf(100);
      try {
        integer1 = (Integer)toolkit.getDesktopProperty("DnD.Autoscroll.initialDelay");
      } catch (Exception exception) {}
      try {
        integer2 = (Integer)toolkit.getDesktopProperty("DnD.Autoscroll.interval");
      } catch (Exception exception) {}
      this.timer = new Timer(integer2.intValue(), this);
      this.timer.setCoalesce(true);
      this.timer.setInitialDelay(integer1.intValue());
      this.locn = param1Point;
      this.prev = param1Point;
      try {
        this.hysteresis = ((Integer)toolkit.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
      } catch (Exception exception) {}
      this.timer.start();
    }
    
    private void updateRegion() throws HeadlessException {
      Insets insets = this.autoScroll.getAutoscrollInsets();
      Dimension dimension = this.component.getSize();
      if (dimension.width != this.outer.width || dimension.height != this.outer.height)
        this.outer.reshape(0, 0, dimension.width, dimension.height); 
      if (this.inner.x != insets.left || this.inner.y != insets.top)
        this.inner.setLocation(insets.left, insets.top); 
      int i = dimension.width - insets.left + insets.right;
      int j = dimension.height - insets.top + insets.bottom;
      if (i != this.inner.width || j != this.inner.height)
        this.inner.setSize(i, j); 
    }
    
    protected void updateLocation(Point param1Point) {
      this.prev = this.locn;
      this.locn = param1Point;
      if (Math.abs(this.locn.x - this.prev.x) > this.hysteresis || Math.abs(this.locn.y - this.prev.y) > this.hysteresis) {
        if (this.timer.isRunning())
          this.timer.stop(); 
      } else if (!this.timer.isRunning()) {
        this.timer.start();
      } 
    }
    
    protected void stop() throws HeadlessException { this.timer.stop(); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      updateRegion();
      if (this.outer.contains(this.locn) && !this.inner.contains(this.locn))
        this.autoScroll.autoscroll(this.locn); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DropTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */