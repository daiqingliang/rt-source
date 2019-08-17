package java.awt.image;

import java.util.Hashtable;

public class ImageFilter implements ImageConsumer, Cloneable {
  protected ImageConsumer consumer;
  
  public ImageFilter getFilterInstance(ImageConsumer paramImageConsumer) {
    ImageFilter imageFilter = (ImageFilter)clone();
    imageFilter.consumer = paramImageConsumer;
    return imageFilter;
  }
  
  public void setDimensions(int paramInt1, int paramInt2) { this.consumer.setDimensions(paramInt1, paramInt2); }
  
  public void setProperties(Hashtable<?, ?> paramHashtable) {
    Hashtable hashtable = (Hashtable)paramHashtable.clone();
    Object object = hashtable.get("filters");
    if (object == null) {
      hashtable.put("filters", toString());
    } else if (object instanceof String) {
      hashtable.put("filters", (String)object + toString());
    } 
    this.consumer.setProperties(hashtable);
  }
  
  public void setColorModel(ColorModel paramColorModel) { this.consumer.setColorModel(paramColorModel); }
  
  public void setHints(int paramInt) { this.consumer.setHints(paramInt); }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) { this.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6); }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) { this.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6); }
  
  public void imageComplete(int paramInt) { this.consumer.imageComplete(paramInt); }
  
  public void resendTopDownLeftRight(ImageProducer paramImageProducer) { paramImageProducer.requestTopDownLeftRightResend(this); }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */