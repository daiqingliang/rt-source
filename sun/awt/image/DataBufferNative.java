package sun.awt.image;

import java.awt.image.DataBuffer;
import sun.java2d.SurfaceData;

public class DataBufferNative extends DataBuffer {
  protected SurfaceData surfaceData;
  
  protected int width;
  
  public DataBufferNative(SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt1, paramInt2 * paramInt3);
    this.width = paramInt2;
    this.surfaceData = paramSurfaceData;
  }
  
  protected native int getElem(int paramInt1, int paramInt2, SurfaceData paramSurfaceData);
  
  public int getElem(int paramInt1, int paramInt2) { return getElem(paramInt2 % this.width, paramInt2 / this.width, this.surfaceData); }
  
  protected native void setElem(int paramInt1, int paramInt2, int paramInt3, SurfaceData paramSurfaceData);
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3) { setElem(paramInt2 % this.width, paramInt2 / this.width, paramInt3, this.surfaceData); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\DataBufferNative.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */