package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalButtonUI extends BasicButtonUI {
  protected Color focusColor;
  
  protected Color selectColor;
  
  protected Color disabledTextColor;
  
  private static final Object METAL_BUTTON_UI_KEY = new Object();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MetalButtonUI metalButtonUI = (MetalButtonUI)appContext.get(METAL_BUTTON_UI_KEY);
    if (metalButtonUI == null) {
      metalButtonUI = new MetalButtonUI();
      appContext.put(METAL_BUTTON_UI_KEY, metalButtonUI);
    } 
    return metalButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton) { super.installDefaults(paramAbstractButton); }
  
  public void uninstallDefaults(AbstractButton paramAbstractButton) { super.uninstallDefaults(paramAbstractButton); }
  
  protected BasicButtonListener createButtonListener(AbstractButton paramAbstractButton) { return super.createButtonListener(paramAbstractButton); }
  
  protected Color getSelectColor() {
    this.selectColor = UIManager.getColor(getPropertyPrefix() + "select");
    return this.selectColor;
  }
  
  protected Color getDisabledTextColor() {
    this.disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
    return this.disabledTextColor;
  }
  
  protected Color getFocusColor() {
    this.focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
    return this.focusColor;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    if (paramJComponent.getBackground() instanceof javax.swing.plaf.UIResource && abstractButton.isContentAreaFilled() && paramJComponent.isEnabled()) {
      ButtonModel buttonModel = abstractButton.getModel();
      if (!MetalUtils.isToolBarButton(paramJComponent)) {
        if (!buttonModel.isArmed() && !buttonModel.isPressed() && MetalUtils.drawGradient(paramJComponent, paramGraphics, "Button.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)) {
          paint(paramGraphics, paramJComponent);
          return;
        } 
      } else if (buttonModel.isRollover() && MetalUtils.drawGradient(paramJComponent, paramGraphics, "Button.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)) {
        paint(paramGraphics, paramJComponent);
        return;
      } 
    } 
    super.update(paramGraphics, paramJComponent);
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) {
    if (paramAbstractButton.isContentAreaFilled()) {
      Dimension dimension = paramAbstractButton.getSize();
      paramGraphics.setColor(getSelectColor());
      paramGraphics.fillRect(0, 0, dimension.width, dimension.height);
    } 
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {
    Rectangle rectangle = new Rectangle();
    String str = paramAbstractButton.getText();
    boolean bool = (paramAbstractButton.getIcon() != null) ? 1 : 0;
    if (str != null && !str.equals("")) {
      if (!bool) {
        rectangle.setBounds(paramRectangle2);
      } else {
        rectangle.setBounds(paramRectangle3.union(paramRectangle2));
      } 
    } else if (bool) {
      rectangle.setBounds(paramRectangle3);
    } 
    paramGraphics.setColor(getFocusColor());
    paramGraphics.drawRect(rectangle.x - 1, rectangle.y - 1, rectangle.width + 1, rectangle.height + 1);
  }
  
  protected void paintText(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle, String paramString) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics);
    int i = abstractButton.getDisplayedMnemonicIndex();
    if (buttonModel.isEnabled()) {
      paramGraphics.setColor(abstractButton.getForeground());
    } else {
      paramGraphics.setColor(getDisabledTextColor());
    } 
    SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */