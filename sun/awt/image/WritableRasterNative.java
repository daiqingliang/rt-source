package sun.awt.image;

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.SurfaceData;

public class WritableRasterNative extends WritableRaster {
  public static WritableRasterNative createNativeRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer) { return new WritableRasterNative(paramSampleModel, paramDataBuffer); }
  
  protected WritableRasterNative(SampleModel paramSampleModel, DataBuffer paramDataBuffer) { super(paramSampleModel, paramDataBuffer, new Point(0, 0)); }
  
  public static WritableRasterNative createNativeRaster(ColorModel paramColorModel, SurfaceData paramSurfaceData, int paramInt1, int paramInt2) {
    DirectColorModel directColorModel;
    int[] arrayOfInt2;
    DataBufferNative dataBufferNative;
    int[] arrayOfInt1;
    SinglePixelPackedSampleModel singlePixelPackedSampleModel;
    PixelInterleavedSampleModel pixelInterleavedSampleModel = null;
    byte b = 0;
    int i = paramInt1;
    switch (paramColorModel.getPixelSize()) {
      case 8:
      case 12:
        if (paramColorModel.getPixelSize() == 8) {
          b = 0;
        } else {
          b = 1;
        } 
        arrayOfInt1 = new int[1];
        arrayOfInt1[0] = 0;
        pixelInterleavedSampleModel = new PixelInterleavedSampleModel(b, paramInt1, paramInt2, 1, i, arrayOfInt1);
        dataBufferNative = new DataBufferNative(paramSurfaceData, b, paramInt1, paramInt2);
        return new WritableRasterNative(pixelInterleavedSampleModel, dataBufferNative);
      case 15:
      case 16:
        b = 1;
        arrayOfInt2 = new int[3];
        directColorModel = (DirectColorModel)paramColorModel;
        arrayOfInt2[0] = directColorModel.getRedMask();
        arrayOfInt2[1] = directColorModel.getGreenMask();
        arrayOfInt2[2] = directColorModel.getBlueMask();
        singlePixelPackedSampleModel = new SinglePixelPackedSampleModel(b, paramInt1, paramInt2, i, arrayOfInt2);
        dataBufferNative = new DataBufferNative(paramSurfaceData, b, paramInt1, paramInt2);
        return new WritableRasterNative(singlePixelPackedSampleModel, dataBufferNative);
      case 24:
      case 32:
        b = 3;
        arrayOfInt2 = new int[3];
        directColorModel = (DirectColorModel)paramColorModel;
        arrayOfInt2[0] = directColorModel.getRedMask();
        arrayOfInt2[1] = directColorModel.getGreenMask();
        arrayOfInt2[2] = directColorModel.getBlueMask();
        singlePixelPackedSampleModel = new SinglePixelPackedSampleModel(b, paramInt1, paramInt2, i, arrayOfInt2);
        dataBufferNative = new DataBufferNative(paramSurfaceData, b, paramInt1, paramInt2);
        return new WritableRasterNative(singlePixelPackedSampleModel, dataBufferNative);
    } 
    throw new InternalError("Unsupported depth " + paramColorModel.getPixelSize());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\WritableRasterNative.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */