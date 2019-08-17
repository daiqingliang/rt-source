package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;

class FreetypeFontScaler extends FontScaler {
  private static final int TRUETYPE_FONT = 1;
  
  private static final int TYPE1_FONT = 2;
  
  private static native void initIDs(Class paramClass);
  
  private void invalidateScaler() throws FontScalerException {
    this.nativeScaler = 0L;
    this.font = null;
    throw new FontScalerException();
  }
  
  public FreetypeFontScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2) {
    byte b = 1;
    if (paramFont2D instanceof Type1Font)
      b = 2; 
    this.nativeScaler = initNativeScaler(paramFont2D, b, paramInt1, paramBoolean, paramInt2);
    this.font = new WeakReference(paramFont2D);
  }
  
  StrikeMetrics getFontMetrics(long paramLong) throws FontScalerException { return (this.nativeScaler != 0L) ? getFontMetricsNative((Font2D)this.font.get(), paramLong, this.nativeScaler) : FontScaler.getNullScaler().getFontMetrics(0L); }
  
  float getGlyphAdvance(long paramLong, int paramInt) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphAdvanceNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt) : FontScaler.getNullScaler().getGlyphAdvance(0L, paramInt); }
  
  void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) throws FontScalerException {
    if (this.nativeScaler != 0L) {
      getGlyphMetricsNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat);
      return;
    } 
    FontScaler.getNullScaler().getGlyphMetrics(0L, paramInt, paramFloat);
  }
  
  long getGlyphImage(long paramLong, int paramInt) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphImageNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt) : FontScaler.getNullScaler().getGlyphImage(0L, paramInt); }
  
  Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphOutlineBoundsNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt) : FontScaler.getNullScaler().getGlyphOutlineBounds(0L, paramInt); }
  
  GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphOutlineNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat1, paramFloat2) : FontScaler.getNullScaler().getGlyphOutline(0L, paramInt, paramFloat1, paramFloat2); }
  
  GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphVectorOutlineNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramArrayOfInt, paramInt, paramFloat1, paramFloat2) : FontScaler.getNullScaler().getGlyphVectorOutline(0L, paramArrayOfInt, paramInt, paramFloat1, paramFloat2); }
  
  long getLayoutTableCache() throws FontScalerException { return getLayoutTableCacheNative(this.nativeScaler); }
  
  public void dispose() throws FontScalerException {
    if (this.nativeScaler != 0L) {
      disposeNativeScaler((Font2D)this.font.get(), this.nativeScaler);
      this.nativeScaler = 0L;
    } 
  }
  
  int getNumGlyphs() throws FontScalerException { return (this.nativeScaler != 0L) ? getNumGlyphsNative(this.nativeScaler) : FontScaler.getNullScaler().getNumGlyphs(); }
  
  int getMissingGlyphCode() throws FontScalerException { return (this.nativeScaler != 0L) ? getMissingGlyphCodeNative(this.nativeScaler) : FontScaler.getNullScaler().getMissingGlyphCode(); }
  
  int getGlyphCode(char paramChar) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphCodeNative((Font2D)this.font.get(), this.nativeScaler, paramChar) : FontScaler.getNullScaler().getGlyphCode(paramChar); }
  
  Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphPointNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt1, paramInt2) : FontScaler.getNullScaler().getGlyphPoint(paramLong, paramInt1, paramInt2); }
  
  long getUnitsPerEm() throws FontScalerException { return getUnitsPerEMNative(this.nativeScaler); }
  
  long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean) { return (this.nativeScaler != 0L) ? createScalerContextNative(this.nativeScaler, paramArrayOfDouble, paramInt1, paramInt2, paramFloat1, paramFloat2) : NullFontScaler.getNullScalerContext(); }
  
  private native long initNativeScaler(Font2D paramFont2D, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3);
  
  private native StrikeMetrics getFontMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2);
  
  private native float getGlyphAdvanceNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native void getGlyphMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, Point2D.Float paramFloat);
  
  private native long getGlyphImageNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native GeneralPath getGlyphOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, float paramFloat1, float paramFloat2);
  
  private native GeneralPath getGlyphVectorOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2);
  
  native Point2D.Float getGlyphPointNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  private native long getLayoutTableCacheNative(long paramLong);
  
  private native void disposeNativeScaler(Font2D paramFont2D, long paramLong);
  
  private native int getGlyphCodeNative(Font2D paramFont2D, long paramLong, char paramChar);
  
  private native int getNumGlyphsNative(long paramLong);
  
  private native int getMissingGlyphCodeNative(long paramLong);
  
  private native long getUnitsPerEMNative(long paramLong);
  
  native long createScalerContextNative(long paramLong, double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2);
  
  void invalidateScalerContext(long paramLong) {}
  
  static  {
    FontManagerNativeLibrary.load();
    initIDs(FreetypeFontScaler.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FreetypeFontScaler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */