package sun.awt.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class OffScreenImage extends BufferedImage {
  protected Component c;
  
  private OffScreenImageSource osis;
  
  private Font defaultFont;
  
  public OffScreenImage(Component paramComponent, ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean) {
    super(paramColorModel, paramWritableRaster, paramBoolean, null);
    this.c = paramComponent;
    initSurface(paramWritableRaster.getWidth(), paramWritableRaster.getHeight());
  }
  
  public Graphics getGraphics() { return createGraphics(); }
  
  public Graphics2D createGraphics() {
    if (this.c == null) {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return graphicsEnvironment.createGraphics(this);
    } 
    Color color1 = this.c.getBackground();
    if (color1 == null)
      color1 = SystemColor.window; 
    Color color2 = this.c.getForeground();
    if (color2 == null)
      color2 = SystemColor.windowText; 
    Font font = this.c.getFont();
    if (font == null) {
      if (this.defaultFont == null)
        this.defaultFont = new Font("Dialog", 0, 12); 
      font = this.defaultFont;
    } 
    return new SunGraphics2D(SurfaceData.getPrimarySurfaceData(this), color2, color1, font);
  }
  
  private void initSurface(int paramInt1, int paramInt2) {
    graphics2D = createGraphics();
    try {
      graphics2D.clearRect(0, 0, paramInt1, paramInt2);
    } finally {
      graphics2D.dispose();
    } 
  }
  
  public ImageProducer getSource() {
    if (this.osis == null)
      this.osis = new OffScreenImageSource(this); 
    return this.osis;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\OffScreenImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */