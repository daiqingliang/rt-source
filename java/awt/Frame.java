package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.FramePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class Frame extends Window implements MenuContainer {
  @Deprecated
  public static final int DEFAULT_CURSOR = 0;
  
  @Deprecated
  public static final int CROSSHAIR_CURSOR = 1;
  
  @Deprecated
  public static final int TEXT_CURSOR = 2;
  
  @Deprecated
  public static final int WAIT_CURSOR = 3;
  
  @Deprecated
  public static final int SW_RESIZE_CURSOR = 4;
  
  @Deprecated
  public static final int SE_RESIZE_CURSOR = 5;
  
  @Deprecated
  public static final int NW_RESIZE_CURSOR = 6;
  
  @Deprecated
  public static final int NE_RESIZE_CURSOR = 7;
  
  @Deprecated
  public static final int N_RESIZE_CURSOR = 8;
  
  @Deprecated
  public static final int S_RESIZE_CURSOR = 9;
  
  @Deprecated
  public static final int W_RESIZE_CURSOR = 10;
  
  @Deprecated
  public static final int E_RESIZE_CURSOR = 11;
  
  @Deprecated
  public static final int HAND_CURSOR = 12;
  
  @Deprecated
  public static final int MOVE_CURSOR = 13;
  
  public static final int NORMAL = 0;
  
  public static final int ICONIFIED = 1;
  
  public static final int MAXIMIZED_HORIZ = 2;
  
  public static final int MAXIMIZED_VERT = 4;
  
  public static final int MAXIMIZED_BOTH = 6;
  
  Rectangle maximizedBounds;
  
  String title = "Untitled";
  
  MenuBar menuBar;
  
  boolean resizable = true;
  
  boolean undecorated = false;
  
  boolean mbManagement = false;
  
  private int state = 0;
  
  Vector<Window> ownedWindows;
  
  private static final String base = "frame";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = 2673458971256075116L;
  
  private int frameSerializedDataVersion = 1;
  
  public Frame() throws HeadlessException { this(""); }
  
  public Frame(GraphicsConfiguration paramGraphicsConfiguration) { this("", paramGraphicsConfiguration); }
  
  public Frame(String paramString) throws HeadlessException { init(paramString, null); }
  
  public Frame(String paramString, GraphicsConfiguration paramGraphicsConfiguration) {
    super(paramGraphicsConfiguration);
    init(paramString, paramGraphicsConfiguration);
  }
  
  private void init(String paramString, GraphicsConfiguration paramGraphicsConfiguration) {
    this.title = paramString;
    SunToolkit.checkAndSetPolicy(this);
  }
  
  String constructComponentName() {
    synchronized (Frame.class) {
      return "frame" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createFrame(this); 
      FramePeer framePeer = (FramePeer)this.peer;
      MenuBar menuBar1 = this.menuBar;
      if (menuBar1 != null) {
        this.mbManagement = true;
        menuBar1.addNotify();
        framePeer.setMenuBar(menuBar1);
      } 
      framePeer.setMaximizedBounds(this.maximizedBounds);
      super.addNotify();
    } 
  }
  
  public String getTitle() { return this.title; }
  
  public void setTitle(String paramString) throws HeadlessException {
    String str = this.title;
    if (paramString == null)
      paramString = ""; 
    synchronized (this) {
      this.title = paramString;
      FramePeer framePeer = (FramePeer)this.peer;
      if (framePeer != null)
        framePeer.setTitle(paramString); 
    } 
    firePropertyChange("title", str, paramString);
  }
  
  public Image getIconImage() {
    List list = this.icons;
    return (list != null && list.size() > 0) ? (Image)list.get(0) : null;
  }
  
  public void setIconImage(Image paramImage) { super.setIconImage(paramImage); }
  
  public MenuBar getMenuBar() { return this.menuBar; }
  
  public void setMenuBar(MenuBar paramMenuBar) {
    synchronized (getTreeLock()) {
      if (this.menuBar == paramMenuBar)
        return; 
      if (paramMenuBar != null && paramMenuBar.parent != null)
        paramMenuBar.parent.remove(paramMenuBar); 
      if (this.menuBar != null)
        remove(this.menuBar); 
      this.menuBar = paramMenuBar;
      if (this.menuBar != null) {
        this.menuBar.parent = this;
        FramePeer framePeer = (FramePeer)this.peer;
        if (framePeer != null) {
          this.mbManagement = true;
          this.menuBar.addNotify();
          invalidateIfValid();
          framePeer.setMenuBar(this.menuBar);
        } 
      } 
    } 
  }
  
  public boolean isResizable() { return this.resizable; }
  
  public void setResizable(boolean paramBoolean) {
    boolean bool = this.resizable;
    boolean bool1 = false;
    synchronized (this) {
      this.resizable = paramBoolean;
      FramePeer framePeer = (FramePeer)this.peer;
      if (framePeer != null) {
        framePeer.setResizable(paramBoolean);
        bool1 = true;
      } 
    } 
    if (bool1)
      invalidateIfValid(); 
    firePropertyChange("resizable", bool, paramBoolean);
  }
  
  public void setState(int paramInt) {
    int i = getExtendedState();
    if (paramInt == 1 && (i & true) == 0) {
      setExtendedState(i | true);
    } else if (paramInt == 0 && (i & true) != 0) {
      setExtendedState(i & 0xFFFFFFFE);
    } 
  }
  
  public void setExtendedState(int paramInt) {
    if (!isFrameStateSupported(paramInt))
      return; 
    synchronized (getObjectLock()) {
      this.state = paramInt;
    } 
    FramePeer framePeer = (FramePeer)this.peer;
    if (framePeer != null)
      framePeer.setState(paramInt); 
  }
  
  private boolean isFrameStateSupported(int paramInt) {
    if (!getToolkit().isFrameStateSupported(paramInt)) {
      if ((paramInt & true) != 0 && !getToolkit().isFrameStateSupported(1))
        return false; 
      paramInt &= 0xFFFFFFFE;
      return getToolkit().isFrameStateSupported(paramInt);
    } 
    return true;
  }
  
  public int getState() { return ((getExtendedState() & true) != 0) ? 1 : 0; }
  
  public int getExtendedState() {
    synchronized (getObjectLock()) {
      return this.state;
    } 
  }
  
  public void setMaximizedBounds(Rectangle paramRectangle) {
    synchronized (getObjectLock()) {
      this.maximizedBounds = paramRectangle;
    } 
    FramePeer framePeer = (FramePeer)this.peer;
    if (framePeer != null)
      framePeer.setMaximizedBounds(paramRectangle); 
  }
  
  public Rectangle getMaximizedBounds() {
    synchronized (getObjectLock()) {
      return this.maximizedBounds;
    } 
  }
  
  public void setUndecorated(boolean paramBoolean) {
    synchronized (getTreeLock()) {
      if (isDisplayable())
        throw new IllegalComponentStateException("The frame is displayable."); 
      if (!paramBoolean) {
        if (getOpacity() < 1.0F)
          throw new IllegalComponentStateException("The frame is not opaque"); 
        if (getShape() != null)
          throw new IllegalComponentStateException("The frame does not have a default shape"); 
        Color color = getBackground();
        if (color != null && color.getAlpha() < 255)
          throw new IllegalComponentStateException("The frame background color is not opaque"); 
      } 
      this.undecorated = paramBoolean;
    } 
  }
  
  public boolean isUndecorated() { return this.undecorated; }
  
  public void setOpacity(float paramFloat) {
    synchronized (getTreeLock()) {
      if (paramFloat < 1.0F && !isUndecorated())
        throw new IllegalComponentStateException("The frame is decorated"); 
      super.setOpacity(paramFloat);
    } 
  }
  
  public void setShape(Shape paramShape) {
    synchronized (getTreeLock()) {
      if (paramShape != null && !isUndecorated())
        throw new IllegalComponentStateException("The frame is decorated"); 
      super.setShape(paramShape);
    } 
  }
  
  public void setBackground(Color paramColor) {
    synchronized (getTreeLock()) {
      if (paramColor != null && paramColor.getAlpha() < 255 && !isUndecorated())
        throw new IllegalComponentStateException("The frame is decorated"); 
      super.setBackground(paramColor);
    } 
  }
  
  public void remove(MenuComponent paramMenuComponent) {
    if (paramMenuComponent == null)
      return; 
    synchronized (getTreeLock()) {
      if (paramMenuComponent == this.menuBar) {
        this.menuBar = null;
        FramePeer framePeer = (FramePeer)this.peer;
        if (framePeer != null) {
          this.mbManagement = true;
          invalidateIfValid();
          framePeer.setMenuBar(null);
          paramMenuComponent.removeNotify();
        } 
        paramMenuComponent.parent = null;
      } else {
        super.remove(paramMenuComponent);
      } 
    } 
  }
  
  public void removeNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      FramePeer framePeer = (FramePeer)this.peer;
      if (framePeer != null) {
        getState();
        if (this.menuBar != null) {
          this.mbManagement = true;
          framePeer.setMenuBar(null);
          this.menuBar.removeNotify();
        } 
      } 
      super.removeNotify();
    } 
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent) {
    if (this.menuBar != null && this.menuBar.handleShortcut(paramKeyEvent)) {
      paramKeyEvent.consume();
      return;
    } 
    super.postProcessKeyEvent(paramKeyEvent);
  }
  
  protected String paramString() {
    String str = super.paramString();
    if (this.title != null)
      str = str + ",title=" + this.title; 
    if (this.resizable)
      str = str + ",resizable"; 
    int i = getExtendedState();
    if (i == 0) {
      str = str + ",normal";
    } else {
      if ((i & true) != 0)
        str = str + ",iconified"; 
      if ((i & 0x6) == 6) {
        str = str + ",maximized";
      } else if ((i & 0x2) != 0) {
        str = str + ",maximized_horiz";
      } else if ((i & 0x4) != 0) {
        str = str + ",maximized_vert";
      } 
    } 
    return str;
  }
  
  @Deprecated
  public void setCursor(int paramInt) {
    if (paramInt < 0 || paramInt > 13)
      throw new IllegalArgumentException("illegal cursor type"); 
    setCursor(Cursor.getPredefinedCursor(paramInt));
  }
  
  @Deprecated
  public int getCursorType() { return getCursor().getType(); }
  
  public static Frame[] getFrames() {
    Window[] arrayOfWindow = Window.getWindows();
    byte b1 = 0;
    for (Window window : arrayOfWindow) {
      if (window instanceof Frame)
        b1++; 
    } 
    Frame[] arrayOfFrame = new Frame[b1];
    byte b2 = 0;
    for (Window window : arrayOfWindow) {
      if (window instanceof Frame)
        arrayOfFrame[b2++] = (Frame)window; 
    } 
    return arrayOfFrame;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.icons != null && this.icons.size() > 0) {
      Image image = (Image)this.icons.get(0);
      if (image instanceof java.io.Serializable) {
        paramObjectOutputStream.writeObject(image);
        return;
      } 
    } 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    paramObjectInputStream.defaultReadObject();
    try {
      Image image = (Image)paramObjectInputStream.readObject();
      if (this.icons == null) {
        this.icons = new ArrayList();
        this.icons.add(image);
      } 
    } catch (OptionalDataException optionalDataException) {
      if (!optionalDataException.eof)
        throw optionalDataException; 
    } 
    if (this.menuBar != null)
      this.menuBar.parent = this; 
    if (this.ownedWindows != null) {
      for (byte b = 0; b < this.ownedWindows.size(); b++)
        connectOwnedWindow((Window)this.ownedWindows.elementAt(b)); 
      this.ownedWindows = null;
    } 
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTFrame(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setFrameAccessor(new AWTAccessor.FrameAccessor() {
          public void setExtendedState(Frame param1Frame, int param1Int) {
            synchronized (param1Frame.getObjectLock()) {
              param1Frame.state = param1Int;
            } 
          }
          
          public int getExtendedState(Frame param1Frame) {
            synchronized (param1Frame.getObjectLock()) {
              return param1Frame.state;
            } 
          }
          
          public Rectangle getMaximizedBounds(Frame param1Frame) {
            synchronized (param1Frame.getObjectLock()) {
              return param1Frame.maximizedBounds;
            } 
          }
        });
  }
  
  protected class AccessibleAWTFrame extends Window.AccessibleAWTWindow {
    private static final long serialVersionUID = -6172960752956030250L;
    
    protected AccessibleAWTFrame() { super(Frame.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.FRAME; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (Frame.this.getFocusOwner() != null)
        accessibleStateSet.add(AccessibleState.ACTIVE); 
      if (Frame.this.isResizable())
        accessibleStateSet.add(AccessibleState.RESIZABLE); 
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */