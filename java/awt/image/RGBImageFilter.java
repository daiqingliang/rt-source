package java.awt.image;

public abstract class RGBImageFilter extends ImageFilter {
  protected ColorModel origmodel;
  
  protected ColorModel newmodel;
  
  protected boolean canFilterIndexColorModel;
  
  public void setColorModel(ColorModel paramColorModel) {
    if (this.canFilterIndexColorModel && paramColorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = filterIndexColorModel((IndexColorModel)paramColorModel);
      substituteColorModel(paramColorModel, indexColorModel);
      this.consumer.setColorModel(indexColorModel);
    } else {
      this.consumer.setColorModel(ColorModel.getRGBdefault());
    } 
  }
  
  public void substituteColorModel(ColorModel paramColorModel1, ColorModel paramColorModel2) {
    this.origmodel = paramColorModel1;
    this.newmodel = paramColorModel2;
  }
  
  public IndexColorModel filterIndexColorModel(IndexColorModel paramIndexColorModel) {
    int i = paramIndexColorModel.getMapSize();
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    byte[] arrayOfByte4 = new byte[i];
    paramIndexColorModel.getReds(arrayOfByte1);
    paramIndexColorModel.getGreens(arrayOfByte2);
    paramIndexColorModel.getBlues(arrayOfByte3);
    paramIndexColorModel.getAlphas(arrayOfByte4);
    int j = paramIndexColorModel.getTransparentPixel();
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      int k = filterRGB(-1, -1, paramIndexColorModel.getRGB(b));
      arrayOfByte4[b] = (byte)(k >> 24);
      if (arrayOfByte4[b] != -1 && b != j)
        bool = true; 
      arrayOfByte1[b] = (byte)(k >> 16);
      arrayOfByte2[b] = (byte)(k >> 8);
      arrayOfByte3[b] = (byte)(k >> 0);
    } 
    return bool ? new IndexColorModel(paramIndexColorModel.getPixelSize(), i, arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4) : new IndexColorModel(paramIndexColorModel.getPixelSize(), i, arrayOfByte1, arrayOfByte2, arrayOfByte3, j);
  }
  
  public void filterRGBPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int i = paramInt5;
    for (int j = 0; j < paramInt4; j++) {
      for (int k = 0; k < paramInt3; k++) {
        paramArrayOfInt[i] = filterRGB(paramInt1 + k, paramInt2 + j, paramArrayOfInt[i]);
        i++;
      } 
      i += paramInt6 - paramInt3;
    } 
    this.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, ColorModel.getRGBdefault(), paramArrayOfInt, paramInt5, paramInt6);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    if (paramColorModel == this.origmodel) {
      this.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, this.newmodel, paramArrayOfByte, paramInt5, paramInt6);
    } else {
      int[] arrayOfInt = new int[paramInt3];
      int i = paramInt5;
      for (int j = 0; j < paramInt4; j++) {
        for (byte b = 0; b < paramInt3; b++) {
          arrayOfInt[b] = paramColorModel.getRGB(paramArrayOfByte[i] & 0xFF);
          i++;
        } 
        i += paramInt6 - paramInt3;
        filterRGBPixels(paramInt1, paramInt2 + j, paramInt3, 1, arrayOfInt, 0, paramInt3);
      } 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    if (paramColorModel == this.origmodel) {
      this.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, this.newmodel, paramArrayOfInt, paramInt5, paramInt6);
    } else {
      int[] arrayOfInt = new int[paramInt3];
      int i = paramInt5;
      for (int j = 0; j < paramInt4; j++) {
        for (byte b = 0; b < paramInt3; b++) {
          arrayOfInt[b] = paramColorModel.getRGB(paramArrayOfInt[i]);
          i++;
        } 
        i += paramInt6 - paramInt3;
        filterRGBPixels(paramInt1, paramInt2 + j, paramInt3, 1, arrayOfInt, 0, paramInt3);
      } 
    } 
  }
  
  public abstract int filterRGB(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\RGBImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */