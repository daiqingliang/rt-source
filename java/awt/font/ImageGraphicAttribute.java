package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

public final class ImageGraphicAttribute extends GraphicAttribute {
  private Image fImage;
  
  private float fImageWidth;
  
  private float fImageHeight;
  
  private float fOriginX;
  
  private float fOriginY;
  
  public ImageGraphicAttribute(Image paramImage, int paramInt) { this(paramImage, paramInt, 0.0F, 0.0F); }
  
  public ImageGraphicAttribute(Image paramImage, int paramInt, float paramFloat1, float paramFloat2) {
    super(paramInt);
    this.fImage = paramImage;
    this.fImageWidth = paramImage.getWidth(null);
    this.fImageHeight = paramImage.getHeight(null);
    this.fOriginX = paramFloat1;
    this.fOriginY = paramFloat2;
  }
  
  public float getAscent() { return Math.max(0.0F, this.fOriginY); }
  
  public float getDescent() { return Math.max(0.0F, this.fImageHeight - this.fOriginY); }
  
  public float getAdvance() { return Math.max(0.0F, this.fImageWidth - this.fOriginX); }
  
  public Rectangle2D getBounds() { return new Rectangle2D.Float(-this.fOriginX, -this.fOriginY, this.fImageWidth, this.fImageHeight); }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) { paramGraphics2D.drawImage(this.fImage, (int)(paramFloat1 - this.fOriginX), (int)(paramFloat2 - this.fOriginY), null); }
  
  public int hashCode() { return this.fImage.hashCode(); }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((ImageGraphicAttribute)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public boolean equals(ImageGraphicAttribute paramImageGraphicAttribute) { return (paramImageGraphicAttribute == null) ? false : ((this == paramImageGraphicAttribute) ? true : ((this.fOriginX != paramImageGraphicAttribute.fOriginX || this.fOriginY != paramImageGraphicAttribute.fOriginY) ? false : ((getAlignment() != paramImageGraphicAttribute.getAlignment()) ? false : (!!this.fImage.equals(paramImageGraphicAttribute.fImage))))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\ImageGraphicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */