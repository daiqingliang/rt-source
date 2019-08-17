package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class MotifGraphicsUtils implements SwingConstants {
  private static final String MAX_ACC_WIDTH = "maxAccWidth";
  
  static void drawPoint(Graphics paramGraphics, int paramInt1, int paramInt2) { paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt2); }
  
  public static void drawGroove(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor1, Color paramColor2) {
    Color color = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(paramColor1);
    paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.setColor(paramColor2);
    paramGraphics.drawLine(1, paramInt4 - 3, 1, 1);
    paramGraphics.drawLine(1, 1, paramInt3 - 3, 1);
    paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.drawLine(paramInt3 - 1, paramInt4 - 1, paramInt3 - 1, 0);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color);
  }
  
  public static void drawStringInRect(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { drawStringInRect(null, paramGraphics, paramString, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  static void drawStringInRect(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i;
    if (paramGraphics.getFont() == null)
      return; 
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics);
    if (fontMetrics == null)
      return; 
    if (paramInt5 == 0) {
      int m = SwingUtilities2.stringWidth(paramJComponent, fontMetrics, paramString);
      if (m > paramInt3)
        m = paramInt3; 
      i = paramInt1 + (paramInt3 - m) / 2;
    } else if (paramInt5 == 4) {
      int m = SwingUtilities2.stringWidth(paramJComponent, fontMetrics, paramString);
      if (m > paramInt3)
        m = paramInt3; 
      i = paramInt1 + paramInt3 - m;
    } else {
      i = paramInt1;
    } 
    int k = (paramInt4 - fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
    if (k < 0)
      k = 0; 
    int j = paramInt2 + paramInt4 - k - fontMetrics.getDescent();
    SwingUtilities2.drawString(paramJComponent, paramGraphics, paramString, i, j);
  }
  
  public static void paintMenuItem(Graphics paramGraphics, JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, Color paramColor1, Color paramColor2, int paramInt) {
    JMenuItem jMenuItem = (JMenuItem)paramJComponent;
    ButtonModel buttonModel = jMenuItem.getModel();
    Dimension dimension = jMenuItem.getSize();
    Insets insets = paramJComponent.getInsets();
    Rectangle rectangle1 = new Rectangle(dimension);
    rectangle1.x += insets.left;
    rectangle1.y += insets.top;
    rectangle1.width -= insets.right + rectangle1.x;
    rectangle1.height -= insets.bottom + rectangle1.y;
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    Rectangle rectangle4 = new Rectangle();
    Rectangle rectangle5 = new Rectangle();
    Rectangle rectangle6 = new Rectangle();
    Font font1 = paramGraphics.getFont();
    Font font2 = paramJComponent.getFont();
    paramGraphics.setFont(font2);
    FontMetrics fontMetrics1 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, font2);
    FontMetrics fontMetrics2 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, UIManager.getFont("MenuItem.acceleratorFont"));
    if (paramJComponent.isOpaque()) {
      if (buttonModel.isArmed() || (paramJComponent instanceof javax.swing.JMenu && buttonModel.isSelected())) {
        paramGraphics.setColor(paramColor1);
      } else {
        paramGraphics.setColor(paramJComponent.getBackground());
      } 
      paramGraphics.fillRect(0, 0, dimension.width, dimension.height);
    } 
    KeyStroke keyStroke = jMenuItem.getAccelerator();
    String str1 = "";
    if (keyStroke != null) {
      int i = keyStroke.getModifiers();
      if (i > 0) {
        str1 = KeyEvent.getKeyModifiersText(i);
        str1 = str1 + "+";
      } 
      str1 = str1 + KeyEvent.getKeyText(keyStroke.getKeyCode());
    } 
    String str2 = layoutMenuItem(paramJComponent, fontMetrics1, jMenuItem.getText(), fontMetrics2, str1, jMenuItem.getIcon(), paramIcon1, paramIcon2, jMenuItem.getVerticalAlignment(), jMenuItem.getHorizontalAlignment(), jMenuItem.getVerticalTextPosition(), jMenuItem.getHorizontalTextPosition(), rectangle1, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6, (jMenuItem.getText() == null) ? 0 : paramInt, paramInt);
    Color color = paramGraphics.getColor();
    if (paramIcon1 != null) {
      if (buttonModel.isArmed() || (paramJComponent instanceof javax.swing.JMenu && buttonModel.isSelected()))
        paramGraphics.setColor(paramColor2); 
      paramIcon1.paintIcon(paramJComponent, paramGraphics, rectangle5.x, rectangle5.y);
      paramGraphics.setColor(color);
    } 
    if (jMenuItem.getIcon() != null) {
      Icon icon;
      if (!buttonModel.isEnabled()) {
        icon = jMenuItem.getDisabledIcon();
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        icon = jMenuItem.getPressedIcon();
        if (icon == null)
          icon = jMenuItem.getIcon(); 
      } else {
        icon = jMenuItem.getIcon();
      } 
      if (icon != null)
        icon.paintIcon(paramJComponent, paramGraphics, rectangle2.x, rectangle2.y); 
    } 
    if (str2 != null && !str2.equals("")) {
      View view = (View)paramJComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, rectangle3);
      } else {
        int i = jMenuItem.getDisplayedMnemonicIndex();
        if (!buttonModel.isEnabled()) {
          paramGraphics.setColor(jMenuItem.getBackground().brighter());
          SwingUtilities2.drawStringUnderlineCharAt(jMenuItem, paramGraphics, str2, i, rectangle3.x, rectangle3.y + fontMetrics2.getAscent());
          paramGraphics.setColor(jMenuItem.getBackground().darker());
          SwingUtilities2.drawStringUnderlineCharAt(jMenuItem, paramGraphics, str2, i, rectangle3.x - 1, rectangle3.y + fontMetrics2.getAscent() - 1);
        } else {
          if (buttonModel.isArmed() || (paramJComponent instanceof javax.swing.JMenu && buttonModel.isSelected())) {
            paramGraphics.setColor(paramColor2);
          } else {
            paramGraphics.setColor(jMenuItem.getForeground());
          } 
          SwingUtilities2.drawStringUnderlineCharAt(jMenuItem, paramGraphics, str2, i, rectangle3.x, rectangle3.y + fontMetrics1.getAscent());
        } 
      } 
    } 
    if (str1 != null && !str1.equals("")) {
      int i = 0;
      Container container = jMenuItem.getParent();
      if (container != null && container instanceof JComponent) {
        JComponent jComponent = (JComponent)container;
        Integer integer = (Integer)jComponent.getClientProperty("maxAccWidth");
        int j = (integer != null) ? integer.intValue() : rectangle4.width;
        i = j - rectangle4.width;
      } 
      paramGraphics.setFont(UIManager.getFont("MenuItem.acceleratorFont"));
      if (!buttonModel.isEnabled()) {
        paramGraphics.setColor(jMenuItem.getBackground().brighter());
        SwingUtilities2.drawString(paramJComponent, paramGraphics, str1, rectangle4.x - i, rectangle4.y + fontMetrics1.getAscent());
        paramGraphics.setColor(jMenuItem.getBackground().darker());
        SwingUtilities2.drawString(paramJComponent, paramGraphics, str1, rectangle4.x - i - 1, rectangle4.y + fontMetrics1.getAscent() - 1);
      } else {
        if (buttonModel.isArmed() || (paramJComponent instanceof javax.swing.JMenu && buttonModel.isSelected())) {
          paramGraphics.setColor(paramColor2);
        } else {
          paramGraphics.setColor(jMenuItem.getForeground());
        } 
        SwingUtilities2.drawString(paramJComponent, paramGraphics, str1, rectangle4.x - i, rectangle4.y + fontMetrics2.getAscent());
      } 
    } 
    if (paramIcon2 != null) {
      if (buttonModel.isArmed() || (paramJComponent instanceof javax.swing.JMenu && buttonModel.isSelected()))
        paramGraphics.setColor(paramColor2); 
      if (!(jMenuItem.getParent() instanceof javax.swing.JMenuBar))
        paramIcon2.paintIcon(paramJComponent, paramGraphics, rectangle6.x, rectangle6.y); 
    } 
    paramGraphics.setColor(color);
    paramGraphics.setFont(font1);
  }
  
  private static String layoutMenuItem(JComponent paramJComponent, FontMetrics paramFontMetrics1, String paramString1, FontMetrics paramFontMetrics2, String paramString2, Icon paramIcon1, Icon paramIcon2, Icon paramIcon3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, Rectangle paramRectangle4, Rectangle paramRectangle5, Rectangle paramRectangle6, int paramInt5, int paramInt6) {
    SwingUtilities.layoutCompoundLabel(paramJComponent, paramFontMetrics1, paramString1, paramIcon1, paramInt1, paramInt2, paramInt3, paramInt4, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
    if (paramString2 == null || paramString2.equals("")) {
      paramRectangle4.width = paramRectangle4.height = 0;
      paramString2 = "";
    } else {
      paramRectangle4.width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics2, paramString2);
      paramRectangle4.height = paramFontMetrics2.getHeight();
    } 
    if (paramIcon2 != null) {
      paramRectangle5.width = paramIcon2.getIconWidth();
      paramRectangle5.height = paramIcon2.getIconHeight();
    } else {
      paramRectangle5.width = paramRectangle5.height = 0;
    } 
    if (paramIcon3 != null) {
      paramRectangle6.width = paramIcon3.getIconWidth();
      paramRectangle6.height = paramIcon3.getIconHeight();
    } else {
      paramRectangle6.width = paramRectangle6.height = 0;
    } 
    Rectangle rectangle = paramRectangle2.union(paramRectangle3);
    if (isLeftToRight(paramJComponent)) {
      paramRectangle3.x += paramRectangle5.width + paramInt6;
      paramRectangle2.x += paramRectangle5.width + paramInt6;
      paramRectangle4.x = paramRectangle1.x + paramRectangle1.width - paramRectangle6.width - paramInt6 - paramRectangle4.width;
      paramRectangle5.x = paramRectangle1.x;
      paramRectangle6.x = paramRectangle1.x + paramRectangle1.width - paramInt6 - paramRectangle6.width;
    } else {
      paramRectangle3.x -= paramRectangle5.width + paramInt6;
      paramRectangle2.x -= paramRectangle5.width + paramInt6;
      paramRectangle4.x = paramRectangle1.x + paramRectangle6.width + paramInt6;
      paramRectangle5.x = paramRectangle1.x + paramRectangle1.width - paramRectangle5.width;
      paramRectangle1.x += paramInt6;
    } 
    paramRectangle4.y = rectangle.y + rectangle.height / 2 - paramRectangle4.height / 2;
    paramRectangle6.y = rectangle.y + rectangle.height / 2 - paramRectangle6.height / 2;
    paramRectangle5.y = rectangle.y + rectangle.height / 2 - paramRectangle5.height / 2;
    return paramString1;
  }
  
  private static void drawMenuBezel(Graphics paramGraphics, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.setColor(paramColor);
    paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    paramGraphics.setColor(paramColor.brighter().brighter());
    paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
    paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 2, paramInt1 + paramInt3 - 1, paramInt2 + 1);
    paramGraphics.setColor(paramColor.darker().darker());
    paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + paramInt3 - 2, paramInt2);
    paramGraphics.drawLine(paramInt1, paramInt2 + 1, paramInt1, paramInt2 + paramInt4 - 2);
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifGraphicsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */