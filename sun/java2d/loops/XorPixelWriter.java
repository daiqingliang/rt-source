package sun.java2d.loops;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

abstract class XorPixelWriter extends PixelWriter {
  protected ColorModel dstCM;
  
  public void writePixel(int paramInt1, int paramInt2) {
    Object object = this.dstRast.getDataElements(paramInt1, paramInt2, null);
    xorPixel(object);
    this.dstRast.setDataElements(paramInt1, paramInt2, object);
  }
  
  protected abstract void xorPixel(Object paramObject);
  
  public static class ByteData extends XorPixelWriter {
    byte[] xorData;
    
    ByteData(Object param1Object1, Object param1Object2) {
      this.xorData = (byte[])param1Object1;
      xorPixel(param1Object2);
      this.xorData = (byte[])param1Object2;
    }
    
    protected void xorPixel(Object param1Object) {
      byte[] arrayOfByte = (byte[])param1Object;
      for (byte b = 0; b < arrayOfByte.length; b++)
        arrayOfByte[b] = (byte)(arrayOfByte[b] ^ this.xorData[b]); 
    }
  }
  
  public static class DoubleData extends XorPixelWriter {
    long[] xorData;
    
    DoubleData(Object param1Object1, Object param1Object2) {
      double[] arrayOfDouble1 = (double[])param1Object1;
      double[] arrayOfDouble2 = (double[])param1Object2;
      this.xorData = new long[arrayOfDouble1.length];
      for (byte b = 0; b < arrayOfDouble1.length; b++)
        this.xorData[b] = Double.doubleToLongBits(arrayOfDouble1[b]) ^ Double.doubleToLongBits(arrayOfDouble2[b]); 
    }
    
    protected void xorPixel(Object param1Object) {
      double[] arrayOfDouble = (double[])param1Object;
      for (byte b = 0; b < arrayOfDouble.length; b++) {
        long l = Double.doubleToLongBits(arrayOfDouble[b]) ^ this.xorData[b];
        arrayOfDouble[b] = Double.longBitsToDouble(l);
      } 
    }
  }
  
  public static class FloatData extends XorPixelWriter {
    int[] xorData;
    
    FloatData(Object param1Object1, Object param1Object2) {
      float[] arrayOfFloat1 = (float[])param1Object1;
      float[] arrayOfFloat2 = (float[])param1Object2;
      this.xorData = new int[arrayOfFloat1.length];
      for (byte b = 0; b < arrayOfFloat1.length; b++)
        this.xorData[b] = Float.floatToIntBits(arrayOfFloat1[b]) ^ Float.floatToIntBits(arrayOfFloat2[b]); 
    }
    
    protected void xorPixel(Object param1Object) {
      float[] arrayOfFloat = (float[])param1Object;
      for (byte b = 0; b < arrayOfFloat.length; b++) {
        int i = Float.floatToIntBits(arrayOfFloat[b]) ^ this.xorData[b];
        arrayOfFloat[b] = Float.intBitsToFloat(i);
      } 
    }
  }
  
  public static class IntData extends XorPixelWriter {
    int[] xorData;
    
    IntData(Object param1Object1, Object param1Object2) {
      this.xorData = (int[])param1Object1;
      xorPixel(param1Object2);
      this.xorData = (int[])param1Object2;
    }
    
    protected void xorPixel(Object param1Object) {
      int[] arrayOfInt = (int[])param1Object;
      for (byte b = 0; b < arrayOfInt.length; b++)
        arrayOfInt[b] = arrayOfInt[b] ^ this.xorData[b]; 
    }
  }
  
  public static class ShortData extends XorPixelWriter {
    short[] xorData;
    
    ShortData(Object param1Object1, Object param1Object2) {
      this.xorData = (short[])param1Object1;
      xorPixel(param1Object2);
      this.xorData = (short[])param1Object2;
    }
    
    protected void xorPixel(Object param1Object) {
      short[] arrayOfShort = (short[])param1Object;
      for (byte b = 0; b < arrayOfShort.length; b++)
        arrayOfShort[b] = (short)(arrayOfShort[b] ^ this.xorData[b]); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XorPixelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */