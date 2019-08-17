package sun.awt.image;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiResolutionCachedImage extends AbstractMultiResolutionImage {
  private final int baseImageWidth;
  
  private final int baseImageHeight;
  
  private final Dimension2D[] sizes;
  
  private final BiFunction<Integer, Integer, Image> mapper;
  
  private int availableInfo;
  
  public MultiResolutionCachedImage(int paramInt1, int paramInt2, BiFunction<Integer, Integer, Image> paramBiFunction) { this(paramInt1, paramInt2, new Dimension[] { new Dimension(paramInt1, paramInt2) }paramBiFunction); }
  
  public MultiResolutionCachedImage(int paramInt1, int paramInt2, Dimension2D[] paramArrayOfDimension2D, BiFunction<Integer, Integer, Image> paramBiFunction) {
    this.baseImageWidth = paramInt1;
    this.baseImageHeight = paramInt2;
    this.sizes = (paramArrayOfDimension2D == null) ? null : (Dimension2D[])Arrays.copyOf(paramArrayOfDimension2D, paramArrayOfDimension2D.length);
    this.mapper = paramBiFunction;
  }
  
  public Image getResolutionVariant(int paramInt1, int paramInt2) {
    ImageCache imageCache = ImageCache.getInstance();
    ImageCacheKey imageCacheKey = new ImageCacheKey(this, paramInt1, paramInt2);
    Image image = imageCache.getImage(imageCacheKey);
    if (image == null) {
      image = (Image)this.mapper.apply(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      imageCache.setImage(imageCacheKey, image);
    } 
    preload(image, this.availableInfo);
    return image;
  }
  
  public List<Image> getResolutionVariants() { return (List)Arrays.stream(this.sizes).map(paramDimension2D -> getResolutionVariant((int)paramDimension2D.getWidth(), (int)paramDimension2D.getHeight())).collect(Collectors.toList()); }
  
  public MultiResolutionCachedImage map(Function<Image, Image> paramFunction) { return new MultiResolutionCachedImage(this.baseImageWidth, this.baseImageHeight, this.sizes, (paramInteger1, paramInteger2) -> (Image)paramFunction.apply(getResolutionVariant(paramInteger1.intValue(), paramInteger2.intValue()))); }
  
  public int getWidth(ImageObserver paramImageObserver) {
    updateInfo(paramImageObserver, 1);
    return super.getWidth(paramImageObserver);
  }
  
  public int getHeight(ImageObserver paramImageObserver) {
    updateInfo(paramImageObserver, 2);
    return super.getHeight(paramImageObserver);
  }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver) {
    updateInfo(paramImageObserver, 4);
    return super.getProperty(paramString, paramImageObserver);
  }
  
  protected Image getBaseImage() { return getResolutionVariant(this.baseImageWidth, this.baseImageHeight); }
  
  private void updateInfo(ImageObserver paramImageObserver, int paramInt) { this.availableInfo |= ((paramImageObserver == null) ? 32 : paramInt); }
  
  private static int getInfo(Image paramImage) { return (paramImage instanceof ToolkitImage) ? ((ToolkitImage)paramImage).getImageRep().check((paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5) -> false) : 0; }
  
  private static void preload(Image paramImage, final int availableInfo) {
    if (paramInt != 0 && paramImage instanceof ToolkitImage)
      ((ToolkitImage)paramImage).preload(new ImageObserver() {
            int flags = availableInfo;
            
            public boolean imageUpdate(Image param1Image, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
              this.flags &= (param1Int1 ^ 0xFFFFFFFF);
              return (this.flags != 0 && (param1Int1 & 0xC0) == 0);
            }
          }); 
  }
  
  private static class ImageCacheKey implements ImageCache.PixelsKey {
    private final int pixelCount;
    
    private final int hash;
    
    private final int w;
    
    private final int h;
    
    private final Image baseImage;
    
    ImageCacheKey(Image param1Image, int param1Int1, int param1Int2) {
      this.baseImage = param1Image;
      this.w = param1Int1;
      this.h = param1Int2;
      this.pixelCount = param1Int1 * param1Int2;
      this.hash = hash();
    }
    
    public int getPixelCount() { return this.pixelCount; }
    
    private int hash() {
      null = this.baseImage.hashCode();
      null = 31 * null + this.w;
      return 31 * null + this.h;
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof ImageCacheKey) {
        ImageCacheKey imageCacheKey = (ImageCacheKey)param1Object;
        return (this.baseImage == imageCacheKey.baseImage && this.w == imageCacheKey.w && this.h == imageCacheKey.h);
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\MultiResolutionCachedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */