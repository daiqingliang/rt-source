package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.text.View;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthIcon;

public class SynthGraphicsUtils {
  private Rectangle paintIconR = new Rectangle();
  
  private Rectangle paintTextR = new Rectangle();
  
  private Rectangle paintViewR = new Rectangle();
  
  private Insets paintInsets = new Insets(0, 0, 0, 0);
  
  private Rectangle iconR = new Rectangle();
  
  private Rectangle textR = new Rectangle();
  
  private Rectangle viewR = new Rectangle();
  
  private Insets viewSizingInsets = new Insets(0, 0, 0, 0);
  
  public void drawLine(SynthContext paramSynthContext, Object paramObject, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramGraphics.drawLine(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void drawLine(SynthContext paramSynthContext, Object paramObject1, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject2) {
    if ("dashed".equals(paramObject2)) {
      if (paramInt1 == paramInt3) {
        paramInt2 += paramInt2 % 2;
        for (int i = paramInt2; i <= paramInt4; i += 2)
          paramGraphics.drawLine(paramInt1, i, paramInt3, i); 
      } else if (paramInt2 == paramInt4) {
        paramInt1 += paramInt1 % 2;
        for (int i = paramInt1; i <= paramInt3; i += 2)
          paramGraphics.drawLine(i, paramInt2, i, paramInt4); 
      } 
    } else {
      drawLine(paramSynthContext, paramObject1, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public String layoutText(SynthContext paramSynthContext, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5) {
    if (paramIcon instanceof SynthIcon) {
      SynthIconWrapper synthIconWrapper = SynthIconWrapper.get((SynthIcon)paramIcon, paramSynthContext);
      String str = SwingUtilities.layoutCompoundLabel(paramSynthContext.getComponent(), paramFontMetrics, paramString, synthIconWrapper, paramInt2, paramInt1, paramInt4, paramInt3, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
      SynthIconWrapper.release(synthIconWrapper);
      return str;
    } 
    return SwingUtilities.layoutCompoundLabel(paramSynthContext.getComponent(), paramFontMetrics, paramString, paramIcon, paramInt2, paramInt1, paramInt4, paramInt3, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
  }
  
  public int computeStringWidth(SynthContext paramSynthContext, Font paramFont, FontMetrics paramFontMetrics, String paramString) { return SwingUtilities2.stringWidth(paramSynthContext.getComponent(), paramFontMetrics, paramString); }
  
  public Dimension getMinimumSize(SynthContext paramSynthContext, Font paramFont, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    JComponent jComponent = paramSynthContext.getComponent();
    Dimension dimension = getPreferredSize(paramSynthContext, paramFont, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    View view = (View)jComponent.getClientProperty("html");
    if (view != null)
      dimension.width = (int)(dimension.width - view.getPreferredSpan(0) - view.getMinimumSpan(0)); 
    return dimension;
  }
  
  public Dimension getMaximumSize(SynthContext paramSynthContext, Font paramFont, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    JComponent jComponent = paramSynthContext.getComponent();
    Dimension dimension = getPreferredSize(paramSynthContext, paramFont, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    View view = (View)jComponent.getClientProperty("html");
    if (view != null)
      dimension.width = (int)(dimension.width + view.getMaximumSpan(0) - view.getPreferredSpan(0)); 
    return dimension;
  }
  
  public int getMaximumCharHeight(SynthContext paramSynthContext) {
    FontMetrics fontMetrics = paramSynthContext.getComponent().getFontMetrics(paramSynthContext.getStyle().getFont(paramSynthContext));
    return fontMetrics.getAscent() + fontMetrics.getDescent();
  }
  
  public Dimension getPreferredSize(SynthContext paramSynthContext, Font paramFont, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    JComponent jComponent = paramSynthContext.getComponent();
    Insets insets = jComponent.getInsets(this.viewSizingInsets);
    int i = insets.left + insets.right;
    int j = insets.top + insets.bottom;
    if (paramIcon == null && (paramString == null || paramFont == null))
      return new Dimension(i, j); 
    if (paramString == null || (paramIcon != null && paramFont == null))
      return new Dimension(SynthIcon.getIconWidth(paramIcon, paramSynthContext) + i, SynthIcon.getIconHeight(paramIcon, paramSynthContext) + j); 
    FontMetrics fontMetrics = jComponent.getFontMetrics(paramFont);
    this.iconR.x = this.iconR.y = this.iconR.width = this.iconR.height = 0;
    this.textR.x = this.textR.y = this.textR.width = this.textR.height = 0;
    this.viewR.x = i;
    this.viewR.y = j;
    this.viewR.width = this.viewR.height = 32767;
    layoutText(paramSynthContext, fontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, this.viewR, this.iconR, this.textR, paramInt5);
    int k = Math.min(this.iconR.x, this.textR.x);
    int m = Math.max(this.iconR.x + this.iconR.width, this.textR.x + this.textR.width);
    int n = Math.min(this.iconR.y, this.textR.y);
    int i1 = Math.max(this.iconR.y + this.iconR.height, this.textR.y + this.textR.height);
    Dimension dimension = new Dimension(m - k, i1 - n);
    dimension.width += i;
    dimension.height += j;
    return dimension;
  }
  
  public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, Rectangle paramRectangle, int paramInt) { paintText(paramSynthContext, paramGraphics, paramString, paramRectangle.x, paramRectangle.y, paramInt); }
  
  public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramString != null) {
      JComponent jComponent = paramSynthContext.getComponent();
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jComponent, paramGraphics);
      paramInt2 += fontMetrics.getAscent();
      SwingUtilities2.drawStringUnderlineCharAt(jComponent, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
    } 
  }
  
  public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    if (paramIcon == null && paramString == null)
      return; 
    JComponent jComponent = paramSynthContext.getComponent();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jComponent, paramGraphics);
    Insets insets = SynthLookAndFeel.getPaintingInsets(paramSynthContext, this.paintInsets);
    this.paintViewR.x = insets.left;
    this.paintViewR.y = insets.top;
    this.paintViewR.width = jComponent.getWidth() - insets.left + insets.right;
    this.paintViewR.height = jComponent.getHeight() - insets.top + insets.bottom;
    this.paintIconR.x = this.paintIconR.y = this.paintIconR.width = this.paintIconR.height = 0;
    this.paintTextR.x = this.paintTextR.y = this.paintTextR.width = this.paintTextR.height = 0;
    String str = layoutText(paramSynthContext, fontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, this.paintViewR, this.paintIconR, this.paintTextR, paramInt5);
    if (paramIcon != null) {
      Color color = paramGraphics.getColor();
      if (paramSynthContext.getStyle().getBoolean(paramSynthContext, "TableHeader.alignSorterArrow", false) && "TableHeader.renderer".equals(jComponent.getName())) {
        this.paintIconR.x = this.paintViewR.width - this.paintIconR.width;
      } else {
        this.paintIconR.x += paramInt7;
      } 
      this.paintIconR.y += paramInt7;
      SynthIcon.paintIcon(paramIcon, paramSynthContext, paramGraphics, this.paintIconR.x, this.paintIconR.y, this.paintIconR.width, this.paintIconR.height);
      paramGraphics.setColor(color);
    } 
    if (paramString != null) {
      View view = (View)jComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, this.paintTextR);
      } else {
        this.paintTextR.x += paramInt7;
        this.paintTextR.y += paramInt7;
        paintText(paramSynthContext, paramGraphics, str, this.paintTextR, paramInt6);
      } 
    } 
  }
  
  static Dimension getPreferredMenuItemSize(SynthContext paramSynthContext1, SynthContext paramSynthContext2, JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt, String paramString1, boolean paramBoolean, String paramString2) {
    JMenuItem jMenuItem = (JMenuItem)paramJComponent;
    SynthMenuItemLayoutHelper synthMenuItemLayoutHelper = new SynthMenuItemLayoutHelper(paramSynthContext1, paramSynthContext2, jMenuItem, paramIcon1, paramIcon2, MenuItemLayoutHelper.createMaxRect(), paramInt, paramString1, SynthLookAndFeel.isLeftToRight(jMenuItem), paramBoolean, paramString2);
    Dimension dimension = new Dimension();
    int i = synthMenuItemLayoutHelper.getGap();
    dimension.width = 0;
    MenuItemLayoutHelper.addMaxWidth(synthMenuItemLayoutHelper.getCheckSize(), i, dimension);
    MenuItemLayoutHelper.addMaxWidth(synthMenuItemLayoutHelper.getLabelSize(), i, dimension);
    MenuItemLayoutHelper.addWidth(synthMenuItemLayoutHelper.getMaxAccOrArrowWidth(), 5 * i, dimension);
    dimension.width -= i;
    dimension.height = MenuItemLayoutHelper.max(new int[] { synthMenuItemLayoutHelper.getCheckSize().getHeight(), synthMenuItemLayoutHelper.getLabelSize().getHeight(), synthMenuItemLayoutHelper.getAccSize().getHeight(), synthMenuItemLayoutHelper.getArrowSize().getHeight() });
    Insets insets = synthMenuItemLayoutHelper.getMenuItem().getInsets();
    if (insets != null) {
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
    } 
    if (dimension.width % 2 == 0)
      dimension.width++; 
    if (dimension.height % 2 == 0)
      dimension.height++; 
    return dimension;
  }
  
  static void applyInsets(Rectangle paramRectangle, Insets paramInsets, boolean paramBoolean) {
    if (paramInsets != null) {
      paramRectangle.x += (paramBoolean ? paramInsets.left : paramInsets.right);
      paramRectangle.y += paramInsets.top;
      paramRectangle.width -= (paramBoolean ? paramInsets.right : paramInsets.left) + paramRectangle.x;
      paramRectangle.height -= paramInsets.bottom + paramRectangle.y;
    } 
  }
  
  static void paint(SynthContext paramSynthContext1, SynthContext paramSynthContext2, Graphics paramGraphics, Icon paramIcon1, Icon paramIcon2, String paramString1, int paramInt, String paramString2) {
    JMenuItem jMenuItem = (JMenuItem)paramSynthContext1.getComponent();
    SynthStyle synthStyle = paramSynthContext1.getStyle();
    paramGraphics.setFont(synthStyle.getFont(paramSynthContext1));
    Rectangle rectangle = new Rectangle(0, 0, jMenuItem.getWidth(), jMenuItem.getHeight());
    boolean bool = SynthLookAndFeel.isLeftToRight(jMenuItem);
    applyInsets(rectangle, jMenuItem.getInsets(), bool);
    SynthMenuItemLayoutHelper synthMenuItemLayoutHelper = new SynthMenuItemLayoutHelper(paramSynthContext1, paramSynthContext2, jMenuItem, paramIcon1, paramIcon2, rectangle, paramInt, paramString1, bool, MenuItemLayoutHelper.useCheckAndArrow(jMenuItem), paramString2);
    MenuItemLayoutHelper.LayoutResult layoutResult = synthMenuItemLayoutHelper.layoutMenuItem();
    paintMenuItem(paramGraphics, synthMenuItemLayoutHelper, layoutResult);
  }
  
  static void paintMenuItem(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    Font font = paramGraphics.getFont();
    Color color = paramGraphics.getColor();
    paintCheckIcon(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintIcon(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintText(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintAccText(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintArrowIcon(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paramGraphics.setColor(color);
    paramGraphics.setFont(font);
  }
  
  static void paintBackground(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper) { paintBackground(paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, paramSynthMenuItemLayoutHelper.getMenuItem()); }
  
  static void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent) { paramSynthContext.getPainter().paintMenuItemBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight()); }
  
  static void paintIcon(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    if (paramSynthMenuItemLayoutHelper.getIcon() != null) {
      Icon icon;
      JMenuItem jMenuItem = paramSynthMenuItemLayoutHelper.getMenuItem();
      ButtonModel buttonModel = jMenuItem.getModel();
      if (!buttonModel.isEnabled()) {
        icon = jMenuItem.getDisabledIcon();
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        icon = jMenuItem.getPressedIcon();
        if (icon == null)
          icon = jMenuItem.getIcon(); 
      } else {
        icon = jMenuItem.getIcon();
      } 
      if (icon != null) {
        Rectangle rectangle = paramLayoutResult.getIconRect();
        SynthIcon.paintIcon(icon, paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
    } 
  }
  
  static void paintCheckIcon(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    if (paramSynthMenuItemLayoutHelper.getCheckIcon() != null) {
      Rectangle rectangle = paramLayoutResult.getCheckRect();
      SynthIcon.paintIcon(paramSynthMenuItemLayoutHelper.getCheckIcon(), paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  static void paintAccText(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    String str = paramSynthMenuItemLayoutHelper.getAccText();
    if (str != null && !str.equals("")) {
      paramGraphics.setColor(paramSynthMenuItemLayoutHelper.getAccStyle().getColor(paramSynthMenuItemLayoutHelper.getAccContext(), ColorType.TEXT_FOREGROUND));
      paramGraphics.setFont(paramSynthMenuItemLayoutHelper.getAccStyle().getFont(paramSynthMenuItemLayoutHelper.getAccContext()));
      paramSynthMenuItemLayoutHelper.getAccGraphicsUtils().paintText(paramSynthMenuItemLayoutHelper.getAccContext(), paramGraphics, str, (paramLayoutResult.getAccRect()).x, (paramLayoutResult.getAccRect()).y, -1);
    } 
  }
  
  static void paintText(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    if (!paramSynthMenuItemLayoutHelper.getText().equals(""))
      if (paramSynthMenuItemLayoutHelper.getHtmlView() != null) {
        paramSynthMenuItemLayoutHelper.getHtmlView().paint(paramGraphics, paramLayoutResult.getTextRect());
      } else {
        paramGraphics.setColor(paramSynthMenuItemLayoutHelper.getStyle().getColor(paramSynthMenuItemLayoutHelper.getContext(), ColorType.TEXT_FOREGROUND));
        paramGraphics.setFont(paramSynthMenuItemLayoutHelper.getStyle().getFont(paramSynthMenuItemLayoutHelper.getContext()));
        paramSynthMenuItemLayoutHelper.getGraphicsUtils().paintText(paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, paramSynthMenuItemLayoutHelper.getText(), (paramLayoutResult.getTextRect()).x, (paramLayoutResult.getTextRect()).y, paramSynthMenuItemLayoutHelper.getMenuItem().getDisplayedMnemonicIndex());
      }  
  }
  
  static void paintArrowIcon(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    if (paramSynthMenuItemLayoutHelper.getArrowIcon() != null) {
      Rectangle rectangle = paramLayoutResult.getArrowRect();
      SynthIcon.paintIcon(paramSynthMenuItemLayoutHelper.getArrowIcon(), paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  private static class SynthIconWrapper implements Icon {
    private static final List<SynthIconWrapper> CACHE = new ArrayList(1);
    
    private SynthIcon synthIcon;
    
    private SynthContext context;
    
    static SynthIconWrapper get(SynthIcon param1SynthIcon, SynthContext param1SynthContext) {
      synchronized (CACHE) {
        int i = CACHE.size();
        if (i > 0) {
          SynthIconWrapper synthIconWrapper = (SynthIconWrapper)CACHE.remove(i - 1);
          synthIconWrapper.reset(param1SynthIcon, param1SynthContext);
          return synthIconWrapper;
        } 
      } 
      return new SynthIconWrapper(param1SynthIcon, param1SynthContext);
    }
    
    static void release(SynthIconWrapper param1SynthIconWrapper) {
      param1SynthIconWrapper.reset(null, null);
      synchronized (CACHE) {
        CACHE.add(param1SynthIconWrapper);
      } 
    }
    
    SynthIconWrapper(SynthIcon param1SynthIcon, SynthContext param1SynthContext) { reset(param1SynthIcon, param1SynthContext); }
    
    void reset(SynthIcon param1SynthIcon, SynthContext param1SynthContext) {
      this.synthIcon = param1SynthIcon;
      this.context = param1SynthContext;
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return this.synthIcon.getIconWidth(this.context); }
    
    public int getIconHeight() { return this.synthIcon.getIconHeight(this.context); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthGraphicsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */