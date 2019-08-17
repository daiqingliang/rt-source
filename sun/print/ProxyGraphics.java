package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class ProxyGraphics extends Graphics {
  private Graphics g;
  
  public ProxyGraphics(Graphics paramGraphics) { this.g = paramGraphics; }
  
  Graphics getGraphics() { return this.g; }
  
  public Graphics create() { return new ProxyGraphics(this.g.create()); }
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new ProxyGraphics(this.g.create(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public void translate(int paramInt1, int paramInt2) { this.g.translate(paramInt1, paramInt2); }
  
  public Color getColor() { return this.g.getColor(); }
  
  public void setColor(Color paramColor) { this.g.setColor(paramColor); }
  
  public void setPaintMode() { this.g.setPaintMode(); }
  
  public void setXORMode(Color paramColor) { this.g.setXORMode(paramColor); }
  
  public Font getFont() { return this.g.getFont(); }
  
  public void setFont(Font paramFont) { this.g.setFont(paramFont); }
  
  public FontMetrics getFontMetrics() { return this.g.getFontMetrics(); }
  
  public FontMetrics getFontMetrics(Font paramFont) { return this.g.getFontMetrics(paramFont); }
  
  public Rectangle getClipBounds() { return this.g.getClipBounds(); }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.clipRect(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.setClip(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public Shape getClip() { return this.g.getClip(); }
  
  public void setClip(Shape paramShape) { this.g.setClip(paramShape); }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.g.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.drawLine(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.fillRect(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.drawRect(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.clearRect(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.g.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.g.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); }
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) { this.g.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean); }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) { this.g.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean); }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.drawOval(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.fillOval(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.g.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this.g.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { this.g.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt); }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { this.g.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt); }
  
  public void drawPolygon(Polygon paramPolygon) { this.g.drawPolygon(paramPolygon); }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) { this.g.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt); }
  
  public void fillPolygon(Polygon paramPolygon) { this.g.fillPolygon(paramPolygon); }
  
  public void drawString(String paramString, int paramInt1, int paramInt2) { this.g.drawString(paramString, paramInt1, paramInt2); }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) { this.g.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2); }
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.g.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return this.g.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver) { return this.g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) { return this.g.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) { return this.g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver) { return this.g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver); }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) { return this.g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver); }
  
  public void dispose() { this.g.dispose(); }
  
  public void finalize() {}
  
  public String toString() { return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]"; }
  
  @Deprecated
  public Rectangle getClipRect() { return this.g.getClipRect(); }
  
  public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return this.g.hitClip(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public Rectangle getClipBounds(Rectangle paramRectangle) { return this.g.getClipBounds(paramRectangle); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\ProxyGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */