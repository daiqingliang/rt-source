package sun.awt.image;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;
import sun.misc.SoftCache;

public class MultiResolutionToolkitImage extends ToolkitImage implements MultiResolutionImage {
  Image resolutionVariant;
  
  private static final int BITS_INFO = 56;
  
  public MultiResolutionToolkitImage(Image paramImage1, Image paramImage2) {
    super(paramImage1.getSource());
    this.resolutionVariant = paramImage2;
  }
  
  public Image getResolutionVariant(int paramInt1, int paramInt2) { return (paramInt1 <= getWidth() && paramInt2 <= getHeight()) ? this : this.resolutionVariant; }
  
  public Image getResolutionVariant() { return this.resolutionVariant; }
  
  public List<Image> getResolutionVariants() { return Arrays.asList(new Image[] { this, this.resolutionVariant }); }
  
  public static ImageObserver getResolutionVariantObserver(Image paramImage, ImageObserver paramImageObserver, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return getResolutionVariantObserver(paramImage, paramImageObserver, paramInt1, paramInt2, paramInt3, paramInt4, false); }
  
  public static ImageObserver getResolutionVariantObserver(Image paramImage, ImageObserver paramImageObserver, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    if (paramImageObserver == null)
      return null; 
    synchronized (ObserverCache.INSTANCE) {
      ImageObserver imageObserver = (ImageObserver)ObserverCache.INSTANCE.get(paramImageObserver);
      if (imageObserver == null) {
        imageObserver = ((paramImage2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5) -> {
            if ((paramInt1 & 0x39) != 0)
              paramInt4 = (paramInt4 + 1) / 2; 
            if ((paramInt1 & 0x3A) != 0)
              paramInt5 = (paramInt5 + 1) / 2; 
            if ((paramInt1 & 0x38) != 0) {
              paramInt2 /= 2;
              paramInt3 /= 2;
            } 
            if (paramBoolean)
              paramInt1 &= ((ToolkitImage)paramImage1).getImageRep().check(null); 
            return paramImageObserver.imageUpdate(paramImage1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
          });
        ObserverCache.INSTANCE.put(paramImageObserver, imageObserver);
      } 
      return imageObserver;
    } 
  }
  
  private static class ObserverCache {
    static final SoftCache INSTANCE = new SoftCache();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\MultiResolutionToolkitImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */