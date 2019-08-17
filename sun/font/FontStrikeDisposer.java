package sun.font;

import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

class FontStrikeDisposer implements DisposerRecord, Disposer.PollDisposable {
  Font2D font2D;
  
  FontStrikeDesc desc;
  
  long[] longGlyphImages;
  
  int[] intGlyphImages;
  
  int[][] segIntGlyphImages;
  
  long[][] segLongGlyphImages;
  
  long pScalerContext = 0L;
  
  boolean disposed = false;
  
  boolean comp = false;
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong, int[] paramArrayOfInt) {
    this.font2D = paramFont2D;
    this.desc = paramFontStrikeDesc;
    this.pScalerContext = paramLong;
    this.intGlyphImages = paramArrayOfInt;
  }
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong, long[] paramArrayOfLong) {
    this.font2D = paramFont2D;
    this.desc = paramFontStrikeDesc;
    this.pScalerContext = paramLong;
    this.longGlyphImages = paramArrayOfLong;
  }
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong) {
    this.font2D = paramFont2D;
    this.desc = paramFontStrikeDesc;
    this.pScalerContext = paramLong;
  }
  
  public FontStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc) {
    this.font2D = paramFont2D;
    this.desc = paramFontStrikeDesc;
    this.comp = true;
  }
  
  public void dispose() {
    if (!this.disposed) {
      this.font2D.removeFromCache(this.desc);
      StrikeCache.disposeStrike(this);
      this.disposed = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontStrikeDisposer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */