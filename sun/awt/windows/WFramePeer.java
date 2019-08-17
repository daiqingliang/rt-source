package sun.awt.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.peer.FramePeer;
import java.security.AccessController;
import sun.awt.AWTAccessor;
import sun.awt.im.InputMethodManager;
import sun.security.action.GetPropertyAction;

class WFramePeer extends WWindowPeer implements FramePeer {
  private static final boolean keepOnMinimize;
  
  private static native void initIDs();
  
  public native void setState(int paramInt);
  
  public native int getState();
  
  public void setExtendedState(int paramInt) { AWTAccessor.getFrameAccessor().setExtendedState((Frame)this.target, paramInt); }
  
  public int getExtendedState() { return AWTAccessor.getFrameAccessor().getExtendedState((Frame)this.target); }
  
  private native void setMaximizedBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private native void clearMaximizedBounds();
  
  public void setMaximizedBounds(Rectangle paramRectangle) {
    if (paramRectangle == null) {
      clearMaximizedBounds();
    } else {
      Rectangle rectangle = (Rectangle)paramRectangle.clone();
      adjustMaximizedBounds(rectangle);
      setMaximizedBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  private void adjustMaximizedBounds(Rectangle paramRectangle) {
    GraphicsConfiguration graphicsConfiguration1 = getGraphicsConfiguration();
    GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    GraphicsConfiguration graphicsConfiguration2 = graphicsDevice.getDefaultConfiguration();
    if (graphicsConfiguration1 != null && graphicsConfiguration1 != graphicsConfiguration2) {
      Rectangle rectangle1 = graphicsConfiguration1.getBounds();
      Rectangle rectangle2 = graphicsConfiguration2.getBounds();
      boolean bool = (rectangle1.width - rectangle2.width > 0 || rectangle1.height - rectangle2.height > 0) ? 1 : 0;
      if (bool) {
        paramRectangle.width -= rectangle1.width - rectangle2.width;
        paramRectangle.height -= rectangle1.height - rectangle2.height;
      } 
    } 
  }
  
  public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) {
    boolean bool = super.updateGraphicsData(paramGraphicsConfiguration);
    Rectangle rectangle = AWTAccessor.getFrameAccessor().getMaximizedBounds((Frame)this.target);
    if (rectangle != null)
      setMaximizedBounds(rectangle); 
    return bool;
  }
  
  boolean isTargetUndecorated() { return ((Frame)this.target).isUndecorated(); }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (((Frame)this.target).isUndecorated()) {
      super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      reshapeFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public Dimension getMinimumSize() {
    Dimension dimension = new Dimension();
    if (!((Frame)this.target).isUndecorated())
      dimension.setSize(getSysMinWidth(), getSysMinHeight()); 
    if (((Frame)this.target).getMenuBar() != null)
      dimension.height += getSysMenuHeight(); 
    return dimension;
  }
  
  public void setMenuBar(MenuBar paramMenuBar) {
    WMenuBarPeer wMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(paramMenuBar);
    if (wMenuBarPeer != null) {
      if (wMenuBarPeer.framePeer != this) {
        paramMenuBar.removeNotify();
        paramMenuBar.addNotify();
        wMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(paramMenuBar);
        if (wMenuBarPeer != null && wMenuBarPeer.framePeer != this)
          throw new IllegalStateException("Wrong parent peer"); 
      } 
      if (wMenuBarPeer != null)
        addChildPeer(wMenuBarPeer); 
    } 
    setMenuBar0(wMenuBarPeer);
    updateInsets(this.insets_);
  }
  
  private native void setMenuBar0(WMenuBarPeer paramWMenuBarPeer);
  
  WFramePeer(Frame paramFrame) {
    super(paramFrame);
    InputMethodManager inputMethodManager = InputMethodManager.getInstance();
    String str = inputMethodManager.getTriggerMenuString();
    if (str != null)
      pSetIMMOption(str); 
  }
  
  native void createAwtFrame(WComponentPeer paramWComponentPeer);
  
  void create(WComponentPeer paramWComponentPeer) {
    preCreate(paramWComponentPeer);
    createAwtFrame(paramWComponentPeer);
  }
  
  void initialize() {
    super.initialize();
    Frame frame = (Frame)this.target;
    if (frame.getTitle() != null)
      setTitle(frame.getTitle()); 
    setResizable(frame.isResizable());
    setState(frame.getExtendedState());
  }
  
  private static native int getSysMenuHeight();
  
  native void pSetIMMOption(String paramString);
  
  void notifyIMMOptionChange() { InputMethodManager.getInstance().notifyChangeRequest((Component)this.target); }
  
  public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3); }
  
  public Rectangle getBoundsPrivate() { return getBounds(); }
  
  public void emulateActivation(boolean paramBoolean) { synthesizeWmActivate(paramBoolean); }
  
  private native void synthesizeWmActivate(boolean paramBoolean);
  
  static  {
    initIDs();
    keepOnMinimize = "true".equals(AccessController.doPrivileged(new GetPropertyAction("sun.awt.keepWorkingSetOnMinimize")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */