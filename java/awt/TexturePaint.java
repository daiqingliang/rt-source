package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class TexturePaint implements Paint {
  BufferedImage bufImg;
  
  double tx;
  
  double ty;
  
  double sx;
  
  double sy;
  
  public TexturePaint(BufferedImage paramBufferedImage, Rectangle2D paramRectangle2D) {
    this.bufImg = paramBufferedImage;
    this.tx = paramRectangle2D.getX();
    this.ty = paramRectangle2D.getY();
    this.sx = paramRectangle2D.getWidth() / this.bufImg.getWidth();
    this.sy = paramRectangle2D.getHeight() / this.bufImg.getHeight();
  }
  
  public BufferedImage getImage() { return this.bufImg; }
  
  public Rectangle2D getAnchorRect() { return new Rectangle2D.Double(this.tx, this.ty, this.sx * this.bufImg.getWidth(), this.sy * this.bufImg.getHeight()); }
  
  public PaintContext createContext(ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints) {
    if (paramAffineTransform == null) {
      paramAffineTransform = new AffineTransform();
    } else {
      paramAffineTransform = (AffineTransform)paramAffineTransform.clone();
    } 
    paramAffineTransform.translate(this.tx, this.ty);
    paramAffineTransform.scale(this.sx, this.sy);
    return TexturePaintContext.getContext(this.bufImg, paramAffineTransform, paramRenderingHints, paramRectangle);
  }
  
  public int getTransparency() { return this.bufImg.getColorModel().getTransparency(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\TexturePaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */