package java.awt;

import java.awt.geom.Dimension2D;
import java.beans.Transient;
import java.io.Serializable;

public class Dimension extends Dimension2D implements Serializable {
  public int width;
  
  public int height;
  
  private static final long serialVersionUID = 4723952579491349524L;
  
  private static native void initIDs();
  
  public Dimension() { this(0, 0); }
  
  public Dimension(Dimension paramDimension) { this(paramDimension.width, paramDimension.height); }
  
  public Dimension(int paramInt1, int paramInt2) {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public double getWidth() { return this.width; }
  
  public double getHeight() { return this.height; }
  
  public void setSize(double paramDouble1, double paramDouble2) {
    this.width = (int)Math.ceil(paramDouble1);
    this.height = (int)Math.ceil(paramDouble2);
  }
  
  @Transient
  public Dimension getSize() { return new Dimension(this.width, this.height); }
  
  public void setSize(Dimension paramDimension) { setSize(paramDimension.width, paramDimension.height); }
  
  public void setSize(int paramInt1, int paramInt2) {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Dimension) {
      Dimension dimension = (Dimension)paramObject;
      return (this.width == dimension.width && this.height == dimension.height);
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.width + this.height;
    return i * (i + 1) / 2 + this.width;
  }
  
  public String toString() { return getClass().getName() + "[width=" + this.width + ",height=" + this.height + "]"; }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Dimension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */