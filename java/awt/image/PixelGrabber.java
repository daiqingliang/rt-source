package java.awt.image;

import java.awt.Image;
import java.util.Hashtable;

public class PixelGrabber implements ImageConsumer {
  ImageProducer producer;
  
  int dstX;
  
  int dstY;
  
  int dstW;
  
  int dstH;
  
  ColorModel imageModel;
  
  byte[] bytePixels;
  
  int[] intPixels;
  
  int dstOff;
  
  int dstScan;
  
  private boolean grabbing;
  
  private int flags;
  
  private static final int GRABBEDBITS = 48;
  
  private static final int DONEBITS = 112;
  
  public PixelGrabber(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6) { this(paramImage.getSource(), paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5, paramInt6); }
  
  public PixelGrabber(ImageProducer paramImageProducer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    this.producer = paramImageProducer;
    this.dstX = paramInt1;
    this.dstY = paramInt2;
    this.dstW = paramInt3;
    this.dstH = paramInt4;
    this.dstOff = paramInt5;
    this.dstScan = paramInt6;
    this.intPixels = paramArrayOfInt;
    this.imageModel = ColorModel.getRGBdefault();
  }
  
  public PixelGrabber(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    this.producer = paramImage.getSource();
    this.dstX = paramInt1;
    this.dstY = paramInt2;
    this.dstW = paramInt3;
    this.dstH = paramInt4;
    if (paramBoolean)
      this.imageModel = ColorModel.getRGBdefault(); 
  }
  
  public void startGrabbing() {
    if ((this.flags & 0x70) != 0)
      return; 
    if (!this.grabbing) {
      this.grabbing = true;
      this.flags &= 0xFFFFFF7F;
      this.producer.startProduction(this);
    } 
  }
  
  public void abortGrabbing() { imageComplete(4); }
  
  public boolean grabPixels() throws InterruptedException { return grabPixels(0L); }
  
  public boolean grabPixels(long paramLong) throws InterruptedException {
    if ((this.flags & 0x70) != 0)
      return ((this.flags & 0x30) != 0); 
    long l = paramLong + System.currentTimeMillis();
    if (!this.grabbing) {
      this.grabbing = true;
      this.flags &= 0xFFFFFF7F;
      this.producer.startProduction(this);
    } 
    while (this.grabbing) {
      long l1;
      if (paramLong == 0L) {
        l1 = 0L;
      } else {
        l1 = l - System.currentTimeMillis();
        if (l1 <= 0L)
          break; 
      } 
      wait(l1);
    } 
    return ((this.flags & 0x30) != 0);
  }
  
  public int getStatus() { return this.flags; }
  
  public int getWidth() { return (this.dstW < 0) ? -1 : this.dstW; }
  
  public int getHeight() { return (this.dstH < 0) ? -1 : this.dstH; }
  
  public Object getPixels() { return (this.bytePixels == null) ? this.intPixels : this.bytePixels; }
  
  public ColorModel getColorModel() { return this.imageModel; }
  
  public void setDimensions(int paramInt1, int paramInt2) {
    if (this.dstW < 0)
      this.dstW = paramInt1 - this.dstX; 
    if (this.dstH < 0)
      this.dstH = paramInt2 - this.dstY; 
    if (this.dstW <= 0 || this.dstH <= 0) {
      imageComplete(3);
    } else if (this.intPixels == null && this.imageModel == ColorModel.getRGBdefault()) {
      this.intPixels = new int[this.dstW * this.dstH];
      this.dstScan = this.dstW;
      this.dstOff = 0;
    } 
    this.flags |= 0x3;
  }
  
  public void setHints(int paramInt) {}
  
  public void setProperties(Hashtable<?, ?> paramHashtable) {}
  
  public void setColorModel(ColorModel paramColorModel) {}
  
  private void convertToRGB() {
    int i = this.dstW * this.dstH;
    int[] arrayOfInt = new int[i];
    if (this.bytePixels != null) {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = this.imageModel.getRGB(this.bytePixels[b] & 0xFF); 
    } else if (this.intPixels != null) {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = this.imageModel.getRGB(this.intPixels[b]); 
    } 
    this.bytePixels = null;
    this.intPixels = arrayOfInt;
    this.dstScan = this.dstW;
    this.dstOff = 0;
    this.imageModel = ColorModel.getRGBdefault();
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    if (paramInt2 < this.dstY) {
      int j = this.dstY - paramInt2;
      if (j >= paramInt4)
        return; 
      paramInt5 += paramInt6 * j;
      paramInt2 += j;
      paramInt4 -= j;
    } 
    if (paramInt2 + paramInt4 > this.dstY + this.dstH) {
      paramInt4 = this.dstY + this.dstH - paramInt2;
      if (paramInt4 <= 0)
        return; 
    } 
    if (paramInt1 < this.dstX) {
      int j = this.dstX - paramInt1;
      if (j >= paramInt3)
        return; 
      paramInt5 += j;
      paramInt1 += j;
      paramInt3 -= j;
    } 
    if (paramInt1 + paramInt3 > this.dstX + this.dstW) {
      paramInt3 = this.dstX + this.dstW - paramInt1;
      if (paramInt3 <= 0)
        return; 
    } 
    int i = this.dstOff + (paramInt2 - this.dstY) * this.dstScan + paramInt1 - this.dstX;
    if (this.intPixels == null) {
      if (this.bytePixels == null) {
        this.bytePixels = new byte[this.dstW * this.dstH];
        this.dstScan = this.dstW;
        this.dstOff = 0;
        this.imageModel = paramColorModel;
      } else if (this.imageModel != paramColorModel) {
        convertToRGB();
      } 
      if (this.bytePixels != null)
        for (int j = paramInt4; j > 0; j--) {
          System.arraycopy(paramArrayOfByte, paramInt5, this.bytePixels, i, paramInt3);
          paramInt5 += paramInt6;
          i += this.dstScan;
        }  
    } 
    if (this.intPixels != null) {
      int j = this.dstScan - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--) {
        for (int n = paramInt3; n > 0; n--)
          this.intPixels[i++] = paramColorModel.getRGB(paramArrayOfByte[paramInt5++] & 0xFF); 
        paramInt5 += k;
        i += j;
      } 
    } 
    this.flags |= 0x8;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    if (paramInt2 < this.dstY) {
      int j = this.dstY - paramInt2;
      if (j >= paramInt4)
        return; 
      paramInt5 += paramInt6 * j;
      paramInt2 += j;
      paramInt4 -= j;
    } 
    if (paramInt2 + paramInt4 > this.dstY + this.dstH) {
      paramInt4 = this.dstY + this.dstH - paramInt2;
      if (paramInt4 <= 0)
        return; 
    } 
    if (paramInt1 < this.dstX) {
      int j = this.dstX - paramInt1;
      if (j >= paramInt3)
        return; 
      paramInt5 += j;
      paramInt1 += j;
      paramInt3 -= j;
    } 
    if (paramInt1 + paramInt3 > this.dstX + this.dstW) {
      paramInt3 = this.dstX + this.dstW - paramInt1;
      if (paramInt3 <= 0)
        return; 
    } 
    if (this.intPixels == null)
      if (this.bytePixels == null) {
        this.intPixels = new int[this.dstW * this.dstH];
        this.dstScan = this.dstW;
        this.dstOff = 0;
        this.imageModel = paramColorModel;
      } else {
        convertToRGB();
      }  
    int i = this.dstOff + (paramInt2 - this.dstY) * this.dstScan + paramInt1 - this.dstX;
    if (this.imageModel == paramColorModel) {
      for (int j = paramInt4; j > 0; j--) {
        System.arraycopy(paramArrayOfInt, paramInt5, this.intPixels, i, paramInt3);
        paramInt5 += paramInt6;
        i += this.dstScan;
      } 
    } else {
      if (this.imageModel != ColorModel.getRGBdefault())
        convertToRGB(); 
      int j = this.dstScan - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--) {
        for (int n = paramInt3; n > 0; n--)
          this.intPixels[i++] = paramColorModel.getRGB(paramArrayOfInt[paramInt5++]); 
        paramInt5 += k;
        i += j;
      } 
    } 
    this.flags |= 0x8;
  }
  
  public void imageComplete(int paramInt) {
    this.grabbing = false;
    switch (paramInt) {
      default:
        this.flags |= 0xC0;
        break;
      case 4:
        this.flags |= 0x80;
        break;
      case 3:
        this.flags |= 0x20;
        break;
      case 2:
        this.flags |= 0x10;
        break;
    } 
    this.producer.removeConsumer(this);
    notifyAll();
  }
  
  public int status() { return this.flags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\PixelGrabber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */