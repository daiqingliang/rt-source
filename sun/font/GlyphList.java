package sun.font;

import java.awt.font.GlyphVector;
import sun.java2d.loops.FontInfo;

public final class GlyphList {
  private static final int MINGRAYLENGTH = 1024;
  
  private static final int MAXGRAYLENGTH = 8192;
  
  private static final int DEFAULT_LENGTH = 32;
  
  int glyphindex;
  
  int[] metrics;
  
  byte[] graybits;
  
  Object strikelist;
  
  int len = 0;
  
  int maxLen = 0;
  
  int maxPosLen = 0;
  
  int[] glyphData;
  
  char[] chData;
  
  long[] images;
  
  float[] positions;
  
  float x;
  
  float y;
  
  float gposx;
  
  float gposy;
  
  boolean usePositions;
  
  boolean lcdRGBOrder;
  
  boolean lcdSubPixPos;
  
  private static GlyphList reusableGL = new GlyphList();
  
  private static boolean inUse;
  
  void ensureCapacity(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    if (this.usePositions && paramInt > this.maxPosLen) {
      this.positions = new float[paramInt * 2 + 2];
      this.maxPosLen = paramInt;
    } 
    if (this.maxLen == 0 || paramInt > this.maxLen) {
      this.glyphData = new int[paramInt];
      this.chData = new char[paramInt];
      this.images = new long[paramInt];
      this.maxLen = paramInt;
    } 
  }
  
  public static GlyphList getInstance() {
    if (inUse)
      return new GlyphList(); 
    synchronized (GlyphList.class) {
      if (inUse)
        return new GlyphList(); 
      inUse = true;
      return reusableGL;
    } 
  }
  
  public boolean setFromString(FontInfo paramFontInfo, String paramString, float paramFloat1, float paramFloat2) {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.strikelist = paramFontInfo.fontStrike;
    this.lcdRGBOrder = paramFontInfo.lcdRGBOrder;
    this.lcdSubPixPos = paramFontInfo.lcdSubPixPos;
    this.len = paramString.length();
    ensureCapacity(this.len);
    paramString.getChars(0, this.len, this.chData, 0);
    return mapChars(paramFontInfo, this.len);
  }
  
  public boolean setFromChars(FontInfo paramFontInfo, char[] paramArrayOfChar, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.strikelist = paramFontInfo.fontStrike;
    this.lcdRGBOrder = paramFontInfo.lcdRGBOrder;
    this.lcdSubPixPos = paramFontInfo.lcdSubPixPos;
    this.len = paramInt2;
    if (paramInt2 < 0) {
      this.len = 0;
    } else {
      this.len = paramInt2;
    } 
    ensureCapacity(this.len);
    System.arraycopy(paramArrayOfChar, paramInt1, this.chData, 0, this.len);
    return mapChars(paramFontInfo, this.len);
  }
  
  private final boolean mapChars(FontInfo paramFontInfo, int paramInt) {
    if (paramFontInfo.font2D.getMapper().charsToGlyphsNS(paramInt, this.chData, this.glyphData))
      return false; 
    paramFontInfo.fontStrike.getGlyphImagePtrs(this.glyphData, this.images, paramInt);
    this.glyphindex = -1;
    return true;
  }
  
  public void setFromGlyphVector(FontInfo paramFontInfo, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.lcdRGBOrder = paramFontInfo.lcdRGBOrder;
    this.lcdSubPixPos = paramFontInfo.lcdSubPixPos;
    StandardGlyphVector standardGlyphVector = StandardGlyphVector.getStandardGV(paramGlyphVector, paramFontInfo);
    this.usePositions = standardGlyphVector.needsPositions(paramFontInfo.devTx);
    this.len = standardGlyphVector.getNumGlyphs();
    ensureCapacity(this.len);
    this.strikelist = standardGlyphVector.setupGlyphImages(this.images, this.usePositions ? this.positions : null, paramFontInfo.devTx);
    this.glyphindex = -1;
  }
  
  public int[] getBounds() {
    if (this.glyphindex >= 0)
      throw new InternalError("calling getBounds after setGlyphIndex"); 
    if (this.metrics == null)
      this.metrics = new int[5]; 
    this.gposx = this.x + 0.5F;
    this.gposy = this.y + 0.5F;
    fillBounds(this.metrics);
    return this.metrics;
  }
  
  public void setGlyphIndex(int paramInt) {
    this.glyphindex = paramInt;
    float f1 = StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.topLeftXOffset);
    float f2 = StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.topLeftYOffset);
    if (this.usePositions) {
      this.metrics[0] = (int)Math.floor((this.positions[paramInt << 1] + this.gposx + f1));
      this.metrics[1] = (int)Math.floor((this.positions[(paramInt << 1) + 1] + this.gposy + f2));
    } else {
      this.metrics[0] = (int)Math.floor((this.gposx + f1));
      this.metrics[1] = (int)Math.floor((this.gposy + f2));
      this.gposx += StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.xAdvanceOffset);
      this.gposy += StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.yAdvanceOffset);
    } 
    this.metrics[2] = StrikeCache.unsafe.getChar(this.images[paramInt] + StrikeCache.widthOffset);
    this.metrics[3] = StrikeCache.unsafe.getChar(this.images[paramInt] + StrikeCache.heightOffset);
    this.metrics[4] = StrikeCache.unsafe.getChar(this.images[paramInt] + StrikeCache.rowBytesOffset);
  }
  
  public int[] getMetrics() { return this.metrics; }
  
  public byte[] getGrayBits() {
    int i = this.metrics[4] * this.metrics[3];
    if (this.graybits == null) {
      this.graybits = new byte[Math.max(i, 1024)];
    } else if (i > this.graybits.length) {
      this.graybits = new byte[i];
    } 
    long l = StrikeCache.unsafe.getAddress(this.images[this.glyphindex] + StrikeCache.pixelDataOffset);
    if (l == 0L)
      return this.graybits; 
    for (byte b = 0; b < i; b++)
      this.graybits[b] = StrikeCache.unsafe.getByte(l + b); 
    return this.graybits;
  }
  
  public long[] getImages() { return this.images; }
  
  public boolean usePositions() { return this.usePositions; }
  
  public float[] getPositions() { return this.positions; }
  
  public float getX() { return this.x; }
  
  public float getY() { return this.y; }
  
  public Object getStrike() { return this.strikelist; }
  
  public boolean isSubPixPos() { return this.lcdSubPixPos; }
  
  public boolean isRGBOrder() { return this.lcdRGBOrder; }
  
  public void dispose() {
    if (this == reusableGL) {
      if (this.graybits != null && this.graybits.length > 8192)
        this.graybits = null; 
      this.usePositions = false;
      this.strikelist = null;
      inUse = false;
    } 
  }
  
  public int getNumGlyphs() { return this.len; }
  
  private void fillBounds(int[] paramArrayOfInt) {
    int i = StrikeCache.topLeftXOffset;
    int j = StrikeCache.topLeftYOffset;
    int k = StrikeCache.widthOffset;
    int m = StrikeCache.heightOffset;
    int n = StrikeCache.xAdvanceOffset;
    int i1 = StrikeCache.yAdvanceOffset;
    if (this.len == 0) {
      paramArrayOfInt[3] = 0;
      paramArrayOfInt[2] = 0;
      paramArrayOfInt[1] = 0;
      paramArrayOfInt[0] = 0;
      return;
    } 
    float f2 = Float.POSITIVE_INFINITY;
    float f1 = f2;
    float f4 = Float.NEGATIVE_INFINITY;
    float f3 = f4;
    byte b1 = 0;
    float f5 = this.x + 0.5F;
    float f6 = this.y + 0.5F;
    for (byte b2 = 0; b2 < this.len; b2++) {
      float f10;
      float f9;
      float f7 = StrikeCache.unsafe.getFloat(this.images[b2] + i);
      float f8 = StrikeCache.unsafe.getFloat(this.images[b2] + j);
      char c1 = StrikeCache.unsafe.getChar(this.images[b2] + k);
      char c2 = StrikeCache.unsafe.getChar(this.images[b2] + m);
      if (this.usePositions) {
        f9 = this.positions[b1++] + f7 + f5;
        f10 = this.positions[b1++] + f8 + f6;
      } else {
        f9 = f5 + f7;
        f10 = f6 + f8;
        f5 += StrikeCache.unsafe.getFloat(this.images[b2] + n);
        f6 += StrikeCache.unsafe.getFloat(this.images[b2] + i1);
      } 
      float f11 = f9 + c1;
      float f12 = f10 + c2;
      if (f1 > f9)
        f1 = f9; 
      if (f2 > f10)
        f2 = f10; 
      if (f3 < f11)
        f3 = f11; 
      if (f4 < f12)
        f4 = f12; 
    } 
    paramArrayOfInt[0] = (int)Math.floor(f1);
    paramArrayOfInt[1] = (int)Math.floor(f2);
    paramArrayOfInt[2] = (int)Math.floor(f3);
    paramArrayOfInt[3] = (int)Math.floor(f4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\GlyphList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */