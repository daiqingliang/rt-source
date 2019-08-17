package java.awt;

public class PointerInfo {
  private final GraphicsDevice device;
  
  private final Point location;
  
  PointerInfo(GraphicsDevice paramGraphicsDevice, Point paramPoint) {
    this.device = paramGraphicsDevice;
    this.location = paramPoint;
  }
  
  public GraphicsDevice getDevice() { return this.device; }
  
  public Point getLocation() { return this.location; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\PointerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */