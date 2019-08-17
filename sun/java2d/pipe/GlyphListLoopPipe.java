package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class GlyphListLoopPipe extends GlyphListPipe implements LoopBasedPipe {
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList, int paramInt) {
    switch (paramInt) {
      case 1:
        paramSunGraphics2D.loops.drawGlyphListLoop.DrawGlyphList(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
        return;
      case 2:
        paramSunGraphics2D.loops.drawGlyphListAALoop.DrawGlyphListAA(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
        return;
      case 4:
      case 6:
        paramSunGraphics2D.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
        return;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\GlyphListLoopPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */