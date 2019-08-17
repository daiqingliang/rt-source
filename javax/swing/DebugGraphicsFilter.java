package javax.swing;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

class DebugGraphicsFilter extends RGBImageFilter {
  Color color;
  
  DebugGraphicsFilter(Color paramColor) { this.color = paramColor; }
  
  public int filterRGB(int paramInt1, int paramInt2, int paramInt3) { return this.color.getRGB() | paramInt3 & 0xFF000000; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DebugGraphicsFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */