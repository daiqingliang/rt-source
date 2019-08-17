package sun.awt.image;

import java.awt.image.ImageConsumer;

class ImageConsumerQueue {
  ImageConsumerQueue next;
  
  ImageConsumer consumer;
  
  boolean interested;
  
  Object securityContext;
  
  boolean secure;
  
  static ImageConsumerQueue removeConsumer(ImageConsumerQueue paramImageConsumerQueue, ImageConsumer paramImageConsumer, boolean paramBoolean) {
    ImageConsumerQueue imageConsumerQueue1 = null;
    for (ImageConsumerQueue imageConsumerQueue2 = paramImageConsumerQueue; imageConsumerQueue2 != null; imageConsumerQueue2 = imageConsumerQueue2.next) {
      if (imageConsumerQueue2.consumer == paramImageConsumer) {
        if (imageConsumerQueue1 == null) {
          paramImageConsumerQueue = imageConsumerQueue2.next;
        } else {
          imageConsumerQueue1.next = imageConsumerQueue2.next;
        } 
        imageConsumerQueue2.interested = paramBoolean;
        break;
      } 
      imageConsumerQueue1 = imageConsumerQueue2;
    } 
    return paramImageConsumerQueue;
  }
  
  static boolean isConsumer(ImageConsumerQueue paramImageConsumerQueue, ImageConsumer paramImageConsumer) {
    for (ImageConsumerQueue imageConsumerQueue = paramImageConsumerQueue; imageConsumerQueue != null; imageConsumerQueue = imageConsumerQueue.next) {
      if (imageConsumerQueue.consumer == paramImageConsumer)
        return true; 
    } 
    return false;
  }
  
  ImageConsumerQueue(InputStreamImageSource paramInputStreamImageSource, ImageConsumer paramImageConsumer) {
    this.consumer = paramImageConsumer;
    this.interested = true;
    if (paramImageConsumer instanceof ImageRepresentation) {
      ImageRepresentation imageRepresentation = (ImageRepresentation)paramImageConsumer;
      if (imageRepresentation.image.source != paramInputStreamImageSource)
        throw new SecurityException("ImageRep added to wrong image source"); 
      this.secure = true;
    } else {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        this.securityContext = securityManager.getSecurityContext();
      } else {
        this.securityContext = null;
      } 
    } 
  }
  
  public String toString() { return "[" + this.consumer + ", " + (this.interested ? "" : "not ") + "interested" + ((this.securityContext != null) ? (", " + this.securityContext) : "") + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImageConsumerQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */