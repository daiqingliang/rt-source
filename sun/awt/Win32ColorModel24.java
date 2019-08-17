package sun.awt;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class Win32ColorModel24 extends ComponentColorModel {
  public Win32ColorModel24() { super(ColorSpace.getInstance(1000), new int[] { 8, 8, 8 }, false, false, 1, 0); }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    int[] arrayOfInt = { 2, 1, 0 };
    return Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 3, 3, arrayOfInt, null);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt = { 2, 1, 0 };
    return new PixelInterleavedSampleModel(0, paramInt1, paramInt2, 3, paramInt1 * 3, arrayOfInt);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\Win32ColorModel24.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */