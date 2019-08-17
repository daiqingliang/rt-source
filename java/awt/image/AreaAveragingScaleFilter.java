package java.awt.image;

public class AreaAveragingScaleFilter extends ReplicateScaleFilter {
  private static final ColorModel rgbmodel = ColorModel.getRGBdefault();
  
  private static final int neededHints = 6;
  
  private boolean passthrough;
  
  private float[] reds;
  
  private float[] greens;
  
  private float[] blues;
  
  private float[] alphas;
  
  private int savedy;
  
  private int savedyrem;
  
  public AreaAveragingScaleFilter(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  public void setHints(int paramInt) {
    this.passthrough = ((paramInt & 0x6) != 6);
    super.setHints(paramInt);
  }
  
  private void makeAccumBuffers() {
    this.reds = new float[this.destWidth];
    this.greens = new float[this.destWidth];
    this.blues = new float[this.destWidth];
    this.alphas = new float[this.destWidth];
  }
  
  private int[] calcRow() {
    float f = this.srcWidth * this.srcHeight;
    if (this.outpixbuf == null || !(this.outpixbuf instanceof int[]))
      this.outpixbuf = new int[this.destWidth]; 
    int[] arrayOfInt = (int[])this.outpixbuf;
    for (byte b = 0; b < this.destWidth; b++) {
      float f1 = f;
      int i = Math.round(this.alphas[b] / f1);
      if (i <= 0) {
        i = 0;
      } else if (i >= 255) {
        i = 255;
      } else {
        f1 = this.alphas[b] / 255.0F;
      } 
      int j = Math.round(this.reds[b] / f1);
      int k = Math.round(this.greens[b] / f1);
      int m = Math.round(this.blues[b] / f1);
      if (j < 0) {
        j = 0;
      } else if (j > 255) {
        j = 255;
      } 
      if (k < 0) {
        k = 0;
      } else if (k > 255) {
        k = 255;
      } 
      if (m < 0) {
        m = 0;
      } else if (m > 255) {
        m = 255;
      } 
      arrayOfInt[b] = i << 24 | j << 16 | k << 8 | m;
    } 
    return arrayOfInt;
  }
  
  private void accumPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, Object paramObject, int paramInt5, int paramInt6) {
    int m;
    int k;
    if (this.reds == null)
      makeAccumBuffers(); 
    int i = paramInt2;
    int j = this.destHeight;
    if (i == 0) {
      k = 0;
      m = 0;
    } else {
      k = this.savedy;
      m = this.savedyrem;
    } 
    while (i < paramInt2 + paramInt4) {
      int n;
      if (m == 0) {
        for (byte b1 = 0; b1 < this.destWidth; b1++) {
          this.blues[b1] = 0.0F;
          this.greens[b1] = 0.0F;
          this.reds[b1] = 0.0F;
          this.alphas[b1] = 0.0F;
        } 
        m = this.srcHeight;
      } 
      if (j < m) {
        n = j;
      } else {
        n = m;
      } 
      int i1 = 0;
      byte b = 0;
      int i2 = 0;
      int i3 = this.srcWidth;
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      while (i1 < paramInt3) {
        int i4;
        if (!i2) {
          i2 = this.destWidth;
          if (paramObject instanceof byte[]) {
            i4 = (byte[])paramObject[paramInt5 + i1] & 0xFF;
          } else {
            i4 = (int[])paramObject[paramInt5 + i1];
          } 
          i4 = paramColorModel.getRGB(i4);
          f1 = (i4 >>> 24);
          f2 = (i4 >> 16 & 0xFF);
          f3 = (i4 >> 8 & 0xFF);
          f4 = (i4 & 0xFF);
          if (f1 != 255.0F) {
            float f5 = f1 / 255.0F;
            f2 *= f5;
            f3 *= f5;
            f4 *= f5;
          } 
        } 
        if (i2 < i3) {
          i4 = i2;
        } else {
          i4 = i3;
        } 
        float f = i4 * n;
        this.alphas[b] = this.alphas[b] + f * f1;
        this.reds[b] = this.reds[b] + f * f2;
        this.greens[b] = this.greens[b] + f * f3;
        this.blues[b] = this.blues[b] + f * f4;
        if (i2 -= i4 == 0)
          i1++; 
        if (i3 -= i4 == 0) {
          b++;
          i3 = this.srcWidth;
        } 
      } 
      if (m -= n == 0) {
        int[] arrayOfInt = calcRow();
        do {
          this.consumer.setPixels(0, k, this.destWidth, 1, rgbmodel, arrayOfInt, 0, this.destWidth);
          k++;
        } while (j -= n >= n && n == this.srcHeight);
      } else {
        j -= n;
      } 
      if (j == 0) {
        j = this.destHeight;
        i++;
        paramInt5 += paramInt6;
      } 
    } 
    this.savedyrem = m;
    this.savedy = k;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    if (this.passthrough) {
      super.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
    } else {
      accumPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    if (this.passthrough) {
      super.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6);
    } else {
      accumPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\AreaAveragingScaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */