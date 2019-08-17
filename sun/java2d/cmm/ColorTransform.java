package sun.java2d.cmm;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public interface ColorTransform {
  public static final int Any = -1;
  
  public static final int In = 1;
  
  public static final int Out = 2;
  
  public static final int Gamut = 3;
  
  public static final int Simulation = 4;
  
  int getNumInComponents();
  
  int getNumOutComponents();
  
  void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2);
  
  void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4);
  
  void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster);
  
  short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2);
  
  byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\ColorTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */