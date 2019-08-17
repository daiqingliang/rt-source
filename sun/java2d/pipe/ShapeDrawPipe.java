package sun.java2d.pipe;

import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public interface ShapeDrawPipe {
  void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape);
  
  void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\ShapeDrawPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */