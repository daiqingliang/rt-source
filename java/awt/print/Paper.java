package java.awt.print;

import java.awt.geom.Rectangle2D;

public class Paper implements Cloneable {
  private static final int INCH = 72;
  
  private static final double LETTER_WIDTH = 612.0D;
  
  private static final double LETTER_HEIGHT = 792.0D;
  
  private double mHeight = 792.0D;
  
  private double mWidth = 612.0D;
  
  private Rectangle2D mImageableArea = new Rectangle2D.Double(72.0D, 72.0D, this.mWidth - 144.0D, this.mHeight - 144.0D);
  
  public Object clone() {
    Object object;
    try {
      object = (Paper)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      cloneNotSupportedException.printStackTrace();
      object = null;
    } 
    return object;
  }
  
  public double getHeight() { return this.mHeight; }
  
  public void setSize(double paramDouble1, double paramDouble2) {
    this.mWidth = paramDouble1;
    this.mHeight = paramDouble2;
  }
  
  public double getWidth() { return this.mWidth; }
  
  public void setImageableArea(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { this.mImageableArea = new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4); }
  
  public double getImageableX() { return this.mImageableArea.getX(); }
  
  public double getImageableY() { return this.mImageableArea.getY(); }
  
  public double getImageableWidth() { return this.mImageableArea.getWidth(); }
  
  public double getImageableHeight() { return this.mImageableArea.getHeight(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\print\Paper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */