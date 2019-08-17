package sun.java2d.pipe;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import sun.java2d.SunGraphics2D;

public class OutlineTextRenderer implements TextPipe {
  public static final int THRESHHOLD = 100;
  
  public void drawChars(SunGraphics2D paramSunGraphics2D, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    String str = new String(paramArrayOfChar, paramInt1, paramInt2);
    drawString(paramSunGraphics2D, str, paramInt3, paramInt4);
  }
  
  public void drawString(SunGraphics2D paramSunGraphics2D, String paramString, double paramDouble1, double paramDouble2) {
    if ("".equals(paramString))
      return; 
    TextLayout textLayout = new TextLayout(paramString, paramSunGraphics2D.getFont(), paramSunGraphics2D.getFontRenderContext());
    Shape shape = textLayout.getOutline(AffineTransform.getTranslateInstance(paramDouble1, paramDouble2));
    int i = (paramSunGraphics2D.getFontInfo()).aaHint;
    int j = -1;
    if (i != 1 && paramSunGraphics2D.antialiasHint != 2) {
      j = paramSunGraphics2D.antialiasHint;
      paramSunGraphics2D.antialiasHint = 2;
      paramSunGraphics2D.validatePipe();
    } else if (i == 1 && paramSunGraphics2D.antialiasHint != 1) {
      j = paramSunGraphics2D.antialiasHint;
      paramSunGraphics2D.antialiasHint = 1;
      paramSunGraphics2D.validatePipe();
    } 
    paramSunGraphics2D.fill(shape);
    if (j != -1) {
      paramSunGraphics2D.antialiasHint = j;
      paramSunGraphics2D.validatePipe();
    } 
  }
  
  public void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    Shape shape = paramGlyphVector.getOutline(paramFloat1, paramFloat2);
    int i = -1;
    FontRenderContext fontRenderContext = paramGlyphVector.getFontRenderContext();
    boolean bool = fontRenderContext.isAntiAliased();
    if (bool && (paramSunGraphics2D.getGVFontInfo(paramGlyphVector.getFont(), fontRenderContext)).aaHint == 1)
      bool = false; 
    if (bool && paramSunGraphics2D.antialiasHint != 2) {
      i = paramSunGraphics2D.antialiasHint;
      paramSunGraphics2D.antialiasHint = 2;
      paramSunGraphics2D.validatePipe();
    } else if (!bool && paramSunGraphics2D.antialiasHint != 1) {
      i = paramSunGraphics2D.antialiasHint;
      paramSunGraphics2D.antialiasHint = 1;
      paramSunGraphics2D.validatePipe();
    } 
    paramSunGraphics2D.fill(shape);
    if (i != -1) {
      paramSunGraphics2D.antialiasHint = i;
      paramSunGraphics2D.validatePipe();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\OutlineTextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */