package sun.awt.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class InputStreamImageSource implements ImageProducer, ImageFetchable {
  ImageConsumerQueue consumers;
  
  ImageDecoder decoder;
  
  ImageDecoder decoders;
  
  boolean awaitingFetch = false;
  
  abstract boolean checkSecurity(Object paramObject, boolean paramBoolean);
  
  int countConsumers(ImageConsumerQueue paramImageConsumerQueue) {
    byte b = 0;
    while (paramImageConsumerQueue != null) {
      b++;
      paramImageConsumerQueue = paramImageConsumerQueue.next;
    } 
    return b;
  }
  
  int countConsumers() {
    ImageDecoder imageDecoder = this.decoders;
    int i = countConsumers(this.consumers);
    while (imageDecoder != null) {
      i += countConsumers(imageDecoder.queue);
      imageDecoder = imageDecoder.next;
    } 
    return i;
  }
  
  public void addConsumer(ImageConsumer paramImageConsumer) { addConsumer(paramImageConsumer, false); }
  
  void printQueue(ImageConsumerQueue paramImageConsumerQueue, String paramString) {
    while (paramImageConsumerQueue != null) {
      System.out.println(paramString + paramImageConsumerQueue);
      paramImageConsumerQueue = paramImageConsumerQueue.next;
    } 
  }
  
  void printQueues(String paramString) {
    System.out.println(paramString + "[ -----------");
    printQueue(this.consumers, "  ");
    for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
      System.out.println("    " + imageDecoder);
      printQueue(imageDecoder.queue, "      ");
    } 
    System.out.println("----------- ]" + paramString);
  }
  
  void addConsumer(ImageConsumer paramImageConsumer, boolean paramBoolean) {
    checkSecurity(null, false);
    for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
      if (imageDecoder.isConsumer(paramImageConsumer))
        return; 
    } 
    ImageConsumerQueue imageConsumerQueue;
    for (imageConsumerQueue = this.consumers; imageConsumerQueue != null && imageConsumerQueue.consumer != paramImageConsumer; imageConsumerQueue = imageConsumerQueue.next);
    if (imageConsumerQueue == null) {
      imageConsumerQueue = new ImageConsumerQueue(this, paramImageConsumer);
      imageConsumerQueue.next = this.consumers;
      this.consumers = imageConsumerQueue;
    } else {
      if (!imageConsumerQueue.secure) {
        Object object = null;
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          object = securityManager.getSecurityContext(); 
        if (imageConsumerQueue.securityContext == null) {
          imageConsumerQueue.securityContext = object;
        } else if (!imageConsumerQueue.securityContext.equals(object)) {
          errorConsumer(imageConsumerQueue, false);
          throw new SecurityException("Applets are trading image data!");
        } 
      } 
      imageConsumerQueue.interested = true;
    } 
    if (paramBoolean && this.decoder == null)
      startProduction(); 
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer) {
    for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
      if (imageDecoder.isConsumer(paramImageConsumer))
        return true; 
    } 
    return ImageConsumerQueue.isConsumer(this.consumers, paramImageConsumer);
  }
  
  private void errorAllConsumers(ImageConsumerQueue paramImageConsumerQueue, boolean paramBoolean) {
    while (paramImageConsumerQueue != null) {
      if (paramImageConsumerQueue.interested)
        errorConsumer(paramImageConsumerQueue, paramBoolean); 
      paramImageConsumerQueue = paramImageConsumerQueue.next;
    } 
  }
  
  private void errorConsumer(ImageConsumerQueue paramImageConsumerQueue, boolean paramBoolean) {
    paramImageConsumerQueue.consumer.imageComplete(1);
    if (paramBoolean && paramImageConsumerQueue.consumer instanceof ImageRepresentation)
      ((ImageRepresentation)paramImageConsumerQueue.consumer).image.flush(); 
    removeConsumer(paramImageConsumerQueue.consumer);
  }
  
  public void removeConsumer(ImageConsumer paramImageConsumer) {
    for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next)
      imageDecoder.removeConsumer(paramImageConsumer); 
    this.consumers = ImageConsumerQueue.removeConsumer(this.consumers, paramImageConsumer, false);
  }
  
  public void startProduction(ImageConsumer paramImageConsumer) { addConsumer(paramImageConsumer, true); }
  
  private void startProduction() {
    if (!this.awaitingFetch)
      if (ImageFetcher.add(this)) {
        this.awaitingFetch = true;
      } else {
        ImageConsumerQueue imageConsumerQueue = this.consumers;
        this.consumers = null;
        errorAllConsumers(imageConsumerQueue, false);
      }  
  }
  
  private void stopProduction() {
    if (this.awaitingFetch) {
      ImageFetcher.remove(this);
      this.awaitingFetch = false;
    } 
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  protected abstract ImageDecoder getDecoder();
  
  protected ImageDecoder decoderForType(InputStream paramInputStream, String paramString) { return null; }
  
  protected ImageDecoder getDecoder(InputStream paramInputStream) {
    if (!paramInputStream.markSupported())
      paramInputStream = new BufferedInputStream(paramInputStream); 
    try {
      paramInputStream.mark(8);
      int i = paramInputStream.read();
      int j = paramInputStream.read();
      int k = paramInputStream.read();
      int m = paramInputStream.read();
      int n = paramInputStream.read();
      int i1 = paramInputStream.read();
      int i2 = paramInputStream.read();
      int i3 = paramInputStream.read();
      paramInputStream.reset();
      paramInputStream.mark(-1);
      if (i == 71 && j == 73 && k == 70 && m == 56)
        return new GifImageDecoder(this, paramInputStream); 
      if (i == 255 && j == 216 && k == 255)
        return new JPEGImageDecoder(this, paramInputStream); 
      if (i == 35 && j == 100 && k == 101 && m == 102)
        return new XbmImageDecoder(this, paramInputStream); 
      if (i == 137 && j == 80 && k == 78 && m == 71 && n == 13 && i1 == 10 && i2 == 26 && i3 == 10)
        return new PNGImageDecoder(this, paramInputStream); 
    } catch (IOException iOException) {}
    return null;
  }
  
  public void doFetch() {
    synchronized (this) {
      if (this.consumers == null) {
        this.awaitingFetch = false;
        return;
      } 
    } 
    imageDecoder = getDecoder();
    if (imageDecoder == null) {
      badDecoder();
    } else {
      setDecoder(imageDecoder);
      try {
        imageDecoder.produceImage();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } catch (ImageFormatException imageFormatException) {
        imageFormatException.printStackTrace();
      } finally {
        removeDecoder(imageDecoder);
        if (Thread.currentThread().isInterrupted() || !Thread.currentThread().isAlive()) {
          errorAllConsumers(imageDecoder.queue, true);
        } else {
          errorAllConsumers(imageDecoder.queue, false);
        } 
      } 
    } 
  }
  
  private void badDecoder() {
    ImageConsumerQueue imageConsumerQueue;
    synchronized (this) {
      imageConsumerQueue = this.consumers;
      this.consumers = null;
      this.awaitingFetch = false;
    } 
    errorAllConsumers(imageConsumerQueue, false);
  }
  
  private void setDecoder(ImageDecoder paramImageDecoder) {
    ImageConsumerQueue imageConsumerQueue;
    synchronized (this) {
      paramImageDecoder.next = this.decoders;
      this.decoders = paramImageDecoder;
      this.decoder = paramImageDecoder;
      imageConsumerQueue = this.consumers;
      paramImageDecoder.queue = imageConsumerQueue;
      this.consumers = null;
      this.awaitingFetch = false;
    } 
    while (imageConsumerQueue != null) {
      if (imageConsumerQueue.interested && !checkSecurity(imageConsumerQueue.securityContext, true))
        errorConsumer(imageConsumerQueue, false); 
      imageConsumerQueue = imageConsumerQueue.next;
    } 
  }
  
  private void removeDecoder(ImageDecoder paramImageDecoder) {
    doneDecoding(paramImageDecoder);
    ImageDecoder imageDecoder1 = null;
    for (ImageDecoder imageDecoder2 = this.decoders; imageDecoder2 != null; imageDecoder2 = imageDecoder2.next) {
      if (imageDecoder2 == paramImageDecoder) {
        if (imageDecoder1 == null) {
          this.decoders = imageDecoder2.next;
          break;
        } 
        imageDecoder1.next = imageDecoder2.next;
        break;
      } 
      imageDecoder1 = imageDecoder2;
    } 
  }
  
  void doneDecoding(ImageDecoder paramImageDecoder) {
    if (this.decoder == paramImageDecoder) {
      this.decoder = null;
      if (this.consumers != null)
        startProduction(); 
    } 
  }
  
  void latchConsumers(ImageDecoder paramImageDecoder) { doneDecoding(paramImageDecoder); }
  
  void flush() { this.decoder = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\InputStreamImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */