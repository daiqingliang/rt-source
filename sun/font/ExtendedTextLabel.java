package sun.font;

import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.Rectangle2D;

public abstract class ExtendedTextLabel extends TextLabel implements TextLineComponent {
  public abstract int getNumCharacters();
  
  public abstract CoreMetrics getCoreMetrics();
  
  public abstract float getCharX(int paramInt);
  
  public abstract float getCharY(int paramInt);
  
  public abstract float getCharAdvance(int paramInt);
  
  public abstract Rectangle2D getCharVisualBounds(int paramInt, float paramFloat1, float paramFloat2);
  
  public abstract int logicalToVisual(int paramInt);
  
  public abstract int visualToLogical(int paramInt);
  
  public abstract int getLineBreakIndex(int paramInt, float paramFloat);
  
  public abstract float getAdvanceBetween(int paramInt1, int paramInt2);
  
  public abstract boolean caretAtOffsetIsValid(int paramInt);
  
  public Rectangle2D getCharVisualBounds(int paramInt) { return getCharVisualBounds(paramInt, 0.0F, 0.0F); }
  
  public abstract TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract int getNumJustificationInfos();
  
  public abstract void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\ExtendedTextLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */