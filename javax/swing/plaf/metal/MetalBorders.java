package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingUtilities2;

public class MetalBorders {
  static Object NO_BUTTON_ROLLOVER = new StringUIClientPropertyKey("NoButtonRollover");
  
  private static Border buttonBorder;
  
  private static Border textBorder;
  
  private static Border textFieldBorder;
  
  private static Border toggleButtonBorder;
  
  public static Border getButtonBorder() {
    if (buttonBorder == null)
      buttonBorder = new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(), new BasicBorders.MarginBorder()); 
    return buttonBorder;
  }
  
  public static Border getTextBorder() {
    if (textBorder == null)
      textBorder = new BorderUIResource.CompoundBorderUIResource(new Flush3DBorder(), new BasicBorders.MarginBorder()); 
    return textBorder;
  }
  
  public static Border getTextFieldBorder() {
    if (textFieldBorder == null)
      textFieldBorder = new BorderUIResource.CompoundBorderUIResource(new TextFieldBorder(), new BasicBorders.MarginBorder()); 
    return textFieldBorder;
  }
  
  public static Border getToggleButtonBorder() {
    if (toggleButtonBorder == null)
      toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(), new BasicBorders.MarginBorder()); 
    return toggleButtonBorder;
  }
  
  public static Border getDesktopIconBorder() { return new BorderUIResource.CompoundBorderUIResource(new LineBorder(MetalLookAndFeel.getControlDarkShadow(), 1), new MatteBorder(2, 2, 1, 2, MetalLookAndFeel.getControl())); }
  
  static Border getToolBarRolloverBorder() { return MetalLookAndFeel.usingOcean() ? new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder()) : new CompoundBorder(new RolloverButtonBorder(), new RolloverMarginBorder()); }
  
  static Border getToolBarNonrolloverBorder() {
    if (MetalLookAndFeel.usingOcean())
      new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder()); 
    return new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
  }
  
  public static class ButtonBorder extends AbstractBorder implements UIResource {
    protected static Insets borderInsets = new Insets(3, 3, 3, 3);
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof AbstractButton))
        return; 
      if (MetalLookAndFeel.usingOcean()) {
        paintOceanBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        return;
      } 
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      if (buttonModel.isEnabled()) {
        boolean bool1 = (buttonModel.isPressed() && buttonModel.isArmed()) ? 1 : 0;
        boolean bool2 = (abstractButton instanceof JButton && ((JButton)abstractButton).isDefaultButton()) ? 1 : 0;
        if (bool1 && bool2) {
          MetalUtils.drawDefaultButtonPressedBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        } else if (bool1) {
          MetalUtils.drawPressed3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        } else if (bool2) {
          MetalUtils.drawDefaultButtonBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false);
        } else {
          MetalUtils.drawButtonBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false);
        } 
      } else {
        MetalUtils.drawDisabledBorder(param1Graphics, param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
      } 
    }
    
    private void paintOceanBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = ((AbstractButton)param1Component).getModel();
      param1Graphics.translate(param1Int1, param1Int2);
      if (MetalUtils.isToolBarButton(abstractButton)) {
        if (buttonModel.isEnabled()) {
          if (buttonModel.isPressed()) {
            param1Graphics.setColor(MetalLookAndFeel.getWhite());
            param1Graphics.fillRect(1, param1Int4 - 1, param1Int3 - 1, 1);
            param1Graphics.fillRect(param1Int3 - 1, 1, 1, param1Int4 - 1);
            param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            param1Graphics.drawRect(0, 0, param1Int3 - 2, param1Int4 - 2);
            param1Graphics.fillRect(1, 1, param1Int3 - 3, 1);
          } else if (buttonModel.isSelected() || buttonModel.isRollover()) {
            param1Graphics.setColor(MetalLookAndFeel.getWhite());
            param1Graphics.fillRect(1, param1Int4 - 1, param1Int3 - 1, 1);
            param1Graphics.fillRect(param1Int3 - 1, 1, 1, param1Int4 - 1);
            param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            param1Graphics.drawRect(0, 0, param1Int3 - 2, param1Int4 - 2);
          } else {
            param1Graphics.setColor(MetalLookAndFeel.getWhite());
            param1Graphics.drawRect(1, 1, param1Int3 - 2, param1Int4 - 2);
            param1Graphics.setColor(UIManager.getColor("Button.toolBarBorderBackground"));
            param1Graphics.drawRect(0, 0, param1Int3 - 2, param1Int4 - 2);
          } 
        } else {
          param1Graphics.setColor(UIManager.getColor("Button.disabledToolBarBorderBackground"));
          param1Graphics.drawRect(0, 0, param1Int3 - 2, param1Int4 - 2);
        } 
      } else if (buttonModel.isEnabled()) {
        boolean bool1 = buttonModel.isPressed();
        boolean bool2 = buttonModel.isArmed();
        if (param1Component instanceof JButton && ((JButton)param1Component).isDefaultButton()) {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.drawRect(1, 1, param1Int3 - 3, param1Int4 - 3);
        } else if (bool1) {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.fillRect(0, 0, param1Int3, 2);
          param1Graphics.fillRect(0, 2, 2, param1Int4 - 2);
          param1Graphics.fillRect(param1Int3 - 1, 1, 1, param1Int4 - 1);
          param1Graphics.fillRect(1, param1Int4 - 1, param1Int3 - 2, 1);
        } else if (buttonModel.isRollover() && abstractButton.getClientProperty(MetalBorders.NO_BUTTON_ROLLOVER) == null) {
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
          param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.drawRect(2, 2, param1Int3 - 5, param1Int4 - 5);
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawRect(1, 1, param1Int3 - 3, param1Int4 - 3);
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
        } 
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getInactiveControlTextColor());
        param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
        if (param1Component instanceof JButton && ((JButton)param1Component).isDefaultButton())
          param1Graphics.drawRect(1, 1, param1Int3 - 3, param1Int4 - 3); 
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(3, 3, 3, 3);
      return param1Insets;
    }
  }
  
  static class DialogBorder extends AbstractBorder implements UIResource {
    private static final int corner = 14;
    
    protected Color getActiveBackground() { return MetalLookAndFeel.getPrimaryControlDarkShadow(); }
    
    protected Color getActiveHighlight() { return MetalLookAndFeel.getPrimaryControlShadow(); }
    
    protected Color getActiveShadow() { return MetalLookAndFeel.getPrimaryControlInfo(); }
    
    protected Color getInactiveBackground() { return MetalLookAndFeel.getControlDarkShadow(); }
    
    protected Color getInactiveHighlight() { return MetalLookAndFeel.getControlShadow(); }
    
    protected Color getInactiveShadow() { return MetalLookAndFeel.getControlInfo(); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Color color3;
      Color color2;
      Color color1;
      Window window = SwingUtilities.getWindowAncestor(param1Component);
      if (window != null && window.isActive()) {
        color1 = getActiveBackground();
        color2 = getActiveHighlight();
        color3 = getActiveShadow();
      } else {
        color1 = getInactiveBackground();
        color2 = getInactiveHighlight();
        color3 = getInactiveShadow();
      } 
      param1Graphics.setColor(color1);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 0, param1Int1 + param1Int3 - 2, param1Int2 + 0);
      param1Graphics.drawLine(param1Int1 + 0, param1Int2 + 1, param1Int1 + 0, param1Int2 + param1Int4 - 2);
      param1Graphics.drawLine(param1Int1 + param1Int3 - 1, param1Int2 + 1, param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 - 2);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + param1Int4 - 1, param1Int1 + param1Int3 - 2, param1Int2 + param1Int4 - 1);
      for (int i = 1; i < 5; i++)
        param1Graphics.drawRect(param1Int1 + i, param1Int2 + i, param1Int3 - i * 2 - 1, param1Int4 - i * 2 - 1); 
      if (window instanceof Dialog && ((Dialog)window).isResizable()) {
        param1Graphics.setColor(color2);
        param1Graphics.drawLine(15, 3, param1Int3 - 14, 3);
        param1Graphics.drawLine(3, 15, 3, param1Int4 - 14);
        param1Graphics.drawLine(param1Int3 - 2, 15, param1Int3 - 2, param1Int4 - 14);
        param1Graphics.drawLine(15, param1Int4 - 2, param1Int3 - 14, param1Int4 - 2);
        param1Graphics.setColor(color3);
        param1Graphics.drawLine(14, 2, param1Int3 - 14 - 1, 2);
        param1Graphics.drawLine(2, 14, 2, param1Int4 - 14 - 1);
        param1Graphics.drawLine(param1Int3 - 3, 14, param1Int3 - 3, param1Int4 - 14 - 1);
        param1Graphics.drawLine(14, param1Int4 - 3, param1Int3 - 14 - 1, param1Int4 - 3);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(5, 5, 5, 5);
      return param1Insets;
    }
  }
  
  static class ErrorDialogBorder extends DialogBorder implements UIResource {
    protected Color getActiveBackground() { return UIManager.getColor("OptionPane.errorDialog.border.background"); }
  }
  
  public static class Flush3DBorder extends AbstractBorder implements UIResource {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Component.isEnabled()) {
        MetalUtils.drawFlush3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } else {
        MetalUtils.drawDisabledBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 2, 2);
      return param1Insets;
    }
  }
  
  static class FrameBorder extends AbstractBorder implements UIResource {
    private static final int corner = 14;
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      ColorUIResource colorUIResource3;
      ColorUIResource colorUIResource2;
      ColorUIResource colorUIResource1;
      Window window = SwingUtilities.getWindowAncestor(param1Component);
      if (window != null && window.isActive()) {
        colorUIResource1 = MetalLookAndFeel.getPrimaryControlDarkShadow();
        colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        colorUIResource3 = MetalLookAndFeel.getPrimaryControlInfo();
      } else {
        colorUIResource1 = MetalLookAndFeel.getControlDarkShadow();
        colorUIResource2 = MetalLookAndFeel.getControlShadow();
        colorUIResource3 = MetalLookAndFeel.getControlInfo();
      } 
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 0, param1Int1 + param1Int3 - 2, param1Int2 + 0);
      param1Graphics.drawLine(param1Int1 + 0, param1Int2 + 1, param1Int1 + 0, param1Int2 + param1Int4 - 2);
      param1Graphics.drawLine(param1Int1 + param1Int3 - 1, param1Int2 + 1, param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 - 2);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + param1Int4 - 1, param1Int1 + param1Int3 - 2, param1Int2 + param1Int4 - 1);
      for (int i = 1; i < 5; i++)
        param1Graphics.drawRect(param1Int1 + i, param1Int2 + i, param1Int3 - i * 2 - 1, param1Int4 - i * 2 - 1); 
      if (window instanceof Frame && ((Frame)window).isResizable()) {
        param1Graphics.setColor(colorUIResource2);
        param1Graphics.drawLine(15, 3, param1Int3 - 14, 3);
        param1Graphics.drawLine(3, 15, 3, param1Int4 - 14);
        param1Graphics.drawLine(param1Int3 - 2, 15, param1Int3 - 2, param1Int4 - 14);
        param1Graphics.drawLine(15, param1Int4 - 2, param1Int3 - 14, param1Int4 - 2);
        param1Graphics.setColor(colorUIResource3);
        param1Graphics.drawLine(14, 2, param1Int3 - 14 - 1, 2);
        param1Graphics.drawLine(2, 14, 2, param1Int4 - 14 - 1);
        param1Graphics.drawLine(param1Int3 - 3, 14, param1Int3 - 3, param1Int4 - 14 - 1);
        param1Graphics.drawLine(14, param1Int4 - 3, param1Int3 - 14 - 1, param1Int4 - 3);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(5, 5, 5, 5);
      return param1Insets;
    }
  }
  
  public static class InternalFrameBorder extends AbstractBorder implements UIResource {
    private static final int corner = 14;
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      ColorUIResource colorUIResource3;
      ColorUIResource colorUIResource2;
      ColorUIResource colorUIResource1;
      if (param1Component instanceof JInternalFrame && ((JInternalFrame)param1Component).isSelected()) {
        colorUIResource1 = MetalLookAndFeel.getPrimaryControlDarkShadow();
        colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        colorUIResource3 = MetalLookAndFeel.getPrimaryControlInfo();
      } else {
        colorUIResource1 = MetalLookAndFeel.getControlDarkShadow();
        colorUIResource2 = MetalLookAndFeel.getControlShadow();
        colorUIResource3 = MetalLookAndFeel.getControlInfo();
      } 
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.drawLine(1, 0, param1Int3 - 2, 0);
      param1Graphics.drawLine(0, 1, 0, param1Int4 - 2);
      param1Graphics.drawLine(param1Int3 - 1, 1, param1Int3 - 1, param1Int4 - 2);
      param1Graphics.drawLine(1, param1Int4 - 1, param1Int3 - 2, param1Int4 - 1);
      for (int i = 1; i < 5; i++)
        param1Graphics.drawRect(param1Int1 + i, param1Int2 + i, param1Int3 - i * 2 - 1, param1Int4 - i * 2 - 1); 
      if (param1Component instanceof JInternalFrame && ((JInternalFrame)param1Component).isResizable()) {
        param1Graphics.setColor(colorUIResource2);
        param1Graphics.drawLine(15, 3, param1Int3 - 14, 3);
        param1Graphics.drawLine(3, 15, 3, param1Int4 - 14);
        param1Graphics.drawLine(param1Int3 - 2, 15, param1Int3 - 2, param1Int4 - 14);
        param1Graphics.drawLine(15, param1Int4 - 2, param1Int3 - 14, param1Int4 - 2);
        param1Graphics.setColor(colorUIResource3);
        param1Graphics.drawLine(14, 2, param1Int3 - 14 - 1, 2);
        param1Graphics.drawLine(2, 14, 2, param1Int4 - 14 - 1);
        param1Graphics.drawLine(param1Int3 - 3, 14, param1Int3 - 3, param1Int4 - 14 - 1);
        param1Graphics.drawLine(14, param1Int4 - 3, param1Int3 - 14 - 1, param1Int4 - 3);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(5, 5, 5, 5);
      return param1Insets;
    }
  }
  
  public static class MenuBarBorder extends AbstractBorder implements UIResource {
    protected static Insets borderInsets = new Insets(1, 0, 1, 0);
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.translate(param1Int1, param1Int2);
      if (MetalLookAndFeel.usingOcean()) {
        if (param1Component instanceof JMenuBar && !MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)param1Component)) {
          param1Graphics.setColor(MetalLookAndFeel.getControl());
          SwingUtilities2.drawHLine(param1Graphics, 0, param1Int3 - 1, param1Int4 - 2);
          param1Graphics.setColor(UIManager.getColor("MenuBar.borderColor"));
          SwingUtilities2.drawHLine(param1Graphics, 0, param1Int3 - 1, param1Int4 - 1);
        } 
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControlShadow());
        SwingUtilities2.drawHLine(param1Graphics, 0, param1Int3 - 1, param1Int4 - 1);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      if (MetalLookAndFeel.usingOcean()) {
        param1Insets.set(0, 0, 2, 0);
      } else {
        param1Insets.set(1, 0, 1, 0);
      } 
      return param1Insets;
    }
  }
  
  public static class MenuItemBorder extends AbstractBorder implements UIResource {
    protected static Insets borderInsets = new Insets(2, 2, 2, 2);
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JMenuItem))
        return; 
      JMenuItem jMenuItem = (JMenuItem)param1Component;
      ButtonModel buttonModel = jMenuItem.getModel();
      param1Graphics.translate(param1Int1, param1Int2);
      if (param1Component.getParent() instanceof JMenuBar) {
        if (buttonModel.isArmed() || buttonModel.isSelected()) {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawLine(0, 0, param1Int3 - 2, 0);
          param1Graphics.drawLine(0, 0, 0, param1Int4 - 1);
          param1Graphics.drawLine(param1Int3 - 2, 2, param1Int3 - 2, param1Int4 - 1);
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
          param1Graphics.drawLine(param1Int3 - 1, 1, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.setColor(MetalLookAndFeel.getMenuBackground());
          param1Graphics.drawLine(param1Int3 - 1, 0, param1Int3 - 1, 0);
        } 
      } else if (buttonModel.isArmed() || (param1Component instanceof javax.swing.JMenu && buttonModel.isSelected())) {
        param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        param1Graphics.drawLine(0, 0, param1Int3 - 1, 0);
        param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
        param1Graphics.drawLine(0, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
        param1Graphics.drawLine(0, 0, 0, param1Int4 - 1);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 2, 2);
      return param1Insets;
    }
  }
  
  public static class OptionDialogBorder extends AbstractBorder implements UIResource {
    int titleHeight = 0;
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Color color;
      param1Graphics.translate(param1Int1, param1Int2);
      int i = -1;
      if (param1Component instanceof JInternalFrame) {
        Object object = ((JInternalFrame)param1Component).getClientProperty("JInternalFrame.messageType");
        if (object instanceof Integer)
          i = ((Integer)object).intValue(); 
      } 
      switch (i) {
        case 0:
          color = UIManager.getColor("OptionPane.errorDialog.border.background");
          break;
        case 3:
          color = UIManager.getColor("OptionPane.questionDialog.border.background");
          break;
        case 2:
          color = UIManager.getColor("OptionPane.warningDialog.border.background");
          break;
        default:
          color = MetalLookAndFeel.getPrimaryControlDarkShadow();
          break;
      } 
      param1Graphics.setColor(color);
      param1Graphics.drawLine(1, 0, param1Int3 - 2, 0);
      param1Graphics.drawLine(0, 1, 0, param1Int4 - 2);
      param1Graphics.drawLine(param1Int3 - 1, 1, param1Int3 - 1, param1Int4 - 2);
      param1Graphics.drawLine(1, param1Int4 - 1, param1Int3 - 2, param1Int4 - 1);
      for (byte b = 1; b < 3; b++)
        param1Graphics.drawRect(b, b, param1Int3 - b * 2 - 1, param1Int4 - b * 2 - 1); 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(3, 3, 3, 3);
      return param1Insets;
    }
  }
  
  public static class PaletteBorder extends AbstractBorder implements UIResource {
    int titleHeight = 0;
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      param1Graphics.drawLine(0, 1, 0, param1Int4 - 2);
      param1Graphics.drawLine(1, param1Int4 - 1, param1Int3 - 2, param1Int4 - 1);
      param1Graphics.drawLine(param1Int3 - 1, 1, param1Int3 - 1, param1Int4 - 2);
      param1Graphics.drawLine(1, 0, param1Int3 - 2, 0);
      param1Graphics.drawRect(1, 1, param1Int3 - 3, param1Int4 - 3);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(1, 1, 1, 1);
      return param1Insets;
    }
  }
  
  public static class PopupMenuBorder extends AbstractBorder implements UIResource {
    protected static Insets borderInsets = new Insets(3, 1, 2, 1);
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(1, 1, param1Int3 - 2, 1);
      param1Graphics.drawLine(1, 2, 1, 2);
      param1Graphics.drawLine(1, param1Int4 - 2, 1, param1Int4 - 2);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(3, 1, 2, 1);
      return param1Insets;
    }
  }
  
  static class QuestionDialogBorder extends DialogBorder implements UIResource {
    protected Color getActiveBackground() { return UIManager.getColor("OptionPane.questionDialog.border.background"); }
  }
  
  public static class RolloverButtonBorder extends ButtonBorder {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      if (buttonModel.isRollover() && (!buttonModel.isPressed() || buttonModel.isArmed()))
        super.paintBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
  }
  
  static class RolloverMarginBorder extends EmptyBorder {
    public RolloverMarginBorder() { super(3, 3, 3, 3); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      Insets insets = null;
      if (param1Component instanceof AbstractButton)
        insets = ((AbstractButton)param1Component).getMargin(); 
      if (insets == null || insets instanceof UIResource) {
        param1Insets.left = this.left;
        param1Insets.top = this.top;
        param1Insets.right = this.right;
        param1Insets.bottom = this.bottom;
      } else {
        param1Insets.left = insets.left;
        param1Insets.top = insets.top;
        param1Insets.right = insets.right;
        param1Insets.bottom = insets.bottom;
      } 
      return param1Insets;
    }
  }
  
  public static class ScrollPaneBorder extends AbstractBorder implements UIResource {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JScrollPane))
        return; 
      JScrollPane jScrollPane = (JScrollPane)param1Component;
      JViewport jViewport1 = jScrollPane.getColumnHeader();
      int i = 0;
      if (jViewport1 != null)
        i = jViewport1.getHeight(); 
      JViewport jViewport2 = jScrollPane.getRowHeader();
      int j = 0;
      if (jViewport2 != null)
        j = jViewport2.getWidth(); 
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      param1Graphics.drawRect(0, 0, param1Int3 - 2, param1Int4 - 2);
      param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
      param1Graphics.drawLine(param1Int3 - 1, 1, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.drawLine(1, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.setColor(MetalLookAndFeel.getControl());
      param1Graphics.drawLine(param1Int3 - 2, 2 + i, param1Int3 - 2, 2 + i);
      param1Graphics.drawLine(1 + j, param1Int4 - 2, 1 + j, param1Int4 - 2);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(1, 1, 2, 2);
      return param1Insets;
    }
  }
  
  public static class TableHeaderBorder extends AbstractBorder {
    protected Insets editorBorderInsets = new Insets(2, 2, 2, 0);
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      param1Graphics.drawLine(param1Int3 - 1, 0, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.drawLine(1, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
      param1Graphics.drawLine(0, 0, param1Int3 - 2, 0);
      param1Graphics.drawLine(0, 0, 0, param1Int4 - 2);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 2, 0);
      return param1Insets;
    }
  }
  
  public static class TextFieldBorder extends Flush3DBorder {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JTextComponent)) {
        if (param1Component.isEnabled()) {
          MetalUtils.drawFlush3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        } else {
          MetalUtils.drawDisabledBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        } 
        return;
      } 
      if (param1Component.isEnabled() && ((JTextComponent)param1Component).isEditable()) {
        MetalUtils.drawFlush3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } else {
        MetalUtils.drawDisabledBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } 
    }
  }
  
  public static class ToggleButtonBorder extends ButtonBorder {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      if (MetalLookAndFeel.usingOcean()) {
        if (buttonModel.isArmed() || !abstractButton.isEnabled()) {
          super.paintBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
        } 
        return;
      } 
      if (!param1Component.isEnabled()) {
        MetalUtils.drawDisabledBorder(param1Graphics, param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        MetalUtils.drawPressed3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } else if (buttonModel.isSelected()) {
        MetalUtils.drawDark3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } else {
        MetalUtils.drawFlush3DBorder(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } 
    }
  }
  
  public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
    protected MetalBumps bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), UIManager.getColor("ToolBar.background"));
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JToolBar))
        return; 
      param1Graphics.translate(param1Int1, param1Int2);
      if (((JToolBar)param1Component).isFloatable())
        if (((JToolBar)param1Component).getOrientation() == 0) {
          byte b = MetalLookAndFeel.usingOcean() ? -1 : 0;
          this.bumps.setBumpArea(10, param1Int4 - 4);
          if (MetalUtils.isLeftToRight(param1Component)) {
            this.bumps.paintIcon(param1Component, param1Graphics, 2, 2 + b);
          } else {
            this.bumps.paintIcon(param1Component, param1Graphics, param1Int3 - 12, 2 + b);
          } 
        } else {
          this.bumps.setBumpArea(param1Int3 - 4, 10);
          this.bumps.paintIcon(param1Component, param1Graphics, 2, 2);
        }  
      if (((JToolBar)param1Component).getOrientation() == 0 && MetalLookAndFeel.usingOcean()) {
        param1Graphics.setColor(MetalLookAndFeel.getControl());
        param1Graphics.drawLine(0, param1Int4 - 2, param1Int3, param1Int4 - 2);
        param1Graphics.setColor(UIManager.getColor("ToolBar.borderColor"));
        param1Graphics.drawLine(0, param1Int4 - 1, param1Int3, param1Int4 - 1);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      if (MetalLookAndFeel.usingOcean()) {
        param1Insets.set(1, 2, 3, 2);
      } else {
        param1Insets.top = param1Insets.left = param1Insets.bottom = param1Insets.right = 2;
      } 
      if (!(param1Component instanceof JToolBar))
        return param1Insets; 
      if (((JToolBar)param1Component).isFloatable())
        if (((JToolBar)param1Component).getOrientation() == 0) {
          if (param1Component.getComponentOrientation().isLeftToRight()) {
            param1Insets.left = 16;
          } else {
            param1Insets.right = 16;
          } 
        } else {
          param1Insets.top = 16;
        }  
      Insets insets = ((JToolBar)param1Component).getMargin();
      if (insets != null) {
        param1Insets.left += insets.left;
        param1Insets.top += insets.top;
        param1Insets.right += insets.right;
        param1Insets.bottom += insets.bottom;
      } 
      return param1Insets;
    }
  }
  
  static class WarningDialogBorder extends DialogBorder implements UIResource {
    protected Color getActiveBackground() { return UIManager.getColor("OptionPane.warningDialog.border.background"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */