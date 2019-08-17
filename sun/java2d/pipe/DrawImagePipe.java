package sun.java2d.pipe;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import sun.java2d.SunGraphics2D;

public interface DrawImagePipe {
  boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver);
  
  boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver);
  
  boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver);
  
  boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver);
  
  boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver);
  
  void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\DrawImagePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */