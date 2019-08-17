package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class CompositeStrike extends FontStrike {
  static final int SLOTMASK = 16777215;
  
  private CompositeFont compFont;
  
  private PhysicalStrike[] strikes;
  
  int numGlyphs = 0;
  
  CompositeStrike(CompositeFont paramCompositeFont, FontStrikeDesc paramFontStrikeDesc) {
    this.compFont = paramCompositeFont;
    this.desc = paramFontStrikeDesc;
    this.disposer = new FontStrikeDisposer(this.compFont, paramFontStrikeDesc);
    if (paramFontStrikeDesc.style != this.compFont.style) {
      this.algoStyle = true;
      if ((paramFontStrikeDesc.style & true) == 1 && (this.compFont.style & true) == 0)
        this.boldness = 1.33F; 
      if ((paramFontStrikeDesc.style & 0x2) == 2 && (this.compFont.style & 0x2) == 0)
        this.italic = 0.7F; 
    } 
    this.strikes = new PhysicalStrike[this.compFont.numSlots];
  }
  
  PhysicalStrike getStrikeForGlyph(int paramInt) { return getStrikeForSlot(paramInt >>> 24); }
  
  PhysicalStrike getStrikeForSlot(int paramInt) {
    if (paramInt >= this.strikes.length)
      paramInt = 0; 
    PhysicalStrike physicalStrike = this.strikes[paramInt];
    if (physicalStrike == null) {
      physicalStrike = (PhysicalStrike)this.compFont.getSlotFont(paramInt).getStrike(this.desc);
      this.strikes[paramInt] = physicalStrike;
    } 
    return physicalStrike;
  }
  
  public int getNumGlyphs() { return this.compFont.getNumGlyphs(); }
  
  StrikeMetrics getFontMetrics() {
    if (this.strikeMetrics == null) {
      StrikeMetrics strikeMetrics = new StrikeMetrics();
      for (byte b = 0; b < this.compFont.numMetricsSlots; b++)
        strikeMetrics.merge(getStrikeForSlot(b).getFontMetrics()); 
      this.strikeMetrics = strikeMetrics;
    } 
    return this.strikeMetrics;
  }
  
  void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt) {
    PhysicalStrike physicalStrike = getStrikeForSlot(0);
    int i = physicalStrike.getSlot0GlyphImagePtrs(paramArrayOfInt, paramArrayOfLong, paramInt);
    if (i == paramInt)
      return; 
    for (int j = i; j < paramInt; j++) {
      physicalStrike = getStrikeForGlyph(paramArrayOfInt[j]);
      paramArrayOfLong[j] = physicalStrike.getGlyphImagePtr(paramArrayOfInt[j] & 0xFFFFFF);
    } 
  }
  
  long getGlyphImagePtr(int paramInt) {
    PhysicalStrike physicalStrike = getStrikeForGlyph(paramInt);
    return physicalStrike.getGlyphImagePtr(paramInt & 0xFFFFFF);
  }
  
  void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle) {
    PhysicalStrike physicalStrike = getStrikeForGlyph(paramInt);
    physicalStrike.getGlyphImageBounds(paramInt & 0xFFFFFF, paramFloat, paramRectangle);
  }
  
  Point2D.Float getGlyphMetrics(int paramInt) {
    PhysicalStrike physicalStrike = getStrikeForGlyph(paramInt);
    return physicalStrike.getGlyphMetrics(paramInt & 0xFFFFFF);
  }
  
  Point2D.Float getCharMetrics(char paramChar) { return getGlyphMetrics(this.compFont.getMapper().charToGlyph(paramChar)); }
  
  float getGlyphAdvance(int paramInt) {
    PhysicalStrike physicalStrike = getStrikeForGlyph(paramInt);
    return physicalStrike.getGlyphAdvance(paramInt & 0xFFFFFF);
  }
  
  float getCodePointAdvance(int paramInt) { return getGlyphAdvance(this.compFont.getMapper().charToGlyph(paramInt)); }
  
  Rectangle2D.Float getGlyphOutlineBounds(int paramInt) {
    PhysicalStrike physicalStrike = getStrikeForGlyph(paramInt);
    return physicalStrike.getGlyphOutlineBounds(paramInt & 0xFFFFFF);
  }
  
  GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) {
    PhysicalStrike physicalStrike = getStrikeForGlyph(paramInt);
    GeneralPath generalPath = physicalStrike.getGlyphOutline(paramInt & 0xFFFFFF, paramFloat1, paramFloat2);
    return (generalPath == null) ? new GeneralPath() : generalPath;
  }
  
  GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2) {
    GeneralPath generalPath = null;
    byte b = 0;
    while (b < paramArrayOfInt.length) {
      byte b1 = b;
      int i = paramArrayOfInt[b] >>> 24;
      while (b < paramArrayOfInt.length && paramArrayOfInt[b + true] >>> 24 == i)
        b++; 
      byte b2 = b - b1 + 1;
      int[] arrayOfInt = new int[b2];
      for (byte b3 = 0; b3 < b2; b3++)
        arrayOfInt[b3] = paramArrayOfInt[b3] & 0xFFFFFF; 
      GeneralPath generalPath1 = getStrikeForSlot(i).getGlyphVectorOutline(arrayOfInt, paramFloat1, paramFloat2);
      if (generalPath == null) {
        generalPath = generalPath1;
        continue;
      } 
      if (generalPath1 != null)
        generalPath.append(generalPath1, false); 
    } 
    return (generalPath == null) ? new GeneralPath() : generalPath;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CompositeStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */