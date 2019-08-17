package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class TreeCellEditorPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_ENABLED_FOCUSED = 3;
  
  static final int BACKGROUND_SELECTED = 4;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.017358616F, -0.11372548F, 0);
  
  private Color color2 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Object[] componentColors;
  
  public TreeCellEditorPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 2:
        paintBackgroundEnabled(paramGraphics2D);
        break;
      case 3:
        paintBackgroundEnabledAndFocused(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndFocused(Graphics2D paramGraphics2D) {
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color2);
    paramGraphics2D.fill(this.path);
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.2F));
    this.path.lineTo(decodeX(2.8F), decodeY(0.2F));
    this.path.lineTo(decodeX(2.8F), decodeY(2.8F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.8F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(0.24000001F));
    this.path.lineTo(decodeX(2.7600007F), decodeY(0.24000001F));
    this.path.lineTo(decodeX(2.7600007F), decodeY(2.7599998F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(2.7599998F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TreeCellEditorPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */