package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;

public class BasicToggleButtonUI extends BasicButtonUI {
  private static final Object BASIC_TOGGLE_BUTTON_UI_KEY = new Object();
  
  private static final String propertyPrefix = "ToggleButton.";
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    BasicToggleButtonUI basicToggleButtonUI = (BasicToggleButtonUI)appContext.get(BASIC_TOGGLE_BUTTON_UI_KEY);
    if (basicToggleButtonUI == null) {
      basicToggleButtonUI = new BasicToggleButtonUI();
      appContext.put(BASIC_TOGGLE_BUTTON_UI_KEY, basicToggleButtonUI);
    } 
    return basicToggleButtonUI;
  }
  
  protected String getPropertyPrefix() { return "ToggleButton."; }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    Dimension dimension = abstractButton.getSize();
    FontMetrics fontMetrics = paramGraphics.getFontMetrics();
    Insets insets = paramJComponent.getInsets();
    Rectangle rectangle1 = new Rectangle(dimension);
    rectangle1.x += insets.left;
    rectangle1.y += insets.top;
    rectangle1.width -= insets.right + rectangle1.x;
    rectangle1.height -= insets.bottom + rectangle1.y;
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    Font font = paramJComponent.getFont();
    paramGraphics.setFont(font);
    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, fontMetrics, abstractButton.getText(), abstractButton.getIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), rectangle1, rectangle2, rectangle3, (abstractButton.getText() == null) ? 0 : abstractButton.getIconTextGap());
    paramGraphics.setColor(abstractButton.getBackground());
    if ((buttonModel.isArmed() && buttonModel.isPressed()) || buttonModel.isSelected())
      paintButtonPressed(paramGraphics, abstractButton); 
    if (abstractButton.getIcon() != null)
      paintIcon(paramGraphics, abstractButton, rectangle2); 
    if (str != null && !str.equals("")) {
      View view = (View)paramJComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, rectangle3);
      } else {
        paintText(paramGraphics, abstractButton, rectangle3, str);
      } 
    } 
    if (abstractButton.isFocusPainted() && abstractButton.hasFocus())
      paintFocus(paramGraphics, abstractButton, rectangle1, rectangle3, rectangle2); 
  }
  
  protected void paintIcon(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle) {
    ButtonModel buttonModel = paramAbstractButton.getModel();
    Icon icon = null;
    if (!buttonModel.isEnabled()) {
      if (buttonModel.isSelected()) {
        icon = paramAbstractButton.getDisabledSelectedIcon();
      } else {
        icon = paramAbstractButton.getDisabledIcon();
      } 
    } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
      icon = paramAbstractButton.getPressedIcon();
      if (icon == null)
        icon = paramAbstractButton.getSelectedIcon(); 
    } else if (buttonModel.isSelected()) {
      if (paramAbstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
        icon = paramAbstractButton.getRolloverSelectedIcon();
        if (icon == null)
          icon = paramAbstractButton.getSelectedIcon(); 
      } else {
        icon = paramAbstractButton.getSelectedIcon();
      } 
    } else if (paramAbstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
      icon = paramAbstractButton.getRolloverIcon();
    } 
    if (icon == null)
      icon = paramAbstractButton.getIcon(); 
    icon.paintIcon(paramAbstractButton, paramGraphics, paramRectangle.x, paramRectangle.y);
  }
  
  protected int getTextShiftOffset() { return 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */