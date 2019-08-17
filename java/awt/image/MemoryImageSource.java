package java.awt.image;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MemoryImageSource implements ImageProducer {
  int width;
  
  int height;
  
  ColorModel model;
  
  Object pixels;
  
  int pixeloffset;
  
  int pixelscan;
  
  Hashtable properties;
  
  Vector theConsumers = new Vector();
  
  boolean animating;
  
  boolean fullbuffers;
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt3, int paramInt4) { initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfByte, paramInt3, paramInt4, null); }
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt3, int paramInt4, Hashtable<?, ?> paramHashtable) { initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfByte, paramInt3, paramInt4, paramHashtable); }
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt3, int paramInt4) { initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfInt, paramInt3, paramInt4, null); }
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt3, int paramInt4, Hashtable<?, ?> paramHashtable) { initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfInt, paramInt3, paramInt4, paramHashtable); }
  
  private void initialize(int paramInt1, int paramInt2, ColorModel paramColorModel, Object paramObject, int paramInt3, int paramInt4, Hashtable paramHashtable) {
    this.width = paramInt1;
    this.height = paramInt2;
    this.model = paramColorModel;
    this.pixels = paramObject;
    this.pixeloffset = paramInt3;
    this.pixelscan = paramInt4;
    if (paramHashtable == null)
      paramHashtable = new Hashtable(); 
    this.properties = paramHashtable;
  }
  
  public MemoryImageSource(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4) { initialize(paramInt1, paramInt2, ColorModel.getRGBdefault(), paramArrayOfInt, paramInt3, paramInt4, null); }
  
  public MemoryImageSource(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, Hashtable<?, ?> paramHashtable) { initialize(paramInt1, paramInt2, ColorModel.getRGBdefault(), paramArrayOfInt, paramInt3, paramInt4, paramHashtable); }
  
  public void addConsumer(ImageConsumer paramImageConsumer) {
    if (this.theConsumers.contains(paramImageConsumer))
      return; 
    this.theConsumers.addElement(paramImageConsumer);
    try {
      initConsumer(paramImageConsumer);
      sendPixels(paramImageConsumer, 0, 0, this.width, this.height);
      if (isConsumer(paramImageConsumer)) {
        paramImageConsumer.imageComplete(this.animating ? 2 : 3);
        if (!this.animating && isConsumer(paramImageConsumer)) {
          paramImageConsumer.imageComplete(1);
          removeConsumer(paramImageConsumer);
        } 
      } 
    } catch (Exception exception) {
      if (isConsumer(paramImageConsumer))
        paramImageConsumer.imageComplete(1); 
    } 
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer) { return this.theConsumers.contains(paramImageConsumer); }
  
  public void removeConsumer(ImageConsumer paramImageConsumer) { this.theConsumers.removeElement(paramImageConsumer); }
  
  public void startProduction(ImageConsumer paramImageConsumer) { addConsumer(paramImageConsumer); }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  public void setAnimated(boolean paramBoolean) {
    this.animating = paramBoolean;
    if (!this.animating) {
      Enumeration enumeration = this.theConsumers.elements();
      while (enumeration.hasMoreElements()) {
        ImageConsumer imageConsumer = (ImageConsumer)enumeration.nextElement();
        imageConsumer.imageComplete(3);
        if (isConsumer(imageConsumer))
          imageConsumer.imageComplete(1); 
      } 
      this.theConsumers.removeAllElements();
    } 
  }
  
  public void setFullBufferUpdates(boolean paramBoolean) {
    if (this.fullbuffers == paramBoolean)
      return; 
    this.fullbuffers = paramBoolean;
    if (this.animating) {
      Enumeration enumeration = this.theConsumers.elements();
      while (enumeration.hasMoreElements()) {
        ImageConsumer imageConsumer = (ImageConsumer)enumeration.nextElement();
        imageConsumer.setHints(paramBoolean ? 6 : 1);
      } 
    } 
  }
  
  public void newPixels() { newPixels(0, 0, this.width, this.height, true); }
  
  public void newPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { newPixels(paramInt1, paramInt2, paramInt3, paramInt4, true); }
  
  public void newPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    if (this.animating) {
      if (this.fullbuffers) {
        paramInt1 = paramInt2 = 0;
        paramInt3 = this.width;
        paramInt4 = this.height;
      } else {
        if (paramInt1 < 0) {
          paramInt3 += paramInt1;
          paramInt1 = 0;
        } 
        if (paramInt1 + paramInt3 > this.width)
          paramInt3 = this.width - paramInt1; 
        if (paramInt2 < 0) {
          paramInt4 += paramInt2;
          paramInt2 = 0;
        } 
        if (paramInt2 + paramInt4 > this.height)
          paramInt4 = this.height - paramInt2; 
      } 
      if ((paramInt3 <= 0 || paramInt4 <= 0) && !paramBoolean)
        return; 
      Enumeration enumeration = this.theConsumers.elements();
      while (enumeration.hasMoreElements()) {
        ImageConsumer imageConsumer = (ImageConsumer)enumeration.nextElement();
        if (paramInt3 > 0 && paramInt4 > 0)
          sendPixels(imageConsumer, paramInt1, paramInt2, paramInt3, paramInt4); 
        if (paramBoolean && isConsumer(imageConsumer))
          imageConsumer.imageComplete(2); 
      } 
    } 
  }
  
  public void newPixels(byte[] paramArrayOfByte, ColorModel paramColorModel, int paramInt1, int paramInt2) {
    this.pixels = paramArrayOfByte;
    this.model = paramColorModel;
    this.pixeloffset = paramInt1;
    this.pixelscan = paramInt2;
    newPixels();
  }
  
  public void newPixels(int[] paramArrayOfInt, ColorModel paramColorModel, int paramInt1, int paramInt2) {
    this.pixels = paramArrayOfInt;
    this.model = paramColorModel;
    this.pixeloffset = paramInt1;
    this.pixelscan = paramInt2;
    newPixels();
  }
  
  private void initConsumer(ImageConsumer paramImageConsumer) {
    if (isConsumer(paramImageConsumer))
      paramImageConsumer.setDimensions(this.width, this.height); 
    if (isConsumer(paramImageConsumer))
      paramImageConsumer.setProperties(this.properties); 
    if (isConsumer(paramImageConsumer))
      paramImageConsumer.setColorModel(this.model); 
    if (isConsumer(paramImageConsumer))
      paramImageConsumer.setHints(this.animating ? (this.fullbuffers ? 6 : 1) : 30); 
  }
  
  private void sendPixels(ImageConsumer paramImageConsumer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = this.pixeloffset + this.pixelscan * paramInt2 + paramInt1;
    if (isConsumer(paramImageConsumer))
      if (this.pixels instanceof byte[]) {
        paramImageConsumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, this.model, (byte[])this.pixels, i, this.pixelscan);
      } else {
        paramImageConsumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, this.model, (int[])this.pixels, i, this.pixelscan);
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\MemoryImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */