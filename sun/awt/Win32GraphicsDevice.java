package sun.awt;

import java.awt.AWTPermission;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ColorModel;
import java.awt.peer.WindowPeer;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Vector;
import sun.awt.windows.WWindowPeer;
import sun.java2d.opengl.WGLGraphicsConfig;
import sun.java2d.windows.WindowsFlags;
import sun.security.action.GetPropertyAction;

public class Win32GraphicsDevice extends GraphicsDevice implements DisplayChangedListener {
  int screen;
  
  ColorModel dynamicColorModel;
  
  ColorModel colorModel;
  
  protected GraphicsConfiguration[] configs;
  
  protected GraphicsConfiguration defaultConfig;
  
  private final String idString;
  
  protected String descString;
  
  private boolean valid;
  
  private SunDisplayChanger topLevels = new SunDisplayChanger();
  
  protected static boolean pfDisabled;
  
  private static AWTPermission fullScreenExclusivePermission;
  
  private DisplayMode defaultDisplayMode;
  
  private WindowListener fsWindowListener;
  
  private static native void initIDs();
  
  native void initDevice(int paramInt);
  
  public Win32GraphicsDevice(int paramInt) {
    this.screen = paramInt;
    this.idString = "\\Display" + this.screen;
    this.descString = "Win32GraphicsDevice[screen=" + this.screen;
    this.valid = true;
    initDevice(paramInt);
  }
  
  public int getType() { return 0; }
  
  public int getScreen() { return this.screen; }
  
  public boolean isValid() { return this.valid; }
  
  protected void invalidate(int paramInt) {
    this.valid = false;
    this.screen = paramInt;
  }
  
  public String getIDstring() { return this.idString; }
  
  public GraphicsConfiguration[] getConfigurations() {
    if (this.configs == null) {
      if (WindowsFlags.isOGLEnabled() && isDefaultDevice()) {
        this.defaultConfig = getDefaultConfiguration();
        if (this.defaultConfig != null) {
          this.configs = new GraphicsConfiguration[1];
          this.configs[0] = this.defaultConfig;
          return (GraphicsConfiguration[])this.configs.clone();
        } 
      } 
      int i = getMaxConfigs(this.screen);
      int j = getDefaultPixID(this.screen);
      Vector vector = new Vector(i);
      if (j == 0) {
        this.defaultConfig = Win32GraphicsConfig.getConfig(this, j);
        vector.addElement(this.defaultConfig);
      } else {
        for (byte b = 1; b <= i; b++) {
          if (isPixFmtSupported(b, this.screen))
            if (b == j) {
              this.defaultConfig = Win32GraphicsConfig.getConfig(this, b);
              vector.addElement(this.defaultConfig);
            } else {
              vector.addElement(Win32GraphicsConfig.getConfig(this, b));
            }  
        } 
      } 
      this.configs = new GraphicsConfiguration[vector.size()];
      vector.copyInto(this.configs);
    } 
    return (GraphicsConfiguration[])this.configs.clone();
  }
  
  protected int getMaxConfigs(int paramInt) { return pfDisabled ? 1 : getMaxConfigsImpl(paramInt); }
  
  private native int getMaxConfigsImpl(int paramInt);
  
  protected native boolean isPixFmtSupported(int paramInt1, int paramInt2);
  
  protected int getDefaultPixID(int paramInt) { return pfDisabled ? 0 : getDefaultPixIDImpl(paramInt); }
  
  private native int getDefaultPixIDImpl(int paramInt);
  
  public GraphicsConfiguration getDefaultConfiguration() {
    if (this.defaultConfig == null) {
      if (WindowsFlags.isOGLEnabled() && isDefaultDevice()) {
        int i = WGLGraphicsConfig.getDefaultPixFmt(this.screen);
        this.defaultConfig = WGLGraphicsConfig.getConfig(this, i);
        if (WindowsFlags.isOGLVerbose()) {
          if (this.defaultConfig != null) {
            System.out.print("OpenGL pipeline enabled");
          } else {
            System.out.print("Could not enable OpenGL pipeline");
          } 
          System.out.println(" for default config on screen " + this.screen);
        } 
      } 
      if (this.defaultConfig == null)
        this.defaultConfig = Win32GraphicsConfig.getConfig(this, 0); 
    } 
    return this.defaultConfig;
  }
  
  public String toString() { return this.valid ? (this.descString + "]") : (this.descString + ", removed]"); }
  
  private boolean isDefaultDevice() { return (this == GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()); }
  
  private static boolean isFSExclusiveModeAllowed() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      if (fullScreenExclusivePermission == null)
        fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive"); 
      try {
        securityManager.checkPermission(fullScreenExclusivePermission);
      } catch (SecurityException securityException) {
        return false;
      } 
    } 
    return true;
  }
  
  public boolean isFullScreenSupported() { return isFSExclusiveModeAllowed(); }
  
  public void setFullScreenWindow(Window paramWindow) {
    Window window = getFullScreenWindow();
    if (paramWindow == window)
      return; 
    if (!isFullScreenSupported()) {
      super.setFullScreenWindow(paramWindow);
      return;
    } 
    if (window != null) {
      if (this.defaultDisplayMode != null) {
        setDisplayMode(this.defaultDisplayMode);
        this.defaultDisplayMode = null;
      } 
      WWindowPeer wWindowPeer = (WWindowPeer)window.getPeer();
      if (wWindowPeer != null) {
        wWindowPeer.setFullScreenExclusiveModeState(false);
        synchronized (wWindowPeer) {
          exitFullScreenExclusive(this.screen, wWindowPeer);
        } 
      } 
      removeFSWindowListener(window);
    } 
    super.setFullScreenWindow(paramWindow);
    if (paramWindow != null) {
      this.defaultDisplayMode = getDisplayMode();
      addFSWindowListener(paramWindow);
      WWindowPeer wWindowPeer = (WWindowPeer)paramWindow.getPeer();
      if (wWindowPeer != null) {
        synchronized (wWindowPeer) {
          enterFullScreenExclusive(this.screen, wWindowPeer);
        } 
        wWindowPeer.setFullScreenExclusiveModeState(true);
      } 
      wWindowPeer.updateGC();
    } 
  }
  
  protected native void enterFullScreenExclusive(int paramInt, WindowPeer paramWindowPeer);
  
  protected native void exitFullScreenExclusive(int paramInt, WindowPeer paramWindowPeer);
  
  public boolean isDisplayChangeSupported() { return (isFullScreenSupported() && getFullScreenWindow() != null); }
  
  public void setDisplayMode(DisplayMode paramDisplayMode) {
    if (!isDisplayChangeSupported()) {
      super.setDisplayMode(paramDisplayMode);
      return;
    } 
    if (paramDisplayMode == null || (paramDisplayMode = getMatchingDisplayMode(paramDisplayMode)) == null)
      throw new IllegalArgumentException("Invalid display mode"); 
    if (getDisplayMode().equals(paramDisplayMode))
      return; 
    Window window = getFullScreenWindow();
    if (window != null) {
      WWindowPeer wWindowPeer = (WWindowPeer)window.getPeer();
      configDisplayMode(this.screen, wWindowPeer, paramDisplayMode.getWidth(), paramDisplayMode.getHeight(), paramDisplayMode.getBitDepth(), paramDisplayMode.getRefreshRate());
      Rectangle rectangle = getDefaultConfiguration().getBounds();
      window.setBounds(rectangle.x, rectangle.y, paramDisplayMode.getWidth(), paramDisplayMode.getHeight());
    } else {
      throw new IllegalStateException("Must be in fullscreen mode in order to set display mode");
    } 
  }
  
  protected native DisplayMode getCurrentDisplayMode(int paramInt);
  
  protected native void configDisplayMode(int paramInt1, WindowPeer paramWindowPeer, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  protected native void enumDisplayModes(int paramInt, ArrayList paramArrayList);
  
  public DisplayMode getDisplayMode() { return getCurrentDisplayMode(this.screen); }
  
  public DisplayMode[] getDisplayModes() {
    ArrayList arrayList = new ArrayList();
    enumDisplayModes(this.screen, arrayList);
    int i = arrayList.size();
    DisplayMode[] arrayOfDisplayMode = new DisplayMode[i];
    for (byte b = 0; b < i; b++)
      arrayOfDisplayMode[b] = (DisplayMode)arrayList.get(b); 
    return arrayOfDisplayMode;
  }
  
  protected DisplayMode getMatchingDisplayMode(DisplayMode paramDisplayMode) {
    if (!isDisplayChangeSupported())
      return null; 
    DisplayMode[] arrayOfDisplayMode = getDisplayModes();
    for (DisplayMode displayMode : arrayOfDisplayMode) {
      if (paramDisplayMode.equals(displayMode) || (paramDisplayMode.getRefreshRate() == 0 && paramDisplayMode.getWidth() == displayMode.getWidth() && paramDisplayMode.getHeight() == displayMode.getHeight() && paramDisplayMode.getBitDepth() == displayMode.getBitDepth()))
        return displayMode; 
    } 
    return null;
  }
  
  public void displayChanged() {
    this.dynamicColorModel = null;
    this.defaultConfig = null;
    this.configs = null;
    this.topLevels.notifyListeners();
  }
  
  public void paletteChanged() {}
  
  public void addDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener) { this.topLevels.add(paramDisplayChangedListener); }
  
  public void removeDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener) { this.topLevels.remove(paramDisplayChangedListener); }
  
  private native ColorModel makeColorModel(int paramInt, boolean paramBoolean);
  
  public ColorModel getDynamicColorModel() {
    if (this.dynamicColorModel == null)
      this.dynamicColorModel = makeColorModel(this.screen, true); 
    return this.dynamicColorModel;
  }
  
  public ColorModel getColorModel() {
    if (this.colorModel == null)
      this.colorModel = makeColorModel(this.screen, false); 
    return this.colorModel;
  }
  
  protected void addFSWindowListener(final Window w) {
    this.fsWindowListener = new Win32FSWindowAdapter(this);
    EventQueue.invokeLater(new Runnable() {
          public void run() { w.addWindowListener(Win32GraphicsDevice.this.fsWindowListener); }
        });
  }
  
  protected void removeFSWindowListener(Window paramWindow) {
    paramWindow.removeWindowListener(this.fsWindowListener);
    this.fsWindowListener = null;
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.nopixfmt"));
    pfDisabled = (str != null);
    initIDs();
  }
  
  private static class Win32FSWindowAdapter extends WindowAdapter {
    private Win32GraphicsDevice device;
    
    private DisplayMode dm;
    
    Win32FSWindowAdapter(Win32GraphicsDevice param1Win32GraphicsDevice) { this.device = param1Win32GraphicsDevice; }
    
    private void setFSWindowsState(Window param1Window, int param1Int) {
      GraphicsDevice[] arrayOfGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
      if (param1Window != null)
        for (GraphicsDevice graphicsDevice : arrayOfGraphicsDevice) {
          if (param1Window == graphicsDevice.getFullScreenWindow())
            return; 
        }  
      for (GraphicsDevice graphicsDevice : arrayOfGraphicsDevice) {
        Window window = graphicsDevice.getFullScreenWindow();
        if (window instanceof Frame)
          ((Frame)window).setExtendedState(param1Int); 
      } 
    }
    
    public void windowDeactivated(WindowEvent param1WindowEvent) { setFSWindowsState(param1WindowEvent.getOppositeWindow(), 1); }
    
    public void windowActivated(WindowEvent param1WindowEvent) { setFSWindowsState(param1WindowEvent.getOppositeWindow(), 0); }
    
    public void windowIconified(WindowEvent param1WindowEvent) {
      DisplayMode displayMode = this.device.defaultDisplayMode;
      if (displayMode != null) {
        this.dm = this.device.getDisplayMode();
        this.device.setDisplayMode(displayMode);
      } 
    }
    
    public void windowDeiconified(WindowEvent param1WindowEvent) {
      if (this.dm != null) {
        this.device.setDisplayMode(this.dm);
        this.dm = null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\Win32GraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */