package java.awt.image;

import java.util.Hashtable;

public class ReplicateScaleFilter extends ImageFilter {
  protected int srcWidth;
  
  protected int srcHeight;
  
  protected int destWidth;
  
  protected int destHeight;
  
  protected int[] srcrows;
  
  protected int[] srccols;
  
  protected Object outpixbuf;
  
  public ReplicateScaleFilter(int paramInt1, int paramInt2) {
    if (paramInt1 == 0 || paramInt2 == 0)
      throw new IllegalArgumentException("Width (" + paramInt1 + ") and height (" + paramInt2 + ") must be non-zero"); 
    this.destWidth = paramInt1;
    this.destHeight = paramInt2;
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable) {
    Hashtable hashtable = (Hashtable)paramHashtable.clone();
    String str1 = "rescale";
    String str2 = this.destWidth + "x" + this.destHeight;
    Object object = hashtable.get(str1);
    if (object != null && object instanceof String)
      str2 = (String)object + ", " + str2; 
    hashtable.put(str1, str2);
    super.setProperties(hashtable);
  }
  
  public void setDimensions(int paramInt1, int paramInt2) {
    this.srcWidth = paramInt1;
    this.srcHeight = paramInt2;
    if (this.destWidth < 0) {
      if (this.destHeight < 0) {
        this.destWidth = this.srcWidth;
        this.destHeight = this.srcHeight;
      } else {
        this.destWidth = this.srcWidth * this.destHeight / this.srcHeight;
      } 
    } else if (this.destHeight < 0) {
      this.destHeight = this.srcHeight * this.destWidth / this.srcWidth;
    } 
    this.consumer.setDimensions(this.destWidth, this.destHeight);
  }
  
  private void calculateMaps() {
    this.srcrows = new int[this.destHeight + 1];
    byte b;
    for (b = 0; b <= this.destHeight; b++)
      this.srcrows[b] = (2 * b * this.srcHeight + this.srcHeight) / 2 * this.destHeight; 
    this.srccols = new int[this.destWidth + 1];
    for (b = 0; b <= this.destWidth; b++)
      this.srccols[b] = (2 * b * this.srcWidth + this.srcWidth) / 2 * this.destWidth; 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    byte[] arrayOfByte;
    if (this.srcrows == null || this.srccols == null)
      calculateMaps(); 
    int j = (2 * paramInt1 * this.destWidth + this.srcWidth - 1) / 2 * this.srcWidth;
    int k = (2 * paramInt2 * this.destHeight + this.srcHeight - 1) / 2 * this.srcHeight;
    if (this.outpixbuf != null && this.outpixbuf instanceof byte[]) {
      arrayOfByte = (byte[])this.outpixbuf;
    } else {
      arrayOfByte = new byte[this.destWidth];
      this.outpixbuf = arrayOfByte;
    } 
    int i;
    for (int m = k; (i = this.srcrows[m]) < paramInt2 + paramInt4; m++) {
      int i1 = paramInt5 + paramInt6 * (i - paramInt2);
      int n;
      int i2;
      for (i2 = j; (n = this.srccols[i2]) < paramInt1 + paramInt3; i2++)
        arrayOfByte[i2] = paramArrayOfByte[i1 + n - paramInt1]; 
      if (i2 > j)
        this.consumer.setPixels(j, m, i2 - j, 1, paramColorModel, arrayOfByte, j, this.destWidth); 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int[] arrayOfInt;
    if (this.srcrows == null || this.srccols == null)
      calculateMaps(); 
    int j = (2 * paramInt1 * this.destWidth + this.srcWidth - 1) / 2 * this.srcWidth;
    int k = (2 * paramInt2 * this.destHeight + this.srcHeight - 1) / 2 * this.srcHeight;
    if (this.outpixbuf != null && this.outpixbuf instanceof int[]) {
      arrayOfInt = (int[])this.outpixbuf;
    } else {
      arrayOfInt = new int[this.destWidth];
      this.outpixbuf = arrayOfInt;
    } 
    int i;
    for (int m = k; (i = this.srcrows[m]) < paramInt2 + paramInt4; m++) {
      int i1 = paramInt5 + paramInt6 * (i - paramInt2);
      int n;
      int i2;
      for (i2 = j; (n = this.srccols[i2]) < paramInt1 + paramInt3; i2++)
        arrayOfInt[i2] = paramArrayOfInt[i1 + n - paramInt1]; 
      if (i2 > j)
        this.consumer.setPixels(j, m, i2 - j, 1, paramColorModel, arrayOfInt, j, this.destWidth); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ReplicateScaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */