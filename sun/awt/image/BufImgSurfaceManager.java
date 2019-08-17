package sun.awt.image;

import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;

public class BufImgSurfaceManager extends SurfaceManager {
  protected BufferedImage bImg;
  
  protected SurfaceData sdDefault;
  
  public BufImgSurfaceManager(BufferedImage paramBufferedImage) {
    this.bImg = paramBufferedImage;
    this.sdDefault = BufImgSurfaceData.createData(paramBufferedImage);
  }
  
  public SurfaceData getPrimarySurfaceData() { return this.sdDefault; }
  
  public SurfaceData restoreContents() { return this.sdDefault; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\BufImgSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */