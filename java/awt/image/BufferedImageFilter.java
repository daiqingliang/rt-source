package java.awt.image;

public class BufferedImageFilter extends ImageFilter implements Cloneable {
  BufferedImageOp bufferedImageOp;
  
  ColorModel model;
  
  int width;
  
  int height;
  
  byte[] bytePixels;
  
  int[] intPixels;
  
  public BufferedImageFilter(BufferedImageOp paramBufferedImageOp) {
    if (paramBufferedImageOp == null)
      throw new NullPointerException("Operation cannot be null"); 
    this.bufferedImageOp = paramBufferedImageOp;
  }
  
  public BufferedImageOp getBufferedImageOp() { return this.bufferedImageOp; }
  
  public void setDimensions(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0) {
      imageComplete(3);
      return;
    } 
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public void setColorModel(ColorModel paramColorModel) { this.model = paramColorModel; }
  
  private void convertToRGB() {
    int i = this.width * this.height;
    int[] arrayOfInt = new int[i];
    if (this.bytePixels != null) {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = this.model.getRGB(this.bytePixels[b] & 0xFF); 
    } else if (this.intPixels != null) {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = this.model.getRGB(this.intPixels[b]); 
    } 
    this.bytePixels = null;
    this.intPixels = arrayOfInt;
    this.model = ColorModel.getRGBdefault();
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    if (paramInt3 < 0 || paramInt4 < 0)
      throw new IllegalArgumentException("Width (" + paramInt3 + ") and height (" + paramInt4 + ") must be > 0"); 
    if (paramInt3 == 0 || paramInt4 == 0)
      return; 
    if (paramInt2 < 0) {
      int j = -paramInt2;
      if (j >= paramInt4)
        return; 
      paramInt5 += paramInt6 * j;
      paramInt2 += j;
      paramInt4 -= j;
    } 
    if (paramInt2 + paramInt4 > this.height) {
      paramInt4 = this.height - paramInt2;
      if (paramInt4 <= 0)
        return; 
    } 
    if (paramInt1 < 0) {
      int j = -paramInt1;
      if (j >= paramInt3)
        return; 
      paramInt5 += j;
      paramInt1 += j;
      paramInt3 -= j;
    } 
    if (paramInt1 + paramInt3 > this.width) {
      paramInt3 = this.width - paramInt1;
      if (paramInt3 <= 0)
        return; 
    } 
    int i = paramInt2 * this.width + paramInt1;
    if (this.intPixels == null) {
      if (this.bytePixels == null) {
        this.bytePixels = new byte[this.width * this.height];
        this.model = paramColorModel;
      } else if (this.model != paramColorModel) {
        convertToRGB();
      } 
      if (this.bytePixels != null)
        for (int j = paramInt4; j > 0; j--) {
          System.arraycopy(paramArrayOfByte, paramInt5, this.bytePixels, i, paramInt3);
          paramInt5 += paramInt6;
          i += this.width;
        }  
    } 
    if (this.intPixels != null) {
      int j = this.width - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--) {
        for (int n = paramInt3; n > 0; n--)
          this.intPixels[i++] = paramColorModel.getRGB(paramArrayOfByte[paramInt5++] & 0xFF); 
        paramInt5 += k;
        i += j;
      } 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    if (paramInt3 < 0 || paramInt4 < 0)
      throw new IllegalArgumentException("Width (" + paramInt3 + ") and height (" + paramInt4 + ") must be > 0"); 
    if (paramInt3 == 0 || paramInt4 == 0)
      return; 
    if (paramInt2 < 0) {
      int j = -paramInt2;
      if (j >= paramInt4)
        return; 
      paramInt5 += paramInt6 * j;
      paramInt2 += j;
      paramInt4 -= j;
    } 
    if (paramInt2 + paramInt4 > this.height) {
      paramInt4 = this.height - paramInt2;
      if (paramInt4 <= 0)
        return; 
    } 
    if (paramInt1 < 0) {
      int j = -paramInt1;
      if (j >= paramInt3)
        return; 
      paramInt5 += j;
      paramInt1 += j;
      paramInt3 -= j;
    } 
    if (paramInt1 + paramInt3 > this.width) {
      paramInt3 = this.width - paramInt1;
      if (paramInt3 <= 0)
        return; 
    } 
    if (this.intPixels == null)
      if (this.bytePixels == null) {
        this.intPixels = new int[this.width * this.height];
        this.model = paramColorModel;
      } else {
        convertToRGB();
      }  
    int i = paramInt2 * this.width + paramInt1;
    if (this.model == paramColorModel) {
      for (int j = paramInt4; j > 0; j--) {
        System.arraycopy(paramArrayOfInt, paramInt5, this.intPixels, i, paramInt3);
        paramInt5 += paramInt6;
        i += this.width;
      } 
    } else {
      if (this.model != ColorModel.getRGBdefault())
        convertToRGB(); 
      int j = this.width - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--) {
        for (int n = paramInt3; n > 0; n--)
          this.intPixels[i++] = paramColorModel.getRGB(paramArrayOfInt[paramInt5++]); 
        paramInt5 += k;
        i += j;
      } 
    } 
  }
  
  public void imageComplete(int paramInt) {
    int j;
    int i;
    ColorModel colorModel;
    WritableRaster writableRaster2;
    BufferedImage bufferedImage;
    WritableRaster writableRaster1;
    switch (paramInt) {
      case 1:
      case 4:
        this.model = null;
        this.width = -1;
        this.height = -1;
        this.intPixels = null;
        this.bytePixels = null;
        break;
      case 2:
      case 3:
        if (this.width <= 0 || this.height <= 0)
          break; 
        if (this.model instanceof DirectColorModel) {
          if (this.intPixels == null)
            break; 
          writableRaster1 = createDCMraster();
        } else if (this.model instanceof IndexColorModel) {
          int[] arrayOfInt = { 0 };
          if (this.bytePixels == null)
            break; 
          DataBufferByte dataBufferByte = new DataBufferByte(this.bytePixels, this.width * this.height);
          writableRaster1 = Raster.createInterleavedRaster(dataBufferByte, this.width, this.height, this.width, 1, arrayOfInt, null);
        } else {
          convertToRGB();
          if (this.intPixels == null)
            break; 
          writableRaster1 = createDCMraster();
        } 
        bufferedImage = new BufferedImage(this.model, writableRaster1, this.model.isAlphaPremultiplied(), null);
        bufferedImage = this.bufferedImageOp.filter(bufferedImage, null);
        writableRaster2 = bufferedImage.getRaster();
        colorModel = bufferedImage.getColorModel();
        i = writableRaster2.getWidth();
        j = writableRaster2.getHeight();
        this.consumer.setDimensions(i, j);
        this.consumer.setColorModel(colorModel);
        if (colorModel instanceof DirectColorModel) {
          DataBufferInt dataBufferInt = (DataBufferInt)writableRaster2.getDataBuffer();
          this.consumer.setPixels(0, 0, i, j, colorModel, dataBufferInt.getData(), 0, i);
          break;
        } 
        if (colorModel instanceof IndexColorModel) {
          DataBufferByte dataBufferByte = (DataBufferByte)writableRaster2.getDataBuffer();
          this.consumer.setPixels(0, 0, i, j, colorModel, dataBufferByte.getData(), 0, i);
          break;
        } 
        throw new InternalError("Unknown color model " + colorModel);
    } 
    this.consumer.imageComplete(paramInt);
  }
  
  private final WritableRaster createDCMraster() {
    DirectColorModel directColorModel = (DirectColorModel)this.model;
    boolean bool = this.model.hasAlpha();
    int[] arrayOfInt = new int[3 + (bool ? 1 : 0)];
    arrayOfInt[0] = directColorModel.getRedMask();
    arrayOfInt[1] = directColorModel.getGreenMask();
    arrayOfInt[2] = directColorModel.getBlueMask();
    if (bool)
      arrayOfInt[3] = directColorModel.getAlphaMask(); 
    DataBufferInt dataBufferInt = new DataBufferInt(this.intPixels, this.width * this.height);
    return Raster.createPackedRaster(dataBufferInt, this.width, this.height, this.width, arrayOfInt, null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\BufferedImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */