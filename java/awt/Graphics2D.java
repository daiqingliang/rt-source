package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public abstract class Graphics2D extends Graphics {
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    Paint paint = getPaint();
    Color color1 = getColor();
    Color color2 = color1.brighter();
    Color color3 = color1.darker();
    setColor(paramBoolean ? color2 : color3);
    fillRect(paramInt1, paramInt2, 1, paramInt4 + 1);
    fillRect(paramInt1 + 1, paramInt2, paramInt3 - 1, 1);
    setColor(paramBoolean ? color3 : color2);
    fillRect(paramInt1 + 1, paramInt2 + paramInt4, paramInt3, 1);
    fillRect(paramInt1 + paramInt3, paramInt2, 1, paramInt4);
    setPaint(paint);
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    Paint paint = getPaint();
    Color color1 = getColor();
    Color color2 = color1.brighter();
    Color color3 = color1.darker();
    if (!paramBoolean) {
      setColor(color3);
    } else if (paint != color1) {
      setColor(color1);
    } 
    fillRect(paramInt1 + 1, paramInt2 + 1, paramInt3 - 2, paramInt4 - 2);
    setColor(paramBoolean ? color2 : color3);
    fillRect(paramInt1, paramInt2, 1, paramInt4);
    fillRect(paramInt1 + 1, paramInt2, paramInt3 - 2, 1);
    setColor(paramBoolean ? color3 : color2);
    fillRect(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt3 - 1, 1);
    fillRect(paramInt1 + paramInt3 - 1, paramInt2, 1, paramInt4 - 1);
    setPaint(paint);
  }
  
  public abstract void draw(Shape paramShape);
  
  public abstract boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver);
  
  public abstract void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2);
  
  public abstract void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform);
  
  public abstract void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform);
  
  public abstract void drawString(String paramString, int paramInt1, int paramInt2);
  
  public abstract void drawString(String paramString, float paramFloat1, float paramFloat2);
  
  public abstract void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2);
  
  public abstract void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2);
  
  public abstract void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2);
  
  public abstract void fill(Shape paramShape);
  
  public abstract boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean);
  
  public abstract GraphicsConfiguration getDeviceConfiguration();
  
  public abstract void setComposite(Composite paramComposite);
  
  public abstract void setPaint(Paint paramPaint);
  
  public abstract void setStroke(Stroke paramStroke);
  
  public abstract void setRenderingHint(RenderingHints.Key paramKey, Object paramObject);
  
  public abstract Object getRenderingHint(RenderingHints.Key paramKey);
  
  public abstract void setRenderingHints(Map<?, ?> paramMap);
  
  public abstract void addRenderingHints(Map<?, ?> paramMap);
  
  public abstract RenderingHints getRenderingHints();
  
  public abstract void translate(int paramInt1, int paramInt2);
  
  public abstract void translate(double paramDouble1, double paramDouble2);
  
  public abstract void rotate(double paramDouble);
  
  public abstract void rotate(double paramDouble1, double paramDouble2, double paramDouble3);
  
  public abstract void scale(double paramDouble1, double paramDouble2);
  
  public abstract void shear(double paramDouble1, double paramDouble2);
  
  public abstract void transform(AffineTransform paramAffineTransform);
  
  public abstract void setTransform(AffineTransform paramAffineTransform);
  
  public abstract AffineTransform getTransform();
  
  public abstract Paint getPaint();
  
  public abstract Composite getComposite();
  
  public abstract void setBackground(Color paramColor);
  
  public abstract Color getBackground();
  
  public abstract Stroke getStroke();
  
  public abstract void clip(Shape paramShape);
  
  public abstract FontRenderContext getFontRenderContext();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Graphics2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */