package sun.font;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public interface TextLineComponent {
  public static final int LEFT_TO_RIGHT = 0;
  
  public static final int RIGHT_TO_LEFT = 1;
  
  public static final int UNCHANGED = 2;
  
  CoreMetrics getCoreMetrics();
  
  void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2);
  
  Rectangle2D getCharVisualBounds(int paramInt);
  
  Rectangle2D getVisualBounds();
  
  float getAdvance();
  
  Shape getOutline(float paramFloat1, float paramFloat2);
  
  int getNumCharacters();
  
  float getCharX(int paramInt);
  
  float getCharY(int paramInt);
  
  float getCharAdvance(int paramInt);
  
  boolean caretAtOffsetIsValid(int paramInt);
  
  int getLineBreakIndex(int paramInt, float paramFloat);
  
  float getAdvanceBetween(int paramInt1, int paramInt2);
  
  Rectangle2D getLogicalBounds();
  
  Rectangle2D getItalicBounds();
  
  AffineTransform getBaselineTransform();
  
  boolean isSimple();
  
  Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2);
  
  TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3);
  
  int getNumJustificationInfos();
  
  void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3);
  
  TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\TextLineComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */