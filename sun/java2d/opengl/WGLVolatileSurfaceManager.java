package sun.java2d.opengl;

import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;

public class WGLVolatileSurfaceManager extends VolatileSurfaceManager {
  private boolean accelerationEnabled;
  
  public WGLVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject) {
    super(paramSunVolatileImage, paramObject);
    int i = paramSunVolatileImage.getTransparency();
    WGLGraphicsConfig wGLGraphicsConfig = (WGLGraphicsConfig)paramSunVolatileImage.getGraphicsConfig();
    this.accelerationEnabled = (i == 1 || (i == 3 && (wGLGraphicsConfig.isCapPresent(12) || wGLGraphicsConfig.isCapPresent(2))));
  }
  
  protected boolean isAccelerationEnabled() { return this.accelerationEnabled; }
  
  protected SurfaceData initAcceleratedSurface() {
    SurfaceData surfaceData;
    Component component = this.vImg.getComponent();
    WComponentPeer wComponentPeer = (component != null) ? (WComponentPeer)component.getPeer() : null;
    try {
      boolean bool = false;
      boolean bool1 = false;
      if (this.context instanceof Boolean) {
        bool1 = ((Boolean)this.context).booleanValue();
        if (bool1) {
          BufferCapabilities bufferCapabilities = wComponentPeer.getBackBufferCaps();
          if (bufferCapabilities instanceof ExtendedBufferCapabilities) {
            ExtendedBufferCapabilities extendedBufferCapabilities = (ExtendedBufferCapabilities)bufferCapabilities;
            if (extendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && extendedBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.COPIED) {
              bool = true;
              bool1 = false;
            } 
          } 
        } 
      } 
      if (bool1) {
        surfaceData = WGLSurfaceData.createData(wComponentPeer, this.vImg, 4);
      } else {
        WGLGraphicsConfig wGLGraphicsConfig = (WGLGraphicsConfig)this.vImg.getGraphicsConfig();
        ColorModel colorModel = wGLGraphicsConfig.getColorModel(this.vImg.getTransparency());
        int i = this.vImg.getForcedAccelSurfaceType();
        if (i == 0)
          i = wGLGraphicsConfig.isCapPresent(12) ? 5 : 2; 
        if (bool) {
          surfaceData = WGLSurfaceData.createData(wComponentPeer, this.vImg, i);
        } else {
          surfaceData = WGLSurfaceData.createData(wGLGraphicsConfig, this.vImg.getWidth(), this.vImg.getHeight(), colorModel, this.vImg, i);
        } 
      } 
    } catch (NullPointerException nullPointerException) {
      surfaceData = null;
    } catch (OutOfMemoryError outOfMemoryError) {
      surfaceData = null;
    } 
    return surfaceData;
  }
  
  protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration) { return (paramGraphicsConfiguration == null || (paramGraphicsConfiguration instanceof WGLGraphicsConfig && paramGraphicsConfiguration == this.vImg.getGraphicsConfig())); }
  
  public void initContents() {
    if (this.vImg.getForcedAccelSurfaceType() != 3)
      super.initContents(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\WGLVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */