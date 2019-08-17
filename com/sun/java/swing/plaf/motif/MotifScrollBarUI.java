package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import sun.swing.SwingUtilities2;

public class MotifScrollBarUI extends BasicScrollBarUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifScrollBarUI(); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Insets insets = paramJComponent.getInsets();
    int i = insets.left + insets.right;
    int j = insets.top + insets.bottom;
    return (this.scrollbar.getOrientation() == 1) ? new Dimension(i + 11, j + 33) : new Dimension(i + 33, j + 11);
  }
  
  protected JButton createDecreaseButton(int paramInt) { return new MotifScrollBarButton(paramInt); }
  
  protected JButton createIncreaseButton(int paramInt) { return new MotifScrollBarButton(paramInt); }
  
  public void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    paramGraphics.setColor(this.trackColor);
    paramGraphics.fillRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
  }
  
  public void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    if (paramRectangle.isEmpty() || !this.scrollbar.isEnabled())
      return; 
    int i = paramRectangle.width;
    int j = paramRectangle.height;
    paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    paramGraphics.setColor(this.thumbColor);
    paramGraphics.fillRect(0, 0, i - 1, j - 1);
    paramGraphics.setColor(this.thumbHighlightColor);
    SwingUtilities2.drawVLine(paramGraphics, 0, 0, j - 1);
    SwingUtilities2.drawHLine(paramGraphics, 1, i - 1, 0);
    paramGraphics.setColor(this.thumbLightShadowColor);
    SwingUtilities2.drawHLine(paramGraphics, 1, i - 1, j - 1);
    SwingUtilities2.drawVLine(paramGraphics, i - 1, 1, j - 2);
    paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */