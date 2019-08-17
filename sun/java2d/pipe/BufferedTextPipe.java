package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class BufferedTextPipe extends GlyphListPipe {
  private static final int BYTES_PER_GLYPH_IMAGE = 8;
  
  private static final int BYTES_PER_GLYPH_POSITION = 8;
  
  private static final int OFFSET_CONTRAST = 8;
  
  private static final int OFFSET_RGBORDER = 2;
  
  private static final int OFFSET_SUBPIXPOS = 1;
  
  private static final int OFFSET_POSITIONS = 0;
  
  protected final RenderQueue rq;
  
  private static int createPackedParams(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) { return (paramGlyphList.usePositions() ? 1 : 0) << false | (paramGlyphList.isSubPixPos() ? 1 : 0) << true | (paramGlyphList.isRGBOrder() ? 1 : 0) << 2 | (paramSunGraphics2D.lcdTextContrast & 0xFF) << 8; }
  
  protected BufferedTextPipe(RenderQueue paramRenderQueue) { this.rq = paramRenderQueue; }
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) {
    Composite composite = paramSunGraphics2D.composite;
    if (composite == AlphaComposite.Src)
      composite = AlphaComposite.SrcOver; 
    this.rq.lock();
    try {
      validateContext(paramSunGraphics2D, composite);
      enqueueGlyphList(paramSunGraphics2D, paramGlyphList);
    } finally {
      this.rq.unlock();
    } 
  }
  
  private void enqueueGlyphList(final SunGraphics2D sg2d, final GlyphList gl) {
    RenderBuffer renderBuffer = this.rq.getBuffer();
    final int totalGlyphs = paramGlyphList.getNumGlyphs();
    int j = i * 8;
    int k = paramGlyphList.usePositions() ? (i * 8) : 0;
    int m = 24 + j + k;
    final long[] images = paramGlyphList.getImages();
    final float glyphListOrigX = paramGlyphList.getX() + 0.5F;
    final float glyphListOrigY = paramGlyphList.getY() + 0.5F;
    this.rq.addReference(paramGlyphList.getStrike());
    if (m <= renderBuffer.capacity()) {
      if (m > renderBuffer.remaining())
        this.rq.flushNow(); 
      this.rq.ensureAlignment(20);
      renderBuffer.putInt(40);
      renderBuffer.putInt(i);
      renderBuffer.putInt(createPackedParams(paramSunGraphics2D, paramGlyphList));
      renderBuffer.putFloat(f1);
      renderBuffer.putFloat(f2);
      renderBuffer.put(arrayOfLong, 0, i);
      if (paramGlyphList.usePositions()) {
        float[] arrayOfFloat = paramGlyphList.getPositions();
        renderBuffer.put(arrayOfFloat, 0, 2 * i);
      } 
    } else {
      this.rq.flushAndInvokeNow(new Runnable() {
            public void run() { BufferedTextPipe.this.drawGlyphList(totalGlyphs, gl.usePositions(), gl.isSubPixPos(), gl.isRGBOrder(), this.val$sg2d.lcdTextContrast, glyphListOrigX, glyphListOrigY, images, gl.getPositions()); }
          });
    } 
  }
  
  protected abstract void drawGlyphList(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, float paramFloat1, float paramFloat2, long[] paramArrayOfLong, float[] paramArrayOfFloat);
  
  protected abstract void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\BufferedTextPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */