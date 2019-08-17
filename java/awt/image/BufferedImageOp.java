package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface BufferedImageOp {
  BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2);
  
  Rectangle2D getBounds2D(BufferedImage paramBufferedImage);
  
  BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel);
  
  Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2);
  
  RenderingHints getRenderingHints();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\BufferedImageOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */