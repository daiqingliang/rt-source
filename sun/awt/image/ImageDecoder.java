package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public abstract class ImageDecoder {
  InputStreamImageSource source;
  
  InputStream input;
  
  Thread feeder;
  
  protected boolean aborted;
  
  protected boolean finished;
  
  ImageConsumerQueue queue;
  
  ImageDecoder next;
  
  public ImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream) {
    this.source = paramInputStreamImageSource;
    this.input = paramInputStream;
    this.feeder = Thread.currentThread();
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer) { return ImageConsumerQueue.isConsumer(this.queue, paramImageConsumer); }
  
  public void removeConsumer(ImageConsumer paramImageConsumer) {
    this.queue = ImageConsumerQueue.removeConsumer(this.queue, paramImageConsumer, false);
    if (!this.finished && this.queue == null)
      abort(); 
  }
  
  protected ImageConsumerQueue nextConsumer(ImageConsumerQueue paramImageConsumerQueue) {
    synchronized (this.source) {
      if (this.aborted)
        return null; 
      for (paramImageConsumerQueue = (paramImageConsumerQueue == null) ? this.queue : paramImageConsumerQueue.next; paramImageConsumerQueue != null; paramImageConsumerQueue = paramImageConsumerQueue.next) {
        if (paramImageConsumerQueue.interested)
          return paramImageConsumerQueue; 
      } 
    } 
    return null;
  }
  
  protected int setDimensions(int paramInt1, int paramInt2) {
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.setDimensions(paramInt1, paramInt2); 
    return b;
  }
  
  protected int setProperties(Hashtable paramHashtable) {
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.setProperties(paramHashtable); 
    return b;
  }
  
  protected int setColorModel(ColorModel paramColorModel) {
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.setColorModel(paramColorModel); 
    return b;
  }
  
  protected int setHints(int paramInt) {
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.setHints(paramInt); 
    return b;
  }
  
  protected void headerComplete() { this.feeder.setPriority(3); }
  
  protected int setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    this.source.latchConsumers(this);
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6); 
    return b;
  }
  
  protected int setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    this.source.latchConsumers(this);
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6); 
    return b;
  }
  
  protected int imageComplete(int paramInt, boolean paramBoolean) {
    this.source.latchConsumers(this);
    if (paramBoolean) {
      this.finished = true;
      this.source.doneDecoding(this);
    } 
    ImageConsumerQueue imageConsumerQueue = null;
    byte b;
    for (b = 0; (imageConsumerQueue = nextConsumer(imageConsumerQueue)) != null; b++)
      imageConsumerQueue.consumer.imageComplete(paramInt); 
    return b;
  }
  
  public abstract void produceImage();
  
  public void abort() {
    this.aborted = true;
    this.source.doneDecoding(this);
    close();
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ImageDecoder.this.feeder.interrupt();
            return null;
          }
        });
  }
  
  public void close() {
    if (this.input != null)
      try {
        this.input.close();
      } catch (IOException iOException) {} 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */