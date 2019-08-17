package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicGraphicsUtils;

public class WindowsBorders {
  public static Border getProgressBarBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(new ProgressBarBorder(uIDefaults.getColor("ProgressBar.shadow"), uIDefaults.getColor("ProgressBar.highlight")), new EmptyBorder(1, 1, 1, 1));
  }
  
  public static Border getToolBarBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new ToolBarBorder(uIDefaults.getColor("ToolBar.shadow"), uIDefaults.getColor("ToolBar.highlight"));
  }
  
  public static Border getFocusCellHighlightBorder() { return new ComplementDashedBorder(); }
  
  public static Border getTableHeaderBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(new BasicBorders.ButtonBorder(uIDefaults.getColor("Table.shadow"), uIDefaults.getColor("Table.darkShadow"), uIDefaults.getColor("Table.light"), uIDefaults.getColor("Table.highlight")), new BasicBorders.MarginBorder());
  }
  
  public static Border getInternalFrameBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new BorderUIResource.CompoundBorderUIResource(BorderFactory.createBevelBorder(0, uIDefaults.getColor("InternalFrame.borderColor"), uIDefaults.getColor("InternalFrame.borderHighlight"), uIDefaults.getColor("InternalFrame.borderDarkShadow"), uIDefaults.getColor("InternalFrame.borderShadow")), new InternalFrameLineBorder(uIDefaults.getColor("InternalFrame.activeBorderColor"), uIDefaults.getColor("InternalFrame.inactiveBorderColor"), uIDefaults.getInt("InternalFrame.borderWidth")));
  }
  
  static class ComplementDashedBorder extends LineBorder implements UIResource {
    private Color origColor;
    
    private Color paintColor;
    
    public ComplementDashedBorder() { super(null); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Color color = param1Component.getBackground();
      if (this.origColor != color) {
        this.origColor = color;
        this.paintColor = new Color(this.origColor.getRGB() ^ 0xFFFFFFFF);
      } 
      param1Graphics.setColor(this.paintColor);
      BasicGraphicsUtils.drawDashedRect(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
    }
  }
  
  public static class DashedBorder extends LineBorder implements UIResource {
    public DashedBorder(Color param1Color) { super(param1Color); }
    
    public DashedBorder(Color param1Color, int param1Int) { super(param1Color, param1Int); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Color color = param1Graphics.getColor();
      param1Graphics.setColor(this.lineColor);
      for (int i = 0; i < this.thickness; i++)
        BasicGraphicsUtils.drawDashedRect(param1Graphics, param1Int1 + i, param1Int2 + i, param1Int3 - i - i, param1Int4 - i - i); 
      param1Graphics.setColor(color);
    }
  }
  
  public static class InternalFrameLineBorder extends LineBorder implements UIResource {
    protected Color activeColor;
    
    protected Color inactiveColor;
    
    public InternalFrameLineBorder(Color param1Color1, Color param1Color2, int param1Int) {
      super(param1Color1, param1Int);
      this.activeColor = param1Color1;
      this.inactiveColor = param1Color2;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      JInternalFrame jInternalFrame = null;
      if (param1Component instanceof JInternalFrame) {
        jInternalFrame = (JInternalFrame)param1Component;
      } else if (param1Component instanceof JInternalFrame.JDesktopIcon) {
        jInternalFrame = ((JInternalFrame.JDesktopIcon)param1Component).getInternalFrame();
      } else {
        return;
      } 
      if (jInternalFrame.isSelected()) {
        this.lineColor = this.activeColor;
        super.paintBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } else {
        this.lineColor = this.inactiveColor;
        super.paintBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } 
    }
  }
  
  public static class ProgressBarBorder extends AbstractBorder implements UIResource {
    protected Color shadow;
    
    protected Color highlight;
    
    public ProgressBarBorder(Color param1Color1, Color param1Color2) {
      this.highlight = param1Color2;
      this.shadow = param1Color1;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.setColor(this.shadow);
      param1Graphics.drawLine(param1Int1, param1Int2, param1Int3 - 1, param1Int2);
      param1Graphics.drawLine(param1Int1, param1Int2, param1Int1, param1Int4 - 1);
      param1Graphics.setColor(this.highlight);
      param1Graphics.drawLine(param1Int1, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
      param1Graphics.drawLine(param1Int3 - 1, param1Int2, param1Int3 - 1, param1Int4 - 1);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(1, 1, 1, 1);
      return param1Insets;
    }
  }
  
  public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
    protected Color shadow;
    
    protected Color highlight;
    
    public ToolBarBorder(Color param1Color1, Color param1Color2) {
      this.highlight = param1Color2;
      this.shadow = param1Color1;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JToolBar))
        return; 
      param1Graphics.translate(param1Int1, param1Int2);
      XPStyle xPStyle = XPStyle.getXP();
      if (xPStyle != null) {
        Border border = xPStyle.getBorder(param1Component, TMSchema.Part.TP_TOOLBAR);
        if (border != null)
          border.paintBorder(param1Component, param1Graphics, 0, 0, param1Int3, param1Int4); 
      } 
      if (((JToolBar)param1Component).isFloatable()) {
        boolean bool = (((JToolBar)param1Component).getOrientation() == 1) ? 1 : 0;
        if (xPStyle != null) {
          int j;
          int i;
          byte b2;
          byte b1;
          TMSchema.Part part = bool ? TMSchema.Part.RP_GRIPPERVERT : TMSchema.Part.RP_GRIPPER;
          XPStyle.Skin skin = xPStyle.getSkin(param1Component, part);
          if (bool) {
            b1 = 0;
            b2 = 2;
            i = param1Int3 - 1;
            j = skin.getHeight();
          } else {
            i = skin.getWidth();
            j = param1Int4 - 1;
            b1 = param1Component.getComponentOrientation().isLeftToRight() ? 2 : (param1Int3 - i - 2);
            b2 = 0;
          } 
          skin.paintSkin(param1Graphics, b1, b2, i, j, TMSchema.State.NORMAL);
        } else if (!bool) {
          if (param1Component.getComponentOrientation().isLeftToRight()) {
            param1Graphics.setColor(this.shadow);
            param1Graphics.drawLine(4, 3, 4, param1Int4 - 4);
            param1Graphics.drawLine(4, param1Int4 - 4, 2, param1Int4 - 4);
            param1Graphics.setColor(this.highlight);
            param1Graphics.drawLine(2, 3, 3, 3);
            param1Graphics.drawLine(2, 3, 2, param1Int4 - 5);
          } else {
            param1Graphics.setColor(this.shadow);
            param1Graphics.drawLine(param1Int3 - 3, 3, param1Int3 - 3, param1Int4 - 4);
            param1Graphics.drawLine(param1Int3 - 4, param1Int4 - 4, param1Int3 - 4, param1Int4 - 4);
            param1Graphics.setColor(this.highlight);
            param1Graphics.drawLine(param1Int3 - 5, 3, param1Int3 - 4, 3);
            param1Graphics.drawLine(param1Int3 - 5, 3, param1Int3 - 5, param1Int4 - 5);
          } 
        } else {
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(3, 4, param1Int3 - 4, 4);
          param1Graphics.drawLine(param1Int3 - 4, 2, param1Int3 - 4, 4);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(3, 2, param1Int3 - 4, 2);
          param1Graphics.drawLine(3, 2, 3, 3);
        } 
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(1, 1, 1, 1);
      if (!(param1Component instanceof JToolBar))
        return param1Insets; 
      if (((JToolBar)param1Component).isFloatable()) {
        byte b = (XPStyle.getXP() != null) ? 12 : 9;
        if (((JToolBar)param1Component).getOrientation() == 0) {
          if (param1Component.getComponentOrientation().isLeftToRight()) {
            param1Insets.left = b;
          } else {
            param1Insets.right = b;
          } 
        } else {
          param1Insets.top = b;
        } 
      } 
      return param1Insets;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */