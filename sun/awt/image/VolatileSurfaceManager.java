package sun.awt.image;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import sun.awt.DisplayChangedListener;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceData;

public abstract class VolatileSurfaceManager extends SurfaceManager implements DisplayChangedListener {
  protected SunVolatileImage vImg;
  
  protected SurfaceData sdAccel;
  
  protected SurfaceData sdBackup;
  
  protected SurfaceData sdCurrent;
  
  protected SurfaceData sdPrevious;
  
  protected boolean lostSurface;
  
  protected Object context;
  
  protected VolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject) {
    this.vImg = paramSunVolatileImage;
    this.context = paramObject;
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (graphicsEnvironment instanceof SunGraphicsEnvironment)
      ((SunGraphicsEnvironment)graphicsEnvironment).addDisplayChangedListener(this); 
  }
  
  public void initialize() {
    if (isAccelerationEnabled()) {
      this.sdAccel = initAcceleratedSurface();
      if (this.sdAccel != null)
        this.sdCurrent = this.sdAccel; 
    } 
    if (this.sdCurrent == null && this.vImg.getForcedAccelSurfaceType() == 0)
      this.sdCurrent = getBackupSurface(); 
  }
  
  public SurfaceData getPrimarySurfaceData() { return this.sdCurrent; }
  
  protected abstract boolean isAccelerationEnabled();
  
  public int validate(GraphicsConfiguration paramGraphicsConfiguration) {
    byte b = 0;
    boolean bool = this.lostSurface;
    this.lostSurface = false;
    if (isAccelerationEnabled()) {
      if (!isConfigValid(paramGraphicsConfiguration)) {
        b = 2;
      } else if (this.sdAccel == null) {
        this.sdAccel = initAcceleratedSurface();
        if (this.sdAccel != null) {
          this.sdCurrent = this.sdAccel;
          this.sdBackup = null;
          b = 1;
        } else {
          this.sdCurrent = getBackupSurface();
        } 
      } else if (this.sdAccel.isSurfaceLost()) {
        try {
          restoreAcceleratedSurface();
          this.sdCurrent = this.sdAccel;
          this.sdAccel.setSurfaceLost(false);
          this.sdBackup = null;
          b = 1;
        } catch (InvalidPipeException invalidPipeException) {
          this.sdCurrent = getBackupSurface();
        } 
      } else if (bool) {
        b = 1;
      } 
    } else if (this.sdAccel != null) {
      this.sdCurrent = getBackupSurface();
      this.sdAccel = null;
      b = 1;
    } 
    if (b != 2 && this.sdCurrent != this.sdPrevious) {
      this.sdPrevious = this.sdCurrent;
      b = 1;
    } 
    if (b == 1)
      initContents(); 
    return b;
  }
  
  public boolean contentsLost() { return this.lostSurface; }
  
  protected abstract SurfaceData initAcceleratedSurface();
  
  protected SurfaceData getBackupSurface() {
    if (this.sdBackup == null) {
      BufferedImage bufferedImage = this.vImg.getBackupImage();
      SunWritableRaster.stealTrackable(bufferedImage.getRaster().getDataBuffer()).setUntrackable();
      this.sdBackup = BufImgSurfaceData.createData(bufferedImage);
    } 
    return this.sdBackup;
  }
  
  public void initContents() {
    if (this.sdCurrent != null) {
      Graphics2D graphics2D = this.vImg.createGraphics();
      graphics2D.clearRect(0, 0, this.vImg.getWidth(), this.vImg.getHeight());
      graphics2D.dispose();
    } 
  }
  
  public SurfaceData restoreContents() { return getBackupSurface(); }
  
  public void acceleratedSurfaceLost() {
    if (isAccelerationEnabled() && this.sdCurrent == this.sdAccel)
      this.lostSurface = true; 
  }
  
  protected void restoreAcceleratedSurface() {}
  
  public void displayChanged() {
    if (!isAccelerationEnabled())
      return; 
    this.lostSurface = true;
    if (this.sdAccel != null) {
      this.sdBackup = null;
      SurfaceData surfaceData = this.sdAccel;
      this.sdAccel = null;
      surfaceData.invalidate();
      this.sdCurrent = getBackupSurface();
    } 
    this.vImg.updateGraphicsConfig();
  }
  
  public void paletteChanged() { this.lostSurface = true; }
  
  protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration) { return (paramGraphicsConfiguration == null || paramGraphicsConfiguration.getDevice() == this.vImg.getGraphicsConfig().getDevice()); }
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration) { return isConfigValid(paramGraphicsConfiguration) ? (isAccelerationEnabled() ? new AcceleratedImageCapabilities() : new ImageCapabilities(false)) : super.getCapabilities(paramGraphicsConfiguration); }
  
  public void flush() {
    this.lostSurface = true;
    SurfaceData surfaceData = this.sdAccel;
    this.sdAccel = null;
    if (surfaceData != null)
      surfaceData.flush(); 
  }
  
  private class AcceleratedImageCapabilities extends ImageCapabilities {
    AcceleratedImageCapabilities() { super(false); }
    
    public boolean isAccelerated() { return (VolatileSurfaceManager.this.sdCurrent == VolatileSurfaceManager.this.sdAccel); }
    
    public boolean isTrueVolatile() { return isAccelerated(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\VolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */