package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.InnocuousThread;

class T2KFontScaler extends FontScaler {
  private int[] bwGlyphs;
  
  private static final int TRUETYPE_FONT = 1;
  
  private static final int TYPE1_FONT = 2;
  
  private long layoutTablePtr = 0L;
  
  private void initBWGlyphs() {
    if (this.font.get() != null && "Courier New".equals(((Font2D)this.font.get()).getFontName(null))) {
      this.bwGlyphs = new int[2];
      CharToGlyphMapper charToGlyphMapper = ((Font2D)this.font.get()).getMapper();
      this.bwGlyphs[0] = charToGlyphMapper.charToGlyph('W');
      this.bwGlyphs[1] = charToGlyphMapper.charToGlyph('w');
    } 
  }
  
  private static native void initIDs(Class paramClass);
  
  private void invalidateScaler() {
    this.nativeScaler = 0L;
    this.font = null;
    throw new FontScalerException();
  }
  
  public T2KFontScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2) {
    byte b = 1;
    if (paramFont2D instanceof Type1Font)
      b = 2; 
    this.font = new WeakReference(paramFont2D);
    initBWGlyphs();
    this.nativeScaler = initNativeScaler(paramFont2D, b, paramInt1, paramBoolean, paramInt2, this.bwGlyphs);
  }
  
  StrikeMetrics getFontMetrics(long paramLong) throws FontScalerException { return (this.nativeScaler != 0L) ? getFontMetricsNative((Font2D)this.font.get(), paramLong, this.nativeScaler) : getNullScaler().getFontMetrics(0L); }
  
  float getGlyphAdvance(long paramLong, int paramInt) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphAdvanceNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt) : getNullScaler().getGlyphAdvance(0L, paramInt); }
  
  void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) throws FontScalerException {
    if (this.nativeScaler != 0L) {
      getGlyphMetricsNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat);
    } else {
      getNullScaler().getGlyphMetrics(0L, paramInt, paramFloat);
    } 
  }
  
  long getGlyphImage(long paramLong, int paramInt) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphImageNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt) : getNullScaler().getGlyphImage(0L, paramInt); }
  
  Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphOutlineBoundsNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt) : getNullScaler().getGlyphOutlineBounds(0L, paramInt); }
  
  GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphOutlineNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat1, paramFloat2) : getNullScaler().getGlyphOutline(0L, paramInt, paramFloat1, paramFloat2); }
  
  GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphVectorOutlineNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramArrayOfInt, paramInt, paramFloat1, paramFloat2) : getNullScaler().getGlyphVectorOutline(0L, paramArrayOfInt, paramInt, paramFloat1, paramFloat2); }
  
  int getNumGlyphs() throws FontScalerException { return (this.nativeScaler != 0L) ? getNumGlyphsNative(this.nativeScaler) : getNullScaler().getNumGlyphs(); }
  
  int getMissingGlyphCode() throws FontScalerException { return (this.nativeScaler != 0L) ? getMissingGlyphCodeNative(this.nativeScaler) : getNullScaler().getMissingGlyphCode(); }
  
  int getGlyphCode(char paramChar) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphCodeNative(this.nativeScaler, paramChar) : getNullScaler().getGlyphCode(paramChar); }
  
  long getLayoutTableCache() throws FontScalerException {
    if (this.nativeScaler == 0L)
      return getNullScaler().getLayoutTableCache(); 
    if (this.layoutTablePtr == 0L)
      this.layoutTablePtr = getLayoutTableCacheNative(this.nativeScaler); 
    return this.layoutTablePtr;
  }
  
  private void disposeScaler() {
    disposeNativeScaler(this.nativeScaler, this.layoutTablePtr);
    this.nativeScaler = 0L;
    this.layoutTablePtr = 0L;
  }
  
  public void dispose() {
    if (this.nativeScaler != 0L || this.layoutTablePtr != 0L) {
      final T2KFontScaler scaler = this;
      Runnable runnable = new Runnable() {
          public void run() { scaler.disposeScaler(); }
        };
      (new InnocuousThread(runnable)).start();
    } 
  }
  
  Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) throws FontScalerException { return (this.nativeScaler != 0L) ? getGlyphPointNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt1, paramInt2) : getNullScaler().getGlyphPoint(paramLong, paramInt1, paramInt2); }
  
  long getUnitsPerEm() throws FontScalerException { return (this.nativeScaler != 0L) ? getUnitsPerEMNative(this.nativeScaler) : getNullScaler().getUnitsPerEm(); }
  
  long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean) { return (this.nativeScaler != 0L) ? createScalerContextNative(this.nativeScaler, paramArrayOfDouble, paramInt1, paramInt2, paramFloat1, paramFloat2, paramBoolean) : NullFontScaler.getNullScalerContext(); }
  
  private native long initNativeScaler(Font2D paramFont2D, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int[] paramArrayOfInt);
  
  private native StrikeMetrics getFontMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2);
  
  private native float getGlyphAdvanceNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native void getGlyphMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, Point2D.Float paramFloat);
  
  private native long getGlyphImageNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native GeneralPath getGlyphOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, float paramFloat1, float paramFloat2);
  
  private native GeneralPath getGlyphVectorOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2);
  
  private native int getGlyphCodeNative(long paramLong, char paramChar);
  
  private native long getLayoutTableCacheNative(long paramLong);
  
  private native void disposeNativeScaler(long paramLong1, long paramLong2);
  
  private native int getNumGlyphsNative(long paramLong);
  
  private native int getMissingGlyphCodeNative(long paramLong);
  
  private native long getUnitsPerEMNative(long paramLong);
  
  private native long createScalerContextNative(long paramLong, double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  private native Point2D.Float getGlyphPointNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  void invalidateScalerContext(long paramLong) {}
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            FontManagerNativeLibrary.load();
            System.loadLibrary("t2k");
            return null;
          }
        });
    initIDs(T2KFontScaler.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\T2KFontScaler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */