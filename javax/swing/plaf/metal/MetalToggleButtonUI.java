package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalToggleButtonUI extends BasicToggleButtonUI {
  private static final Object METAL_TOGGLE_BUTTON_UI_KEY = new Object();
  
  protected Color focusColor;
  
  protected Color selectColor;
  
  protected Color disabledTextColor;
  
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MetalToggleButtonUI metalToggleButtonUI = (MetalToggleButtonUI)appContext.get(METAL_TOGGLE_BUTTON_UI_KEY);
    if (metalToggleButtonUI == null) {
      metalToggleButtonUI = new MetalToggleButtonUI();
      appContext.put(METAL_TOGGLE_BUTTON_UI_KEY, metalToggleButtonUI);
    } 
    return metalToggleButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      this.selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      this.disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
      this.defaults_initialized = true;
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getSelectColor() { return this.selectColor; }
  
  protected Color getDisabledTextColor() { return this.disabledTextColor; }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    if (paramJComponent.getBackground() instanceof javax.swing.plaf.UIResource && abstractButton.isContentAreaFilled() && paramJComponent.isEnabled()) {
      ButtonModel buttonModel = abstractButton.getModel();
      if (!MetalUtils.isToolBarButton(paramJComponent)) {
        if (!buttonModel.isArmed() && !buttonModel.isPressed() && MetalUtils.drawGradient(paramJComponent, paramGraphics, "ToggleButton.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)) {
          paint(paramGraphics, paramJComponent);
          return;
        } 
      } else if ((buttonModel.isRollover() || buttonModel.isSelected()) && MetalUtils.drawGradient(paramJComponent, paramGraphics, "ToggleButton.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)) {
        paint(paramGraphics, paramJComponent);
        return;
      } 
    } 
    super.update(paramGraphics, paramJComponent);
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) {
    if (paramAbstractButton.isContentAreaFilled()) {
      paramGraphics.setColor(getSelectColor());
      paramGraphics.fillRect(0, 0, paramAbstractButton.getWidth(), paramAbstractButton.getHeight());
    } 
  }
  
  protected void paintText(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle, String paramString) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(abstractButton, paramGraphics);
    int i = abstractButton.getDisplayedMnemonicIndex();
    if (buttonModel.isEnabled()) {
      paramGraphics.setColor(abstractButton.getForeground());
    } else if (buttonModel.isSelected()) {
      paramGraphics.setColor(paramJComponent.getBackground());
    } else {
      paramGraphics.setColor(getDisabledTextColor());
    } 
    SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent());
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
  
  protected void paintIcon(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle) { super.paintIcon(paramGraphics, paramAbstractButton, paramRectangle); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */