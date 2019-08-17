package sun.java2d.pipe;

import sun.java2d.SunGraphics2D;

public interface PixelFillPipe {
  void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\PixelFillPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */