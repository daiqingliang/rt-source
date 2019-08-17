package java.awt;

public class ImageCapabilities implements Cloneable {
  private boolean accelerated = false;
  
  public ImageCapabilities(boolean paramBoolean) { this.accelerated = paramBoolean; }
  
  public boolean isAccelerated() { return this.accelerated; }
  
  public boolean isTrueVolatile() { return false; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ImageCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */