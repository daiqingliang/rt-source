package javax.swing.plaf.nimbus;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

class DropShadowEffect extends ShadowEffect {
  Effect.EffectType getEffectType() { return Effect.EffectType.UNDER; }
  
  BufferedImage applyEffect(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, int paramInt1, int paramInt2) {
    if (paramBufferedImage1 == null || paramBufferedImage1.getType() != 2)
      throw new IllegalArgumentException("Effect only works with source images of type BufferedImage.TYPE_INT_ARGB."); 
    if (paramBufferedImage2 != null && paramBufferedImage2.getType() != 2)
      throw new IllegalArgumentException("Effect only works with destination images of type BufferedImage.TYPE_INT_ARGB."); 
    double d = Math.toRadians((this.angle - 90));
    int i = (int)(Math.sin(d) * this.distance);
    int j = (int)(Math.cos(d) * this.distance);
    int k = i + this.size;
    int m = i + this.size;
    int n = paramInt1 + i + this.size + this.size;
    int i1 = paramInt2 + i + this.size;
    int[] arrayOfInt = getArrayCache().getTmpIntArray(paramInt1);
    byte[] arrayOfByte1 = getArrayCache().getTmpByteArray1(n * i1);
    Arrays.fill(arrayOfByte1, (byte)0);
    byte[] arrayOfByte2 = getArrayCache().getTmpByteArray2(n * i1);
    WritableRaster writableRaster1 = paramBufferedImage1.getRaster();
    for (int i2 = 0; i2 < paramInt2; i2++) {
      int i7 = i2 + m;
      int i8 = i7 * n;
      writableRaster1.getDataElements(0, i2, paramInt1, 1, arrayOfInt);
      for (int i9 = 0; i9 < paramInt1; i9++) {
        int i10 = i9 + k;
        arrayOfByte1[i8 + i10] = (byte)((arrayOfInt[i9] & 0xFF000000) >>> 24);
      } 
    } 
    float[] arrayOfFloat = EffectUtils.createGaussianKernel(this.size);
    EffectUtils.blur(arrayOfByte1, arrayOfByte2, n, i1, arrayOfFloat, this.size);
    EffectUtils.blur(arrayOfByte2, arrayOfByte1, i1, n, arrayOfFloat, this.size);
    float f = Math.min(1.0F / (1.0F - 0.01F * this.spread), 255.0F);
    for (byte b = 0; b < arrayOfByte1.length; b++) {
      int i7 = (int)((arrayOfByte1[b] & 0xFF) * f);
      arrayOfByte1[b] = (i7 > 255) ? -1 : (byte)i7;
    } 
    if (paramBufferedImage2 == null)
      paramBufferedImage2 = new BufferedImage(paramInt1, paramInt2, 2); 
    WritableRaster writableRaster2 = paramBufferedImage2.getRaster();
    int i3 = this.color.getRed();
    int i4 = this.color.getGreen();
    int i5 = this.color.getBlue();
    for (int i6 = 0; i6 < paramInt2; i6++) {
      int i7 = i6 + m;
      int i8 = (i7 - j) * n;
      for (int i9 = 0; i9 < paramInt1; i9++) {
        int i10 = i9 + k;
        arrayOfInt[i9] = arrayOfByte1[i8 + i10 - i] << 24 | i3 << 16 | i4 << 8 | i5;
      } 
      writableRaster2.setDataElements(0, i6, paramInt1, 1, arrayOfInt);
    } 
    return paramBufferedImage2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\DropShadowEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */