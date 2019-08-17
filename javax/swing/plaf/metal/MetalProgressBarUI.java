package javax.swing.plaf.metal;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MetalProgressBarUI extends BasicProgressBarUI {
  private Rectangle innards;
  
  private Rectangle box;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalProgressBarUI(); }
  
  public void paintDeterminate(Graphics paramGraphics, JComponent paramJComponent) {
    super.paintDeterminate(paramGraphics, paramJComponent);
    if (!(paramGraphics instanceof Graphics2D))
      return; 
    if (this.progressBar.isBorderPainted()) {
      Insets insets = this.progressBar.getInsets();
      int i = this.progressBar.getWidth() - insets.left + insets.right;
      int j = this.progressBar.getHeight() - insets.top + insets.bottom;
      int k = getAmountFull(insets, i, j);
      boolean bool = MetalUtils.isLeftToRight(paramJComponent);
      int m = insets.left;
      int n = insets.top;
      int i1 = insets.left + i - 1;
      int i2 = insets.top + j - 1;
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      graphics2D.setStroke(new BasicStroke(1.0F));
      if (this.progressBar.getOrientation() == 0) {
        graphics2D.setColor(MetalLookAndFeel.getControlShadow());
        graphics2D.drawLine(m, n, i1, n);
        if (k > 0) {
          graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
          if (bool) {
            graphics2D.drawLine(m, n, m + k - 1, n);
          } else {
            graphics2D.drawLine(i1, n, i1 - k + 1, n);
            if (this.progressBar.getPercentComplete() != 1.0D)
              graphics2D.setColor(MetalLookAndFeel.getControlShadow()); 
          } 
        } 
        graphics2D.drawLine(m, n, m, i2);
      } else {
        graphics2D.setColor(MetalLookAndFeel.getControlShadow());
        graphics2D.drawLine(m, n, m, i2);
        if (k > 0) {
          graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
          graphics2D.drawLine(m, i2, m, i2 - k + 1);
        } 
        graphics2D.setColor(MetalLookAndFeel.getControlShadow());
        if (this.progressBar.getPercentComplete() == 1.0D)
          graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow()); 
        graphics2D.drawLine(m, n, i1, n);
      } 
    } 
  }
  
  public void paintIndeterminate(Graphics paramGraphics, JComponent paramJComponent) {
    super.paintIndeterminate(paramGraphics, paramJComponent);
    if (!this.progressBar.isBorderPainted() || !(paramGraphics instanceof Graphics2D))
      return; 
    Insets insets = this.progressBar.getInsets();
    int i = this.progressBar.getWidth() - insets.left + insets.right;
    int j = this.progressBar.getHeight() - insets.top + insets.bottom;
    int k = getAmountFull(insets, i, j);
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    Rectangle rectangle = null;
    rectangle = getBox(rectangle);
    int m = insets.left;
    int n = insets.top;
    int i1 = insets.left + i - 1;
    int i2 = insets.top + j - 1;
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    graphics2D.setStroke(new BasicStroke(1.0F));
    if (this.progressBar.getOrientation() == 0) {
      graphics2D.setColor(MetalLookAndFeel.getControlShadow());
      graphics2D.drawLine(m, n, i1, n);
      graphics2D.drawLine(m, n, m, i2);
      graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      graphics2D.drawLine(rectangle.x, n, rectangle.x + rectangle.width - 1, n);
    } else {
      graphics2D.setColor(MetalLookAndFeel.getControlShadow());
      graphics2D.drawLine(m, n, m, i2);
      graphics2D.drawLine(m, n, i1, n);
      graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      graphics2D.drawLine(m, rectangle.y, m, rectangle.y + rectangle.height - 1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */