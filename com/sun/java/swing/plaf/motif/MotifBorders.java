package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class MotifBorders {
  public static void drawBezel(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) {
    Color color = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    if (paramBoolean1) {
      if (paramBoolean2) {
        paramGraphics.setColor(paramColor4);
        paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
      } 
      paramGraphics.setColor(paramColor1);
      paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
      paramGraphics.setColor(paramColor2);
      paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
      paramGraphics.drawLine(paramInt3 - 3, 2, paramInt3 - 3, paramInt4 - 4);
    } else {
      if (paramBoolean2) {
        paramGraphics.setColor(paramColor4);
        paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
        paramGraphics.setColor(paramColor2);
        paramGraphics.drawLine(1, 1, 1, paramInt4 - 3);
        paramGraphics.drawLine(2, 1, paramInt3 - 4, 1);
        paramGraphics.setColor(paramColor1);
        paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
        paramGraphics.drawLine(paramInt3 - 3, 1, paramInt3 - 3, paramInt4 - 4);
        paramGraphics.setColor(paramColor3);
        paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
        paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, 1);
      } else {
        paramGraphics.setColor(paramColor2);
        paramGraphics.drawLine(1, 1, 1, paramInt4 - 3);
        paramGraphics.drawLine(2, 1, paramInt3 - 4, 1);
        paramGraphics.setColor(paramColor1);
        paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
        paramGraphics.drawLine(paramInt3 - 3, 1, paramInt3 - 3, paramInt4 - 4);
        paramGraphics.setColor(paramColor3);
        paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
        paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, 0);
      } 
      paramGraphics.translate(-paramInt1, -paramInt2);
    } 
    paramGraphics.setColor(color);
  }
  
  public static class BevelBorder extends AbstractBorder implements UIResource {
    private Color darkShadow = UIManager.getColor("controlShadow");
    
    private Color lightShadow = UIManager.getColor("controlLtHighlight");
    
    private boolean isRaised;
    
    public BevelBorder(boolean param1Boolean, Color param1Color1, Color param1Color2) {
      this.isRaised = param1Boolean;
      this.darkShadow = param1Color1;
      this.lightShadow = param1Color2;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.setColor(this.isRaised ? this.lightShadow : this.darkShadow);
      param1Graphics.drawLine(param1Int1, param1Int2, param1Int1 + param1Int3 - 1, param1Int2);
      param1Graphics.drawLine(param1Int1, param1Int2 + param1Int4 - 1, param1Int1, param1Int2 + 1);
      param1Graphics.setColor(this.isRaised ? this.darkShadow : this.lightShadow);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + param1Int4 - 1, param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 - 1);
      param1Graphics.drawLine(param1Int1 + param1Int3 - 1, param1Int2 + param1Int4 - 1, param1Int1 + param1Int3 - 1, param1Int2 + 1);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(1, 1, 1, 1);
      return param1Insets;
    }
    
    public boolean isOpaque(Component param1Component) { return true; }
  }
  
  public static class ButtonBorder extends AbstractBorder implements UIResource {
    protected Color focus = UIManager.getColor("activeCaptionBorder");
    
    protected Color shadow = UIManager.getColor("Button.shadow");
    
    protected Color highlight = UIManager.getColor("Button.light");
    
    protected Color darkShadow;
    
    public ButtonBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) {
      this.shadow = param1Color1;
      this.highlight = param1Color2;
      this.darkShadow = param1Color3;
      this.focus = param1Color4;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      if (param1Component instanceof AbstractButton) {
        AbstractButton abstractButton = (AbstractButton)param1Component;
        ButtonModel buttonModel = abstractButton.getModel();
        bool1 = (buttonModel.isArmed() && buttonModel.isPressed()) ? 1 : 0;
        bool2 = ((buttonModel.isArmed() && bool1) || (abstractButton.isFocusPainted() && abstractButton.hasFocus())) ? 1 : 0;
        if (abstractButton instanceof JButton) {
          bool3 = ((JButton)abstractButton).isDefaultCapable();
          bool4 = ((JButton)abstractButton).isDefaultButton();
        } 
      } 
      int i = param1Int1 + 1;
      int j = param1Int2 + 1;
      int k = param1Int1 + param1Int3 - 2;
      int m = param1Int2 + param1Int4 - 2;
      if (bool3) {
        if (bool4) {
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 3, param1Int1 + 3, param1Int2 + param1Int4 - 4);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 3, param1Int1 + param1Int3 - 4, param1Int2 + 3);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(param1Int1 + 4, param1Int2 + param1Int4 - 4, param1Int1 + param1Int3 - 4, param1Int2 + param1Int4 - 4);
          param1Graphics.drawLine(param1Int1 + param1Int3 - 4, param1Int2 + 3, param1Int1 + param1Int3 - 4, param1Int2 + param1Int4 - 4);
        } 
        i += 6;
        j += 6;
        k -= 6;
        m -= 6;
      } 
      if (bool2) {
        param1Graphics.setColor(this.focus);
        if (bool4) {
          param1Graphics.drawRect(param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
        } else {
          param1Graphics.drawRect(i - 1, j - 1, k - i + 2, m - j + 2);
        } 
      } 
      param1Graphics.setColor(bool1 ? this.shadow : this.highlight);
      param1Graphics.drawLine(i, j, k, j);
      param1Graphics.drawLine(i, j, i, m);
      param1Graphics.setColor(bool1 ? this.highlight : this.shadow);
      param1Graphics.drawLine(k, j + 1, k, m);
      param1Graphics.drawLine(i + 1, m, k, m);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      byte b = (param1Component instanceof JButton && ((JButton)param1Component).isDefaultCapable()) ? 8 : 2;
      param1Insets.set(b, b, b, b);
      return param1Insets;
    }
  }
  
  public static class FocusBorder extends AbstractBorder implements UIResource {
    private Color focus;
    
    private Color control;
    
    public FocusBorder(Color param1Color1, Color param1Color2) {
      this.control = param1Color1;
      this.focus = param1Color2;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Component.hasFocus()) {
        param1Graphics.setColor(this.focus);
        param1Graphics.drawRect(param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
      } else {
        param1Graphics.setColor(this.control);
        param1Graphics.drawRect(param1Int1, param1Int2, param1Int3 - 1, param1Int4 - 1);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(1, 1, 1, 1);
      return param1Insets;
    }
  }
  
  public static class FrameBorder extends AbstractBorder implements UIResource {
    JComponent jcomp;
    
    Color frameHighlight;
    
    Color frameColor;
    
    Color frameShadow;
    
    public static final int BORDER_SIZE = 5;
    
    public FrameBorder(JComponent param1JComponent) { this.jcomp = param1JComponent; }
    
    public void setComponent(JComponent param1JComponent) { this.jcomp = param1JComponent; }
    
    public JComponent component() { return this.jcomp; }
    
    protected Color getFrameHighlight() { return this.frameHighlight; }
    
    protected Color getFrameColor() { return this.frameColor; }
    
    protected Color getFrameShadow() { return this.frameShadow; }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(5, 5, 5, 5);
      return param1Insets;
    }
    
    protected boolean drawTopBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = new Rectangle(param1Int1, param1Int2, param1Int3, 5);
      if (!param1Graphics.getClipBounds().intersects(rectangle))
        return false; 
      int i = param1Int3 - 1;
      byte b = 4;
      param1Graphics.setColor(this.frameColor);
      param1Graphics.drawLine(param1Int1, param1Int2 + 2, i - 2, param1Int2 + 2);
      param1Graphics.drawLine(param1Int1, param1Int2 + 3, i - 2, param1Int2 + 3);
      param1Graphics.drawLine(param1Int1, param1Int2 + 4, i - 2, param1Int2 + 4);
      param1Graphics.setColor(this.frameHighlight);
      param1Graphics.drawLine(param1Int1, param1Int2, i, param1Int2);
      param1Graphics.drawLine(param1Int1, param1Int2 + 1, i, param1Int2 + 1);
      param1Graphics.drawLine(param1Int1, param1Int2 + 2, param1Int1, param1Int2 + 4);
      param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 2, param1Int1 + 1, param1Int2 + 4);
      param1Graphics.setColor(this.frameShadow);
      param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 4, i - 4, param1Int2 + 4);
      param1Graphics.drawLine(i, param1Int2 + 1, i, b);
      param1Graphics.drawLine(i - 1, param1Int2 + 2, i - 1, b);
      return true;
    }
    
    protected boolean drawLeftBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = new Rectangle(0, 0, (getBorderInsets(param1Component)).left, param1Int4);
      if (!param1Graphics.getClipBounds().intersects(rectangle))
        return false; 
      byte b = 5;
      param1Graphics.setColor(this.frameHighlight);
      param1Graphics.drawLine(param1Int1, b, param1Int1, param1Int4 - 1);
      param1Graphics.drawLine(param1Int1 + 1, b, param1Int1 + 1, param1Int4 - 2);
      param1Graphics.setColor(this.frameColor);
      param1Graphics.fillRect(param1Int1 + 2, b, param1Int1 + 2, param1Int4 - 3);
      param1Graphics.setColor(this.frameShadow);
      param1Graphics.drawLine(param1Int1 + 4, b, param1Int1 + 4, param1Int4 - 5);
      return true;
    }
    
    protected boolean drawRightBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = new Rectangle(param1Int3 - (getBorderInsets(param1Component)).right, 0, (getBorderInsets(param1Component)).right, param1Int4);
      if (!param1Graphics.getClipBounds().intersects(rectangle))
        return false; 
      int i = param1Int3 - (getBorderInsets(param1Component)).right;
      byte b = 5;
      param1Graphics.setColor(this.frameColor);
      param1Graphics.fillRect(i + 1, b, 2, param1Int4 - 1);
      param1Graphics.setColor(this.frameShadow);
      param1Graphics.fillRect(i + 3, b, 2, param1Int4 - 1);
      param1Graphics.setColor(this.frameHighlight);
      param1Graphics.drawLine(i, b, i, param1Int4 - 1);
      return true;
    }
    
    protected boolean drawBottomBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = new Rectangle(0, param1Int4 - (getBorderInsets(param1Component)).bottom, param1Int3, (getBorderInsets(param1Component)).bottom);
      if (!param1Graphics.getClipBounds().intersects(rectangle))
        return false; 
      int i = param1Int4 - (getBorderInsets(param1Component)).bottom;
      param1Graphics.setColor(this.frameShadow);
      param1Graphics.drawLine(param1Int1 + 1, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.drawLine(param1Int1 + 2, param1Int4 - 2, param1Int3 - 2, param1Int4 - 2);
      param1Graphics.setColor(this.frameColor);
      param1Graphics.fillRect(param1Int1 + 2, i + 1, param1Int3 - 4, 2);
      param1Graphics.setColor(this.frameHighlight);
      param1Graphics.drawLine(param1Int1 + 5, i, param1Int3 - 5, i);
      return true;
    }
    
    protected boolean isActiveFrame() { return this.jcomp.hasFocus(); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (isActiveFrame()) {
        this.frameColor = UIManager.getColor("activeCaptionBorder");
      } else {
        this.frameColor = UIManager.getColor("inactiveCaptionBorder");
      } 
      this.frameHighlight = this.frameColor.brighter();
      this.frameShadow = this.frameColor.darker().darker();
      drawTopBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      drawLeftBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      drawRightBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      drawBottomBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
    }
  }
  
  public static class InternalFrameBorder extends FrameBorder {
    JInternalFrame frame;
    
    public static final int CORNER_SIZE = 24;
    
    public InternalFrameBorder(JInternalFrame param1JInternalFrame) {
      super(param1JInternalFrame);
      this.frame = param1JInternalFrame;
    }
    
    public void setFrame(JInternalFrame param1JInternalFrame) { this.frame = param1JInternalFrame; }
    
    public JInternalFrame frame() { return this.frame; }
    
    public int resizePartWidth() { return !this.frame.isResizable() ? 0 : 5; }
    
    protected boolean drawTopBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (super.drawTopBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4) && this.frame.isResizable()) {
        param1Graphics.setColor(getFrameShadow());
        param1Graphics.drawLine(23, param1Int2 + 1, 23, param1Int2 + 4);
        param1Graphics.drawLine(param1Int3 - 24 - 1, param1Int2 + 1, param1Int3 - 24 - 1, param1Int2 + 4);
        param1Graphics.setColor(getFrameHighlight());
        param1Graphics.drawLine(24, param1Int2, 24, param1Int2 + 4);
        param1Graphics.drawLine(param1Int3 - 24, param1Int2, param1Int3 - 24, param1Int2 + 4);
        return true;
      } 
      return false;
    }
    
    protected boolean drawLeftBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (super.drawLeftBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4) && this.frame.isResizable()) {
        param1Graphics.setColor(getFrameHighlight());
        int i = param1Int2 + 24;
        param1Graphics.drawLine(param1Int1, i, param1Int1 + 4, i);
        int j = param1Int4 - 24;
        param1Graphics.drawLine(param1Int1 + 1, j, param1Int1 + 5, j);
        param1Graphics.setColor(getFrameShadow());
        param1Graphics.drawLine(param1Int1 + 1, i - 1, param1Int1 + 5, i - 1);
        param1Graphics.drawLine(param1Int1 + 1, j - 1, param1Int1 + 5, j - 1);
        return true;
      } 
      return false;
    }
    
    protected boolean drawRightBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (super.drawRightBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4) && this.frame.isResizable()) {
        int i = param1Int3 - (getBorderInsets(param1Component)).right;
        param1Graphics.setColor(getFrameHighlight());
        int j = param1Int2 + 24;
        param1Graphics.drawLine(i, j, param1Int3 - 2, j);
        int k = param1Int4 - 24;
        param1Graphics.drawLine(i + 1, k, i + 3, k);
        param1Graphics.setColor(getFrameShadow());
        param1Graphics.drawLine(i + 1, j - 1, param1Int3 - 2, j - 1);
        param1Graphics.drawLine(i + 1, k - 1, i + 3, k - 1);
        return true;
      } 
      return false;
    }
    
    protected boolean drawBottomBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (super.drawBottomBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4) && this.frame.isResizable()) {
        int i = param1Int4 - (getBorderInsets(param1Component)).bottom;
        param1Graphics.setColor(getFrameShadow());
        param1Graphics.drawLine(23, i + 1, 23, param1Int4 - 1);
        param1Graphics.drawLine(param1Int3 - 24, i + 1, param1Int3 - 24, param1Int4 - 1);
        param1Graphics.setColor(getFrameHighlight());
        param1Graphics.drawLine(24, i, 24, param1Int4 - 2);
        param1Graphics.drawLine(param1Int3 - 24 + 1, i, param1Int3 - 24 + 1, param1Int4 - 2);
        return true;
      } 
      return false;
    }
    
    protected boolean isActiveFrame() { return this.frame.isSelected(); }
  }
  
  public static class MenuBarBorder extends ButtonBorder {
    public MenuBarBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Color1, param1Color2, param1Color3, param1Color4); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JMenuBar))
        return; 
      JMenuBar jMenuBar = (JMenuBar)param1Component;
      if (jMenuBar.isBorderPainted() == true) {
        Dimension dimension = jMenuBar.getSize();
        MotifBorders.drawBezel(param1Graphics, param1Int1, param1Int2, dimension.width, dimension.height, false, false, this.shadow, this.highlight, this.darkShadow, this.focus);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(6, 6, 6, 6);
      return param1Insets;
    }
  }
  
  public static class MotifPopupMenuBorder extends AbstractBorder implements UIResource {
    protected Font font;
    
    protected Color background;
    
    protected Color foreground;
    
    protected Color shadowColor;
    
    protected Color highlightColor;
    
    protected static final int TEXT_SPACING = 2;
    
    protected static final int GROOVE_HEIGHT = 2;
    
    public MotifPopupMenuBorder(Font param1Font, Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) {
      this.font = param1Font;
      this.background = param1Color1;
      this.foreground = param1Color2;
      this.shadowColor = param1Color3;
      this.highlightColor = param1Color4;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JPopupMenu))
        return; 
      Font font1 = param1Graphics.getFont();
      Color color = param1Graphics.getColor();
      JPopupMenu jPopupMenu = (JPopupMenu)param1Component;
      String str = jPopupMenu.getLabel();
      if (str == null)
        return; 
      param1Graphics.setFont(this.font);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jPopupMenu, param1Graphics, this.font);
      int i = fontMetrics.getHeight();
      int j = fontMetrics.getDescent();
      int k = fontMetrics.getAscent();
      Point point = new Point();
      int m = SwingUtilities2.stringWidth(jPopupMenu, fontMetrics, str);
      point.y = param1Int2 + k + 2;
      point.x = param1Int1 + (param1Int3 - m) / 2;
      param1Graphics.setColor(this.background);
      param1Graphics.fillRect(point.x - 2, point.y - i - j, m + 4, i - j);
      param1Graphics.setColor(this.foreground);
      SwingUtilities2.drawString(jPopupMenu, param1Graphics, str, point.x, point.y);
      MotifGraphicsUtils.drawGroove(param1Graphics, param1Int1, point.y + 2, param1Int3, 2, this.shadowColor, this.highlightColor);
      param1Graphics.setFont(font1);
      param1Graphics.setColor(color);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      if (!(param1Component instanceof JPopupMenu))
        return param1Insets; 
      int i = 0;
      int j = 16;
      String str = ((JPopupMenu)param1Component).getLabel();
      if (str == null) {
        param1Insets.left = param1Insets.top = param1Insets.right = param1Insets.bottom = 0;
        return param1Insets;
      } 
      FontMetrics fontMetrics = param1Component.getFontMetrics(this.font);
      if (fontMetrics != null) {
        i = fontMetrics.getDescent();
        j = fontMetrics.getAscent();
      } 
      param1Insets.top += j + i + 2 + 2;
      return param1Insets;
    }
  }
  
  public static class ToggleButtonBorder extends ButtonBorder {
    public ToggleButtonBorder(Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Color1, param1Color2, param1Color3, param1Color4); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Component instanceof AbstractButton) {
        AbstractButton abstractButton = (AbstractButton)param1Component;
        ButtonModel buttonModel = abstractButton.getModel();
        if ((buttonModel.isArmed() && buttonModel.isPressed()) || buttonModel.isSelected()) {
          MotifBorders.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, (buttonModel.isPressed() || buttonModel.isSelected()), (abstractButton.isFocusPainted() && abstractButton.hasFocus()), this.shadow, this.highlight, this.darkShadow, this.focus);
        } else {
          MotifBorders.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false, (abstractButton.isFocusPainted() && abstractButton.hasFocus()), this.shadow, this.highlight, this.darkShadow, this.focus);
        } 
      } else {
        MotifBorders.drawBezel(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, false, false, this.shadow, this.highlight, this.darkShadow, this.focus);
      } 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 3, 3);
      return param1Insets;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */