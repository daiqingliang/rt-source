package sun.java2d.loops;

import java.awt.image.WritableRaster;

abstract class PixelWriter {
  protected WritableRaster dstRast;
  
  public void setRaster(WritableRaster paramWritableRaster) { this.dstRast = paramWritableRaster; }
  
  public abstract void writePixel(int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\PixelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */