package sun.awt;

import java.applet.Applet;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.FramePeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

public abstract class EmbeddedFrame extends Frame implements KeyEventDispatcher, PropertyChangeListener {
  private boolean isCursorAllowed = true;
  
  private boolean supportsXEmbed = false;
  
  private KeyboardFocusManager appletKFM;
  
  private static final long serialVersionUID = 2967042741780317130L;
  
  protected static final boolean FORWARD = true;
  
  protected static final boolean BACKWARD = false;
  
  public boolean supportsXEmbed() { return (this.supportsXEmbed && SunToolkit.needsXEmbed()); }
  
  protected EmbeddedFrame(boolean paramBoolean) { this(0L, paramBoolean); }
  
  protected EmbeddedFrame() { this(0L); }
  
  @Deprecated
  protected EmbeddedFrame(int paramInt) { this(paramInt); }
  
  protected EmbeddedFrame(long paramLong) { this(paramLong, false); }
  
  protected EmbeddedFrame(long paramLong, boolean paramBoolean) {
    this.supportsXEmbed = paramBoolean;
    registerListeners();
  }
  
  public Container getParent() { return null; }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (!paramPropertyChangeEvent.getPropertyName().equals("managingFocus"))
      return; 
    if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE)
      return; 
    removeTraversingOutListeners((KeyboardFocusManager)paramPropertyChangeEvent.getSource());
    this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    if (isVisible())
      addTraversingOutListeners(this.appletKFM); 
  }
  
  private void addTraversingOutListeners(KeyboardFocusManager paramKeyboardFocusManager) {
    paramKeyboardFocusManager.addKeyEventDispatcher(this);
    paramKeyboardFocusManager.addPropertyChangeListener("managingFocus", this);
  }
  
  private void removeTraversingOutListeners(KeyboardFocusManager paramKeyboardFocusManager) {
    paramKeyboardFocusManager.removeKeyEventDispatcher(this);
    paramKeyboardFocusManager.removePropertyChangeListener("managingFocus", this);
  }
  
  public void registerListeners() {
    if (this.appletKFM != null)
      removeTraversingOutListeners(this.appletKFM); 
    this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    if (isVisible())
      addTraversingOutListeners(this.appletKFM); 
  }
  
  public void show() {
    if (this.appletKFM != null)
      addTraversingOutListeners(this.appletKFM); 
    super.show();
  }
  
  public void hide() {
    if (this.appletKFM != null)
      removeTraversingOutListeners(this.appletKFM); 
    super.hide();
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
    Container container = AWTAccessor.getKeyboardFocusManagerAccessor().getCurrentFocusCycleRoot();
    if (this != container)
      return false; 
    if (paramKeyEvent.getID() == 400)
      return false; 
    if (!getFocusTraversalKeysEnabled() || paramKeyEvent.isConsumed())
      return false; 
    AWTKeyStroke aWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
    Component component = paramKeyEvent.getComponent();
    Set set = getFocusTraversalKeys(0);
    if (set.contains(aWTKeyStroke)) {
      Component component1 = getFocusTraversalPolicy().getLastComponent(this);
      if ((component == component1 || component1 == null) && traverseOut(true)) {
        paramKeyEvent.consume();
        return true;
      } 
    } 
    set = getFocusTraversalKeys(1);
    if (set.contains(aWTKeyStroke)) {
      Component component1 = getFocusTraversalPolicy().getFirstComponent(this);
      if ((component == component1 || component1 == null) && traverseOut(false)) {
        paramKeyEvent.consume();
        return true;
      } 
    } 
    return false;
  }
  
  public boolean traverseIn(boolean paramBoolean) {
    Component component = null;
    if (paramBoolean == true) {
      component = getFocusTraversalPolicy().getFirstComponent(this);
    } else {
      component = getFocusTraversalPolicy().getLastComponent(this);
    } 
    if (component != null) {
      AWTAccessor.getKeyboardFocusManagerAccessor().setMostRecentFocusOwner(this, component);
      synthesizeWindowActivation(true);
    } 
    return (null != component);
  }
  
  protected boolean traverseOut(boolean paramBoolean) { return false; }
  
  public void setTitle(String paramString) {}
  
  public void setIconImage(Image paramImage) {}
  
  public void setIconImages(List<? extends Image> paramList) {}
  
  public void setMenuBar(MenuBar paramMenuBar) {}
  
  public void setResizable(boolean paramBoolean) {}
  
  public void remove(MenuComponent paramMenuComponent) {}
  
  public boolean isResizable() { return true; }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (getPeer() == null)
        setPeer(new NullEmbeddedFramePeer(null)); 
      super.addNotify();
    } 
  }
  
  public void setCursorAllowed(boolean paramBoolean) {
    this.isCursorAllowed = paramBoolean;
    getPeer().updateCursorImmediately();
  }
  
  public boolean isCursorAllowed() { return this.isCursorAllowed; }
  
  public Cursor getCursor() { return this.isCursorAllowed ? super.getCursor() : Cursor.getPredefinedCursor(0); }
  
  protected void setPeer(ComponentPeer paramComponentPeer) { AWTAccessor.getComponentAccessor().setPeer(this, paramComponentPeer); }
  
  public void synthesizeWindowActivation(boolean paramBoolean) {}
  
  protected void setLocationPrivate(int paramInt1, int paramInt2) {
    Dimension dimension = getSize();
    setBoundsPrivate(paramInt1, paramInt2, dimension.width, dimension.height);
  }
  
  protected Point getLocationPrivate() {
    Rectangle rectangle = getBoundsPrivate();
    return new Point(rectangle.x, rectangle.y);
  }
  
  protected void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    FramePeer framePeer = (FramePeer)getPeer();
    if (framePeer != null)
      framePeer.setBoundsPrivate(paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  protected Rectangle getBoundsPrivate() {
    FramePeer framePeer = (FramePeer)getPeer();
    return (framePeer != null) ? framePeer.getBoundsPrivate() : getBounds();
  }
  
  public void toFront() {}
  
  public void toBack() {}
  
  public abstract void registerAccelerator(AWTKeyStroke paramAWTKeyStroke);
  
  public abstract void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke);
  
  public static Applet getAppletIfAncestorOf(Component paramComponent) {
    Container container = paramComponent.getParent();
    Applet applet = null;
    while (container != null && !(container instanceof EmbeddedFrame)) {
      if (container instanceof Applet)
        applet = (Applet)container; 
      container = container.getParent();
    } 
    return (container == null) ? null : applet;
  }
  
  public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean) {}
  
  private static class NullEmbeddedFramePeer extends NullComponentPeer implements FramePeer {
    private NullEmbeddedFramePeer() {}
    
    public void setTitle(String param1String) {}
    
    public void setIconImage(Image param1Image) {}
    
    public void updateIconImages() {}
    
    public void setMenuBar(MenuBar param1MenuBar) {}
    
    public void setResizable(boolean param1Boolean) {}
    
    public void setState(int param1Int) {}
    
    public int getState() { return 0; }
    
    public void setMaximizedBounds(Rectangle param1Rectangle) {}
    
    public void toFront() {}
    
    public void toBack() {}
    
    public void updateFocusableWindowState() {}
    
    public void updateAlwaysOnTop() {}
    
    public void updateAlwaysOnTopState() {}
    
    public Component getGlobalHeavyweightFocusOwner() { return null; }
    
    public void setBoundsPrivate(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { setBounds(param1Int1, param1Int2, param1Int3, param1Int4, 3); }
    
    public Rectangle getBoundsPrivate() { return getBounds(); }
    
    public void setModalBlocked(Dialog param1Dialog, boolean param1Boolean) {}
    
    public void restack() { throw new UnsupportedOperationException(); }
    
    public boolean isRestackSupported() { return false; }
    
    public boolean requestWindowFocus() { return false; }
    
    public void updateMinimumSize() {}
    
    public void setOpacity(float param1Float) {}
    
    public void setOpaque(boolean param1Boolean) {}
    
    public void updateWindow() {}
    
    public void repositionSecurityWarning() {}
    
    public void emulateActivation(boolean param1Boolean) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\EmbeddedFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */