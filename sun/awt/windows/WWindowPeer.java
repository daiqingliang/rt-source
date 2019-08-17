package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.DataBufferInt;
import java.awt.peer.ComponentPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsDevice;
import sun.awt.Win32GraphicsEnvironment;
import sun.java2d.pipe.Region;
import sun.util.logging.PlatformLogger;

public class WWindowPeer extends WPanelPeer implements WindowPeer, DisplayChangedListener {
  private static final PlatformLogger log;
  
  private static final PlatformLogger screenLog = (log = PlatformLogger.getLogger("sun.awt.windows.WWindowPeer")).getLogger("sun.awt.windows.screen.WWindowPeer");
  
  private WWindowPeer modalBlocker = null;
  
  private boolean isOpaque;
  
  private TranslucentWindowPainter painter;
  
  private static final StringBuffer ACTIVE_WINDOWS_KEY = new StringBuffer("active_windows_list");
  
  private static PropertyChangeListener activeWindowListener = new ActiveWindowListener(null);
  
  private static final PropertyChangeListener guiDisposedListener = new GuiDisposedListener(null);
  
  private WindowListener windowListener;
  
  private float opacity = 1.0F;
  
  private static native void initIDs();
  
  protected void disposeImpl() {
    AppContext appContext = SunToolkit.targetToAppContext(this.target);
    synchronized (appContext) {
      List list = (List)appContext.get(ACTIVE_WINDOWS_KEY);
      if (list != null)
        list.remove(this); 
    } 
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
    ((Win32GraphicsDevice)graphicsConfiguration.getDevice()).removeDisplayChangedListener(this);
    synchronized (getStateLock()) {
      TranslucentWindowPainter translucentWindowPainter = this.painter;
      if (translucentWindowPainter != null)
        translucentWindowPainter.flush(); 
    } 
    super.disposeImpl();
  }
  
  public void toFront() {
    updateFocusableWindowState();
    _toFront();
  }
  
  private native void _toFront();
  
  public native void toBack();
  
  private native void setAlwaysOnTopNative(boolean paramBoolean);
  
  public void setAlwaysOnTop(boolean paramBoolean) {
    if ((paramBoolean && ((Window)this.target).isVisible()) || !paramBoolean)
      setAlwaysOnTopNative(paramBoolean); 
  }
  
  public void updateAlwaysOnTopState() { setAlwaysOnTop(((Window)this.target).isAlwaysOnTop()); }
  
  public void updateFocusableWindowState() { setFocusableWindow(((Window)this.target).isFocusableWindow()); }
  
  native void setFocusableWindow(boolean paramBoolean);
  
  public void setTitle(String paramString) {
    if (paramString == null)
      paramString = ""; 
    _setTitle(paramString);
  }
  
  private native void _setTitle(String paramString);
  
  public void setResizable(boolean paramBoolean) { _setResizable(paramBoolean); }
  
  private native void _setResizable(boolean paramBoolean);
  
  WWindowPeer(Window paramWindow) { super(paramWindow); }
  
  void initialize() {
    super.initialize();
    updateInsets(this.insets_);
    Font font = ((Window)this.target).getFont();
    if (font == null) {
      font = defaultFont;
      ((Window)this.target).setFont(font);
      setFont(font);
    } 
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
    ((Win32GraphicsDevice)graphicsConfiguration.getDevice()).addDisplayChangedListener(this);
    initActiveWindowsTracking((Window)this.target);
    updateIconImages();
    Shape shape = ((Window)this.target).getShape();
    if (shape != null)
      applyShape(Region.getInstance(shape, null)); 
    float f = ((Window)this.target).getOpacity();
    if (f < 1.0F)
      setOpacity(f); 
    synchronized (getStateLock()) {
      this.isOpaque = true;
      setOpaque(((Window)this.target).isOpaque());
    } 
  }
  
  native void createAwtWindow(WComponentPeer paramWComponentPeer);
  
  void preCreate(WComponentPeer paramWComponentPeer) { this.windowType = ((Window)this.target).getType(); }
  
  void create(WComponentPeer paramWComponentPeer) {
    preCreate(paramWComponentPeer);
    createAwtWindow(paramWComponentPeer);
  }
  
  final WComponentPeer getNativeParent() {
    Window window = ((Window)this.target).getOwner();
    return (WComponentPeer)WToolkit.targetToPeer(window);
  }
  
  protected void realShow() { super.show(); }
  
  public void show() {
    updateFocusableWindowState();
    boolean bool = ((Window)this.target).isAlwaysOnTop();
    updateGC();
    realShow();
    updateMinimumSize();
    if (((Window)this.target).isAlwaysOnTopSupported() && bool)
      setAlwaysOnTop(bool); 
    synchronized (getStateLock()) {
      if (!this.isOpaque)
        updateWindow(true); 
    } 
    WComponentPeer wComponentPeer = getNativeParent();
    if (wComponentPeer != null && wComponentPeer.isLightweightFramePeer()) {
      Rectangle rectangle = getBounds();
      handleExpose(0, 0, rectangle.width, rectangle.height);
    } 
  }
  
  native void updateInsets(Insets paramInsets);
  
  static native int getSysMinWidth();
  
  static native int getSysMinHeight();
  
  static native int getSysIconWidth();
  
  static native int getSysIconHeight();
  
  static native int getSysSmIconWidth();
  
  static native int getSysSmIconHeight();
  
  native void setIconImagesData(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4);
  
  native void reshapeFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean requestWindowFocus(CausedFocusEvent.Cause paramCause) { return !focusAllowedFor() ? false : requestWindowFocus((paramCause == CausedFocusEvent.Cause.MOUSE_EVENT)); }
  
  private native boolean requestWindowFocus(boolean paramBoolean);
  
  public boolean focusAllowedFor() {
    Window window = (Window)this.target;
    return (!window.isVisible() || !window.isEnabled() || !window.isFocusableWindow()) ? false : (!isModalBlocked());
  }
  
  void hide() {
    WindowListener windowListener1 = this.windowListener;
    if (windowListener1 != null)
      windowListener1.windowClosing(new WindowEvent((Window)this.target, 201)); 
    super.hide();
  }
  
  void preprocessPostEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof WindowEvent) {
      WindowListener windowListener1 = this.windowListener;
      if (windowListener1 != null)
        switch (paramAWTEvent.getID()) {
          case 201:
            windowListener1.windowClosing((WindowEvent)paramAWTEvent);
            break;
          case 203:
            windowListener1.windowIconified((WindowEvent)paramAWTEvent);
            break;
        }  
    } 
  }
  
  void addWindowListener(WindowListener paramWindowListener) { this.windowListener = AWTEventMulticaster.add(this.windowListener, paramWindowListener); }
  
  void removeWindowListener(WindowListener paramWindowListener) { this.windowListener = AWTEventMulticaster.remove(this.windowListener, paramWindowListener); }
  
  public void updateMinimumSize() {
    Dimension dimension = null;
    if (((Component)this.target).isMinimumSizeSet())
      dimension = ((Component)this.target).getMinimumSize(); 
    if (dimension != null) {
      int i = getSysMinWidth();
      int j = getSysMinHeight();
      int k = (dimension.width >= i) ? dimension.width : i;
      int m = (dimension.height >= j) ? dimension.height : j;
      setMinSize(k, m);
    } else {
      setMinSize(0, 0);
    } 
  }
  
  public void updateIconImages() {
    List list = ((Window)this.target).getIconImages();
    if (list == null || list.size() == 0) {
      setIconImagesData(null, 0, 0, null, 0, 0);
    } else {
      int i = getSysIconWidth();
      int j = getSysIconHeight();
      int k = getSysSmIconWidth();
      int m = getSysSmIconHeight();
      DataBufferInt dataBufferInt1 = SunToolkit.getScaledIconData(list, i, j);
      DataBufferInt dataBufferInt2 = SunToolkit.getScaledIconData(list, k, m);
      if (dataBufferInt1 != null && dataBufferInt2 != null) {
        setIconImagesData(dataBufferInt1.getData(), i, j, dataBufferInt2.getData(), k, m);
      } else {
        setIconImagesData(null, 0, 0, null, 0, 0);
      } 
    } 
  }
  
  native void setMinSize(int paramInt1, int paramInt2);
  
  public boolean isModalBlocked() { return (this.modalBlocker != null); }
  
  public void setModalBlocked(Dialog paramDialog, boolean paramBoolean) {
    synchronized (((Component)getTarget()).getTreeLock()) {
      WWindowPeer wWindowPeer = (WWindowPeer)paramDialog.getPeer();
      if (paramBoolean) {
        this.modalBlocker = wWindowPeer;
        if (wWindowPeer instanceof WFileDialogPeer) {
          ((WFileDialogPeer)wWindowPeer).blockWindow(this);
        } else if (wWindowPeer instanceof WPrintDialogPeer) {
          ((WPrintDialogPeer)wWindowPeer).blockWindow(this);
        } else {
          modalDisable(paramDialog, wWindowPeer.getHWnd());
        } 
      } else {
        this.modalBlocker = null;
        if (wWindowPeer instanceof WFileDialogPeer) {
          ((WFileDialogPeer)wWindowPeer).unblockWindow(this);
        } else if (wWindowPeer instanceof WPrintDialogPeer) {
          ((WPrintDialogPeer)wWindowPeer).unblockWindow(this);
        } else {
          modalEnable(paramDialog);
        } 
      } 
    } 
  }
  
  native void modalDisable(Dialog paramDialog, long paramLong);
  
  native void modalEnable(Dialog paramDialog);
  
  public static long[] getActiveWindowHandles(Component paramComponent) {
    AppContext appContext = SunToolkit.targetToAppContext(paramComponent);
    if (appContext == null)
      return null; 
    synchronized (appContext) {
      List list = (List)appContext.get(ACTIVE_WINDOWS_KEY);
      if (list == null)
        return null; 
      long[] arrayOfLong = new long[list.size()];
      for (byte b = 0; b < list.size(); b++)
        arrayOfLong[b] = ((WWindowPeer)list.get(b)).getHWnd(); 
      return arrayOfLong;
    } 
  }
  
  void draggedToNewScreen() { SunToolkit.executeOnEventHandlerThread((Component)this.target, new Runnable() {
          public void run() { WWindowPeer.this.displayChanged(); }
        }); }
  
  public void updateGC() {
    Win32GraphicsDevice win32GraphicsDevice2;
    int i = getScreenImOn();
    if (screenLog.isLoggable(PlatformLogger.Level.FINER))
      log.finer("Screen number: " + i); 
    Win32GraphicsDevice win32GraphicsDevice1 = (Win32GraphicsDevice)this.winGraphicsConfig.getDevice();
    GraphicsDevice[] arrayOfGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    if (i >= arrayOfGraphicsDevice.length) {
      win32GraphicsDevice2 = (Win32GraphicsDevice)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    } else {
      win32GraphicsDevice2 = (Win32GraphicsDevice)arrayOfGraphicsDevice[i];
    } 
    this.winGraphicsConfig = (Win32GraphicsConfig)win32GraphicsDevice2.getDefaultConfiguration();
    if (screenLog.isLoggable(PlatformLogger.Level.FINE) && this.winGraphicsConfig == null)
      screenLog.fine("Assertion (winGraphicsConfig != null) failed"); 
    if (win32GraphicsDevice1 != win32GraphicsDevice2) {
      win32GraphicsDevice1.removeDisplayChangedListener(this);
      win32GraphicsDevice2.addDisplayChangedListener(this);
    } 
    AWTAccessor.getComponentAccessor().setGraphicsConfiguration((Component)this.target, this.winGraphicsConfig);
  }
  
  public void displayChanged() { updateGC(); }
  
  public void paletteChanged() {}
  
  private native int getScreenImOn();
  
  public final native void setFullScreenExclusiveModeState(boolean paramBoolean);
  
  public void grab() { nativeGrab(); }
  
  public void ungrab() { nativeUngrab(); }
  
  private native void nativeGrab();
  
  private native void nativeUngrab();
  
  private final boolean hasWarningWindow() { return (((Window)this.target).getWarningString() != null); }
  
  boolean isTargetUndecorated() { return true; }
  
  public native void repositionSecurityWarning();
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    this.sysX = paramInt1;
    this.sysY = paramInt2;
    this.sysW = paramInt3;
    this.sysH = paramInt4;
    super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void print(Graphics paramGraphics) {
    Shape shape = AWTAccessor.getWindowAccessor().getShape((Window)this.target);
    if (shape != null)
      paramGraphics.setClip(shape); 
    super.print(paramGraphics);
  }
  
  private void replaceSurfaceDataRecursively(Component paramComponent) {
    if (paramComponent instanceof Container)
      for (Component component : ((Container)paramComponent).getComponents())
        replaceSurfaceDataRecursively(component);  
    ComponentPeer componentPeer = paramComponent.getPeer();
    if (componentPeer instanceof WComponentPeer)
      ((WComponentPeer)componentPeer).replaceSurfaceDataLater(); 
  }
  
  public final Graphics getTranslucentGraphics() {
    synchronized (getStateLock()) {
      return this.isOpaque ? null : this.painter.getBackBuffer(false).getGraphics();
    } 
  }
  
  public void setBackground(Color paramColor) {
    super.setBackground(paramColor);
    synchronized (getStateLock()) {
      if (!this.isOpaque && ((Window)this.target).isVisible())
        updateWindow(true); 
    } 
  }
  
  private native void setOpacity(int paramInt);
  
  public void setOpacity(float paramFloat) {
    if (!((SunToolkit)((Window)this.target).getToolkit()).isWindowOpacitySupported())
      return; 
    if (paramFloat < 0.0F || paramFloat > 1.0F)
      throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f]."); 
    if (((this.opacity == 1.0F && paramFloat < 1.0F) || (this.opacity < 1.0F && paramFloat == 1.0F)) && !Win32GraphicsEnvironment.isVistaOS())
      replaceSurfaceDataRecursively((Component)getTarget()); 
    this.opacity = paramFloat;
    int i = (int)(paramFloat * 255.0F);
    if (i < 0)
      i = 0; 
    if (i > 255)
      i = 255; 
    setOpacity(i);
    synchronized (getStateLock()) {
      if (!this.isOpaque && ((Window)this.target).isVisible())
        updateWindow(true); 
    } 
  }
  
  private native void setOpaqueImpl(boolean paramBoolean);
  
  public void setOpaque(boolean paramBoolean) {
    synchronized (getStateLock()) {
      if (this.isOpaque == paramBoolean)
        return; 
    } 
    Window window = (Window)getTarget();
    if (!paramBoolean) {
      SunToolkit sunToolkit = (SunToolkit)window.getToolkit();
      if (!sunToolkit.isWindowTranslucencySupported() || !sunToolkit.isTranslucencyCapable(window.getGraphicsConfiguration()))
        return; 
    } 
    boolean bool = Win32GraphicsEnvironment.isVistaOS();
    if (this.isOpaque != paramBoolean && !bool)
      replaceSurfaceDataRecursively(window); 
    synchronized (getStateLock()) {
      this.isOpaque = paramBoolean;
      setOpaqueImpl(paramBoolean);
      if (paramBoolean) {
        TranslucentWindowPainter translucentWindowPainter = this.painter;
        if (translucentWindowPainter != null) {
          translucentWindowPainter.flush();
          this.painter = null;
        } 
      } else {
        this.painter = TranslucentWindowPainter.createInstance(this);
      } 
    } 
    if (bool) {
      Shape shape = window.getShape();
      if (shape != null)
        window.setShape(shape); 
    } 
    if (window.isVisible())
      updateWindow(true); 
  }
  
  native void updateWindowImpl(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  public void updateWindow() { updateWindow(false); }
  
  private void updateWindow(boolean paramBoolean) {
    Window window = (Window)this.target;
    synchronized (getStateLock()) {
      if (this.isOpaque || !window.isVisible() || window.getWidth() <= 0 || window.getHeight() <= 0)
        return; 
      TranslucentWindowPainter translucentWindowPainter = this.painter;
      if (translucentWindowPainter != null) {
        translucentWindowPainter.updateWindow(paramBoolean);
      } else if (log.isLoggable(PlatformLogger.Level.FINER)) {
        log.finer("Translucent window painter is null in updateWindow");
      } 
    } 
  }
  
  private static void initActiveWindowsTracking(Window paramWindow) {
    AppContext appContext = AppContext.getAppContext();
    synchronized (appContext) {
      List list = (List)appContext.get(ACTIVE_WINDOWS_KEY);
      if (list == null) {
        list = new LinkedList();
        appContext.put(ACTIVE_WINDOWS_KEY, list);
        appContext.addPropertyChangeListener("guidisposed", guiDisposedListener);
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.addPropertyChangeListener("activeWindow", activeWindowListener);
      } 
    } 
  }
  
  static  {
    initIDs();
  }
  
  private static class ActiveWindowListener implements PropertyChangeListener {
    private ActiveWindowListener() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      Window window = (Window)param1PropertyChangeEvent.getNewValue();
      if (window == null)
        return; 
      AppContext appContext = SunToolkit.targetToAppContext(window);
      synchronized (appContext) {
        WWindowPeer wWindowPeer;
        List list = (List)appContext.get(ACTIVE_WINDOWS_KEY);
        if (list != null) {
          list.remove(wWindowPeer);
          list.add(wWindowPeer);
        } 
      } 
    }
  }
  
  private static class GuiDisposedListener implements PropertyChangeListener {
    private GuiDisposedListener() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      boolean bool = ((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue();
      if (bool != true && log.isLoggable(PlatformLogger.Level.FINE))
        log.fine(" Assertion (newValue != true) failed for AppContext.GUI_DISPOSED "); 
      AppContext appContext = AppContext.getAppContext();
      synchronized (appContext) {
        appContext.remove(ACTIVE_WINDOWS_KEY);
        appContext.removePropertyChangeListener("guidisposed", this);
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.removePropertyChangeListener("activeWindow", activeWindowListener);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WWindowPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */