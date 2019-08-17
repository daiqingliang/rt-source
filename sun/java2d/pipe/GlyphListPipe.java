package sun.java2d.pipe;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.FontInfo;

public abstract class GlyphListPipe implements TextPipe {
  public void drawString(SunGraphics2D paramSunGraphics2D, String paramString, double paramDouble1, double paramDouble2) {
    float f2;
    float f1;
    FontInfo fontInfo = paramSunGraphics2D.getFontInfo();
    if (fontInfo.pixelHeight > 100) {
      SurfaceData.outlineTextRenderer.drawString(paramSunGraphics2D, paramString, paramDouble1, paramDouble2);
      return;
    } 
    if (paramSunGraphics2D.transformState >= 3) {
      double[] arrayOfDouble = { paramDouble1 + fontInfo.originX, paramDouble2 + fontInfo.originY };
      paramSunGraphics2D.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 1);
      f1 = (float)arrayOfDouble[0];
      f2 = (float)arrayOfDouble[1];
    } else {
      f1 = (float)(paramDouble1 + fontInfo.originX + paramSunGraphics2D.transX);
      f2 = (float)(paramDouble2 + fontInfo.originY + paramSunGraphics2D.transY);
    } 
    GlyphList glyphList = GlyphList.getInstance();
    if (glyphList.setFromString(fontInfo, paramString, f1, f2)) {
      drawGlyphList(paramSunGraphics2D, glyphList);
      glyphList.dispose();
    } else {
      glyphList.dispose();
      TextLayout textLayout = new TextLayout(paramString, paramSunGraphics2D.getFont(), paramSunGraphics2D.getFontRenderContext());
      textLayout.draw(paramSunGraphics2D, (float)paramDouble1, (float)paramDouble2);
    } 
  }
  
  public void drawChars(SunGraphics2D paramSunGraphics2D, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    float f2;
    float f1;
    FontInfo fontInfo = paramSunGraphics2D.getFontInfo();
    if (fontInfo.pixelHeight > 100) {
      SurfaceData.outlineTextRenderer.drawChars(paramSunGraphics2D, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    } 
    if (paramSunGraphics2D.transformState >= 3) {
      double[] arrayOfDouble = { (paramInt3 + fontInfo.originX), (paramInt4 + fontInfo.originY) };
      paramSunGraphics2D.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 1);
      f1 = (float)arrayOfDouble[0];
      f2 = (float)arrayOfDouble[1];
    } else {
      f1 = paramInt3 + fontInfo.originX + paramSunGraphics2D.transX;
      f2 = paramInt4 + fontInfo.originY + paramSunGraphics2D.transY;
    } 
    GlyphList glyphList = GlyphList.getInstance();
    if (glyphList.setFromChars(fontInfo, paramArrayOfChar, paramInt1, paramInt2, f1, f2)) {
      drawGlyphList(paramSunGraphics2D, glyphList);
      glyphList.dispose();
    } else {
      glyphList.dispose();
      TextLayout textLayout = new TextLayout(new String(paramArrayOfChar, paramInt1, paramInt2), paramSunGraphics2D.getFont(), paramSunGraphics2D.getFontRenderContext());
      textLayout.draw(paramSunGraphics2D, paramInt3, paramInt4);
    } 
  }
  
  public void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    FontRenderContext fontRenderContext = paramGlyphVector.getFontRenderContext();
    FontInfo fontInfo = paramSunGraphics2D.getGVFontInfo(paramGlyphVector.getFont(), fontRenderContext);
    if (fontInfo.pixelHeight > 100) {
      SurfaceData.outlineTextRenderer.drawGlyphVector(paramSunGraphics2D, paramGlyphVector, paramFloat1, paramFloat2);
      return;
    } 
    if (paramSunGraphics2D.transformState >= 3) {
      double[] arrayOfDouble = { paramFloat1, paramFloat2 };
      paramSunGraphics2D.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 1);
      paramFloat1 = (float)arrayOfDouble[0];
      paramFloat2 = (float)arrayOfDouble[1];
    } else {
      paramFloat1 += paramSunGraphics2D.transX;
      paramFloat2 += paramSunGraphics2D.transY;
    } 
    GlyphList glyphList = GlyphList.getInstance();
    glyphList.setFromGlyphVector(fontInfo, paramGlyphVector, paramFloat1, paramFloat2);
    drawGlyphList(paramSunGraphics2D, glyphList, fontInfo.aaHint);
    glyphList.dispose();
  }
  
  protected abstract void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList);
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList, int paramInt) { drawGlyphList(paramSunGraphics2D, paramGlyphList); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\GlyphListPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */