package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;

public abstract class BufferedMaskFill extends MaskFill {
  protected final RenderQueue rq;
  
  protected BufferedMaskFill(RenderQueue paramRenderQueue, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    this.rq = paramRenderQueue;
  }
  
  public void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, final int x, final int y, final int w, final int h, final byte[] mask, final int maskoff, final int maskscan) {
    AlphaComposite alphaComposite = (AlphaComposite)paramComposite;
    if (alphaComposite.getRule() != 3)
      paramComposite = AlphaComposite.SrcOver; 
    this.rq.lock();
    try {
      int i;
      validateContext(paramSunGraphics2D, paramComposite, 2);
      if (paramArrayOfByte != null) {
        i = paramArrayOfByte.length + 3 & 0xFFFFFFFC;
      } else {
        i = 0;
      } 
      byte b = 32 + i;
      RenderBuffer renderBuffer = this.rq.getBuffer();
      if (b <= renderBuffer.capacity()) {
        if (b > renderBuffer.remaining())
          this.rq.flushNow(); 
        renderBuffer.putInt(32);
        renderBuffer.putInt(paramInt1).putInt(paramInt2).putInt(paramInt3).putInt(paramInt4);
        renderBuffer.putInt(paramInt5);
        renderBuffer.putInt(paramInt6);
        renderBuffer.putInt(i);
        if (paramArrayOfByte != null) {
          int j = i - paramArrayOfByte.length;
          renderBuffer.put(paramArrayOfByte);
          if (j != 0)
            renderBuffer.position((renderBuffer.position() + j)); 
        } 
      } else {
        this.rq.flushAndInvokeNow(new Runnable() {
              public void run() { BufferedMaskFill.this.maskFill(x, y, w, h, maskoff, maskscan, mask.length, mask); }
            });
      } 
    } finally {
      this.rq.unlock();
    } 
  }
  
  protected abstract void maskFill(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, byte[] paramArrayOfByte);
  
  protected abstract void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\BufferedMaskFill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */