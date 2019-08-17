package sun.java2d.opengl;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import sun.awt.SunToolkit;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;

public abstract class WGLSurfaceData extends OGLSurfaceData {
  protected WComponentPeer peer;
  
  private WGLGraphicsConfig graphicsConfig;
  
  private native void initOps(long paramLong1, WComponentPeer paramWComponentPeer, long paramLong2);
  
  protected native boolean initPbuffer(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
  
  protected WGLSurfaceData(WComponentPeer paramWComponentPeer, WGLGraphicsConfig paramWGLGraphicsConfig, ColorModel paramColorModel, int paramInt) {
    super(paramWGLGraphicsConfig, paramColorModel, paramInt);
    this.peer = paramWComponentPeer;
    this.graphicsConfig = paramWGLGraphicsConfig;
    long l1 = paramWGLGraphicsConfig.getNativeConfigInfo();
    long l2 = (paramWComponentPeer != null) ? paramWComponentPeer.getHWnd() : 0L;
    initOps(l1, paramWComponentPeer, l2);
  }
  
  public GraphicsConfiguration getDeviceConfiguration() { return this.graphicsConfig; }
  
  public static WGLWindowSurfaceData createData(WComponentPeer paramWComponentPeer) {
    if (!paramWComponentPeer.isAccelCapable() || !SunToolkit.isContainingTopLevelOpaque((Component)paramWComponentPeer.getTarget()))
      return null; 
    WGLGraphicsConfig wGLGraphicsConfig = getGC(paramWComponentPeer);
    return new WGLWindowSurfaceData(paramWComponentPeer, wGLGraphicsConfig);
  }
  
  public static WGLOffScreenSurfaceData createData(WComponentPeer paramWComponentPeer, Image paramImage, int paramInt) {
    if (!paramWComponentPeer.isAccelCapable() || !SunToolkit.isContainingTopLevelOpaque((Component)paramWComponentPeer.getTarget()))
      return null; 
    WGLGraphicsConfig wGLGraphicsConfig = getGC(paramWComponentPeer);
    Rectangle rectangle = paramWComponentPeer.getBounds();
    return (paramInt == 4) ? new WGLOffScreenSurfaceData(paramWComponentPeer, wGLGraphicsConfig, rectangle.width, rectangle.height, paramImage, paramWComponentPeer.getColorModel(), paramInt) : new WGLVSyncOffScreenSurfaceData(paramWComponentPeer, wGLGraphicsConfig, rectangle.width, rectangle.height, paramImage, paramWComponentPeer.getColorModel(), paramInt);
  }
  
  public static WGLOffScreenSurfaceData createData(WGLGraphicsConfig paramWGLGraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, int paramInt3) { return new WGLOffScreenSurfaceData(null, paramWGLGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, paramInt3); }
  
  public static WGLGraphicsConfig getGC(WComponentPeer paramWComponentPeer) {
    if (paramWComponentPeer != null)
      return (WGLGraphicsConfig)paramWComponentPeer.getGraphicsConfiguration(); 
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
    return (WGLGraphicsConfig)graphicsDevice.getDefaultConfiguration();
  }
  
  public static native boolean updateWindowAccelImpl(long paramLong, WComponentPeer paramWComponentPeer, int paramInt1, int paramInt2);
  
  public static class WGLOffScreenSurfaceData extends WGLSurfaceData {
    private Image offscreenImage;
    
    private int width;
    
    private int height;
    
    public WGLOffScreenSurfaceData(WComponentPeer param1WComponentPeer, WGLGraphicsConfig param1WGLGraphicsConfig, int param1Int1, int param1Int2, Image param1Image, ColorModel param1ColorModel, int param1Int3) {
      super(param1WComponentPeer, param1WGLGraphicsConfig, param1ColorModel, param1Int3);
      this.width = param1Int1;
      this.height = param1Int2;
      this.offscreenImage = param1Image;
      initSurface(param1Int1, param1Int2);
    }
    
    public SurfaceData getReplacement() { return restoreContents(this.offscreenImage); }
    
    public Rectangle getBounds() {
      if (this.type == 4) {
        Rectangle rectangle = this.peer.getBounds();
        rectangle.x = rectangle.y = 0;
        return rectangle;
      } 
      return new Rectangle(this.width, this.height);
    }
    
    public Object getDestination() { return this.offscreenImage; }
  }
  
  public static class WGLVSyncOffScreenSurfaceData extends WGLOffScreenSurfaceData {
    private WGLSurfaceData.WGLOffScreenSurfaceData flipSurface;
    
    public WGLVSyncOffScreenSurfaceData(WComponentPeer param1WComponentPeer, WGLGraphicsConfig param1WGLGraphicsConfig, int param1Int1, int param1Int2, Image param1Image, ColorModel param1ColorModel, int param1Int3) {
      super(param1WComponentPeer, param1WGLGraphicsConfig, param1Int1, param1Int2, param1Image, param1ColorModel, param1Int3);
      this.flipSurface = WGLSurfaceData.createData(param1WComponentPeer, param1Image, 4);
    }
    
    public SurfaceData getFlipSurface() { return this.flipSurface; }
    
    public void flush() {
      this.flipSurface.flush();
      super.flush();
    }
  }
  
  public static class WGLWindowSurfaceData extends WGLSurfaceData {
    public WGLWindowSurfaceData(WComponentPeer param1WComponentPeer, WGLGraphicsConfig param1WGLGraphicsConfig) { super(param1WComponentPeer, param1WGLGraphicsConfig, param1WComponentPeer.getColorModel(), 1); }
    
    public SurfaceData getReplacement() { return this.peer.getSurfaceData(); }
    
    public Rectangle getBounds() {
      Rectangle rectangle = this.peer.getBounds();
      rectangle.x = rectangle.y = 0;
      return rectangle;
    }
    
    public Object getDestination() { return this.peer.getTarget(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\WGLSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */