package sun.font;

import java.awt.geom.Point2D;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public final class SunLayoutEngine implements GlyphLayout.LayoutEngine, GlyphLayout.LayoutEngineFactory {
  private GlyphLayout.LayoutEngineKey key;
  
  private static GlyphLayout.LayoutEngineFactory instance;
  
  private SoftReference cacheref = new SoftReference(null);
  
  private static native void initGVIDs();
  
  public static GlyphLayout.LayoutEngineFactory instance() {
    if (instance == null)
      instance = new SunLayoutEngine(); 
    return instance;
  }
  
  private SunLayoutEngine() {}
  
  public GlyphLayout.LayoutEngine getEngine(Font2D paramFont2D, int paramInt1, int paramInt2) { return getEngine(new GlyphLayout.LayoutEngineKey(paramFont2D, paramInt1, paramInt2)); }
  
  public GlyphLayout.LayoutEngine getEngine(GlyphLayout.LayoutEngineKey paramLayoutEngineKey) {
    ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap)this.cacheref.get();
    if (concurrentHashMap == null) {
      concurrentHashMap = new ConcurrentHashMap();
      this.cacheref = new SoftReference(concurrentHashMap);
    } 
    GlyphLayout.LayoutEngine layoutEngine = (GlyphLayout.LayoutEngine)concurrentHashMap.get(paramLayoutEngineKey);
    if (layoutEngine == null) {
      GlyphLayout.LayoutEngineKey layoutEngineKey = paramLayoutEngineKey.copy();
      layoutEngine = new SunLayoutEngine(layoutEngineKey);
      concurrentHashMap.put(layoutEngineKey, layoutEngine);
    } 
    return layoutEngine;
  }
  
  private SunLayoutEngine(GlyphLayout.LayoutEngineKey paramLayoutEngineKey) { this.key = paramLayoutEngineKey; }
  
  public void layout(FontStrikeDesc paramFontStrikeDesc, float[] paramArrayOfFloat, int paramInt1, int paramInt2, TextRecord paramTextRecord, int paramInt3, Point2D.Float paramFloat, GlyphLayout.GVData paramGVData) {
    Font2D font2D = this.key.font();
    FontStrike fontStrike = font2D.getStrike(paramFontStrikeDesc);
    long l = font2D.getLayoutTableCache();
    nativeLayout(font2D, fontStrike, paramArrayOfFloat, paramInt1, paramInt2, paramTextRecord.text, paramTextRecord.start, paramTextRecord.limit, paramTextRecord.min, paramTextRecord.max, this.key.script(), this.key.lang(), paramInt3, paramFloat, paramGVData, font2D.getUnitsPerEm(), l);
  }
  
  private static native void nativeLayout(Font2D paramFont2D, FontStrike paramFontStrike, float[] paramArrayOfFloat, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, Point2D.Float paramFloat, GlyphLayout.GVData paramGVData, long paramLong1, long paramLong2);
  
  static  {
    FontManagerNativeLibrary.load();
    initGVIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\SunLayoutEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */