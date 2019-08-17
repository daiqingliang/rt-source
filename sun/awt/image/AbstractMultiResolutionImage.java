package sun.awt.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public abstract class AbstractMultiResolutionImage extends Image implements MultiResolutionImage {
  public int getWidth(ImageObserver paramImageObserver) { return getBaseImage().getWidth(null); }
  
  public int getHeight(ImageObserver paramImageObserver) { return getBaseImage().getHeight(null); }
  
  public ImageProducer getSource() { return getBaseImage().getSource(); }
  
  public Graphics getGraphics() { return getBaseImage().getGraphics(); }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver) { return getBaseImage().getProperty(paramString, paramImageObserver); }
  
  protected abstract Image getBaseImage();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\AbstractMultiResolutionImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */