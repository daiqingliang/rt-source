package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;

public class MotifButtonUI extends BasicButtonUI {
  protected Color selectColor;
  
  private boolean defaults_initialized = false;
  
  private static final Object MOTIF_BUTTON_UI_KEY = new Object();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MotifButtonUI motifButtonUI = (MotifButtonUI)appContext.get(MOTIF_BUTTON_UI_KEY);
    if (motifButtonUI == null) {
      motifButtonUI = new MotifButtonUI();
      appContext.put(MOTIF_BUTTON_UI_KEY, motifButtonUI);
    } 
    return motifButtonUI;
  }
  
  protected BasicButtonListener createButtonListener(AbstractButton paramAbstractButton) { return new MotifButtonListener(paramAbstractButton); }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      this.defaults_initialized = true;
    } 
    LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getSelectColor() { return this.selectColor; }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    fillContentArea(paramGraphics, (AbstractButton)paramJComponent, paramJComponent.getBackground());
    super.paint(paramGraphics, paramJComponent);
  }
  
  protected void paintIcon(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    Shape shape = paramGraphics.getClip();
    Rectangle rectangle1 = AbstractBorder.getInteriorRectangle(paramJComponent, paramJComponent.getBorder(), 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    Rectangle rectangle2 = shape.getBounds();
    rectangle1 = SwingUtilities.computeIntersection(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, rectangle1);
    paramGraphics.setClip(rectangle1);
    super.paintIcon(paramGraphics, paramJComponent, paramRectangle);
    paramGraphics.setClip(shape);
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {}
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) { fillContentArea(paramGraphics, paramAbstractButton, this.selectColor); }
  
  protected void fillContentArea(Graphics paramGraphics, AbstractButton paramAbstractButton, Color paramColor) {
    if (paramAbstractButton.isContentAreaFilled()) {
      Insets insets1 = paramAbstractButton.getMargin();
      Insets insets2 = paramAbstractButton.getInsets();
      Dimension dimension = paramAbstractButton.getSize();
      paramGraphics.setColor(paramColor);
      paramGraphics.fillRect(insets2.left - insets1.left, insets2.top - insets1.top, dimension.width - insets2.left - insets1.left - insets2.right - insets1.right, dimension.height - insets2.top - insets1.top - insets2.bottom - insets1.bottom);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */