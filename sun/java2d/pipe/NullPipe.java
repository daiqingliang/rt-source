package sun.java2d.pipe;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import sun.java2d.SunGraphics2D;

public class NullPipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, TextPipe, DrawImagePipe {
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {}
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {}
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {}
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {}
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {}
  
  public void drawString(SunGraphics2D paramSunGraphics2D, String paramString, double paramDouble1, double paramDouble2) {}
  
  public void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {}
  
  public void drawChars(SunGraphics2D paramSunGraphics2D, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) { return false; }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver) { return false; }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) { return false; }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) { return false; }
  
  public boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) { return false; }
  
  public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\NullPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */