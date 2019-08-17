package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class SynthButtonUI extends BasicButtonUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthButtonUI(); }
  
  protected void installDefaults(AbstractButton paramAbstractButton) {
    updateStyle(paramAbstractButton);
    LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
  }
  
  protected void installListeners(AbstractButton paramAbstractButton) {
    super.installListeners(paramAbstractButton);
    paramAbstractButton.addPropertyChangeListener(this);
  }
  
  void updateStyle(AbstractButton paramAbstractButton) {
    SynthContext synthContext = getContext(paramAbstractButton, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      if (paramAbstractButton.getMargin() == null || paramAbstractButton.getMargin() instanceof javax.swing.plaf.UIResource) {
        Insets insets = (Insets)this.style.get(synthContext, getPropertyPrefix() + "margin");
        if (insets == null)
          insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS; 
        paramAbstractButton.setMargin(insets);
      } 
      Object object = this.style.get(synthContext, getPropertyPrefix() + "iconTextGap");
      if (object != null)
        LookAndFeel.installProperty(paramAbstractButton, "iconTextGap", object); 
      object = this.style.get(synthContext, getPropertyPrefix() + "contentAreaFilled");
      LookAndFeel.installProperty(paramAbstractButton, "contentAreaFilled", (object != null) ? object : Boolean.TRUE);
      if (synthStyle != null) {
        uninstallKeyboardActions(paramAbstractButton);
        installKeyboardActions(paramAbstractButton);
      } 
    } 
    synthContext.dispose();
  }
  
  protected void uninstallListeners(AbstractButton paramAbstractButton) {
    super.uninstallListeners(paramAbstractButton);
    paramAbstractButton.removePropertyChangeListener(this);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    SynthContext synthContext = getContext(paramAbstractButton, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) {
    char c = '\001';
    if (!paramJComponent.isEnabled())
      c = '\b'; 
    if (SynthLookAndFeel.getSelectedUI() == this)
      return SynthLookAndFeel.getSelectedUIState() | true; 
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    if (buttonModel.isPressed())
      if (buttonModel.isArmed()) {
        c = '\004';
      } else {
        c = '\002';
      }  
    if (buttonModel.isRollover())
      c |= 0x2; 
    if (buttonModel.isSelected())
      c |= 0x200; 
    if (paramJComponent.isFocusOwner() && abstractButton.isFocusPainted())
      c |= 0x100; 
    if (paramJComponent instanceof JButton && ((JButton)paramJComponent).isDefaultButton())
      c |= 0x400; 
    return c;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    int i;
    if (paramJComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    String str = abstractButton.getText();
    if (str == null || "".equals(str))
      return -1; 
    Insets insets = abstractButton.getInsets();
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    rectangle1.x = insets.left;
    rectangle1.y = insets.top;
    rectangle1.width = paramInt1 - insets.right + rectangle1.x;
    rectangle1.height = paramInt2 - insets.bottom + rectangle1.y;
    SynthContext synthContext = getContext(abstractButton);
    FontMetrics fontMetrics = synthContext.getComponent().getFontMetrics(synthContext.getStyle().getFont(synthContext));
    synthContext.getStyle().getGraphicsUtils(synthContext).layoutText(synthContext, fontMetrics, abstractButton.getText(), abstractButton.getIcon(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), rectangle1, rectangle3, rectangle2, abstractButton.getIconTextGap());
    View view = (View)abstractButton.getClientProperty("html");
    if (view != null) {
      i = BasicHTML.getHTMLBaseline(view, rectangle2.width, rectangle2.height);
      if (i >= 0)
        i += rectangle2.y; 
    } else {
      i = rectangle2.y + fontMetrics.getAscent();
    } 
    synthContext.dispose();
    return i;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    paintBackground(synthContext, paramGraphics, paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    AbstractButton abstractButton = (AbstractButton)paramSynthContext.getComponent();
    paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
    paramGraphics.setFont(this.style.getFont(paramSynthContext));
    paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, abstractButton.getText(), getIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex(), getTextShiftOffset(paramSynthContext));
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) {
    if (((AbstractButton)paramJComponent).isContentAreaFilled())
      paramSynthContext.getPainter().paintButtonBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight()); 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected Icon getDefaultIcon(AbstractButton paramAbstractButton) {
    SynthContext synthContext = getContext(paramAbstractButton);
    Icon icon = synthContext.getStyle().getIcon(synthContext, getPropertyPrefix() + "icon");
    synthContext.dispose();
    return icon;
  }
  
  protected Icon getIcon(AbstractButton paramAbstractButton) {
    Icon icon = paramAbstractButton.getIcon();
    ButtonModel buttonModel = paramAbstractButton.getModel();
    if (!buttonModel.isEnabled()) {
      icon = getSynthDisabledIcon(paramAbstractButton, icon);
    } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
      icon = getPressedIcon(paramAbstractButton, getSelectedIcon(paramAbstractButton, icon));
    } else if (paramAbstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
      icon = getRolloverIcon(paramAbstractButton, getSelectedIcon(paramAbstractButton, icon));
    } else if (buttonModel.isSelected()) {
      icon = getSelectedIcon(paramAbstractButton, icon);
    } else {
      icon = getEnabledIcon(paramAbstractButton, icon);
    } 
    return (icon == null) ? getDefaultIcon(paramAbstractButton) : icon;
  }
  
  private Icon getIcon(AbstractButton paramAbstractButton, Icon paramIcon1, Icon paramIcon2, int paramInt) {
    Icon icon = paramIcon1;
    if (icon == null)
      if (paramIcon2 instanceof javax.swing.plaf.UIResource) {
        icon = getSynthIcon(paramAbstractButton, paramInt);
        if (icon == null)
          icon = paramIcon2; 
      } else {
        icon = paramIcon2;
      }  
    return icon;
  }
  
  private Icon getSynthIcon(AbstractButton paramAbstractButton, int paramInt) { return this.style.getIcon(getContext(paramAbstractButton, paramInt), getPropertyPrefix() + "icon"); }
  
  private Icon getEnabledIcon(AbstractButton paramAbstractButton, Icon paramIcon) {
    if (paramIcon == null)
      paramIcon = getSynthIcon(paramAbstractButton, 1); 
    return paramIcon;
  }
  
  private Icon getSelectedIcon(AbstractButton paramAbstractButton, Icon paramIcon) { return getIcon(paramAbstractButton, paramAbstractButton.getSelectedIcon(), paramIcon, 512); }
  
  private Icon getRolloverIcon(AbstractButton paramAbstractButton, Icon paramIcon) {
    Icon icon;
    ButtonModel buttonModel = paramAbstractButton.getModel();
    if (buttonModel.isSelected()) {
      icon = getIcon(paramAbstractButton, paramAbstractButton.getRolloverSelectedIcon(), paramIcon, 514);
    } else {
      icon = getIcon(paramAbstractButton, paramAbstractButton.getRolloverIcon(), paramIcon, 2);
    } 
    return icon;
  }
  
  private Icon getPressedIcon(AbstractButton paramAbstractButton, Icon paramIcon) { return getIcon(paramAbstractButton, paramAbstractButton.getPressedIcon(), paramIcon, 4); }
  
  private Icon getSynthDisabledIcon(AbstractButton paramAbstractButton, Icon paramIcon) {
    Icon icon;
    ButtonModel buttonModel = paramAbstractButton.getModel();
    if (buttonModel.isSelected()) {
      icon = getIcon(paramAbstractButton, paramAbstractButton.getDisabledSelectedIcon(), paramIcon, 520);
    } else {
      icon = getIcon(paramAbstractButton, paramAbstractButton.getDisabledIcon(), paramIcon, 8);
    } 
    return icon;
  }
  
  private int getTextShiftOffset(SynthContext paramSynthContext) {
    AbstractButton abstractButton = (AbstractButton)paramSynthContext.getComponent();
    ButtonModel buttonModel = abstractButton.getModel();
    return (buttonModel.isArmed() && buttonModel.isPressed() && abstractButton.getPressedIcon() == null) ? paramSynthContext.getStyle().getInt(paramSynthContext, getPropertyPrefix() + "textShiftOffset", 0) : 0;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    if (paramJComponent.getComponentCount() > 0 && paramJComponent.getLayout() != null)
      return null; 
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    SynthContext synthContext = getContext(paramJComponent);
    Dimension dimension = synthContext.getStyle().getGraphicsUtils(synthContext).getMinimumSize(synthContext, synthContext.getStyle().getFont(synthContext), abstractButton.getText(), getSizingIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex());
    synthContext.dispose();
    return dimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    if (paramJComponent.getComponentCount() > 0 && paramJComponent.getLayout() != null)
      return null; 
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    SynthContext synthContext = getContext(paramJComponent);
    Dimension dimension = synthContext.getStyle().getGraphicsUtils(synthContext).getPreferredSize(synthContext, synthContext.getStyle().getFont(synthContext), abstractButton.getText(), getSizingIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex());
    synthContext.dispose();
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    if (paramJComponent.getComponentCount() > 0 && paramJComponent.getLayout() != null)
      return null; 
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    SynthContext synthContext = getContext(paramJComponent);
    Dimension dimension = synthContext.getStyle().getGraphicsUtils(synthContext).getMaximumSize(synthContext, synthContext.getStyle().getFont(synthContext), abstractButton.getText(), getSizingIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex());
    synthContext.dispose();
    return dimension;
  }
  
  protected Icon getSizingIcon(AbstractButton paramAbstractButton) {
    Icon icon = getEnabledIcon(paramAbstractButton, paramAbstractButton.getIcon());
    if (icon == null)
      icon = getDefaultIcon(paramAbstractButton); 
    return icon;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((AbstractButton)paramPropertyChangeEvent.getSource()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */