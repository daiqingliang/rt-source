package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.image.SunWritableRaster;
import sun.awt.windows.ThemeReader;
import sun.security.action.GetPropertyAction;
import sun.swing.CachedPainter;

class XPStyle {
  private static XPStyle xp;
  
  private static SkinPainter skinPainter = new SkinPainter();
  
  private static Boolean themeActive = null;
  
  private HashMap<String, Border> borderMap = new HashMap();
  
  private HashMap<String, Color> colorMap = new HashMap();
  
  private boolean flatMenus = getSysBoolean(TMSchema.Prop.FLATMENUS);
  
  static void invalidateStyle() {
    xp = null;
    themeActive = null;
    skinPainter.flush();
  }
  
  static XPStyle getXP() {
    if (themeActive == null) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      themeActive = (Boolean)toolkit.getDesktopProperty("win.xpstyle.themeActive");
      if (themeActive == null)
        themeActive = Boolean.FALSE; 
      if (themeActive.booleanValue()) {
        GetPropertyAction getPropertyAction = new GetPropertyAction("swing.noxp");
        if (AccessController.doPrivileged(getPropertyAction) == null && ThemeReader.isThemed() && !(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel))
          xp = new XPStyle(); 
      } 
    } 
    return ThemeReader.isXPStyleEnabled() ? xp : null;
  }
  
  static boolean isVista() {
    XPStyle xPStyle = getXP();
    return (xPStyle != null && xPStyle.isSkinDefined(null, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT));
  }
  
  String getString(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) { return getTypeEnumName(paramComponent, paramPart, paramState, paramProp); }
  
  TMSchema.TypeEnum getTypeEnum(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
    int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return TMSchema.TypeEnum.getTypeEnum(paramProp, i);
  }
  
  private static String getTypeEnumName(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
    int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return (i == -1) ? null : TMSchema.TypeEnum.getTypeEnum(paramProp, i).getName();
  }
  
  int getInt(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, int paramInt) { return ThemeReader.getInt(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue()); }
  
  Dimension getDimension(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
    Dimension dimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return (dimension != null) ? dimension : new Dimension();
  }
  
  Point getPoint(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
    Dimension dimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return (dimension != null) ? new Point(dimension.width, dimension.height) : new Point();
  }
  
  Insets getMargin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) {
    Insets insets = ThemeReader.getThemeMargins(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return (insets != null) ? insets : new Insets(0, 0, 0, 0);
  }
  
  Color getColor(Skin paramSkin, TMSchema.Prop paramProp, Color paramColor) {
    String str = paramSkin.toString() + "." + paramProp.name();
    TMSchema.Part part = paramSkin.part;
    Color color = (Color)this.colorMap.get(str);
    if (color == null) {
      color = ThemeReader.getColor(part.getControlName(null), part.getValue(), TMSchema.State.getValue(part, paramSkin.state), paramProp.getValue());
      if (color != null) {
        color = new ColorUIResource(color);
        this.colorMap.put(str, color);
      } 
    } 
    return (color != null) ? color : paramColor;
  }
  
  Color getColor(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, Color paramColor) { return getColor(new Skin(paramComponent, paramPart, paramState), paramProp, paramColor); }
  
  Border getBorder(Component paramComponent, TMSchema.Part paramPart) {
    if (paramPart == TMSchema.Part.MENU)
      return this.flatMenus ? new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1) : null; 
    Skin skin;
    Border border = (Border)this.borderMap.get(skin.string);
    if (border == null) {
      String str = getTypeEnumName(paramComponent, paramPart, null, TMSchema.Prop.BGTYPE);
      if ("borderfill".equalsIgnoreCase(str)) {
        int i = getInt(paramComponent, paramPart, null, TMSchema.Prop.BORDERSIZE, 1);
        Color color = getColor(skin, TMSchema.Prop.BORDERCOLOR, Color.black);
        border = new XPFillBorder(color, i);
        if (paramPart == TMSchema.Part.CP_COMBOBOX)
          border = new XPStatefulFillBorder(color, i, paramPart, TMSchema.Prop.BORDERCOLOR); 
      } else if ("imagefile".equalsIgnoreCase(str)) {
        Insets insets = getMargin(paramComponent, paramPart, null, TMSchema.Prop.SIZINGMARGINS);
        if (insets != null)
          if (getBoolean(paramComponent, paramPart, null, TMSchema.Prop.BORDERONLY)) {
            border = new XPImageBorder(paramComponent, paramPart);
          } else if (paramPart == TMSchema.Part.CP_COMBOBOX) {
            border = new EmptyBorder(1, 1, 1, 1);
          } else if (paramPart == TMSchema.Part.TP_BUTTON) {
            border = new XPEmptyBorder(new Insets(3, 3, 3, 3));
          } else {
            border = new XPEmptyBorder(insets);
          }  
      } 
      if (border != null)
        this.borderMap.put(skin.string, border); 
    } 
    return border;
  }
  
  boolean isSkinDefined(Component paramComponent, TMSchema.Part paramPart) { return (paramPart.getValue() == 0 || ThemeReader.isThemePartDefined(paramPart.getControlName(paramComponent), paramPart.getValue(), 0)); }
  
  Skin getSkin(Component paramComponent, TMSchema.Part paramPart) {
    assert isSkinDefined(paramComponent, paramPart) : "part " + paramPart + " is not defined";
    return new Skin(paramComponent, paramPart, null);
  }
  
  long getThemeTransitionDuration(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, TMSchema.Prop paramProp) { return ThemeReader.getThemeTransitionDuration(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState1), TMSchema.State.getValue(paramPart, paramState2), (paramProp != null) ? paramProp.getValue() : 0); }
  
  private boolean getBoolean(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp) { return ThemeReader.getBoolean(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue()); }
  
  static Dimension getPartSize(TMSchema.Part paramPart, TMSchema.State paramState) { return ThemeReader.getPartSize(paramPart.getControlName(null), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState)); }
  
  private static boolean getSysBoolean(TMSchema.Prop paramProp) { return ThemeReader.getSysBoolean("window", paramProp.getValue()); }
  
  static  {
    invalidateStyle();
  }
  
  static class GlyphButton extends JButton {
    private XPStyle.Skin skin;
    
    public GlyphButton(Component param1Component, TMSchema.Part param1Part) {
      XPStyle xPStyle = XPStyle.getXP();
      this.skin = (xPStyle != null) ? xPStyle.getSkin(param1Component, param1Part) : null;
      setBorder(null);
      setContentAreaFilled(false);
      setMinimumSize(new Dimension(5, 5));
      setPreferredSize(new Dimension(16, 16));
      setMaximumSize(new Dimension(2147483647, 2147483647));
    }
    
    public boolean isFocusTraversable() { return false; }
    
    protected TMSchema.State getState() {
      TMSchema.State state = TMSchema.State.NORMAL;
      if (!isEnabled()) {
        state = TMSchema.State.DISABLED;
      } else if (getModel().isPressed()) {
        state = TMSchema.State.PRESSED;
      } else if (getModel().isRollover()) {
        state = TMSchema.State.HOT;
      } 
      return state;
    }
    
    public void paintComponent(Graphics param1Graphics) {
      if (XPStyle.getXP() == null || this.skin == null)
        return; 
      Dimension dimension = getSize();
      this.skin.paintSkin(param1Graphics, 0, 0, dimension.width, dimension.height, getState());
    }
    
    public void setPart(Component param1Component, TMSchema.Part param1Part) {
      XPStyle xPStyle = XPStyle.getXP();
      this.skin = (xPStyle != null) ? xPStyle.getSkin(param1Component, param1Part) : null;
      revalidate();
      repaint();
    }
    
    protected void paintBorder(Graphics param1Graphics) {}
  }
  
  static class Skin {
    final Component component;
    
    final TMSchema.Part part;
    
    final TMSchema.State state;
    
    private final String string;
    
    private Dimension size = null;
    
    Skin(Component param1Component, TMSchema.Part param1Part) { this(param1Component, param1Part, null); }
    
    Skin(TMSchema.Part param1Part, TMSchema.State param1State) { this(null, param1Part, param1State); }
    
    Skin(Component param1Component, TMSchema.Part param1Part, TMSchema.State param1State) {
      this.component = param1Component;
      this.part = param1Part;
      this.state = param1State;
      String str = param1Part.getControlName(param1Component) + "." + param1Part.name();
      if (param1State != null)
        str = str + "(" + param1State.name() + ")"; 
      this.string = str;
    }
    
    Insets getContentMargin() {
      byte b1 = 100;
      byte b2 = 100;
      Insets insets = ThemeReader.getThemeBackgroundContentMargins(this.part.getControlName(null), this.part.getValue(), 0, b1, b2);
      return (insets != null) ? insets : new Insets(0, 0, 0, 0);
    }
    
    private int getWidth(TMSchema.State param1State) {
      if (this.size == null)
        this.size = XPStyle.getPartSize(this.part, param1State); 
      return (this.size != null) ? this.size.width : 0;
    }
    
    int getWidth() { return getWidth((this.state != null) ? this.state : TMSchema.State.NORMAL); }
    
    private int getHeight(TMSchema.State param1State) {
      if (this.size == null)
        this.size = XPStyle.getPartSize(this.part, param1State); 
      return (this.size != null) ? this.size.height : 0;
    }
    
    int getHeight() { return getHeight((this.state != null) ? this.state : TMSchema.State.NORMAL); }
    
    public String toString() { return this.string; }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof Skin && ((Skin)param1Object).string.equals(this.string)); }
    
    public int hashCode() { return this.string.hashCode(); }
    
    void paintSkin(Graphics param1Graphics, int param1Int1, int param1Int2, TMSchema.State param1State) {
      if (param1State == null)
        param1State = this.state; 
      paintSkin(param1Graphics, param1Int1, param1Int2, getWidth(param1State), getHeight(param1State), param1State);
    }
    
    void paintSkin(Graphics param1Graphics, Rectangle param1Rectangle, TMSchema.State param1State) { paintSkin(param1Graphics, param1Rectangle.x, param1Rectangle.y, param1Rectangle.width, param1Rectangle.height, param1State); }
    
    void paintSkin(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, TMSchema.State param1State) {
      if (XPStyle.getXP() == null)
        return; 
      if (ThemeReader.isGetThemeTransitionDurationDefined() && this.component instanceof JComponent && SwingUtilities.getAncestorOfClass(javax.swing.CellRendererPane.class, this.component) == null) {
        AnimationController.paintSkin((JComponent)this.component, this, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1State);
      } else {
        paintSkinRaw(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1State);
      } 
    }
    
    void paintSkinRaw(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, TMSchema.State param1State) {
      if (XPStyle.getXP() == null)
        return; 
      skinPainter.paint(null, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, new Object[] { this, param1State });
    }
    
    void paintSkin(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, TMSchema.State param1State, boolean param1Boolean) {
      if (XPStyle.getXP() == null)
        return; 
      if (param1Boolean && "borderfill".equals(XPStyle.getTypeEnumName(this.component, this.part, param1State, TMSchema.Prop.BGTYPE)))
        return; 
      skinPainter.paint(null, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, new Object[] { this, param1State });
    }
  }
  
  private static class SkinPainter extends CachedPainter {
    SkinPainter() {
      super(30);
      flush();
    }
    
    public void flush() { super.flush(); }
    
    protected void paintToImage(Component param1Component, Image param1Image, Graphics param1Graphics, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      boolean bool = false;
      XPStyle.Skin skin = (XPStyle.Skin)param1ArrayOfObject[0];
      TMSchema.Part part = skin.part;
      TMSchema.State state = (TMSchema.State)param1ArrayOfObject[1];
      if (state == null)
        state = skin.state; 
      if (param1Component == null)
        param1Component = skin.component; 
      BufferedImage bufferedImage = (BufferedImage)param1Image;
      WritableRaster writableRaster = bufferedImage.getRaster();
      DataBufferInt dataBufferInt = (DataBufferInt)writableRaster.getDataBuffer();
      ThemeReader.paintBackground(SunWritableRaster.stealData(dataBufferInt, 0), part.getControlName(param1Component), part.getValue(), TMSchema.State.getValue(part, state), 0, 0, param1Int1, param1Int2, param1Int1);
      SunWritableRaster.markDirty(dataBufferInt);
    }
    
    protected Image createImage(Component param1Component, int param1Int1, int param1Int2, GraphicsConfiguration param1GraphicsConfiguration, Object[] param1ArrayOfObject) { return new BufferedImage(param1Int1, param1Int2, 2); }
  }
  
  private class XPEmptyBorder extends EmptyBorder implements UIResource {
    XPEmptyBorder(Insets param1Insets) { super(param1Insets.top + 2, param1Insets.left + 2, param1Insets.bottom + 2, param1Insets.right + 2); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets = super.getBorderInsets(param1Component, param1Insets);
      Insets insets = null;
      if (param1Component instanceof AbstractButton) {
        Insets insets1 = ((AbstractButton)param1Component).getMargin();
        if (param1Component.getParent() instanceof JToolBar && !(param1Component instanceof javax.swing.JRadioButton) && !(param1Component instanceof javax.swing.JCheckBox) && insets1 instanceof javax.swing.plaf.InsetsUIResource) {
          param1Insets.top -= 2;
          param1Insets.left -= 2;
          param1Insets.bottom -= 2;
          param1Insets.right -= 2;
        } else {
          insets = insets1;
        } 
      } else if (param1Component instanceof JToolBar) {
        insets = ((JToolBar)param1Component).getMargin();
      } else if (param1Component instanceof JTextComponent) {
        insets = ((JTextComponent)param1Component).getMargin();
      } 
      if (insets != null) {
        insets.top += 2;
        insets.left += 2;
        insets.bottom += 2;
        insets.right += 2;
      } 
      return param1Insets;
    }
  }
  
  private class XPFillBorder extends LineBorder implements UIResource {
    XPFillBorder(Color param1Color, int param1Int) { super(param1Color, param1Int); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      Insets insets = null;
      if (param1Component instanceof AbstractButton) {
        insets = ((AbstractButton)param1Component).getMargin();
      } else if (param1Component instanceof JToolBar) {
        insets = ((JToolBar)param1Component).getMargin();
      } else if (param1Component instanceof JTextComponent) {
        insets = ((JTextComponent)param1Component).getMargin();
      } 
      param1Insets.top = ((insets != null) ? insets.top : 0) + this.thickness;
      param1Insets.left = ((insets != null) ? insets.left : 0) + this.thickness;
      param1Insets.bottom = ((insets != null) ? insets.bottom : 0) + this.thickness;
      param1Insets.right = ((insets != null) ? insets.right : 0) + this.thickness;
      return param1Insets;
    }
  }
  
  private class XPImageBorder extends AbstractBorder implements UIResource {
    XPStyle.Skin skin;
    
    XPImageBorder(Component param1Component, TMSchema.Part param1Part) { this.skin = this$0.getSkin(param1Component, param1Part); }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { this.skin.paintSkin(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, null); }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      Insets insets1 = null;
      Insets insets2 = this.skin.getContentMargin();
      if (insets2 == null)
        insets2 = new Insets(0, 0, 0, 0); 
      if (param1Component instanceof AbstractButton) {
        insets1 = ((AbstractButton)param1Component).getMargin();
      } else if (param1Component instanceof JToolBar) {
        insets1 = ((JToolBar)param1Component).getMargin();
      } else if (param1Component instanceof JTextComponent) {
        insets1 = ((JTextComponent)param1Component).getMargin();
      } 
      param1Insets.top = ((insets1 != null) ? insets1.top : 0) + insets2.top;
      param1Insets.left = ((insets1 != null) ? insets1.left : 0) + insets2.left;
      param1Insets.bottom = ((insets1 != null) ? insets1.bottom : 0) + insets2.bottom;
      param1Insets.right = ((insets1 != null) ? insets1.right : 0) + insets2.right;
      return param1Insets;
    }
  }
  
  private class XPStatefulFillBorder extends XPFillBorder {
    private final TMSchema.Part part;
    
    private final TMSchema.Prop prop;
    
    XPStatefulFillBorder(Color param1Color, int param1Int, TMSchema.Part param1Part, TMSchema.Prop param1Prop) {
      super(XPStyle.this, param1Color, param1Int);
      this.part = param1Part;
      this.prop = param1Prop;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      TMSchema.State state = TMSchema.State.NORMAL;
      if (param1Component instanceof JComboBox) {
        JComboBox jComboBox = (JComboBox)param1Component;
        if (jComboBox.getUI() instanceof WindowsComboBoxUI) {
          WindowsComboBoxUI windowsComboBoxUI = (WindowsComboBoxUI)jComboBox.getUI();
          state = windowsComboBoxUI.getXPComboBoxState(jComboBox);
        } 
      } 
      this.lineColor = XPStyle.this.getColor(param1Component, this.part, state, this.prop, Color.black);
      super.paintBorder(param1Component, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\XPStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */