package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class PixelWriterDrawHandler extends ProcessPath.DrawHandler {
  PixelWriter pw;
  
  SurfaceData sData;
  
  Region clip;
  
  public PixelWriterDrawHandler(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, Region paramRegion, int paramInt) {
    super(paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramInt);
    this.sData = paramSurfaceData;
    this.pw = paramPixelWriter;
    this.clip = paramRegion;
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { GeneralRenderer.doDrawLine(this.sData, this.pw, null, this.clip, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void drawPixel(int paramInt1, int paramInt2) { GeneralRenderer.doSetRect(this.sData, this.pw, paramInt1, paramInt2, paramInt1 + 1, paramInt2 + 1); }
  
  public void drawScanline(int paramInt1, int paramInt2, int paramInt3) { GeneralRenderer.doSetRect(this.sData, this.pw, paramInt1, paramInt3, paramInt2 + 1, paramInt3 + 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\PixelWriterDrawHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */