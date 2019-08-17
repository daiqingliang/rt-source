package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class BasicButtonUI extends ButtonUI {
  protected int defaultTextIconGap;
  
  private int shiftOffset = 0;
  
  protected int defaultTextShiftOffset;
  
  private static final String propertyPrefix = "Button.";
  
  private static final Object BASIC_BUTTON_UI_KEY = new Object();
  
  private static Rectangle viewRect = new Rectangle();
  
  private static Rectangle textRect = new Rectangle();
  
  private static Rectangle iconRect = new Rectangle();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    BasicButtonUI basicButtonUI = (BasicButtonUI)appContext.get(BASIC_BUTTON_UI_KEY);
    if (basicButtonUI == null) {
      basicButtonUI = new BasicButtonUI();
      appContext.put(BASIC_BUTTON_UI_KEY, basicButtonUI);
    } 
    return basicButtonUI;
  }
  
  protected String getPropertyPrefix() { return "Button."; }
  
  public void installUI(JComponent paramJComponent) {
    installDefaults((AbstractButton)paramJComponent);
    installListeners((AbstractButton)paramJComponent);
    installKeyboardActions((AbstractButton)paramJComponent);
    BasicHTML.updateRenderer(paramJComponent, ((AbstractButton)paramJComponent).getText());
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton) {
    String str = getPropertyPrefix();
    this.defaultTextShiftOffset = UIManager.getInt(str + "textShiftOffset");
    if (paramAbstractButton.isContentAreaFilled()) {
      LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.TRUE);
    } else {
      LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
    } 
    if (paramAbstractButton.getMargin() == null || paramAbstractButton.getMargin() instanceof javax.swing.plaf.UIResource)
      paramAbstractButton.setMargin(UIManager.getInsets(str + "margin")); 
    LookAndFeel.installColorsAndFont(paramAbstractButton, str + "background", str + "foreground", str + "font");
    LookAndFeel.installBorder(paramAbstractButton, str + "border");
    Object object = UIManager.get(str + "rollover");
    if (object != null)
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", object); 
    LookAndFeel.installProperty(paramAbstractButton, "iconTextGap", Integer.valueOf(4));
  }
  
  protected void installListeners(AbstractButton paramAbstractButton) {
    BasicButtonListener basicButtonListener = createButtonListener(paramAbstractButton);
    if (basicButtonListener != null) {
      paramAbstractButton.addMouseListener(basicButtonListener);
      paramAbstractButton.addMouseMotionListener(basicButtonListener);
      paramAbstractButton.addFocusListener(basicButtonListener);
      paramAbstractButton.addPropertyChangeListener(basicButtonListener);
      paramAbstractButton.addChangeListener(basicButtonListener);
    } 
  }
  
  protected void installKeyboardActions(AbstractButton paramAbstractButton) {
    BasicButtonListener basicButtonListener = getButtonListener(paramAbstractButton);
    if (basicButtonListener != null)
      basicButtonListener.installKeyboardActions(paramAbstractButton); 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallKeyboardActions((AbstractButton)paramJComponent);
    uninstallListeners((AbstractButton)paramJComponent);
    uninstallDefaults((AbstractButton)paramJComponent);
    BasicHTML.updateRenderer(paramJComponent, "");
  }
  
  protected void uninstallKeyboardActions(AbstractButton paramAbstractButton) {
    BasicButtonListener basicButtonListener = getButtonListener(paramAbstractButton);
    if (basicButtonListener != null)
      basicButtonListener.uninstallKeyboardActions(paramAbstractButton); 
  }
  
  protected void uninstallListeners(AbstractButton paramAbstractButton) {
    BasicButtonListener basicButtonListener = getButtonListener(paramAbstractButton);
    if (basicButtonListener != null) {
      paramAbstractButton.removeMouseListener(basicButtonListener);
      paramAbstractButton.removeMouseMotionListener(basicButtonListener);
      paramAbstractButton.removeFocusListener(basicButtonListener);
      paramAbstractButton.removeChangeListener(basicButtonListener);
      paramAbstractButton.removePropertyChangeListener(basicButtonListener);
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) { LookAndFeel.uninstallBorder(paramAbstractButton); }
  
  protected BasicButtonListener createButtonListener(AbstractButton paramAbstractButton) { return new BasicButtonListener(paramAbstractButton); }
  
  public int getDefaultTextIconGap(AbstractButton paramAbstractButton) { return this.defaultTextIconGap; }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    String str = layout(abstractButton, SwingUtilities2.getFontMetrics(abstractButton, paramGraphics), abstractButton.getWidth(), abstractButton.getHeight());
    clearTextShiftOffset();
    if (buttonModel.isArmed() && buttonModel.isPressed())
      paintButtonPressed(paramGraphics, abstractButton); 
    if (abstractButton.getIcon() != null)
      paintIcon(paramGraphics, paramJComponent, iconRect); 
    if (str != null && !str.equals("")) {
      View view = (View)paramJComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, textRect);
      } else {
        paintText(paramGraphics, abstractButton, textRect, str);
      } 
    } 
    if (abstractButton.isFocusPainted() && abstractButton.hasFocus())
      paintFocus(paramGraphics, abstractButton, viewRect, textRect, iconRect); 
  }
  
  protected void paintIcon(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    Icon icon1 = abstractButton.getIcon();
    Icon icon2 = null;
    if (icon1 == null)
      return; 
    Icon icon3 = null;
    if (buttonModel.isSelected()) {
      icon3 = abstractButton.getSelectedIcon();
      if (icon3 != null)
        icon1 = icon3; 
    } 
    if (!buttonModel.isEnabled()) {
      if (buttonModel.isSelected()) {
        icon2 = abstractButton.getDisabledSelectedIcon();
        if (icon2 == null)
          icon2 = icon3; 
      } 
      if (icon2 == null)
        icon2 = abstractButton.getDisabledIcon(); 
    } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
      icon2 = abstractButton.getPressedIcon();
      if (icon2 != null)
        clearTextShiftOffset(); 
    } else if (abstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
      if (buttonModel.isSelected()) {
        icon2 = abstractButton.getRolloverSelectedIcon();
        if (icon2 == null)
          icon2 = icon3; 
      } 
      if (icon2 == null)
        icon2 = abstractButton.getRolloverIcon(); 
    } 
    if (icon2 != null)
      icon1 = icon2; 
    if (buttonModel.isPressed() && buttonModel.isArmed()) {
      icon1.paintIcon(paramJComponent, paramGraphics, paramRectangle.x + getTextShiftOffset(), paramRectangle.y + getTextShiftOffset());
    } else {
      icon1.paintIcon(paramJComponent, paramGraphics, paramRectangle.x, paramRectangle.y);
    } 
  }
  
  protected void paintText(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle, String paramString) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics);
    int i = abstractButton.getDisplayedMnemonicIndex();
    if (buttonModel.isEnabled()) {
      paramGraphics.setColor(abstractButton.getForeground());
      SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, paramRectangle.x + getTextShiftOffset(), paramRectangle.y + fontMetrics.getAscent() + getTextShiftOffset());
    } else {
      paramGraphics.setColor(abstractButton.getBackground().brighter());
      SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent());
      paramGraphics.setColor(abstractButton.getBackground().darker());
      SwingUtilities2.drawStringUnderlineCharAt(paramJComponent, paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + fontMetrics.getAscent() - 1);
    } 
  }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString) { paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString); }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {}
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) {}
  
  protected void clearTextShiftOffset() { this.shiftOffset = 0; }
  
  protected void setTextShiftOffset() { this.shiftOffset = this.defaultTextShiftOffset; }
  
  protected int getTextShiftOffset() { return this.shiftOffset; }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(paramJComponent);
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null)
      dimension.width = (int)(dimension.width - view.getPreferredSpan(0) - view.getMinimumSpan(0)); 
    return dimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    return BasicGraphicsUtils.getPreferredButtonSize(abstractButton, abstractButton.getIconTextGap());
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(paramJComponent);
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null)
      dimension.width = (int)(dimension.width + view.getMaximumSpan(0) - view.getPreferredSpan(0)); 
    return dimension;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    String str = abstractButton.getText();
    if (str == null || "".equals(str))
      return -1; 
    FontMetrics fontMetrics = abstractButton.getFontMetrics(abstractButton.getFont());
    layout(abstractButton, fontMetrics, paramInt1, paramInt2);
    return BasicHTML.getBaseline(abstractButton, textRect.y, fontMetrics.getAscent(), textRect.width, textRect.height);
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    if (paramJComponent.getClientProperty("html") != null)
      return Component.BaselineResizeBehavior.OTHER; 
    switch (((AbstractButton)paramJComponent).getVerticalAlignment()) {
      case 1:
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      case 3:
        return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
      case 0:
        return Component.BaselineResizeBehavior.CENTER_OFFSET;
    } 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  private String layout(AbstractButton paramAbstractButton, FontMetrics paramFontMetrics, int paramInt1, int paramInt2) {
    Insets insets = paramAbstractButton.getInsets();
    viewRect.x = insets.left;
    viewRect.y = insets.top;
    viewRect.width = paramInt1 - insets.right + viewRect.x;
    viewRect.height = paramInt2 - insets.bottom + viewRect.y;
    textRect.x = textRect.y = textRect.width = textRect.height = 0;
    iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
    return SwingUtilities.layoutCompoundLabel(paramAbstractButton, paramFontMetrics, paramAbstractButton.getText(), paramAbstractButton.getIcon(), paramAbstractButton.getVerticalAlignment(), paramAbstractButton.getHorizontalAlignment(), paramAbstractButton.getVerticalTextPosition(), paramAbstractButton.getHorizontalTextPosition(), viewRect, iconRect, textRect, (paramAbstractButton.getText() == null) ? 0 : paramAbstractButton.getIconTextGap());
  }
  
  private BasicButtonListener getButtonListener(AbstractButton paramAbstractButton) {
    MouseMotionListener[] arrayOfMouseMotionListener = paramAbstractButton.getMouseMotionListeners();
    if (arrayOfMouseMotionListener != null)
      for (MouseMotionListener mouseMotionListener : arrayOfMouseMotionListener) {
        if (mouseMotionListener instanceof BasicButtonListener)
          return (BasicButtonListener)mouseMotionListener; 
      }  
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */