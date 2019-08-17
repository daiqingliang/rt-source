package java.awt.geom;

public abstract class Dimension2D implements Cloneable {
  public abstract double getWidth();
  
  public abstract double getHeight();
  
  public abstract void setSize(double paramDouble1, double paramDouble2);
  
  public void setSize(Dimension2D paramDimension2D) { setSize(paramDimension2D.getWidth(), paramDimension2D.getHeight()); }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\Dimension2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */