package java.awt.image;

import java.util.Hashtable;

public class FilteredImageSource implements ImageProducer {
  ImageProducer src;
  
  ImageFilter filter;
  
  private Hashtable proxies;
  
  public FilteredImageSource(ImageProducer paramImageProducer, ImageFilter paramImageFilter) {
    this.src = paramImageProducer;
    this.filter = paramImageFilter;
  }
  
  public void addConsumer(ImageConsumer paramImageConsumer) {
    if (this.proxies == null)
      this.proxies = new Hashtable(); 
    if (!this.proxies.containsKey(paramImageConsumer)) {
      ImageFilter imageFilter = this.filter.getFilterInstance(paramImageConsumer);
      this.proxies.put(paramImageConsumer, imageFilter);
      this.src.addConsumer(imageFilter);
    } 
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer) { return (this.proxies != null && this.proxies.containsKey(paramImageConsumer)); }
  
  public void removeConsumer(ImageConsumer paramImageConsumer) {
    if (this.proxies != null) {
      ImageFilter imageFilter = (ImageFilter)this.proxies.get(paramImageConsumer);
      if (imageFilter != null) {
        this.src.removeConsumer(imageFilter);
        this.proxies.remove(paramImageConsumer);
        if (this.proxies.isEmpty())
          this.proxies = null; 
      } 
    } 
  }
  
  public void startProduction(ImageConsumer paramImageConsumer) {
    if (this.proxies == null)
      this.proxies = new Hashtable(); 
    ImageFilter imageFilter = (ImageFilter)this.proxies.get(paramImageConsumer);
    if (imageFilter == null) {
      imageFilter = this.filter.getFilterInstance(paramImageConsumer);
      this.proxies.put(paramImageConsumer, imageFilter);
    } 
    this.src.startProduction(imageFilter);
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {
    if (this.proxies != null) {
      ImageFilter imageFilter = (ImageFilter)this.proxies.get(paramImageConsumer);
      if (imageFilter != null)
        imageFilter.resendTopDownLeftRight(this.src); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\FilteredImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */