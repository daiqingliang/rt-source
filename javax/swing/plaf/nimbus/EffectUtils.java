package javax.swing.plaf.nimbus;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

class EffectUtils {
  static void clearImage(BufferedImage paramBufferedImage) {
    Graphics2D graphics2D = paramBufferedImage.createGraphics();
    graphics2D.setComposite(AlphaComposite.Clear);
    graphics2D.fillRect(0, 0, paramBufferedImage.getWidth(), paramBufferedImage.getHeight());
    graphics2D.dispose();
  }
  
  static BufferedImage gaussianBlur(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, int paramInt) {
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    if (paramBufferedImage2 == null || paramBufferedImage2.getWidth() != i || paramBufferedImage2.getHeight() != j || paramBufferedImage1.getType() != paramBufferedImage2.getType())
      paramBufferedImage2 = createColorModelCompatibleImage(paramBufferedImage1); 
    float[] arrayOfFloat = createGaussianKernel(paramInt);
    if (paramBufferedImage1.getType() == 2) {
      int[] arrayOfInt1 = new int[i * j];
      int[] arrayOfInt2 = new int[i * j];
      getPixels(paramBufferedImage1, 0, 0, i, j, arrayOfInt1);
      blur(arrayOfInt1, arrayOfInt2, i, j, arrayOfFloat, paramInt);
      blur(arrayOfInt2, arrayOfInt1, j, i, arrayOfFloat, paramInt);
      setPixels(paramBufferedImage2, 0, 0, i, j, arrayOfInt1);
    } else if (paramBufferedImage1.getType() == 10) {
      byte[] arrayOfByte1 = new byte[i * j];
      byte[] arrayOfByte2 = new byte[i * j];
      getPixels(paramBufferedImage1, 0, 0, i, j, arrayOfByte1);
      blur(arrayOfByte1, arrayOfByte2, i, j, arrayOfFloat, paramInt);
      blur(arrayOfByte2, arrayOfByte1, j, i, arrayOfFloat, paramInt);
      setPixels(paramBufferedImage2, 0, 0, i, j, arrayOfByte1);
    } else {
      throw new IllegalArgumentException("EffectUtils.gaussianBlur() src image is not a supported type, type=[" + paramBufferedImage1.getType() + "]");
    } 
    return paramBufferedImage2;
  }
  
  private static void blur(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3) {
    for (int i = 0; i < paramInt2; i++) {
      int j = i;
      int k = i * paramInt1;
      for (int m = 0; m < paramInt1; m++) {
        float f4 = 0.0F;
        float f3 = f4;
        float f2 = f3;
        float f1 = f2;
        for (int i4 = -paramInt3; i4 <= paramInt3; i4++) {
          int i5 = m + i4;
          if (i5 < 0 || i5 >= paramInt1)
            i5 = (m + paramInt1) % paramInt1; 
          int i6 = paramArrayOfInt1[k + i5];
          float f = paramArrayOfFloat[paramInt3 + i4];
          f1 += f * (i6 >> 24 & 0xFF);
          f2 += f * (i6 >> 16 & 0xFF);
          f3 += f * (i6 >> 8 & 0xFF);
          f4 += f * (i6 & 0xFF);
        } 
        int n = (int)(f1 + 0.5F);
        int i1 = (int)(f2 + 0.5F);
        int i2 = (int)(f3 + 0.5F);
        int i3 = (int)(f4 + 0.5F);
        paramArrayOfInt2[j] = ((n > 255) ? 255 : n) << '\030' | ((i1 > 255) ? 255 : i1) << '\020' | ((i2 > 255) ? 255 : i2) << '\b' | ((i3 > 255) ? 255 : i3);
        j += paramInt2;
      } 
    } 
  }
  
  static void blur(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3) {
    for (int i = 0; i < paramInt2; i++) {
      int j = i;
      int k = i * paramInt1;
      for (int m = 0; m < paramInt1; m++) {
        float f = 0.0F;
        for (int i1 = -paramInt3; i1 <= paramInt3; i1++) {
          int i2 = m + i1;
          if (i2 < 0 || i2 >= paramInt1)
            i2 = (m + paramInt1) % paramInt1; 
          byte b = paramArrayOfByte1[k + i2] & 0xFF;
          float f1 = paramArrayOfFloat[paramInt3 + i1];
          f += f1 * b;
        } 
        int n = (int)(f + 0.5F);
        paramArrayOfByte2[j] = (byte)((n > 255) ? 255 : n);
        j += paramInt2;
      } 
    } 
  }
  
  static float[] createGaussianKernel(int paramInt) {
    if (paramInt < 1)
      throw new IllegalArgumentException("Radius must be >= 1"); 
    float[] arrayOfFloat = new float[paramInt * 2 + 1];
    float f1 = paramInt / 3.0F;
    float f2 = 2.0F * f1 * f1;
    float f3 = (float)Math.sqrt(f2 * Math.PI);
    float f4 = 0.0F;
    int i;
    for (i = -paramInt; i <= paramInt; i++) {
      float f = (i * i);
      int j = i + paramInt;
      arrayOfFloat[j] = (float)Math.exp((-f / f2)) / f3;
      f4 += arrayOfFloat[j];
    } 
    for (i = 0; i < arrayOfFloat.length; i++)
      arrayOfFloat[i] = arrayOfFloat[i] / f4; 
    return arrayOfFloat;
  }
  
  static byte[] getPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    if (paramInt3 == 0 || paramInt4 == 0)
      return new byte[0]; 
    if (paramArrayOfByte == null) {
      paramArrayOfByte = new byte[paramInt3 * paramInt4];
    } else if (paramArrayOfByte.length < paramInt3 * paramInt4) {
      throw new IllegalArgumentException("pixels array must have a length >= w*h");
    } 
    int i = paramBufferedImage.getType();
    if (i == 10) {
      WritableRaster writableRaster = paramBufferedImage.getRaster();
      return (byte[])writableRaster.getDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
    } 
    throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
  }
  
  static void setPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null || paramInt3 == 0 || paramInt4 == 0)
      return; 
    if (paramArrayOfByte.length < paramInt3 * paramInt4)
      throw new IllegalArgumentException("pixels array must have a length >= w*h"); 
    int i = paramBufferedImage.getType();
    if (i == 10) {
      WritableRaster writableRaster = paramBufferedImage.getRaster();
      writableRaster.setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
    } else {
      throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
    } 
  }
  
  public static int[] getPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    if (paramInt3 == 0 || paramInt4 == 0)
      return new int[0]; 
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt3 * paramInt4];
    } else if (paramArrayOfInt.length < paramInt3 * paramInt4) {
      throw new IllegalArgumentException("pixels array must have a length >= w*h");
    } 
    int i = paramBufferedImage.getType();
    if (i == 2 || i == 1) {
      WritableRaster writableRaster = paramBufferedImage.getRaster();
      return (int[])writableRaster.getDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    } 
    return paramBufferedImage.getRGB(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, 0, paramInt3);
  }
  
  public static void setPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    if (paramArrayOfInt == null || paramInt3 == 0 || paramInt4 == 0)
      return; 
    if (paramArrayOfInt.length < paramInt3 * paramInt4)
      throw new IllegalArgumentException("pixels array must have a length >= w*h"); 
    int i = paramBufferedImage.getType();
    if (i == 2 || i == 1) {
      WritableRaster writableRaster = paramBufferedImage.getRaster();
      writableRaster.setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    } else {
      paramBufferedImage.setRGB(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, 0, paramInt3);
    } 
  }
  
  public static BufferedImage createColorModelCompatibleImage(BufferedImage paramBufferedImage) {
    ColorModel colorModel = paramBufferedImage.getColorModel();
    return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(paramBufferedImage.getWidth(), paramBufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), null);
  }
  
  public static BufferedImage createCompatibleTranslucentImage(int paramInt1, int paramInt2) { return isHeadless() ? new BufferedImage(paramInt1, paramInt2, 2) : getGraphicsConfiguration().createCompatibleImage(paramInt1, paramInt2, 3); }
  
  private static boolean isHeadless() { return GraphicsEnvironment.isHeadless(); }
  
  private static GraphicsConfiguration getGraphicsConfiguration() { return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\EffectUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */