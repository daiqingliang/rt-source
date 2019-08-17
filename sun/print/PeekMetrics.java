package sun.print;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.font.TextLayout;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

public class PeekMetrics {
  private boolean mHasNonSolidColors;
  
  private boolean mHasCompositing;
  
  private boolean mHasText;
  
  private boolean mHasImages;
  
  public boolean hasNonSolidColors() { return this.mHasNonSolidColors; }
  
  public boolean hasCompositing() { return this.mHasCompositing; }
  
  public boolean hasText() { return this.mHasText; }
  
  public boolean hasImages() { return this.mHasImages; }
  
  public void fill(Graphics2D paramGraphics2D) { checkDrawingMode(paramGraphics2D); }
  
  public void draw(Graphics2D paramGraphics2D) { checkDrawingMode(paramGraphics2D); }
  
  public void clear(Graphics2D paramGraphics2D) { checkPaint(paramGraphics2D.getBackground()); }
  
  public void drawText(Graphics2D paramGraphics2D) {
    this.mHasText = true;
    checkDrawingMode(paramGraphics2D);
  }
  
  public void drawText(Graphics2D paramGraphics2D, TextLayout paramTextLayout) {
    this.mHasText = true;
    checkDrawingMode(paramGraphics2D);
  }
  
  public void drawImage(Graphics2D paramGraphics2D, Image paramImage) { this.mHasImages = true; }
  
  public void drawImage(Graphics2D paramGraphics2D, RenderedImage paramRenderedImage) { this.mHasImages = true; }
  
  public void drawImage(Graphics2D paramGraphics2D, RenderableImage paramRenderableImage) { this.mHasImages = true; }
  
  private void checkDrawingMode(Graphics2D paramGraphics2D) {
    checkPaint(paramGraphics2D.getPaint());
    checkAlpha(paramGraphics2D.getComposite());
  }
  
  private void checkPaint(Paint paramPaint) {
    if (paramPaint instanceof Color) {
      if (((Color)paramPaint).getAlpha() < 255)
        this.mHasNonSolidColors = true; 
    } else {
      this.mHasNonSolidColors = true;
    } 
  }
  
  private void checkAlpha(Composite paramComposite) {
    if (paramComposite instanceof AlphaComposite) {
      AlphaComposite alphaComposite = (AlphaComposite)paramComposite;
      float f = alphaComposite.getAlpha();
      int i = alphaComposite.getRule();
      if (f != 1.0D || (i != 2 && i != 3))
        this.mHasCompositing = true; 
    } else {
      this.mHasCompositing = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PeekMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */