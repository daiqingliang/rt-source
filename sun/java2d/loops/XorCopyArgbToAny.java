package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

class XorCopyArgbToAny extends Blit {
  XorCopyArgbToAny() { super(SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any); }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Raster raster = paramSurfaceData1.getRaster(paramInt1, paramInt2, paramInt5, paramInt6);
    IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)raster;
    int[] arrayOfInt1 = integerComponentRaster.getDataStorage();
    WritableRaster writableRaster = (WritableRaster)paramSurfaceData2.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    ColorModel colorModel = paramSurfaceData2.getColorModel();
    Region region = CustomComponent.getRegionOfInterest(paramSurfaceData1, paramSurfaceData2, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    SpanIterator spanIterator = region.getSpanIterator();
    int i = ((XORComposite)paramComposite).getXorColor().getRGB();
    Object object1 = colorModel.getDataElements(i, null);
    Object object2 = null;
    Object object3 = null;
    int j = integerComponentRaster.getScanlineStride();
    paramInt1 -= paramInt3;
    paramInt2 -= paramInt4;
    int[] arrayOfInt2 = new int[4];
    while (spanIterator.nextSpan(arrayOfInt2)) {
      int k = integerComponentRaster.getDataOffset(0) + (paramInt2 + arrayOfInt2[1]) * j + paramInt1 + arrayOfInt2[0];
      for (int m = arrayOfInt2[1]; m < arrayOfInt2[3]; m++) {
        int n = k;
        for (int i1 = arrayOfInt2[0]; i1 < arrayOfInt2[2]; i1++) {
          byte b5;
          double[] arrayOfDouble3;
          double[] arrayOfDouble2;
          byte b4;
          double[] arrayOfDouble1;
          float[] arrayOfFloat3;
          float[] arrayOfFloat2;
          byte b3;
          float[] arrayOfFloat1;
          int[] arrayOfInt5;
          int[] arrayOfInt4;
          int[] arrayOfInt3;
          byte b2;
          short[] arrayOfShort3;
          short[] arrayOfShort2;
          byte b1;
          short[] arrayOfShort1;
          byte[] arrayOfByte3;
          byte[] arrayOfByte2;
          byte[] arrayOfByte1;
          object2 = colorModel.getDataElements(arrayOfInt1[n++], object2);
          object3 = writableRaster.getDataElements(i1, m, object3);
          switch (colorModel.getTransferType()) {
            case 0:
              arrayOfByte1 = (byte[])object2;
              arrayOfByte2 = (byte[])object3;
              arrayOfByte3 = (byte[])object1;
              for (b1 = 0; b1 < arrayOfByte2.length; b1++)
                arrayOfByte2[b1] = (byte)(arrayOfByte2[b1] ^ arrayOfByte1[b1] ^ arrayOfByte3[b1]); 
              break;
            case 1:
            case 2:
              arrayOfShort1 = (short[])object2;
              arrayOfShort2 = (short[])object3;
              arrayOfShort3 = (short[])object1;
              for (b2 = 0; b2 < arrayOfShort2.length; b2++)
                arrayOfShort2[b2] = (short)(arrayOfShort2[b2] ^ arrayOfShort1[b2] ^ arrayOfShort3[b2]); 
              break;
            case 3:
              arrayOfInt3 = (int[])object2;
              arrayOfInt4 = (int[])object3;
              arrayOfInt5 = (int[])object1;
              for (b3 = 0; b3 < arrayOfInt4.length; b3++)
                arrayOfInt4[b3] = arrayOfInt4[b3] ^ arrayOfInt3[b3] ^ arrayOfInt5[b3]; 
              break;
            case 4:
              arrayOfFloat1 = (float[])object2;
              arrayOfFloat2 = (float[])object3;
              arrayOfFloat3 = (float[])object1;
              for (b4 = 0; b4 < arrayOfFloat2.length; b4++) {
                int i2 = Float.floatToIntBits(arrayOfFloat2[b4]) ^ Float.floatToIntBits(arrayOfFloat1[b4]) ^ Float.floatToIntBits(arrayOfFloat3[b4]);
                arrayOfFloat2[b4] = Float.intBitsToFloat(i2);
              } 
              break;
            case 5:
              arrayOfDouble1 = (double[])object2;
              arrayOfDouble2 = (double[])object3;
              arrayOfDouble3 = (double[])object1;
              for (b5 = 0; b5 < arrayOfDouble2.length; b5++) {
                long l = Double.doubleToLongBits(arrayOfDouble2[b5]) ^ Double.doubleToLongBits(arrayOfDouble1[b5]) ^ Double.doubleToLongBits(arrayOfDouble3[b5]);
                arrayOfDouble2[b5] = Double.longBitsToDouble(l);
              } 
              break;
            default:
              throw new InternalError("Unsupported XOR pixel type");
          } 
          writableRaster.setDataElements(i1, m, object3);
        } 
        k += j;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XorCopyArgbToAny.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */