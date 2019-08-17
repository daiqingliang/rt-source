package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.peer.FramePeer;
import java.util.List;

public abstract class LightweightFrame extends Frame {
  private int hostX;
  
  private int hostY;
  
  private int hostW;
  
  private int hostH;
  
  public LightweightFrame() {
    setUndecorated(true);
    setResizable(true);
    setEnabled(true);
  }
  
  public final Container getParent() { return null; }
  
  public Graphics getGraphics() { return null; }
  
  public final boolean isResizable() { return true; }
  
  public final void setTitle(String paramString) {}
  
  public final void setIconImage(Image paramImage) {}
  
  public final void setIconImages(List<? extends Image> paramList) {}
  
  public final void setMenuBar(MenuBar paramMenuBar) {}
  
  public final void setResizable(boolean paramBoolean) {}
  
  public final void remove(MenuComponent paramMenuComponent) {}
  
  public final void toFront() {}
  
  public final void toBack() {}
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (getPeer() == null) {
        SunToolkit sunToolkit = (SunToolkit)Toolkit.getDefaultToolkit();
        try {
          setPeer(sunToolkit.createLightweightFrame(this));
        } catch (Exception exception) {
          throw new RuntimeException(exception);
        } 
      } 
      super.addNotify();
    } 
  }
  
  private void setPeer(FramePeer paramFramePeer) { AWTAccessor.getComponentAccessor().setPeer(this, paramFramePeer); }
  
  public void emulateActivation(boolean paramBoolean) { ((FramePeer)getPeer()).emulateActivation(paramBoolean); }
  
  public abstract void grabFocus();
  
  public abstract void ungrabFocus();
  
  public abstract int getScaleFactor();
  
  public abstract void notifyDisplayChanged(int paramInt);
  
  public Rectangle getHostBounds() { return (this.hostX == 0 && this.hostY == 0 && this.hostW == 0 && this.hostH == 0) ? getBounds() : new Rectangle(this.hostX, this.hostY, this.hostW, this.hostH); }
  
  public void setHostBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.hostX = paramInt1;
    this.hostY = paramInt2;
    this.hostW = paramInt3;
    this.hostH = paramInt4;
  }
  
  public abstract <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener);
  
  public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException;
  
  public abstract void addDropTarget(DropTarget paramDropTarget);
  
  public abstract void removeDropTarget(DropTarget paramDropTarget);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\LightweightFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */