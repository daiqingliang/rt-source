package sun.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TextSourceLabel extends TextLabel {
  TextSource source;
  
  Rectangle2D lb;
  
  Rectangle2D ab;
  
  Rectangle2D vb;
  
  Rectangle2D ib;
  
  GlyphVector gv;
  
  public TextSourceLabel(TextSource paramTextSource) { this(paramTextSource, null, null, null); }
  
  public TextSourceLabel(TextSource paramTextSource, Rectangle2D paramRectangle2D1, Rectangle2D paramRectangle2D2, GlyphVector paramGlyphVector) {
    this.source = paramTextSource;
    this.lb = paramRectangle2D1;
    this.ab = paramRectangle2D2;
    this.gv = paramGlyphVector;
  }
  
  public TextSource getSource() { return this.source; }
  
  public final Rectangle2D getLogicalBounds(float paramFloat1, float paramFloat2) {
    if (this.lb == null)
      this.lb = createLogicalBounds(); 
    return new Rectangle2D.Float((float)(this.lb.getX() + paramFloat1), (float)(this.lb.getY() + paramFloat2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
  }
  
  public final Rectangle2D getVisualBounds(float paramFloat1, float paramFloat2) {
    if (this.vb == null)
      this.vb = createVisualBounds(); 
    return new Rectangle2D.Float((float)(this.vb.getX() + paramFloat1), (float)(this.vb.getY() + paramFloat2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
  }
  
  public final Rectangle2D getAlignBounds(float paramFloat1, float paramFloat2) {
    if (this.ab == null)
      this.ab = createAlignBounds(); 
    return new Rectangle2D.Float((float)(this.ab.getX() + paramFloat1), (float)(this.ab.getY() + paramFloat2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
  }
  
  public Rectangle2D getItalicBounds(float paramFloat1, float paramFloat2) {
    if (this.ib == null)
      this.ib = createItalicBounds(); 
    return new Rectangle2D.Float((float)(this.ib.getX() + paramFloat1), (float)(this.ib.getY() + paramFloat2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) { return getGV().getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2); }
  
  public AffineTransform getBaselineTransform() {
    Font font = this.source.getFont();
    return font.hasLayoutAttributes() ? AttributeValues.getBaselineTransform(font.getAttributes()) : null;
  }
  
  public Shape getOutline(float paramFloat1, float paramFloat2) { return getGV().getOutline(paramFloat1, paramFloat2); }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) { paramGraphics2D.drawGlyphVector(getGV(), paramFloat1, paramFloat2); }
  
  protected Rectangle2D createLogicalBounds() { return getGV().getLogicalBounds(); }
  
  protected Rectangle2D createVisualBounds() { return getGV().getVisualBounds(); }
  
  protected Rectangle2D createItalicBounds() { return getGV().getLogicalBounds(); }
  
  protected Rectangle2D createAlignBounds() { return createLogicalBounds(); }
  
  private final GlyphVector getGV() {
    if (this.gv == null)
      this.gv = createGV(); 
    return this.gv;
  }
  
  protected GlyphVector createGV() {
    Font font = this.source.getFont();
    FontRenderContext fontRenderContext = this.source.getFRC();
    int i = this.source.getLayoutFlags();
    char[] arrayOfChar = this.source.getChars();
    int j = this.source.getStart();
    int k = this.source.getLength();
    GlyphLayout glyphLayout = GlyphLayout.get(null);
    StandardGlyphVector standardGlyphVector = glyphLayout.layout(font, fontRenderContext, arrayOfChar, j, k, i, null);
    GlyphLayout.done(glyphLayout);
    return standardGlyphVector;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\TextSourceLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */