package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

class GifFrame {
  private static final boolean verbose = false;
  
  private static IndexColorModel trans_model;
  
  static final int DISPOSAL_NONE = 0;
  
  static final int DISPOSAL_SAVE = 1;
  
  static final int DISPOSAL_BGCOLOR = 2;
  
  static final int DISPOSAL_PREVIOUS = 3;
  
  GifImageDecoder decoder;
  
  int disposal_method;
  
  int delay;
  
  IndexColorModel model;
  
  int x;
  
  int y;
  
  int width;
  
  int height;
  
  boolean initialframe;
  
  public GifFrame(GifImageDecoder paramGifImageDecoder, int paramInt1, int paramInt2, boolean paramBoolean, IndexColorModel paramIndexColorModel, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    this.decoder = paramGifImageDecoder;
    this.disposal_method = paramInt1;
    this.delay = paramInt2;
    this.model = paramIndexColorModel;
    this.initialframe = paramBoolean;
    this.x = paramInt3;
    this.y = paramInt4;
    this.width = paramInt5;
    this.height = paramInt6;
  }
  
  private void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) { this.decoder.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6); }
  
  public boolean dispose() {
    byte[] arrayOfByte2;
    byte b;
    IndexColorModel indexColorModel;
    byte[] arrayOfByte1;
    if (this.decoder.imageComplete(2, false) == 0)
      return false; 
    if (this.delay > 0) {
      try {
        Thread.sleep(this.delay);
      } catch (InterruptedException interruptedException) {
        return false;
      } 
    } else {
      Thread.yield();
    } 
    int i = this.decoder.global_width;
    int j = this.decoder.global_height;
    if (this.x < 0) {
      this.width += this.x;
      this.x = 0;
    } 
    if (this.x + this.width > i)
      this.width = i - this.x; 
    if (this.width <= 0) {
      this.disposal_method = 0;
    } else {
      if (this.y < 0) {
        this.height += this.y;
        this.y = 0;
      } 
      if (this.y + this.height > j)
        this.height = j - this.y; 
      if (this.height <= 0)
        this.disposal_method = 0; 
    } 
    switch (this.disposal_method) {
      case 3:
        arrayOfByte1 = this.decoder.saved_image;
        indexColorModel = this.decoder.saved_model;
        if (arrayOfByte1 != null)
          setPixels(this.x, this.y, this.width, this.height, indexColorModel, arrayOfByte1, this.y * i + this.x, i); 
        break;
      case 2:
        if (this.model.getTransparentPixel() < 0) {
          this.model = trans_model;
          if (this.model == null) {
            this.model = new IndexColorModel(8, 1, new byte[4], 0, true);
            trans_model = this.model;
          } 
          b = 0;
        } else {
          b = (byte)this.model.getTransparentPixel();
        } 
        arrayOfByte2 = new byte[this.width];
        if (b != 0)
          for (byte b1 = 0; b1 < this.width; b1++)
            arrayOfByte2[b1] = b;  
        if (this.decoder.saved_image != null)
          for (byte b1 = 0; b1 < i * j; b1++)
            this.decoder.saved_image[b1] = b;  
        setPixels(this.x, this.y, this.width, this.height, this.model, arrayOfByte2, 0, 0);
        break;
      case 1:
        this.decoder.saved_model = this.model;
        break;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\GifFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */