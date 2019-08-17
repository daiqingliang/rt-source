package java.awt;

import java.awt.image.ImageObserver;
import java.io.Serializable;

class ImageMediaEntry extends MediaEntry implements ImageObserver, Serializable {
  Image image;
  
  int width;
  
  int height;
  
  private static final long serialVersionUID = 4739377000350280650L;
  
  ImageMediaEntry(MediaTracker paramMediaTracker, Image paramImage, int paramInt1, int paramInt2, int paramInt3) {
    super(paramMediaTracker, paramInt1);
    this.image = paramImage;
    this.width = paramInt2;
    this.height = paramInt3;
  }
  
  boolean matches(Image paramImage, int paramInt1, int paramInt2) { return (this.image == paramImage && this.width == paramInt1 && this.height == paramInt2); }
  
  Object getMedia() { return this.image; }
  
  int getStatus(boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean2) {
      int i = this.tracker.target.checkImage(this.image, this.width, this.height, null);
      int j = parseflags(i);
      if (j == 0) {
        if ((this.status & 0xC) != 0)
          setStatus(2); 
      } else if (j != this.status) {
        setStatus(j);
      } 
    } 
    return super.getStatus(paramBoolean1, paramBoolean2);
  }
  
  void startLoad() {
    if (this.tracker.target.prepareImage(this.image, this.width, this.height, this))
      setStatus(8); 
  }
  
  int parseflags(int paramInt) { return ((paramInt & 0x40) != 0) ? 4 : (((paramInt & 0x80) != 0) ? 2 : (((paramInt & 0x30) != 0) ? 8 : 0)); }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (this.cancelled)
      return false; 
    int i = parseflags(paramInt1);
    if (i != 0 && i != this.status)
      setStatus(i); 
    return ((this.status & true) != 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ImageMediaEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */