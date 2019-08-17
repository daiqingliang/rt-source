package sun.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public abstract class TextLabel {
  public abstract Rectangle2D getVisualBounds(float paramFloat1, float paramFloat2);
  
  public abstract Rectangle2D getLogicalBounds(float paramFloat1, float paramFloat2);
  
  public abstract Rectangle2D getAlignBounds(float paramFloat1, float paramFloat2);
  
  public abstract Rectangle2D getItalicBounds(float paramFloat1, float paramFloat2);
  
  public abstract Shape getOutline(float paramFloat1, float paramFloat2);
  
  public abstract void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2);
  
  public Rectangle2D getVisualBounds() { return getVisualBounds(0.0F, 0.0F); }
  
  public Rectangle2D getLogicalBounds() { return getLogicalBounds(0.0F, 0.0F); }
  
  public Rectangle2D getAlignBounds() { return getAlignBounds(0.0F, 0.0F); }
  
  public Rectangle2D getItalicBounds() { return getItalicBounds(0.0F, 0.0F); }
  
  public Shape getOutline() { return getOutline(0.0F, 0.0F); }
  
  public void draw(Graphics2D paramGraphics2D) { draw(paramGraphics2D, 0.0F, 0.0F); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\TextLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */