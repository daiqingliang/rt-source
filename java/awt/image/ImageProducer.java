package java.awt.image;

public interface ImageProducer {
  void addConsumer(ImageConsumer paramImageConsumer);
  
  boolean isConsumer(ImageConsumer paramImageConsumer);
  
  void removeConsumer(ImageConsumer paramImageConsumer);
  
  void startProduction(ImageConsumer paramImageConsumer);
  
  void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ImageProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */