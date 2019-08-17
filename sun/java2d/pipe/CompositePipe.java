package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public interface CompositePipe {
  Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt);
  
  boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  void skipTile(Object paramObject, int paramInt1, int paramInt2);
  
  void endSequence(Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\CompositePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */