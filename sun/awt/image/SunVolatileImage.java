package sun.awt.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import sun.java2d.DestSurfaceProvider;
import sun.java2d.SunGraphics2D;
import sun.java2d.Surface;
import sun.java2d.SurfaceManagerFactory;

public class SunVolatileImage extends VolatileImage implements DestSurfaceProvider {
  protected VolatileSurfaceManager volSurfaceManager;
  
  protected Component comp;
  
  private GraphicsConfiguration graphicsConfig;
  
  private Font defaultFont;
  
  private int width;
  
  private int height;
  
  private int forcedAccelSurfaceType;
  
  protected SunVolatileImage(Component paramComponent, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object paramObject, int paramInt3, ImageCapabilities paramImageCapabilities, int paramInt4) {
    this.comp = paramComponent;
    this.graphicsConfig = paramGraphicsConfiguration;
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new IllegalArgumentException("Width (" + paramInt1 + ") and height (" + paramInt2 + ") cannot be <= 0"); 
    this.width = paramInt1;
    this.height = paramInt2;
    this.forcedAccelSurfaceType = paramInt4;
    if (paramInt3 != 1 && paramInt3 != 2 && paramInt3 != 3)
      throw new IllegalArgumentException("Unknown transparency type:" + paramInt3); 
    this.transparency = paramInt3;
    this.volSurfaceManager = createSurfaceManager(paramObject, paramImageCapabilities);
    SurfaceManager.setManager(this, this.volSurfaceManager);
    this.volSurfaceManager.initialize();
    this.volSurfaceManager.initContents();
  }
  
  private SunVolatileImage(Component paramComponent, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object paramObject, ImageCapabilities paramImageCapabilities) { this(paramComponent, paramGraphicsConfiguration, paramInt1, paramInt2, paramObject, 1, paramImageCapabilities, 0); }
  
  public SunVolatileImage(Component paramComponent, int paramInt1, int paramInt2) { this(paramComponent, paramInt1, paramInt2, null); }
  
  public SunVolatileImage(Component paramComponent, int paramInt1, int paramInt2, Object paramObject) { this(paramComponent, paramComponent.getGraphicsConfiguration(), paramInt1, paramInt2, paramObject, null); }
  
  public SunVolatileImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, int paramInt3, ImageCapabilities paramImageCapabilities) { this(null, paramGraphicsConfiguration, paramInt1, paramInt2, null, paramInt3, paramImageCapabilities, 0); }
  
  public int getWidth() { return this.width; }
  
  public int getHeight() { return this.height; }
  
  public GraphicsConfiguration getGraphicsConfig() { return this.graphicsConfig; }
  
  public void updateGraphicsConfig() {
    if (this.comp != null) {
      GraphicsConfiguration graphicsConfiguration = this.comp.getGraphicsConfiguration();
      if (graphicsConfiguration != null)
        this.graphicsConfig = graphicsConfiguration; 
    } 
  }
  
  public Component getComponent() { return this.comp; }
  
  public int getForcedAccelSurfaceType() { return this.forcedAccelSurfaceType; }
  
  protected VolatileSurfaceManager createSurfaceManager(Object paramObject, ImageCapabilities paramImageCapabilities) {
    if (this.graphicsConfig instanceof BufferedImageGraphicsConfig || this.graphicsConfig instanceof sun.print.PrinterGraphicsConfig || (paramImageCapabilities != null && !paramImageCapabilities.isAccelerated()))
      return new BufImgVolatileSurfaceManager(this, paramObject); 
    SurfaceManagerFactory surfaceManagerFactory = SurfaceManagerFactory.getInstance();
    return surfaceManagerFactory.createVolatileManager(this, paramObject);
  }
  
  private Color getForeground() { return (this.comp != null) ? this.comp.getForeground() : Color.black; }
  
  private Color getBackground() { return (this.comp != null) ? this.comp.getBackground() : Color.white; }
  
  private Font getFont() {
    if (this.comp != null)
      return this.comp.getFont(); 
    if (this.defaultFont == null)
      this.defaultFont = new Font("Dialog", 0, 12); 
    return this.defaultFont;
  }
  
  public Graphics2D createGraphics() { return new SunGraphics2D(this.volSurfaceManager.getPrimarySurfaceData(), getForeground(), getBackground(), getFont()); }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver) {
    if (paramString == null)
      throw new NullPointerException("null property name is not allowed"); 
    return Image.UndefinedProperty;
  }
  
  public int getWidth(ImageObserver paramImageObserver) { return getWidth(); }
  
  public int getHeight(ImageObserver paramImageObserver) { return getHeight(); }
  
  public BufferedImage getBackupImage() { return this.graphicsConfig.createCompatibleImage(getWidth(), getHeight(), getTransparency()); }
  
  public BufferedImage getSnapshot() {
    BufferedImage bufferedImage = getBackupImage();
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setComposite(AlphaComposite.Src);
    graphics2D.drawImage(this, 0, 0, null);
    graphics2D.dispose();
    return bufferedImage;
  }
  
  public int validate(GraphicsConfiguration paramGraphicsConfiguration) { return this.volSurfaceManager.validate(paramGraphicsConfiguration); }
  
  public boolean contentsLost() { return this.volSurfaceManager.contentsLost(); }
  
  public ImageCapabilities getCapabilities() { return this.volSurfaceManager.getCapabilities(this.graphicsConfig); }
  
  public Surface getDestSurface() { return this.volSurfaceManager.getPrimarySurfaceData(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\SunVolatileImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */