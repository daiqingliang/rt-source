package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class AATextRenderer extends GlyphListLoopPipe implements LoopBasedPipe {
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) { paramSunGraphics2D.loops.drawGlyphListAALoop.DrawGlyphListAA(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\AATextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */