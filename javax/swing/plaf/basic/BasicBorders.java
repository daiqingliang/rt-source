package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.swing.SwingUtilities2;

public class BasicBorders {
  public static Border getButtonBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(uIDefaults.getColor("Button.shadow"), uIDefaults.getColor("Button.darkShadow"), uIDefaults.getColor("Button.light"), uIDefaults.getColor("Button.highlight")), new MarginBorder());
  }
  
  public static Border getRadioButtonBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(new RadioButtonBorder(uIDefaults.getColor("RadioButton.shadow"), uIDefaults.getColor("RadioButton.darkShadow"), uIDefaults.getColor("RadioButton.light"), uIDefaults.getColor("RadioButton.highlight")), new MarginBorder());
  }
  
  public static Border getToggleButtonBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(uIDefaults.getColor("ToggleButton.shadow"), uIDefaults.getColor("ToggleButton.darkShadow"), uIDefaults.getColor("ToggleButton.light"), uIDefaults.getColor("ToggleButton.highlight")), new MarginBorder());
  }
  
  public static Border getMenuBarBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new MenuBarBorder(uIDefaults.getColor("MenuBar.shadow"), uIDefaults.getColor("MenuBar.highlight"));
  }
  
  public static Border getSplitPaneBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new SplitPaneBorder(uIDefaults.getColor("SplitPane.highlight"), uIDefaults.getColor("SplitPane.darkShadow"));
  }
  
  public static Border getSplitPaneDividerBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new SplitPaneDividerBorder(uIDefaults.getColor("SplitPane.highlight"), uIDefaults.getColor("SplitPane.darkShadow"));
  }
  
  public static Border getTextFieldBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new FieldBorder(uIDefaults.getColor("TextField.shadow"), uIDefaults.getColor("TextField.darkShadow"), uIDefaults.getColor("TextField.light"), uIDefaults.getColor("TextField.highlight"));
  }
  
  public static Border getProgressBarBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.LineBorderUIResource(Color.green, 2);
  }
  
  public static Border getInternalFrameBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(new BevelBorder(0, uIDefaults.getColor("InternalFrame.borderLight"), uIDefaults.getColor("InternalFrame.borderHighlight"), uIDefaults.getColor("InternalFrame.borderDarkShadow"), uIDefaults.getColor("InternalFrame.borderShadow")), BorderFactory.createLineBorder(uIDefaults.getColor("InternalFrame.borderColor"), 1));
  }
  
  public static class ButtonBorder extends AbstractBorder implements UIResource {
    protected Color shadow;
    
    protected Color darkShadow;
    
    protected Color highlight;
    
    protected Color lightHighlight;
    
    public ButtonBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) {
      this.shadow = param1Color1;
      this.darkShadow = param1Color2;
      this.highlight = param1Color3;
      this.lightHighlight = param1Color4;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool1 = false;
      boolean bool2 = false;
      if (param1Component instanceof AbstractButton) {
        AbstractButton abstractButton = (AbstractButton)param1Component;
        ButtonModel buttonModel = abstractButton.getModel();
        bool1 = (buttonModel.isPressed() && buttonModel.isArmed());
        if (param1Component instanceof JButton)
          bool2 = ((JButton)param1Component).isDefaultButton(); 
      } 
      BasicGraphicsUtils.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, bool1, bool2, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 3, 3, 3);
      return param1Insets;
    }
  }
  
  public static class FieldBorder extends AbstractBorder implements UIResource {
    protected Color shadow;
    
    protected Color darkShadow;
    
    protected Color highlight;
    
    protected Color lightHighlight;
    
    public FieldBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) {
      this.shadow = param1Color1;
      this.highlight = param1Color3;
      this.darkShadow = param1Color2;
      this.lightHighlight = param1Color4;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { BasicGraphicsUtils.drawEtchedRect(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, this.shadow, this.darkShadow, this.highlight, this.lightHighlight); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      Insets insets = null;
      if (param1Component instanceof JTextComponent)
        insets = ((JTextComponent)param1Component).getMargin(); 
      param1Insets.top = (insets != null) ? (2 + insets.top) : 2;
      param1Insets.left = (insets != null) ? (2 + insets.left) : 2;
      param1Insets.bottom = (insets != null) ? (2 + insets.bottom) : 2;
      param1Insets.right = (insets != null) ? (2 + insets.right) : 2;
      return param1Insets;
    }
  }
  
  public static class MarginBorder extends AbstractBorder implements UIResource {
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      Insets insets = null;
      if (param1Component instanceof AbstractButton) {
        AbstractButton abstractButton = (AbstractButton)param1Component;
        insets = abstractButton.getMargin();
      } else if (param1Component instanceof JToolBar) {
        JToolBar jToolBar = (JToolBar)param1Component;
        insets = jToolBar.getMargin();
      } else if (param1Component instanceof JTextComponent) {
        JTextComponent jTextComponent = (JTextComponent)param1Component;
        insets = jTextComponent.getMargin();
      } 
      param1Insets.top = (insets != null) ? insets.top : 0;
      param1Insets.left = (insets != null) ? insets.left : 0;
      param1Insets.bottom = (insets != null) ? insets.bottom : 0;
      param1Insets.right = (insets != null) ? insets.right : 0;
      return param1Insets;
    }
  }
  
  public static class MenuBarBorder extends AbstractBorder implements UIResource {
    private Color shadow;
    
    private Color highlight;
    
    public MenuBarBorder(Color param1Color1, Color param1Color2) {
      this.shadow = param1Color1;
      this.highlight = param1Color2;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Color color = param1Graphics.getColor();
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(this.shadow);
      SwingUtilities2.drawHLine(param1Graphics, 0, param1Int3 - 1, param1Int4 - 2);
      param1Graphics.setColor(this.highlight);
      SwingUtilities2.drawHLine(param1Graphics, 0, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.translate(-param1Int1, -param1Int2);
      param1Graphics.setColor(color);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(0, 0, 2, 0);
      return param1Insets;
    }
  }
  
  public static class RadioButtonBorder extends ButtonBorder {
    public RadioButtonBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Color1, param1Color2, param1Color3, param1Color4); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Component instanceof AbstractButton) {
        AbstractButton abstractButton = (AbstractButton)param1Component;
        ButtonModel buttonModel = abstractButton.getModel();
        if ((buttonModel.isArmed() && buttonModel.isPressed()) || buttonModel.isSelected()) {
          BasicGraphicsUtils.drawLoweredBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
        } else {
          BasicGraphicsUtils.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false, (abstractButton.isFocusPainted() && abstractButton.hasFocus()), this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
        } 
      } else {
        BasicGraphicsUtils.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false, false, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 2, 2);
      return param1Insets;
    }
  }
  
  public static class RolloverButtonBorder extends ButtonBorder {
    public RolloverButtonBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Color1, param1Color2, param1Color3, param1Color4); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      Color color = this.shadow;
      Container container = abstractButton.getParent();
      if (container != null && container.getBackground().equals(this.shadow))
        color = this.darkShadow; 
      if ((buttonModel.isRollover() && (!buttonModel.isPressed() || buttonModel.isArmed())) || buttonModel.isSelected()) {
        Color color1 = param1Graphics.getColor();
        param1Graphics.translate(param1Int1, param1Int2);
        if ((buttonModel.isPressed() && buttonModel.isArmed()) || buttonModel.isSelected()) {
          param1Graphics.setColor(color);
          param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.setColor(this.lightHighlight);
          param1Graphics.drawLine(param1Int3 - 1, 0, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.drawLine(0, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
        } else {
          param1Graphics.setColor(this.lightHighlight);
          param1Graphics.drawRect(0, 0, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.setColor(color);
          param1Graphics.drawLine(param1Int3 - 1, 0, param1Int3 - 1, param1Int4 - 1);
          param1Graphics.drawLine(0, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
        } 
        param1Graphics.translate(-param1Int1, -param1Int2);
        param1Graphics.setColor(color1);
      } 
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
  
  public static class SplitPaneBorder implements Border, UIResource {
    protected Color highlight;
    
    protected Color shadow;
    
    public SplitPaneBorder(Color param1Color1, Color param1Color2) {
      this.highlight = param1Color1;
      this.shadow = param1Color2;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JSplitPane))
        return; 
      JSplitPane jSplitPane = (JSplitPane)param1Component;
      Component component = jSplitPane.getLeftComponent();
      param1Graphics.setColor(param1Component.getBackground());
      param1Graphics.drawRect(param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
      if (jSplitPane.getOrientation() == 1) {
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(0, 0, rectangle.width + 1, 0);
          param1Graphics.drawLine(0, 1, 0, rectangle.height + 1);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(0, rectangle.height + 1, rectangle.width + 1, rectangle.height + 1);
        } 
        component = jSplitPane.getRightComponent();
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          int i = rectangle.x + rectangle.width;
          int j = rectangle.y + rectangle.height;
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(rectangle.x - 1, 0, i, 0);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(rectangle.x - 1, j, i, j);
          param1Graphics.drawLine(i, 0, i, j + 1);
        } 
      } else {
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(0, 0, rectangle.width + 1, 0);
          param1Graphics.drawLine(0, 1, 0, rectangle.height);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(1 + rectangle.width, 0, 1 + rectangle.width, rectangle.height + 1);
          param1Graphics.drawLine(0, rectangle.height + 1, 0, rectangle.height + 1);
        } 
        component = jSplitPane.getRightComponent();
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          int i = rectangle.x + rectangle.width;
          int j = rectangle.y + rectangle.height;
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(0, rectangle.y - 1, 0, j);
          param1Graphics.drawLine(i, rectangle.y - 1, i, rectangle.y - 1);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(0, j, rectangle.width + 1, j);
          param1Graphics.drawLine(i, rectangle.y, i, j);
        } 
      } 
    }
    
    public Insets getBorderInsets(Component param1Component) { return new Insets(1, 1, 1, 1); }
    
    public boolean isBorderOpaque() { return true; }
  }
  
  static class SplitPaneDividerBorder implements Border, UIResource {
    Color highlight;
    
    Color shadow;
    
    SplitPaneDividerBorder(Color param1Color1, Color param1Color2) {
      this.highlight = param1Color1;
      this.shadow = param1Color2;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof BasicSplitPaneDivider))
        return; 
      JSplitPane jSplitPane = ((BasicSplitPaneDivider)param1Component).getBasicSplitPaneUI().getSplitPane();
      Dimension dimension = param1Component.getSize();
      Component component = jSplitPane.getLeftComponent();
      param1Graphics.setColor(param1Component.getBackground());
      param1Graphics.drawRect(param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
      if (jSplitPane.getOrientation() == 1) {
        if (component != null) {
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(0, 0, 0, dimension.height);
        } 
        component = jSplitPane.getRightComponent();
        if (component != null) {
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(dimension.width - 1, 0, dimension.width - 1, dimension.height);
        } 
      } else {
        if (component != null) {
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(0, 0, dimension.width, 0);
        } 
        component = jSplitPane.getRightComponent();
        if (component != null) {
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(0, dimension.height - 1, dimension.width, dimension.height - 1);
        } 
      } 
    }
    
    public Insets getBorderInsets(Component param1Component) {
      Insets insets = new Insets(0, 0, 0, 0);
      if (param1Component instanceof BasicSplitPaneDivider) {
        BasicSplitPaneUI basicSplitPaneUI = ((BasicSplitPaneDivider)param1Component).getBasicSplitPaneUI();
        if (basicSplitPaneUI != null) {
          JSplitPane jSplitPane = basicSplitPaneUI.getSplitPane();
          if (jSplitPane != null) {
            if (jSplitPane.getOrientation() == 1) {
              insets.top = insets.bottom = 0;
              insets.left = insets.right = 1;
              return insets;
            } 
            insets.top = insets.bottom = 1;
            insets.left = insets.right = 0;
            return insets;
          } 
        } 
      } 
      insets.top = insets.bottom = insets.left = insets.right = 1;
      return insets;
    }
    
    public boolean isBorderOpaque() { return true; }
  }
  
  public static class ToggleButtonBorder extends ButtonBorder {
    public ToggleButtonBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Color1, param1Color2, param1Color3, param1Color4); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { BasicGraphicsUtils.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false, false, this.shadow, this.darkShadow, this.highlight, this.lightHighlight); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 2, 2);
      return param1Insets;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */