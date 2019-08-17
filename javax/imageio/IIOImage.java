package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;

public class IIOImage {
  protected RenderedImage image;
  
  protected Raster raster;
  
  protected List<? extends BufferedImage> thumbnails = null;
  
  protected IIOMetadata metadata;
  
  public IIOImage(RenderedImage paramRenderedImage, List<? extends BufferedImage> paramList, IIOMetadata paramIIOMetadata) {
    if (paramRenderedImage == null)
      throw new IllegalArgumentException("image == null!"); 
    this.image = paramRenderedImage;
    this.raster = null;
    this.thumbnails = paramList;
    this.metadata = paramIIOMetadata;
  }
  
  public IIOImage(Raster paramRaster, List<? extends BufferedImage> paramList, IIOMetadata paramIIOMetadata) {
    if (paramRaster == null)
      throw new IllegalArgumentException("raster == null!"); 
    this.raster = paramRaster;
    this.image = null;
    this.thumbnails = paramList;
    this.metadata = paramIIOMetadata;
  }
  
  public RenderedImage getRenderedImage() {
    synchronized (this) {
      return this.image;
    } 
  }
  
  public void setRenderedImage(RenderedImage paramRenderedImage) {
    synchronized (this) {
      if (paramRenderedImage == null)
        throw new IllegalArgumentException("image == null!"); 
      this.image = paramRenderedImage;
      this.raster = null;
    } 
  }
  
  public boolean hasRaster() {
    synchronized (this) {
      return (this.raster != null);
    } 
  }
  
  public Raster getRaster() {
    synchronized (this) {
      return this.raster;
    } 
  }
  
  public void setRaster(Raster paramRaster) {
    synchronized (this) {
      if (paramRaster == null)
        throw new IllegalArgumentException("raster == null!"); 
      this.raster = paramRaster;
      this.image = null;
    } 
  }
  
  public int getNumThumbnails() { return (this.thumbnails == null) ? 0 : this.thumbnails.size(); }
  
  public BufferedImage getThumbnail(int paramInt) {
    if (this.thumbnails == null)
      throw new IndexOutOfBoundsException("No thumbnails available!"); 
    return (BufferedImage)this.thumbnails.get(paramInt);
  }
  
  public List<? extends BufferedImage> getThumbnails() { return this.thumbnails; }
  
  public void setThumbnails(List<? extends BufferedImage> paramList) { this.thumbnails = paramList; }
  
  public IIOMetadata getMetadata() { return this.metadata; }
  
  public void setMetadata(IIOMetadata paramIIOMetadata) { this.metadata = paramIIOMetadata; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\IIOImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */