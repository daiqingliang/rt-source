package sun.awt.image;

import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import sun.awt.AppContext;

public final class ImageCache {
  private final LinkedHashMap<PixelsKey, ImageSoftReference> map = new LinkedHashMap(16, 0.75F, true);
  
  private final int maxPixelCount;
  
  private int currentPixelCount = 0;
  
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  
  private final ReferenceQueue<Image> referenceQueue = new ReferenceQueue();
  
  public static ImageCache getInstance() { return (ImageCache)AppContext.getSoftReferenceValue(ImageCache.class, () -> new ImageCache()); }
  
  ImageCache(int paramInt) { this.maxPixelCount = paramInt; }
  
  ImageCache() { this(2097152); }
  
  public void flush() {
    this.lock.writeLock().lock();
    try {
      this.map.clear();
    } finally {
      this.lock.writeLock().unlock();
    } 
  }
  
  public Image getImage(PixelsKey paramPixelsKey) {
    ImageSoftReference imageSoftReference;
    this.lock.readLock().lock();
    try {
      imageSoftReference = (ImageSoftReference)this.map.get(paramPixelsKey);
    } finally {
      this.lock.readLock().unlock();
    } 
    return (imageSoftReference == null) ? null : (Image)imageSoftReference.get();
  }
  
  public void setImage(PixelsKey paramPixelsKey, Image paramImage) {
    this.lock.writeLock().lock();
    try {
      ImageSoftReference imageSoftReference = (ImageSoftReference)this.map.get(paramPixelsKey);
      if (imageSoftReference != null) {
        if (imageSoftReference.get() != null)
          return; 
        this.currentPixelCount -= paramPixelsKey.getPixelCount();
        this.map.remove(paramPixelsKey);
      } 
      int i = paramPixelsKey.getPixelCount();
      this.currentPixelCount += i;
      if (this.currentPixelCount > this.maxPixelCount)
        while ((imageSoftReference = (ImageSoftReference)this.referenceQueue.poll()) != null) {
          this.map.remove(imageSoftReference.key);
          this.currentPixelCount -= imageSoftReference.key.getPixelCount();
        }  
      if (this.currentPixelCount > this.maxPixelCount) {
        Iterator iterator = this.map.entrySet().iterator();
        while (this.currentPixelCount > this.maxPixelCount && iterator.hasNext()) {
          Map.Entry entry = (Map.Entry)iterator.next();
          iterator.remove();
          Image image = (Image)((ImageSoftReference)entry.getValue()).get();
          if (image != null)
            image.flush(); 
          this.currentPixelCount -= ((ImageSoftReference)entry.getValue()).key.getPixelCount();
        } 
      } 
      this.map.put(paramPixelsKey, new ImageSoftReference(paramPixelsKey, paramImage, this.referenceQueue));
    } finally {
      this.lock.writeLock().unlock();
    } 
  }
  
  private static class ImageSoftReference extends SoftReference<Image> {
    final ImageCache.PixelsKey key;
    
    ImageSoftReference(ImageCache.PixelsKey param1PixelsKey, Image param1Image, ReferenceQueue<? super Image> param1ReferenceQueue) {
      super(param1Image, param1ReferenceQueue);
      this.key = param1PixelsKey;
    }
  }
  
  public static interface PixelsKey {
    int getPixelCount();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImageCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */