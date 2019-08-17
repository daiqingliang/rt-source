package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public abstract class FontScaler implements DisposerRecord {
  private static FontScaler nullScaler = null;
  
  private static Constructor<FontScaler> scalerConstructor = null;
  
  protected WeakReference<Font2D> font = null;
  
  protected long nativeScaler = 0L;
  
  protected boolean disposed = false;
  
  public static FontScaler getScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2) {
    FontScaler fontScaler = null;
    try {
      Object[] arrayOfObject = { paramFont2D, Integer.valueOf(paramInt1), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2) };
      fontScaler = (FontScaler)scalerConstructor.newInstance(arrayOfObject);
      Disposer.addObjectRecord(paramFont2D, fontScaler);
    } catch (Throwable throwable) {
      fontScaler = nullScaler;
      FontManager fontManager = FontManagerFactory.getInstance();
      fontManager.deRegisterBadFont(paramFont2D);
    } 
    return fontScaler;
  }
  
  public static FontScaler getNullScaler() {
    if (nullScaler == null)
      nullScaler = new NullFontScaler(); 
    return nullScaler;
  }
  
  abstract StrikeMetrics getFontMetrics(long paramLong) throws FontScalerException;
  
  abstract float getGlyphAdvance(long paramLong, int paramInt) throws FontScalerException;
  
  abstract void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) throws FontScalerException;
  
  abstract long getGlyphImage(long paramLong, int paramInt) throws FontScalerException;
  
  abstract Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) throws FontScalerException;
  
  abstract GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException;
  
  abstract GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException;
  
  public void dispose() {}
  
  abstract int getNumGlyphs() throws FontScalerException;
  
  abstract int getMissingGlyphCode() throws FontScalerException;
  
  abstract int getGlyphCode(char paramChar) throws FontScalerException;
  
  abstract long getLayoutTableCache() throws FontScalerException;
  
  abstract Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) throws FontScalerException;
  
  abstract long getUnitsPerEm() throws FontScalerException;
  
  abstract long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  abstract void invalidateScalerContext(long paramLong);
  
  static  {
    Class clazz = null;
    Class[] arrayOfClass = { Font2D.class, int.class, boolean.class, int.class };
    try {
      if (FontUtilities.isOpenJDK) {
        clazz = Class.forName("sun.font.FreetypeFontScaler");
      } else {
        clazz = Class.forName("sun.font.T2KFontScaler");
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      clazz = NullFontScaler.class;
    } 
    try {
      scalerConstructor = clazz.getConstructor(arrayOfClass);
    } catch (NoSuchMethodException noSuchMethodException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontScaler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */