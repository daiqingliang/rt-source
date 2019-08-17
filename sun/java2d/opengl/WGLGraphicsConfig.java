package sun.java2d.opengl;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.ImageCapabilities;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsDevice;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.SunGraphics2D;
import sun.java2d.Surface;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.windows.GDIWindowSurfaceData;

public class WGLGraphicsConfig extends Win32GraphicsConfig implements OGLGraphicsConfig {
  protected static boolean wglAvailable;
  
  private static ImageCapabilities imageCaps = new WGLImageCaps(null);
  
  private BufferCapabilities bufferCaps;
  
  private long pConfigInfo;
  
  private ContextCapabilities oglCaps;
  
  private OGLContext context;
  
  private Object disposerReferent = new Object();
  
  public static native int getDefaultPixFmt(int paramInt);
  
  private static native boolean initWGL();
  
  private static native long getWGLConfigInfo(int paramInt1, int paramInt2);
  
  private static native int getOGLCapabilities(long paramLong);
  
  protected WGLGraphicsConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt, long paramLong, ContextCapabilities paramContextCapabilities) {
    super(paramWin32GraphicsDevice, paramInt);
    this.pConfigInfo = paramLong;
    this.oglCaps = paramContextCapabilities;
    this.context = new OGLContext(OGLRenderQueue.getInstance(), this);
    Disposer.addRecord(this.disposerReferent, new WGLGCDisposerRecord(this.pConfigInfo, paramWin32GraphicsDevice.getScreen()));
  }
  
  public Object getProxyKey() { return this; }
  
  public SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3) { return WGLSurfaceData.createData(this, paramInt1, paramInt2, getColorModel(paramInt3), null, 3); }
  
  public static WGLGraphicsConfig getConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt) {
    if (!wglAvailable)
      return null; 
    long l = 0L;
    final String[] ids = new String[1];
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      OGLContext.invalidateCurrentContext();
      WGLGetConfigInfo wGLGetConfigInfo = new WGLGetConfigInfo(paramWin32GraphicsDevice.getScreen(), paramInt, null);
      oGLRenderQueue.flushAndInvokeNow(wGLGetConfigInfo);
      l = wGLGetConfigInfo.getConfigInfo();
      if (l != 0L) {
        OGLContext.setScratchSurface(l);
        oGLRenderQueue.flushAndInvokeNow(new Runnable() {
              public void run() { ids[0] = OGLContext.getOGLIdString(); }
            });
      } 
    } finally {
      oGLRenderQueue.unlock();
    } 
    if (l == 0L)
      return null; 
    int i = getOGLCapabilities(l);
    OGLContext.OGLContextCaps oGLContextCaps = new OGLContext.OGLContextCaps(i, arrayOfString[0]);
    return new WGLGraphicsConfig(paramWin32GraphicsDevice, paramInt, l, oGLContextCaps);
  }
  
  public static boolean isWGLAvailable() { return wglAvailable; }
  
  public final boolean isCapPresent(int paramInt) { return ((this.oglCaps.getCaps() & paramInt) != 0); }
  
  public final long getNativeConfigInfo() { return this.pConfigInfo; }
  
  public final OGLContext getContext() { return this.context; }
  
  public void displayChanged() {
    super.displayChanged();
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      OGLContext.invalidateCurrentContext();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  public ColorModel getColorModel(int paramInt) {
    ColorSpace colorSpace;
    switch (paramInt) {
      case 1:
        return new DirectColorModel(24, 16711680, 65280, 255);
      case 2:
        return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
        colorSpace = ColorSpace.getInstance(1000);
        return new DirectColorModel(colorSpace, 32, 16711680, 65280, 255, -16777216, true, 3);
    } 
    return null;
  }
  
  public String toString() { return "WGLGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]"; }
  
  public SurfaceData createSurfaceData(WComponentPeer paramWComponentPeer, int paramInt) {
    GDIWindowSurfaceData gDIWindowSurfaceData = WGLSurfaceData.createData(paramWComponentPeer);
    if (gDIWindowSurfaceData == null)
      gDIWindowSurfaceData = GDIWindowSurfaceData.createData(paramWComponentPeer); 
    return gDIWindowSurfaceData;
  }
  
  public void assertOperationSupported(Component paramComponent, int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
    if (paramInt > 2)
      throw new AWTException("Only double or single buffering is supported"); 
    BufferCapabilities bufferCapabilities = getBufferCapabilities();
    if (!bufferCapabilities.isPageFlipping())
      throw new AWTException("Page flipping is not supported"); 
    if (paramBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.PRIOR)
      throw new AWTException("FlipContents.PRIOR is not supported"); 
  }
  
  public VolatileImage createBackBuffer(WComponentPeer paramWComponentPeer) {
    Component component = (Component)paramWComponentPeer.getTarget();
    int i = Math.max(1, component.getWidth());
    int j = Math.max(1, component.getHeight());
    return new SunVolatileImage(component, i, j, Boolean.TRUE);
  }
  
  public void flip(WComponentPeer paramWComponentPeer, Component paramComponent, VolatileImage paramVolatileImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents) {
    if (paramFlipContents == BufferCapabilities.FlipContents.COPIED) {
      SurfaceManager surfaceManager = SurfaceManager.getManager(paramVolatileImage);
      SurfaceData surfaceData = surfaceManager.getPrimarySurfaceData();
      if (surfaceData instanceof WGLSurfaceData.WGLVSyncOffScreenSurfaceData) {
        WGLSurfaceData.WGLVSyncOffScreenSurfaceData wGLVSyncOffScreenSurfaceData = (WGLSurfaceData.WGLVSyncOffScreenSurfaceData)surfaceData;
        SurfaceData surfaceData1 = wGLVSyncOffScreenSurfaceData.getFlipSurface();
        sunGraphics2D = new SunGraphics2D(surfaceData1, Color.black, Color.white, null);
        try {
          sunGraphics2D.drawImage(paramVolatileImage, 0, 0, null);
        } finally {
          sunGraphics2D.dispose();
        } 
      } else {
        graphics = paramWComponentPeer.getGraphics();
        try {
          graphics.drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
        } finally {
          graphics.dispose();
        } 
        return;
      } 
    } else if (paramFlipContents == BufferCapabilities.FlipContents.PRIOR) {
      return;
    } 
    OGLSurfaceData.swapBuffers(paramWComponentPeer.getData());
    if (paramFlipContents == BufferCapabilities.FlipContents.BACKGROUND) {
      graphics = paramVolatileImage.getGraphics();
      try {
        graphics.setColor(paramComponent.getBackground());
        graphics.fillRect(0, 0, paramVolatileImage.getWidth(), paramVolatileImage.getHeight());
      } finally {
        graphics.dispose();
      } 
    } 
  }
  
  public BufferCapabilities getBufferCapabilities() {
    if (this.bufferCaps == null) {
      boolean bool = isCapPresent(65536);
      this.bufferCaps = new WGLBufferCaps(bool);
    } 
    return this.bufferCaps;
  }
  
  public ImageCapabilities getImageCapabilities() { return imageCaps; }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt4 == 4 || paramInt4 == 1 || paramInt4 == 0 || paramInt3 == 2)
      return null; 
    if (paramInt4 == 5) {
      if (!isCapPresent(12))
        return null; 
    } else if (paramInt4 == 2) {
      boolean bool = (paramInt3 == 1) ? 1 : 0;
      if (!bool && !isCapPresent(2))
        return null; 
    } 
    AccelTypedVolatileImage accelTypedVolatileImage = new AccelTypedVolatileImage(this, paramInt1, paramInt2, paramInt3, paramInt4);
    Surface surface = accelTypedVolatileImage.getDestSurface();
    if (!(surface instanceof AccelSurface) || ((AccelSurface)surface).getType() != paramInt4) {
      accelTypedVolatileImage.flush();
      accelTypedVolatileImage = null;
    } 
    return accelTypedVolatileImage;
  }
  
  public ContextCapabilities getContextCapabilities() { return this.oglCaps; }
  
  public void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener) { AccelDeviceEventNotifier.addListener(paramAccelDeviceEventListener, this.screen.getScreen()); }
  
  public void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener) { AccelDeviceEventNotifier.removeListener(paramAccelDeviceEventListener); }
  
  static  {
    wglAvailable = initWGL();
  }
  
  private static class WGLBufferCaps extends BufferCapabilities {
    public WGLBufferCaps(boolean param1Boolean) { super(imageCaps, imageCaps, param1Boolean ? BufferCapabilities.FlipContents.UNDEFINED : null); }
  }
  
  private static class WGLGCDisposerRecord implements DisposerRecord {
    private long pCfgInfo;
    
    private int screen;
    
    public WGLGCDisposerRecord(long param1Long, int param1Int) { this.pCfgInfo = param1Long; }
    
    public void dispose() {
      oGLRenderQueue = OGLRenderQueue.getInstance();
      oGLRenderQueue.lock();
      try {
        oGLRenderQueue.flushAndInvokeNow(new Runnable() {
              public void run() {
                AccelDeviceEventNotifier.eventOccured(WGLGraphicsConfig.WGLGCDisposerRecord.this.screen, 0);
                AccelDeviceEventNotifier.eventOccured(WGLGraphicsConfig.WGLGCDisposerRecord.this.screen, 1);
              }
            });
      } finally {
        oGLRenderQueue.unlock();
      } 
      if (this.pCfgInfo != 0L) {
        OGLRenderQueue.disposeGraphicsConfig(this.pCfgInfo);
        this.pCfgInfo = 0L;
      } 
    }
  }
  
  private static class WGLGetConfigInfo implements Runnable {
    private int screen;
    
    private int pixfmt;
    
    private long cfginfo;
    
    private WGLGetConfigInfo(int param1Int1, int param1Int2) {
      this.screen = param1Int1;
      this.pixfmt = param1Int2;
    }
    
    public void run() { this.cfginfo = WGLGraphicsConfig.getWGLConfigInfo(this.screen, this.pixfmt); }
    
    public long getConfigInfo() { return this.cfginfo; }
  }
  
  private static class WGLImageCaps extends ImageCapabilities {
    private WGLImageCaps() { super(true); }
    
    public boolean isTrueVolatile() { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\WGLGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */