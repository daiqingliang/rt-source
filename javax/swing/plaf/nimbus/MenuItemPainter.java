package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class MenuItemPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_MOUSEOVER = 3;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusSelection", 0.0F, 0.0F, 0.0F, 0);
  
  private Object[] componentColors;
  
  public MenuItemPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 3:
        paintBackgroundMouseOver(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(1.0F), decodeY(1.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.0F)));
    return this.rect;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\MenuItemPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */