package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class TextRenderer extends GlyphListPipe {
  CompositePipe outpipe;
  
  public TextRenderer(CompositePipe paramCompositePipe) { this.outpipe = paramCompositePipe; }
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) {
    int i = paramGlyphList.getNumGlyphs();
    Region region = paramSunGraphics2D.getCompClip();
    int j = region.getLoX();
    int k = region.getLoY();
    int m = region.getHiX();
    int n = region.getHiY();
    object = null;
    try {
      int[] arrayOfInt = paramGlyphList.getBounds();
      Rectangle rectangle = new Rectangle(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
      Shape shape = paramSunGraphics2D.untransformShape(rectangle);
      object = this.outpipe.startSequence(paramSunGraphics2D, shape, rectangle, arrayOfInt);
      for (byte b = 0; b < i; b++) {
        paramGlyphList.setGlyphIndex(b);
        int[] arrayOfInt1 = paramGlyphList.getMetrics();
        int i1 = arrayOfInt1[0];
        int i2 = arrayOfInt1[1];
        int i3 = arrayOfInt1[2];
        int i4 = i1 + i3;
        int i5 = i2 + arrayOfInt1[3];
        int i6 = 0;
        if (i1 < j) {
          i6 = j - i1;
          i1 = j;
        } 
        if (i2 < k) {
          i6 += (k - i2) * i3;
          i2 = k;
        } 
        if (i4 > m)
          i4 = m; 
        if (i5 > n)
          i5 = n; 
        if (i4 > i1 && i5 > i2 && this.outpipe.needTile(object, i1, i2, i4 - i1, i5 - i2)) {
          byte[] arrayOfByte = paramGlyphList.getGrayBits();
          this.outpipe.renderPathTile(object, arrayOfByte, i6, i3, i1, i2, i4 - i1, i5 - i2);
        } else {
          this.outpipe.skipTile(object, i1, i2);
        } 
      } 
    } finally {
      if (object != null)
        this.outpipe.endSequence(object); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\TextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */