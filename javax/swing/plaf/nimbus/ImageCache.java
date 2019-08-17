package javax.swing.plaf.nimbus;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ImageCache {
  private final LinkedHashMap<Integer, PixelCountSoftReference> map = new LinkedHashMap(16, 0.75F, true);
  
  private final int maxPixelCount = 2097152;
  
  private final int maxSingleImagePixelSize = 90000;
  
  private int currentPixelCount = 0;
  
  private ReadWriteLock lock = new ReentrantReadWriteLock();
  
  private ReferenceQueue<Image> referenceQueue = new ReferenceQueue();
  
  private static final ImageCache instance = new ImageCache();
  
  static ImageCache getInstance() { return instance; }
  
  public ImageCache() {}
  
  public ImageCache(int paramInt1, int paramInt2) {}
  
  public void flush() {
    this.lock.readLock().lock();
    try {
      this.map.clear();
    } finally {
      this.lock.readLock().unlock();
    } 
  }
  
  public boolean isImageCachable(int paramInt1, int paramInt2) { return (paramInt1 * paramInt2 < this.maxSingleImagePixelSize); }
  
  public Image getImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs) {
    this.lock.readLock().lock();
    try {
      PixelCountSoftReference pixelCountSoftReference = (PixelCountSoftReference)this.map.get(Integer.valueOf(hash(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs)));
      if (pixelCountSoftReference != null && pixelCountSoftReference.equals(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs))
        return (Image)pixelCountSoftReference.get(); 
      return null;
    } finally {
      this.lock.readLock().unlock();
    } 
  }
  
  public boolean setImage(Image paramImage, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs) {
    if (!isImageCachable(paramInt1, paramInt2))
      return false; 
    int i = hash(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs);
    this.lock.writeLock().lock();
    try {
      PixelCountSoftReference pixelCountSoftReference = (PixelCountSoftReference)this.map.get(Integer.valueOf(i));
      if (pixelCountSoftReference != null && pixelCountSoftReference.get() == paramImage)
        return true; 
      if (pixelCountSoftReference != null) {
        this.currentPixelCount -= pixelCountSoftReference.pixelCount;
        this.map.remove(Integer.valueOf(i));
      } 
      int j = paramImage.getWidth(null) * paramImage.getHeight(null);
      this.currentPixelCount += j;
      if (this.currentPixelCount > this.maxPixelCount)
        while ((pixelCountSoftReference = (PixelCountSoftReference)this.referenceQueue.poll()) != null) {
          this.map.remove(Integer.valueOf(pixelCountSoftReference.hash));
          this.currentPixelCount -= pixelCountSoftReference.pixelCount;
        }  
      if (this.currentPixelCount > this.maxPixelCount) {
        Iterator iterator = this.map.entrySet().iterator();
        while (this.currentPixelCount > this.maxPixelCount && iterator.hasNext()) {
          Map.Entry entry = (Map.Entry)iterator.next();
          iterator.remove();
          Image image = (Image)((PixelCountSoftReference)entry.getValue()).get();
          if (image != null)
            image.flush(); 
          this.currentPixelCount -= ((PixelCountSoftReference)entry.getValue()).pixelCount;
        } 
      } 
      this.map.put(Integer.valueOf(i), new PixelCountSoftReference(paramImage, this.referenceQueue, j, i, paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs));
      return true;
    } finally {
      this.lock.writeLock().unlock();
    } 
  }
  
  private int hash(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs) {
    null = (paramGraphicsConfiguration != null) ? paramGraphicsConfiguration.hashCode() : 0;
    null = 31 * null + paramInt1;
    null = 31 * null + paramInt2;
    return 31 * null + Arrays.deepHashCode(paramVarArgs);
  }
  
  private static class PixelCountSoftReference extends SoftReference<Image> {
    private final int pixelCount;
    
    private final int hash;
    
    private final GraphicsConfiguration config;
    
    private final int w;
    
    private final int h;
    
    private final Object[] args;
    
    public PixelCountSoftReference(Image param1Image, ReferenceQueue<? super Image> param1ReferenceQueue, int param1Int1, int param1Int2, GraphicsConfiguration param1GraphicsConfiguration, int param1Int3, int param1Int4, Object[] param1ArrayOfObject) {
      super(param1Image, param1ReferenceQueue);
      this.pixelCount = param1Int1;
      this.hash = param1Int2;
      this.config = param1GraphicsConfiguration;
      this.w = param1Int3;
      this.h = param1Int4;
      this.args = param1ArrayOfObject;
    }
    
    public boolean equals(GraphicsConfiguration param1GraphicsConfiguration, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) { return (param1GraphicsConfiguration == this.config && param1Int1 == this.w && param1Int2 == this.h && Arrays.equals(param1ArrayOfObject, this.args)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ImageCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */