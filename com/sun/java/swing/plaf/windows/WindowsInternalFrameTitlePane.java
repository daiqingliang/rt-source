package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

public class WindowsInternalFrameTitlePane extends BasicInternalFrameTitlePane {
  private Color selectedTitleGradientColor;
  
  private Color notSelectedTitleGradientColor;
  
  private JPopupMenu systemPopupMenu;
  
  private JLabel systemLabel;
  
  private Font titleFont;
  
  private int titlePaneHeight;
  
  private int buttonWidth;
  
  private int buttonHeight;
  
  private boolean hotTrackingOn;
  
  public WindowsInternalFrameTitlePane(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  protected void addSubComponents() {
    add(this.systemLabel);
    add(this.iconButton);
    add(this.maxButton);
    add(this.closeButton);
  }
  
  protected void installDefaults() {
    super.installDefaults();
    this.titlePaneHeight = UIManager.getInt("InternalFrame.titlePaneHeight");
    this.buttonWidth = UIManager.getInt("InternalFrame.titleButtonWidth") - 4;
    this.buttonHeight = UIManager.getInt("InternalFrame.titleButtonHeight") - 4;
    Object object = UIManager.get("InternalFrame.titleButtonToolTipsOn");
    this.hotTrackingOn = (object instanceof Boolean) ? ((Boolean)object).booleanValue() : 1;
    if (XPStyle.getXP() != null) {
      this.buttonWidth = this.buttonHeight;
      Dimension dimension = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
      if (dimension != null && dimension.width != 0 && dimension.height != 0)
        this.buttonWidth = (int)(this.buttonWidth * dimension.width / dimension.height); 
    } else {
      this.buttonWidth += 2;
      Color color = UIManager.getColor("InternalFrame.activeBorderColor");
      setBorder(BorderFactory.createLineBorder(color, 1));
    } 
    this.selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
    this.notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
  }
  
  protected void uninstallListeners() { super.uninstallListeners(); }
  
  protected void createButtons() {
    super.createButtons();
    if (XPStyle.getXP() != null) {
      this.iconButton.setContentAreaFilled(false);
      this.maxButton.setContentAreaFilled(false);
      this.closeButton.setContentAreaFilled(false);
    } 
  }
  
  protected void setButtonIcons() {
    super.setButtonIcons();
    if (!this.hotTrackingOn) {
      this.iconButton.setToolTipText(null);
      this.maxButton.setToolTipText(null);
      this.closeButton.setToolTipText(null);
    } 
  }
  
  public void paintComponent(Graphics paramGraphics) {
    XPStyle xPStyle = XPStyle.getXP();
    paintTitleBackground(paramGraphics);
    String str = this.frame.getTitle();
    if (str != null) {
      int k;
      int j;
      boolean bool = this.frame.isSelected();
      Font font1 = paramGraphics.getFont();
      Font font2 = (this.titleFont != null) ? this.titleFont : getFont();
      paramGraphics.setFont(font2);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, paramGraphics, font2);
      int i = (getHeight() + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
      Rectangle rectangle = new Rectangle(0, 0, 0, 0);
      if (this.frame.isIconifiable()) {
        rectangle = this.iconButton.getBounds();
      } else if (this.frame.isMaximizable()) {
        rectangle = this.maxButton.getBounds();
      } else if (this.frame.isClosable()) {
        rectangle = this.closeButton.getBounds();
      } 
      int m = 2;
      if (WindowsGraphicsUtils.isLeftToRight(this.frame)) {
        if (rectangle.x == 0)
          rectangle.x = this.frame.getWidth() - (this.frame.getInsets()).right; 
        j = this.systemLabel.getX() + this.systemLabel.getWidth() + m;
        if (xPStyle != null)
          j += 2; 
        k = rectangle.x - j - m;
      } else {
        if (rectangle.x == 0)
          rectangle.x = (this.frame.getInsets()).left; 
        k = SwingUtilities2.stringWidth(this.frame, fontMetrics, str);
        int n = rectangle.x + rectangle.width + m;
        if (xPStyle != null)
          n += 2; 
        int i1 = this.systemLabel.getX() - m - n;
        if (i1 > k) {
          j = this.systemLabel.getX() - m - k;
        } else {
          j = n;
          k = i1;
        } 
      } 
      str = getTitle(this.frame.getTitle(), fontMetrics, k);
      if (xPStyle != null) {
        String str1 = null;
        if (bool)
          str1 = xPStyle.getString(this, TMSchema.Part.WP_CAPTION, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWTYPE); 
        if ("single".equalsIgnoreCase(str1)) {
          Point point = xPStyle.getPoint(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWOFFSET);
          Color color = xPStyle.getColor(this, TMSchema.Part.WP_WINDOW, TMSchema.State.ACTIVE, TMSchema.Prop.TEXTSHADOWCOLOR, null);
          if (point != null && color != null) {
            paramGraphics.setColor(color);
            SwingUtilities2.drawString(this.frame, paramGraphics, str, j + point.x, i + point.y);
          } 
        } 
      } 
      paramGraphics.setColor(bool ? this.selectedTextColor : this.notSelectedTextColor);
      SwingUtilities2.drawString(this.frame, paramGraphics, str, j, i);
      paramGraphics.setFont(font1);
    } 
  }
  
  public Dimension getPreferredSize() { return getMinimumSize(); }
  
  public Dimension getMinimumSize() {
    Dimension dimension = new Dimension(super.getMinimumSize());
    dimension.height = this.titlePaneHeight + 2;
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null)
      if (this.frame.isMaximum()) {
        dimension.height--;
      } else {
        dimension.height += 3;
      }  
    return dimension;
  }
  
  protected void paintTitleBackground(Graphics paramGraphics) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      TMSchema.Part part = this.frame.isIcon() ? TMSchema.Part.WP_MINCAPTION : (this.frame.isMaximum() ? TMSchema.Part.WP_MAXCAPTION : TMSchema.Part.WP_CAPTION);
      TMSchema.State state = this.frame.isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
      XPStyle.Skin skin = xPStyle.getSkin(this, part);
      skin.paintSkin(paramGraphics, 0, 0, getWidth(), getHeight(), state);
    } else {
      Boolean bool = (Boolean)LookAndFeel.getDesktopPropertyValue("win.frame.captionGradientsOn", Boolean.valueOf(false));
      if (bool.booleanValue() && paramGraphics instanceof Graphics2D) {
        Graphics2D graphics2D = (Graphics2D)paramGraphics;
        Paint paint = graphics2D.getPaint();
        boolean bool1 = this.frame.isSelected();
        int i = getWidth();
        if (bool1) {
          GradientPaint gradientPaint = new GradientPaint(0.0F, 0.0F, this.selectedTitleColor, (int)(i * 0.75D), 0.0F, this.selectedTitleGradientColor);
          graphics2D.setPaint(gradientPaint);
        } else {
          GradientPaint gradientPaint = new GradientPaint(0.0F, 0.0F, this.notSelectedTitleColor, (int)(i * 0.75D), 0.0F, this.notSelectedTitleGradientColor);
          graphics2D.setPaint(gradientPaint);
        } 
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.setPaint(paint);
      } else {
        super.paintTitleBackground(paramGraphics);
      } 
    } 
  }
  
  protected void assembleSystemMenu() {
    this.systemPopupMenu = new JPopupMenu();
    addSystemMenuItems(this.systemPopupMenu);
    enableActions();
    this.systemLabel = new JLabel(this.frame.getFrameIcon()) {
        protected void paintComponent(Graphics param1Graphics) {
          int i = 0;
          int j = 0;
          int k = getWidth();
          int m = getHeight();
          param1Graphics = param1Graphics.create();
          if (isOpaque()) {
            param1Graphics.setColor(getBackground());
            param1Graphics.fillRect(0, 0, k, m);
          } 
          Icon icon = getIcon();
          int n;
          int i1;
          if (icon != null && (n = icon.getIconWidth()) > 0 && (i1 = icon.getIconHeight()) > 0) {
            double d;
            if (n > i1) {
              j = (m - k * i1 / n) / 2;
              d = k / n;
            } else {
              i = (k - m * n / i1) / 2;
              d = m / i1;
            } 
            ((Graphics2D)param1Graphics).translate(i, j);
            ((Graphics2D)param1Graphics).scale(d, d);
            icon.paintIcon(this, param1Graphics, 0, 0);
          } 
          param1Graphics.dispose();
        }
      };
    this.systemLabel.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if (param1MouseEvent.getClickCount() == 2 && WindowsInternalFrameTitlePane.this.frame.isClosable() && !WindowsInternalFrameTitlePane.this.frame.isIcon()) {
              WindowsInternalFrameTitlePane.this.systemPopupMenu.setVisible(false);
              WindowsInternalFrameTitlePane.this.frame.doDefaultCloseAction();
            } else {
              super.mouseClicked(param1MouseEvent);
            } 
          }
          
          public void mousePressed(MouseEvent param1MouseEvent) {
            try {
              WindowsInternalFrameTitlePane.this.frame.setSelected(true);
            } catch (PropertyVetoException propertyVetoException) {}
            WindowsInternalFrameTitlePane.this.showSystemPopupMenu(param1MouseEvent.getComponent());
          }
        });
  }
  
  protected void addSystemMenuItems(JPopupMenu paramJPopupMenu) {
    JMenuItem jMenuItem = paramJPopupMenu.add(this.restoreAction);
    jMenuItem.setMnemonic(getButtonMnemonic("restore"));
    jMenuItem = paramJPopupMenu.add(this.moveAction);
    jMenuItem.setMnemonic(getButtonMnemonic("move"));
    jMenuItem = paramJPopupMenu.add(this.sizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("size"));
    jMenuItem = paramJPopupMenu.add(this.iconifyAction);
    jMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    jMenuItem = paramJPopupMenu.add(this.maximizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    paramJPopupMenu.add(new JSeparator());
    jMenuItem = paramJPopupMenu.add(this.closeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("close"));
  }
  
  private static int getButtonMnemonic(String paramString) {
    try {
      return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + paramString + "Button.mnemonic"));
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  protected void showSystemMenu() { showSystemPopupMenu(this.systemLabel); }
  
  private void showSystemPopupMenu(Component paramComponent) {
    Dimension dimension = new Dimension();
    Border border = this.frame.getBorder();
    if (border != null) {
      dimension.width += (border.getBorderInsets(this.frame)).left + (border.getBorderInsets(this.frame)).right;
      dimension.height += (border.getBorderInsets(this.frame)).bottom + (border.getBorderInsets(this.frame)).top;
    } 
    if (!this.frame.isIcon()) {
      this.systemPopupMenu.show(paramComponent, getX() - dimension.width, getY() + getHeight() - dimension.height);
    } else {
      this.systemPopupMenu.show(paramComponent, getX() - dimension.width, getY() - (this.systemPopupMenu.getPreferredSize()).height - dimension.height);
    } 
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return new WindowsPropertyChangeHandler(); }
  
  protected LayoutManager createLayout() { return new WindowsTitlePaneLayout(); }
  
  public static class ScalableIconUIResource implements Icon, UIResource {
    private static final int SIZE = 16;
    
    private Icon[] icons;
    
    public ScalableIconUIResource(Object[] param1ArrayOfObject) {
      this.icons = new Icon[param1ArrayOfObject.length];
      for (byte b = 0; b < param1ArrayOfObject.length; b++) {
        if (param1ArrayOfObject[b] instanceof UIDefaults.LazyValue) {
          this.icons[b] = (Icon)((UIDefaults.LazyValue)param1ArrayOfObject[b]).createValue(null);
        } else {
          this.icons[b] = (Icon)param1ArrayOfObject[b];
        } 
      } 
    }
    
    protected Icon getBestIcon(int param1Int) {
      if (this.icons != null && this.icons.length > 0) {
        byte b1 = 0;
        int i = Integer.MAX_VALUE;
        for (byte b2 = 0; b2 < this.icons.length; b2++) {
          Icon icon = this.icons[b2];
          int j;
          if (icon != null && (j = icon.getIconWidth()) > 0) {
            int k = Math.abs(j - param1Int);
            if (k < i) {
              i = k;
              b1 = b2;
            } 
          } 
        } 
        return this.icons[b1];
      } 
      return null;
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      Graphics2D graphics2D = (Graphics2D)param1Graphics.create();
      int i = getIconWidth();
      double d = graphics2D.getTransform().getScaleX();
      Icon icon = getBestIcon((int)(i * d));
      int j;
      if (icon != null && (j = icon.getIconWidth()) > 0) {
        double d1 = i / j;
        graphics2D.translate(param1Int1, param1Int2);
        graphics2D.scale(d1, d1);
        icon.paintIcon(param1Component, graphics2D, 0, 0);
      } 
      graphics2D.dispose();
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 16; }
  }
  
  public class WindowsPropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler {
    public WindowsPropertyChangeHandler() { super(WindowsInternalFrameTitlePane.this); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("frameIcon".equals(str) && WindowsInternalFrameTitlePane.this.systemLabel != null)
        WindowsInternalFrameTitlePane.this.systemLabel.setIcon(WindowsInternalFrameTitlePane.this.frame.getFrameIcon()); 
      super.propertyChange(param1PropertyChangeEvent);
    }
  }
  
  public class WindowsTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout {
    private Insets captionMargin = null;
    
    private Insets contentMargin = null;
    
    private XPStyle xp = XPStyle.getXP();
    
    WindowsTitlePaneLayout() {
      super(WindowsInternalFrameTitlePane.this);
      if (this.xp != null) {
        WindowsInternalFrameTitlePane windowsInternalFrameTitlePane = this$0;
        this.captionMargin = this.xp.getMargin(windowsInternalFrameTitlePane, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CAPTIONMARGINS);
        this.contentMargin = this.xp.getMargin(windowsInternalFrameTitlePane, TMSchema.Part.WP_CAPTION, null, TMSchema.Prop.CONTENTMARGINS);
      } 
      if (this.captionMargin == null)
        this.captionMargin = new Insets(0, 2, 0, 2); 
      if (this.contentMargin == null)
        this.contentMargin = new Insets(0, 0, 0, 0); 
    }
    
    private int layoutButton(JComponent param1JComponent, TMSchema.Part param1Part, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, boolean param1Boolean) {
      if (!param1Boolean)
        param1Int1 -= param1Int3; 
      param1JComponent.setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
      if (param1Boolean) {
        param1Int1 += param1Int3 + 2;
      } else {
        param1Int1 -= 2;
      } 
      return param1Int1;
    }
    
    public void layoutContainer(Container param1Container) {
      int i;
      boolean bool = WindowsGraphicsUtils.isLeftToRight(WindowsInternalFrameTitlePane.this.frame);
      int k = WindowsInternalFrameTitlePane.this.getWidth();
      int m = WindowsInternalFrameTitlePane.this.getHeight();
      int n = (this.xp != null) ? ((m - 2) * 6 / 10) : (m - 4);
      if (this.xp != null) {
        i = bool ? (this.captionMargin.left + 2) : (k - this.captionMargin.right - 2);
      } else {
        i = bool ? this.captionMargin.left : (k - this.captionMargin.right);
      } 
      int j = (m - n) / 2;
      layoutButton(WindowsInternalFrameTitlePane.this.systemLabel, TMSchema.Part.WP_SYSBUTTON, i, j, n, n, 0, bool);
      if (this.xp != null) {
        i = bool ? (k - this.captionMargin.right - 2) : (this.captionMargin.left + 2);
        j = 1;
        if (WindowsInternalFrameTitlePane.this.frame.isMaximum()) {
          j++;
        } else {
          j += 5;
        } 
      } else {
        i = bool ? (k - this.captionMargin.right) : this.captionMargin.left;
        j = (m - WindowsInternalFrameTitlePane.this.buttonHeight) / 2;
      } 
      if (WindowsInternalFrameTitlePane.this.frame.isClosable())
        i = layoutButton(WindowsInternalFrameTitlePane.this.closeButton, TMSchema.Part.WP_CLOSEBUTTON, i, j, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 2, !bool); 
      if (WindowsInternalFrameTitlePane.this.frame.isMaximizable())
        i = layoutButton(WindowsInternalFrameTitlePane.this.maxButton, TMSchema.Part.WP_MAXBUTTON, i, j, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, (this.xp != null) ? 2 : 0, !bool); 
      if (WindowsInternalFrameTitlePane.this.frame.isIconifiable())
        layoutButton(WindowsInternalFrameTitlePane.this.iconButton, TMSchema.Part.WP_MINBUTTON, i, j, WindowsInternalFrameTitlePane.this.buttonWidth, WindowsInternalFrameTitlePane.this.buttonHeight, 0, !bool); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */