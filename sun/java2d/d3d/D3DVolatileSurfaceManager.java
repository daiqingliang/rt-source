package sun.java2d.d3d;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.ColorModel;
import sun.awt.Win32GraphicsConfig;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.VolatileSurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.InvalidPipeException;
import sun.java2d.SurfaceData;
import sun.java2d.windows.GDIWindowSurfaceData;

public class D3DVolatileSurfaceManager extends VolatileSurfaceManager {
  private boolean accelerationEnabled;
  
  private int restoreCountdown;
  
  public D3DVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject) {
    super(paramSunVolatileImage, paramObject);
    int i = paramSunVolatileImage.getTransparency();
    D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)paramSunVolatileImage.getGraphicsConfig().getDevice();
    this.accelerationEnabled = (i == 1 || (i == 3 && (d3DGraphicsDevice.isCapPresent(2) || d3DGraphicsDevice.isCapPresent(4))));
  }
  
  protected boolean isAccelerationEnabled() { return this.accelerationEnabled; }
  
  public void setAccelerationEnabled(boolean paramBoolean) { this.accelerationEnabled = paramBoolean; }
  
  protected SurfaceData initAcceleratedSurface() {
    SurfaceData surfaceData;
    Component component = this.vImg.getComponent();
    WComponentPeer wComponentPeer = (component != null) ? (WComponentPeer)component.getPeer() : null;
    try {
      boolean bool = false;
      if (this.context instanceof Boolean)
        bool = ((Boolean)this.context).booleanValue(); 
      if (bool) {
        surfaceData = D3DSurfaceData.createData(wComponentPeer, this.vImg);
      } else {
        D3DGraphicsConfig d3DGraphicsConfig = (D3DGraphicsConfig)this.vImg.getGraphicsConfig();
        ColorModel colorModel = d3DGraphicsConfig.getColorModel(this.vImg.getTransparency());
        int i = this.vImg.getForcedAccelSurfaceType();
        if (i == 0)
          i = 5; 
        surfaceData = D3DSurfaceData.createData(d3DGraphicsConfig, this.vImg.getWidth(), this.vImg.getHeight(), colorModel, this.vImg, i);
      } 
    } catch (NullPointerException nullPointerException) {
      surfaceData = null;
    } catch (OutOfMemoryError outOfMemoryError) {
      surfaceData = null;
    } catch (InvalidPipeException invalidPipeException) {
      surfaceData = null;
    } 
    return surfaceData;
  }
  
  protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration) { return (paramGraphicsConfiguration == null || paramGraphicsConfiguration == this.vImg.getGraphicsConfig()); }
  
  private void setRestoreCountdown(int paramInt) { this.restoreCountdown = paramInt; }
  
  protected void restoreAcceleratedSurface() {
    synchronized (this) {
      if (this.restoreCountdown > 0) {
        this.restoreCountdown--;
        throw new InvalidPipeException("Will attempt to restore surface  in " + this.restoreCountdown);
      } 
    } 
    SurfaceData surfaceData = initAcceleratedSurface();
    if (surfaceData != null) {
      this.sdAccel = surfaceData;
    } else {
      throw new InvalidPipeException("could not restore surface");
    } 
  }
  
  public SurfaceData restoreContents() {
    acceleratedSurfaceLost();
    return super.restoreContents();
  }
  
  static void handleVItoScreenOp(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2) {
    if (paramSurfaceData1 instanceof D3DSurfaceData && paramSurfaceData2 instanceof GDIWindowSurfaceData) {
      D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)paramSurfaceData1;
      SurfaceManager surfaceManager = SurfaceManager.getManager((Image)d3DSurfaceData.getDestination());
      if (surfaceManager instanceof D3DVolatileSurfaceManager) {
        D3DVolatileSurfaceManager d3DVolatileSurfaceManager = (D3DVolatileSurfaceManager)surfaceManager;
        if (d3DVolatileSurfaceManager != null) {
          d3DSurfaceData.setSurfaceLost(true);
          GDIWindowSurfaceData gDIWindowSurfaceData = (GDIWindowSurfaceData)paramSurfaceData2;
          WComponentPeer wComponentPeer = gDIWindowSurfaceData.getPeer();
          if (D3DScreenUpdateManager.canUseD3DOnScreen(wComponentPeer, (Win32GraphicsConfig)wComponentPeer.getGraphicsConfiguration(), wComponentPeer.getBackBuffersNum())) {
            d3DVolatileSurfaceManager.setRestoreCountdown(10);
          } else {
            d3DVolatileSurfaceManager.setAccelerationEnabled(false);
          } 
        } 
      } 
    } 
  }
  
  public void initContents() {
    if (this.vImg.getForcedAccelSurfaceType() != 3)
      super.initContents(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */