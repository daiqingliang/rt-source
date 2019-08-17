package javax.swing.plaf.metal;

import java.awt.Color;
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
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalRadioButtonUI extends BasicRadioButtonUI {
  private static final Object METAL_RADIO_BUTTON_UI_KEY = new Object();
  
  protected Color focusColor;
  
  protected Color selectColor;
  
  protected Color disabledTextColor;
  
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MetalRadioButtonUI metalRadioButtonUI = (MetalRadioButtonUI)appContext.get(METAL_RADIO_BUTTON_UI_KEY);
    if (metalRadioButtonUI == null) {
      metalRadioButtonUI = new MetalRadioButtonUI();
      appContext.put(METAL_RADIO_BUTTON_UI_KEY, metalRadioButtonUI);
    } 
    return metalRadioButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      this.selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      this.disabledTextColor = UIManager.getColor(getPropertyPrefix() + "disabledText");
      this.defaults_initialized = true;
    } 
    LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getSelectColor() { return this.selectColor; }
  
  protected Color getDisabledTextColor() { return this.disabledTextColor; }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    Dimension dimension = paramJComponent.getSize();
    int i = dimension.width;
    int j = dimension.height;
    Font font = paramJComponent.getFont();
    paramGraphics.setFont(font);
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, font);
    Rectangle rectangle1 = new Rectangle(dimension);
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    Insets insets = paramJComponent.getInsets();
    rectangle1.x += insets.left;
    rectangle1.y += insets.top;
    rectangle1.width -= insets.right + rectangle1.x;
    rectangle1.height -= insets.bottom + rectangle1.y;
    Icon icon = abstractButton.getIcon();
    Object object1 = null;
    Object object2 = null;
    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, fontMetrics, abstractButton.getText(), (icon != null) ? icon : getDefaultIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), rectangle1, rectangle2, rectangle3, abstractButton.getIconTextGap());
    if (paramJComponent.isOpaque()) {
      paramGraphics.setColor(abstractButton.getBackground());
      paramGraphics.fillRect(0, 0, dimension.width, dimension.height);
    } 
    if (icon != null) {
      if (!buttonModel.isEnabled()) {
        if (buttonModel.isSelected()) {
          icon = abstractButton.getDisabledSelectedIcon();
        } else {
          icon = abstractButton.getDisabledIcon();
        } 
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        icon = abstractButton.getPressedIcon();
        if (icon == null)
          icon = abstractButton.getSelectedIcon(); 
      } else if (buttonModel.isSelected()) {
        if (abstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
          icon = abstractButton.getRolloverSelectedIcon();
          if (icon == null)
            icon = abstractButton.getSelectedIcon(); 
        } else {
          icon = abstractButton.getSelectedIcon();
        } 
      } else if (abstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
        icon = abstractButton.getRolloverIcon();
      } 
      if (icon == null)
        icon = abstractButton.getIcon(); 
      icon.paintIcon(paramJComponent, paramGraphics, rectangle2.x, rectangle2.y);
    } else {
      getDefaultIcon().paintIcon(paramJComponent, paramGraphics, rectangle2.x, rectangle2.y);
    } 
    if (str != null) {
      View view = (View)paramJComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, rectangle3);
      } else {
        int k = abstractButton.getDisplayedMnemonicIndex();
        if (buttonModel.isEnabled()) {
          paramGraphics.setColor(abstractButton.getForeground());
        } else {
          paramGraphics.setColor(getDisabledTextColor());
        } 
        SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, str, k, rectangle3.x, rectangle3.y + fontMetrics.getAscent());
      } 
      if (abstractButton.hasFocus() && abstractButton.isFocusPainted() && rectangle3.width > 0 && rectangle3.height > 0)
        paintFocus(paramGraphics, rectangle3, dimension); 
    } 
  }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension) {
    paramGraphics.setColor(getFocusColor());
    paramGraphics.drawRect(paramRectangle.x - 1, paramRectangle.y - 1, paramRectangle.width + 1, paramRectangle.height + 1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */