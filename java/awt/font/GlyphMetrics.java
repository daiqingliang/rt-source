package java.awt.font;

import java.awt.geom.Rectangle2D;

public final class GlyphMetrics {
  private boolean horizontal = true;
  
  private float advanceX;
  
  private float advanceY;
  
  private Rectangle2D.Float bounds;
  
  private byte glyphType;
  
  public static final byte STANDARD = 0;
  
  public static final byte LIGATURE = 1;
  
  public static final byte COMBINING = 2;
  
  public static final byte COMPONENT = 3;
  
  public static final byte WHITESPACE = 4;
  
  public GlyphMetrics(float paramFloat, Rectangle2D paramRectangle2D, byte paramByte) {
    this.advanceX = paramFloat;
    this.advanceY = 0.0F;
    this.bounds = new Rectangle2D.Float();
    this.bounds.setRect(paramRectangle2D);
    this.glyphType = paramByte;
  }
  
  public GlyphMetrics(boolean paramBoolean, float paramFloat1, float paramFloat2, Rectangle2D paramRectangle2D, byte paramByte) {
    this.advanceX = paramFloat1;
    this.advanceY = paramFloat2;
    this.bounds = new Rectangle2D.Float();
    this.bounds.setRect(paramRectangle2D);
    this.glyphType = paramByte;
  }
  
  public float getAdvance() { return this.horizontal ? this.advanceX : this.advanceY; }
  
  public float getAdvanceX() { return this.advanceX; }
  
  public float getAdvanceY() { return this.advanceY; }
  
  public Rectangle2D getBounds2D() { return new Rectangle2D.Float(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height); }
  
  public float getLSB() { return this.horizontal ? this.bounds.x : this.bounds.y; }
  
  public float getRSB() { return this.horizontal ? (this.advanceX - this.bounds.x - this.bounds.width) : (this.advanceY - this.bounds.y - this.bounds.height); }
  
  public int getType() { return this.glyphType; }
  
  public boolean isStandard() { return ((this.glyphType & 0x3) == 0); }
  
  public boolean isLigature() { return ((this.glyphType & 0x3) == 1); }
  
  public boolean isCombining() { return ((this.glyphType & 0x3) == 2); }
  
  public boolean isComponent() { return ((this.glyphType & 0x3) == 3); }
  
  public boolean isWhitespace() { return ((this.glyphType & 0x4) == 4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\GlyphMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */