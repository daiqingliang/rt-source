package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;

public abstract class GlobalCursorManager {
  private final NativeUpdater nativeUpdater = new NativeUpdater();
  
  private long lastUpdateMillis;
  
  private final Object lastUpdateLock = new Object();
  
  public void updateCursorImmediately() {
    synchronized (this.nativeUpdater) {
      this.nativeUpdater.pending = false;
    } 
    _updateCursor(false);
  }
  
  public void updateCursorImmediately(InputEvent paramInputEvent) {
    boolean bool;
    synchronized (this.lastUpdateLock) {
      bool = (paramInputEvent.getWhen() >= this.lastUpdateMillis) ? 1 : 0;
    } 
    if (bool)
      _updateCursor(true); 
  }
  
  public void updateCursorLater(Component paramComponent) { this.nativeUpdater.postIfNotPending(paramComponent, new InvocationEvent(Toolkit.getDefaultToolkit(), this.nativeUpdater)); }
  
  protected abstract void setCursor(Component paramComponent, Cursor paramCursor, boolean paramBoolean);
  
  protected abstract void getCursorPos(Point paramPoint);
  
  protected abstract Point getLocationOnScreen(Component paramComponent);
  
  protected abstract Component findHeavyweightUnderCursor(boolean paramBoolean);
  
  private void _updateCursor(boolean paramBoolean) {
    synchronized (this.lastUpdateLock) {
      this.lastUpdateMillis = System.currentTimeMillis();
    } 
    Point point1 = null;
    Point point2 = null;
    try {
      Component component = findHeavyweightUnderCursor(paramBoolean);
      if (component == null) {
        updateCursorOutOfJava();
        return;
      } 
      if (component instanceof java.awt.Window) {
        point2 = AWTAccessor.getComponentAccessor().getLocation(component);
      } else if (component instanceof Container) {
        point2 = getLocationOnScreen(component);
      } 
      if (point2 != null) {
        point1 = new Point();
        getCursorPos(point1);
        Component component1 = AWTAccessor.getContainerAccessor().findComponentAt((Container)component, point1.x - point2.x, point1.y - point2.y, false);
        if (component1 != null)
          component = component1; 
      } 
      setCursor(component, AWTAccessor.getComponentAccessor().getCursor(component), paramBoolean);
    } catch (IllegalComponentStateException illegalComponentStateException) {}
  }
  
  protected void updateCursorOutOfJava() {}
  
  class NativeUpdater implements Runnable {
    boolean pending = false;
    
    public void run() {
      boolean bool = false;
      synchronized (this) {
        if (this.pending) {
          this.pending = false;
          bool = true;
        } 
      } 
      if (bool)
        GlobalCursorManager.this._updateCursor(false); 
    }
    
    public void postIfNotPending(Component param1Component, InvocationEvent param1InvocationEvent) {
      boolean bool = false;
      synchronized (this) {
        if (!this.pending)
          this.pending = bool = true; 
      } 
      if (bool)
        SunToolkit.postEvent(SunToolkit.targetToAppContext(param1Component), param1InvocationEvent); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\GlobalCursorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */