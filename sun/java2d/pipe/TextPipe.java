package sun.java2d.pipe;

import java.awt.font.GlyphVector;
import sun.java2d.SunGraphics2D;

public interface TextPipe {
  void drawString(SunGraphics2D paramSunGraphics2D, String paramString, double paramDouble1, double paramDouble2);
  
  void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2);
  
  void drawChars(SunGraphics2D paramSunGraphics2D, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\TextPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */