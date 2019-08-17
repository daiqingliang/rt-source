package sun.java2d.d3d;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.ImageCapabilities;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import sun.awt.Win32GraphicsConfig;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.windows.WComponentPeer;
import sun.java2d.Surface;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import sun.java2d.pipe.hw.ContextCapabilities;

public class D3DGraphicsConfig extends Win32GraphicsConfig implements AccelGraphicsConfig {
  private static ImageCapabilities imageCaps = new D3DImageCaps(null);
  
  private BufferCapabilities bufferCaps;
  
  private D3DGraphicsDevice device;
  
  protected D3DGraphicsConfig(D3DGraphicsDevice paramD3DGraphicsDevice) {
    super(paramD3DGraphicsDevice, 0);
    this.device = paramD3DGraphicsDevice;
  }
  
  public SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3) { return D3DSurfaceData.createData(this, paramInt1, paramInt2, getColorModel(paramInt3), null, 3); }
  
  public void displayChanged() {
    super.displayChanged();
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      D3DContext.invalidateCurrentContext();
    } finally {
      d3DRenderQueue.unlock();
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
  
  public String toString() { return "D3DGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]"; }
  
  public SurfaceData createSurfaceData(WComponentPeer paramWComponentPeer, int paramInt) { return super.createSurfaceData(paramWComponentPeer, paramInt); }
  
  public void assertOperationSupported(Component paramComponent, int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
    if (paramInt < 2 || paramInt > 4)
      throw new AWTException("Only 2-4 buffers supported"); 
    if (paramBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.COPIED && paramInt != 2)
      throw new AWTException("FlipContents.COPIED is onlysupported for 2 buffers"); 
  }
  
  public VolatileImage createBackBuffer(WComponentPeer paramWComponentPeer) {
    Component component = (Component)paramWComponentPeer.getTarget();
    int i = Math.max(1, component.getWidth());
    int j = Math.max(1, component.getHeight());
    return new SunVolatileImage(component, i, j, Boolean.TRUE);
  }
  
  public void flip(WComponentPeer paramWComponentPeer, Component paramComponent, VolatileImage paramVolatileImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents) {
    SurfaceManager surfaceManager = SurfaceManager.getManager(paramVolatileImage);
    SurfaceData surfaceData = surfaceManager.getPrimarySurfaceData();
    if (surfaceData instanceof D3DSurfaceData) {
      D3DSurfaceData d3DSurfaceData;
      (d3DSurfaceData = (D3DSurfaceData)surfaceData).swapBuffers(d3DSurfaceData, paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      graphics = paramWComponentPeer.getGraphics();
      try {
        graphics.drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
      } finally {
        graphics.dispose();
      } 
    } 
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
    if (this.bufferCaps == null)
      this.bufferCaps = new D3DBufferCaps(); 
    return this.bufferCaps;
  }
  
  public ImageCapabilities getImageCapabilities() { return imageCaps; }
  
  D3DGraphicsDevice getD3DDevice() { return this.device; }
  
  public D3DContext getContext() { return this.device.getContext(); }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt4 == 4 || paramInt4 == 1 || paramInt4 == 0 || paramInt3 == 2)
      return null; 
    boolean bool = (paramInt3 == 1) ? 1 : 0;
    if (paramInt4 == 5) {
      byte b = bool ? 8 : 4;
      if (!this.device.isCapPresent(b))
        return null; 
    } else if (paramInt4 == 2 && !bool && !this.device.isCapPresent(2)) {
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
  
  public ContextCapabilities getContextCapabilities() { return this.device.getContextCapabilities(); }
  
  public void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener) { AccelDeviceEventNotifier.addListener(paramAccelDeviceEventListener, this.device.getScreen()); }
  
  public void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener) { AccelDeviceEventNotifier.removeListener(paramAccelDeviceEventListener); }
  
  private static class D3DBufferCaps extends BufferCapabilities {
    public D3DBufferCaps() { super(imageCaps, imageCaps, BufferCapabilities.FlipContents.UNDEFINED); }
    
    public boolean isMultiBufferAvailable() { return true; }
  }
  
  private static class D3DImageCaps extends ImageCapabilities {
    private D3DImageCaps() { super(true); }
    
    public boolean isTrueVolatile() { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */