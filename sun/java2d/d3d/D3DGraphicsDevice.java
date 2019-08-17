package sun.java2d.d3d;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.WindowPeer;
import java.util.ArrayList;
import sun.awt.Win32GraphicsDevice;
import sun.awt.windows.WWindowPeer;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.windows.WindowsFlags;
import sun.misc.PerfCounter;

public class D3DGraphicsDevice extends Win32GraphicsDevice {
  private D3DContext context;
  
  private static boolean d3dAvailable;
  
  private ContextCapabilities d3dCaps;
  
  private boolean fsStatus;
  
  private Rectangle ownerOrigBounds = null;
  
  private boolean ownerWasVisible;
  
  private Window realFSWindow;
  
  private WindowListener fsWindowListener;
  
  private boolean fsWindowWasAlwaysOnTop;
  
  private static native boolean initD3D();
  
  public static D3DGraphicsDevice createDevice(int paramInt) {
    if (!d3dAvailable)
      return null; 
    ContextCapabilities contextCapabilities = getDeviceCaps(paramInt);
    if ((contextCapabilities.getCaps() & 0x40000) == 0) {
      if (WindowsFlags.isD3DVerbose())
        System.out.println("Could not enable Direct3D pipeline on screen " + paramInt); 
      return null;
    } 
    if (WindowsFlags.isD3DVerbose())
      System.out.println("Direct3D pipeline enabled on screen " + paramInt); 
    return new D3DGraphicsDevice(paramInt, contextCapabilities);
  }
  
  private static native int getDeviceCapsNative(int paramInt);
  
  private static native String getDeviceIdNative(int paramInt);
  
  private static ContextCapabilities getDeviceCaps(final int screen) {
    D3DContext.D3DContextCaps d3DContextCaps = null;
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      class Result {
        int caps;
        
        String id;
      };
      final Result res = new Result();
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() {
              this.val$res.caps = D3DGraphicsDevice.getDeviceCapsNative(screen);
              this.val$res.id = D3DGraphicsDevice.getDeviceIdNative(screen);
            }
          });
      d3DContextCaps = new D3DContext.D3DContextCaps(result.caps, result.id);
    } finally {
      d3DRenderQueue.unlock();
    } 
    return (d3DContextCaps != null) ? d3DContextCaps : new D3DContext.D3DContextCaps(0, null);
  }
  
  public final boolean isCapPresent(int paramInt) { return ((this.d3dCaps.getCaps() & paramInt) != 0); }
  
  private D3DGraphicsDevice(int paramInt, ContextCapabilities paramContextCapabilities) {
    super(paramInt);
    this.descString = "D3DGraphicsDevice[screen=" + paramInt;
    this.d3dCaps = paramContextCapabilities;
    this.context = new D3DContext(D3DRenderQueue.getInstance(), this);
  }
  
  public boolean isD3DEnabledOnDevice() { return (isValid() && isCapPresent(262144)); }
  
  public static boolean isD3DAvailable() { return d3dAvailable; }
  
  private Frame getToplevelOwner(Window paramWindow) {
    Window window = paramWindow;
    while (window != null) {
      window = window.getOwner();
      if (window instanceof Frame)
        return (Frame)window; 
    } 
    return null;
  }
  
  private static native boolean enterFullScreenExclusiveNative(int paramInt, long paramLong);
  
  protected void enterFullScreenExclusive(final int screen, WindowPeer paramWindowPeer) {
    final WWindowPeer wpeer = (WWindowPeer)this.realFSWindow.getPeer();
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() {
              long l = wpeer.getHWnd();
              if (l == 0L) {
                D3DGraphicsDevice.this.fsStatus = false;
                return;
              } 
              D3DGraphicsDevice.this.fsStatus = D3DGraphicsDevice.enterFullScreenExclusiveNative(screen, l);
            }
          });
    } finally {
      d3DRenderQueue.unlock();
    } 
    if (!this.fsStatus)
      super.enterFullScreenExclusive(paramInt, paramWindowPeer); 
  }
  
  private static native boolean exitFullScreenExclusiveNative(int paramInt);
  
  protected void exitFullScreenExclusive(final int screen, WindowPeer paramWindowPeer) {
    if (this.fsStatus) {
      d3DRenderQueue = D3DRenderQueue.getInstance();
      d3DRenderQueue.lock();
      try {
        d3DRenderQueue.flushAndInvokeNow(new Runnable() {
              public void run() { D3DGraphicsDevice.exitFullScreenExclusiveNative(screen); }
            });
      } finally {
        d3DRenderQueue.unlock();
      } 
    } else {
      super.exitFullScreenExclusive(paramInt, paramWindowPeer);
    } 
  }
  
  protected void addFSWindowListener(Window paramWindow) {
    if (!(paramWindow instanceof Frame) && !(paramWindow instanceof java.awt.Dialog) && (this.realFSWindow = getToplevelOwner(paramWindow)) != null) {
      this.ownerOrigBounds = this.realFSWindow.getBounds();
      WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
      this.ownerWasVisible = this.realFSWindow.isVisible();
      Rectangle rectangle = paramWindow.getBounds();
      wWindowPeer.reshape(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      wWindowPeer.setVisible(true);
    } else {
      this.realFSWindow = paramWindow;
    } 
    this.fsWindowWasAlwaysOnTop = this.realFSWindow.isAlwaysOnTop();
    ((WWindowPeer)this.realFSWindow.getPeer()).setAlwaysOnTop(true);
    this.fsWindowListener = new D3DFSWindowAdapter(null);
    this.realFSWindow.addWindowListener(this.fsWindowListener);
  }
  
  protected void removeFSWindowListener(Window paramWindow) {
    this.realFSWindow.removeWindowListener(this.fsWindowListener);
    this.fsWindowListener = null;
    WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
    if (wWindowPeer != null) {
      if (this.ownerOrigBounds != null) {
        if (this.ownerOrigBounds.width == 0)
          this.ownerOrigBounds.width = 1; 
        if (this.ownerOrigBounds.height == 0)
          this.ownerOrigBounds.height = 1; 
        wWindowPeer.reshape(this.ownerOrigBounds.x, this.ownerOrigBounds.y, this.ownerOrigBounds.width, this.ownerOrigBounds.height);
        if (!this.ownerWasVisible)
          wWindowPeer.setVisible(false); 
        this.ownerOrigBounds = null;
      } 
      if (!this.fsWindowWasAlwaysOnTop)
        wWindowPeer.setAlwaysOnTop(false); 
    } 
    this.realFSWindow = null;
  }
  
  private static native DisplayMode getCurrentDisplayModeNative(int paramInt);
  
  protected DisplayMode getCurrentDisplayMode(final int screen) {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      class Result {
        DisplayMode dm = null;
      };
      final Result res = new Result();
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() { this.val$res.dm = D3DGraphicsDevice.getCurrentDisplayModeNative(screen); }
          });
      if (result.dm == null)
        return super.getCurrentDisplayMode(paramInt); 
      return result.dm;
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  private static native void configDisplayModeNative(int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  protected void configDisplayMode(final int screen, WindowPeer paramWindowPeer, final int width, final int height, final int bitDepth, final int refreshRate) {
    if (!this.fsStatus) {
      super.configDisplayMode(paramInt1, paramWindowPeer, paramInt2, paramInt3, paramInt4, paramInt5);
      return;
    } 
    final WWindowPeer wpeer = (WWindowPeer)this.realFSWindow.getPeer();
    if (getFullScreenWindow() != this.realFSWindow) {
      Rectangle rectangle = getDefaultConfiguration().getBounds();
      wWindowPeer.reshape(rectangle.x, rectangle.y, paramInt2, paramInt3);
    } 
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() {
              long l = wpeer.getHWnd();
              if (l == 0L)
                return; 
              D3DGraphicsDevice.configDisplayModeNative(screen, l, width, height, bitDepth, refreshRate);
            }
          });
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  private static native void enumDisplayModesNative(int paramInt, ArrayList paramArrayList);
  
  protected void enumDisplayModes(final int screen, final ArrayList modes) {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() { D3DGraphicsDevice.enumDisplayModesNative(screen, modes); }
          });
      if (paramArrayList.size() == 0)
        paramArrayList.add(getCurrentDisplayModeNative(paramInt)); 
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  private static native long getAvailableAcceleratedMemoryNative(int paramInt);
  
  public int getAvailableAcceleratedMemory() {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      class Result {
        long mem = 0L;
      };
      final Result res = new Result();
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() { this.val$res.mem = D3DGraphicsDevice.getAvailableAcceleratedMemoryNative(D3DGraphicsDevice.this.getScreen()); }
          });
      return (int)result.mem;
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public GraphicsConfiguration[] getConfigurations() {
    if (this.configs == null && isD3DEnabledOnDevice()) {
      this.defaultConfig = getDefaultConfiguration();
      if (this.defaultConfig != null) {
        this.configs = new GraphicsConfiguration[1];
        this.configs[0] = this.defaultConfig;
        return (GraphicsConfiguration[])this.configs.clone();
      } 
    } 
    return super.getConfigurations();
  }
  
  public GraphicsConfiguration getDefaultConfiguration() {
    if (this.defaultConfig == null)
      if (isD3DEnabledOnDevice()) {
        this.defaultConfig = new D3DGraphicsConfig(this);
      } else {
        this.defaultConfig = super.getDefaultConfiguration();
      }  
    return this.defaultConfig;
  }
  
  private static native boolean isD3DAvailableOnDeviceNative(int paramInt);
  
  public static boolean isD3DAvailableOnDevice(final int screen) {
    if (!d3dAvailable)
      return false; 
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      class Result {
        boolean avail = false;
      };
      final Result res = new Result();
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() { this.val$res.avail = D3DGraphicsDevice.isD3DAvailableOnDeviceNative(screen); }
          });
      return result.avail;
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  D3DContext getContext() { return this.context; }
  
  ContextCapabilities getContextCapabilities() { return this.d3dCaps; }
  
  public void displayChanged() {
    super.displayChanged();
    if (d3dAvailable)
      this.d3dCaps = getDeviceCaps(getScreen()); 
  }
  
  protected void invalidate(int paramInt) {
    super.invalidate(paramInt);
    this.d3dCaps = new D3DContext.D3DContextCaps(0, null);
  }
  
  static  {
    Toolkit.getDefaultToolkit();
    d3dAvailable = initD3D();
    if (d3dAvailable) {
      pfDisabled = true;
      PerfCounter.getD3DAvailable().set(1L);
    } else {
      PerfCounter.getD3DAvailable().set(0L);
    } 
  }
  
  private static class D3DFSWindowAdapter extends WindowAdapter {
    private D3DFSWindowAdapter() {}
    
    public void windowDeactivated(WindowEvent param1WindowEvent) { D3DRenderQueue.getInstance().restoreDevices(); }
    
    public void windowActivated(WindowEvent param1WindowEvent) { D3DRenderQueue.getInstance().restoreDevices(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DGraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */