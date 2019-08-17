package java.awt.image;

import java.awt.Rectangle;
import java.util.Hashtable;

public class CropImageFilter extends ImageFilter {
  int cropX;
  
  int cropY;
  
  int cropW;
  
  int cropH;
  
  public CropImageFilter(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.cropX = paramInt1;
    this.cropY = paramInt2;
    this.cropW = paramInt3;
    this.cropH = paramInt4;
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable) {
    Hashtable hashtable = (Hashtable)paramHashtable.clone();
    hashtable.put("croprect", new Rectangle(this.cropX, this.cropY, this.cropW, this.cropH));
    super.setProperties(hashtable);
  }
  
  public void setDimensions(int paramInt1, int paramInt2) { this.consumer.setDimensions(this.cropW, this.cropH); }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    int i = paramInt1;
    if (i < this.cropX)
      i = this.cropX; 
    int j = addWithoutOverflow(paramInt1, paramInt3);
    if (j > this.cropX + this.cropW)
      j = this.cropX + this.cropW; 
    int k = paramInt2;
    if (k < this.cropY)
      k = this.cropY; 
    int m = addWithoutOverflow(paramInt2, paramInt4);
    if (m > this.cropY + this.cropH)
      m = this.cropY + this.cropH; 
    if (i >= j || k >= m)
      return; 
    this.consumer.setPixels(i - this.cropX, k - this.cropY, j - i, m - k, paramColorModel, paramArrayOfByte, paramInt5 + (k - paramInt2) * paramInt6 + i - paramInt1, paramInt6);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int i = paramInt1;
    if (i < this.cropX)
      i = this.cropX; 
    int j = addWithoutOverflow(paramInt1, paramInt3);
    if (j > this.cropX + this.cropW)
      j = this.cropX + this.cropW; 
    int k = paramInt2;
    if (k < this.cropY)
      k = this.cropY; 
    int m = addWithoutOverflow(paramInt2, paramInt4);
    if (m > this.cropY + this.cropH)
      m = this.cropY + this.cropH; 
    if (i >= j || k >= m)
      return; 
    this.consumer.setPixels(i - this.cropX, k - this.cropY, j - i, m - k, paramColorModel, paramArrayOfInt, paramInt5 + (k - paramInt2) * paramInt6 + i - paramInt1, paramInt6);
  }
  
  private int addWithoutOverflow(int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    if (paramInt1 > 0 && paramInt2 > 0 && i < 0) {
      i = Integer.MAX_VALUE;
    } else if (paramInt1 < 0 && paramInt2 < 0 && i > 0) {
      i = Integer.MIN_VALUE;
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\CropImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */