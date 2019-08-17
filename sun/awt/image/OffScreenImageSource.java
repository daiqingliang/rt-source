package sun.awt.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class OffScreenImageSource implements ImageProducer {
  BufferedImage image;
  
  int width;
  
  int height;
  
  Hashtable properties;
  
  private ImageConsumer theConsumer;
  
  public OffScreenImageSource(BufferedImage paramBufferedImage, Hashtable paramHashtable) {
    this.image = paramBufferedImage;
    if (paramHashtable != null) {
      this.properties = paramHashtable;
    } else {
      this.properties = new Hashtable();
    } 
    this.width = paramBufferedImage.getWidth();
    this.height = paramBufferedImage.getHeight();
  }
  
  public OffScreenImageSource(BufferedImage paramBufferedImage) { this(paramBufferedImage, null); }
  
  public void addConsumer(ImageConsumer paramImageConsumer) {
    this.theConsumer = paramImageConsumer;
    produce();
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer) { return (paramImageConsumer == this.theConsumer); }
  
  public void removeConsumer(ImageConsumer paramImageConsumer) {
    if (this.theConsumer == paramImageConsumer)
      this.theConsumer = null; 
  }
  
  public void startProduction(ImageConsumer paramImageConsumer) { addConsumer(paramImageConsumer); }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  private void sendPixels() {
    ColorModel colorModel = this.image.getColorModel();
    WritableRaster writableRaster = this.image.getRaster();
    int i = writableRaster.getNumDataElements();
    int j = writableRaster.getDataBuffer().getDataType();
    int[] arrayOfInt = new int[this.width * i];
    boolean bool = true;
    if (colorModel instanceof java.awt.image.IndexColorModel) {
      byte[] arrayOfByte = new byte[this.width];
      this.theConsumer.setColorModel(colorModel);
      if (writableRaster instanceof ByteComponentRaster) {
        bool = false;
        for (byte b = 0; b < this.height; b++) {
          writableRaster.getDataElements(0, b, this.width, 1, arrayOfByte);
          this.theConsumer.setPixels(0, b, this.width, 1, colorModel, arrayOfByte, 0, this.width);
        } 
      } else if (writableRaster instanceof BytePackedRaster) {
        bool = false;
        for (byte b = 0; b < this.height; b++) {
          writableRaster.getPixels(0, b, this.width, 1, arrayOfInt);
          for (byte b1 = 0; b1 < this.width; b1++)
            arrayOfByte[b1] = (byte)arrayOfInt[b1]; 
          this.theConsumer.setPixels(0, b, this.width, 1, colorModel, arrayOfByte, 0, this.width);
        } 
      } else if (j == 2 || j == 3) {
        bool = false;
        for (byte b = 0; b < this.height; b++) {
          writableRaster.getPixels(0, b, this.width, 1, arrayOfInt);
          this.theConsumer.setPixels(0, b, this.width, 1, colorModel, arrayOfInt, 0, this.width);
        } 
      } 
    } else if (colorModel instanceof java.awt.image.DirectColorModel) {
      byte b3;
      short[] arrayOfShort;
      byte b2;
      byte[] arrayOfByte;
      byte b1;
      this.theConsumer.setColorModel(colorModel);
      bool = false;
      switch (j) {
        case 3:
          for (b1 = 0; b1 < this.height; b1++) {
            writableRaster.getDataElements(0, b1, this.width, 1, arrayOfInt);
            this.theConsumer.setPixels(0, b1, this.width, 1, colorModel, arrayOfInt, 0, this.width);
          } 
          break;
        case 0:
          arrayOfByte = new byte[this.width];
          for (b2 = 0; b2 < this.height; b2++) {
            writableRaster.getDataElements(0, b2, this.width, 1, arrayOfByte);
            for (byte b = 0; b < this.width; b++)
              arrayOfInt[b] = arrayOfByte[b] & 0xFF; 
            this.theConsumer.setPixels(0, b2, this.width, 1, colorModel, arrayOfInt, 0, this.width);
          } 
          break;
        case 1:
          arrayOfShort = new short[this.width];
          for (b3 = 0; b3 < this.height; b3++) {
            writableRaster.getDataElements(0, b3, this.width, 1, arrayOfShort);
            for (byte b = 0; b < this.width; b++)
              arrayOfInt[b] = arrayOfShort[b] & 0xFFFF; 
            this.theConsumer.setPixels(0, b3, this.width, 1, colorModel, arrayOfInt, 0, this.width);
          } 
          break;
        default:
          bool = true;
          break;
      } 
    } 
    if (bool) {
      ColorModel colorModel1 = ColorModel.getRGBdefault();
      this.theConsumer.setColorModel(colorModel1);
      for (byte b = 0; b < this.height; b++) {
        for (byte b1 = 0; b1 < this.width; b1++)
          arrayOfInt[b1] = this.image.getRGB(b1, b); 
        this.theConsumer.setPixels(0, b, this.width, 1, colorModel1, arrayOfInt, 0, this.width);
      } 
    } 
  }
  
  private void produce() {
    try {
      this.theConsumer.setDimensions(this.image.getWidth(), this.image.getHeight());
      this.theConsumer.setProperties(this.properties);
      sendPixels();
      this.theConsumer.imageComplete(2);
      this.theConsumer.imageComplete(3);
    } catch (NullPointerException nullPointerException) {
      if (this.theConsumer != null)
        this.theConsumer.imageComplete(1); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\OffScreenImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */