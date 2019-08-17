package javax.swing.plaf.nimbus;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JComponent;

final class ToolBarSeparatorPainter extends AbstractRegionPainter {
  private static final int SPACE = 3;
  
  private static final int INSET = 2;
  
  protected AbstractRegionPainter.PaintContext getPaintContext() { return new AbstractRegionPainter.PaintContext(new Insets(1, 0, 1, 0), new Dimension(38, 7), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D); }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    paramGraphics2D.setColor(paramJComponent.getForeground());
    int i = paramInt2 / 2;
    for (byte b = 2; b <= paramInt1 - 2; b += 3)
      paramGraphics2D.fillRect(b, i, 1, 1); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ToolBarSeparatorPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */