package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import sun.swing.SwingUtilities2;

public class BasicGraphicsUtils {
  private static final Insets GROOVE_INSETS = new Insets(2, 2, 2, 2);
  
  private static final Insets ETCHED_INSETS = new Insets(2, 2, 2, 2);
  
  public static void drawEtchedRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) {
    Color color = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(paramColor1);
    paramGraphics.drawLine(0, 0, paramInt3 - 1, 0);
    paramGraphics.drawLine(0, 1, 0, paramInt4 - 2);
    paramGraphics.setColor(paramColor2);
    paramGraphics.drawLine(1, 1, paramInt3 - 3, 1);
    paramGraphics.drawLine(1, 2, 1, paramInt4 - 3);
    paramGraphics.setColor(paramColor4);
    paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.setColor(paramColor3);
    paramGraphics.drawLine(paramInt3 - 2, 1, paramInt3 - 2, paramInt4 - 3);
    paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color);
  }
  
  public static Insets getEtchedInsets() { return ETCHED_INSETS; }
  
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
  
  public static Insets getGrooveInsets() { return GROOVE_INSETS; }
  
  public static void drawBezel(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) {
    Color color = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    if (paramBoolean1 && paramBoolean2) {
      paramGraphics.setColor(paramColor2);
      paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(paramColor1);
      paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
    } else if (paramBoolean1) {
      drawLoweredBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramColor1, paramColor2, paramColor3, paramColor4);
    } else if (paramBoolean2) {
      paramGraphics.setColor(paramColor2);
      paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(paramColor4);
      paramGraphics.drawLine(1, 1, 1, paramInt4 - 3);
      paramGraphics.drawLine(2, 1, paramInt3 - 3, 1);
      paramGraphics.setColor(paramColor3);
      paramGraphics.drawLine(2, 2, 2, paramInt4 - 4);
      paramGraphics.drawLine(3, 2, paramInt3 - 4, 2);
      paramGraphics.setColor(paramColor1);
      paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
      paramGraphics.drawLine(paramInt3 - 3, 2, paramInt3 - 3, paramInt4 - 4);
      paramGraphics.setColor(paramColor2);
      paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
      paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, 1);
    } else {
      paramGraphics.setColor(paramColor4);
      paramGraphics.drawLine(0, 0, 0, paramInt4 - 1);
      paramGraphics.drawLine(1, 0, paramInt3 - 2, 0);
      paramGraphics.setColor(paramColor3);
      paramGraphics.drawLine(1, 1, 1, paramInt4 - 3);
      paramGraphics.drawLine(2, 1, paramInt3 - 3, 1);
      paramGraphics.setColor(paramColor1);
      paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
      paramGraphics.drawLine(paramInt3 - 2, 1, paramInt3 - 2, paramInt4 - 3);
      paramGraphics.setColor(paramColor2);
      paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(paramInt3 - 1, paramInt4 - 1, paramInt3 - 1, 0);
    } 
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(color);
  }
  
  public static void drawLoweredBezel(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) {
    paramGraphics.setColor(paramColor2);
    paramGraphics.drawLine(0, 0, 0, paramInt4 - 1);
    paramGraphics.drawLine(1, 0, paramInt3 - 2, 0);
    paramGraphics.setColor(paramColor1);
    paramGraphics.drawLine(1, 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(1, 1, paramInt3 - 3, 1);
    paramGraphics.setColor(paramColor4);
    paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.drawLine(paramInt3 - 1, paramInt4 - 1, paramInt3 - 1, 0);
    paramGraphics.setColor(paramColor3);
    paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, 1);
  }
  
  public static void drawString(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3) {
    int i = -1;
    if (paramInt1 != 0) {
      char c1 = Character.toUpperCase((char)paramInt1);
      char c2 = Character.toLowerCase((char)paramInt1);
      int j = paramString.indexOf(c1);
      int k = paramString.indexOf(c2);
      if (j == -1) {
        i = k;
      } else if (k == -1) {
        i = j;
      } else {
        i = (k < j) ? k : j;
      } 
    } 
    drawStringUnderlineCharAt(paramGraphics, paramString, i, paramInt2, paramInt3);
  }
  
  public static void drawStringUnderlineCharAt(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3) { SwingUtilities2.drawStringUnderlineCharAt(null, paramGraphics, paramString, paramInt1, paramInt2, paramInt3); }
  
  public static void drawDashedRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    for (int i = paramInt1; i < paramInt1 + paramInt3; i += 2) {
      paramGraphics.fillRect(i, paramInt2, 1, 1);
      paramGraphics.fillRect(i, paramInt2 + paramInt4 - 1, 1, 1);
    } 
    for (int j = paramInt2; j < paramInt2 + paramInt4; j += 2) {
      paramGraphics.fillRect(paramInt1, j, 1, 1);
      paramGraphics.fillRect(paramInt1 + paramInt3 - 1, j, 1, 1);
    } 
  }
  
  public static Dimension getPreferredButtonSize(AbstractButton paramAbstractButton, int paramInt) {
    if (paramAbstractButton.getComponentCount() > 0)
      return null; 
    Icon icon = paramAbstractButton.getIcon();
    String str = paramAbstractButton.getText();
    Font font = paramAbstractButton.getFont();
    FontMetrics fontMetrics = paramAbstractButton.getFontMetrics(font);
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle(32767, 32767);
    SwingUtilities.layoutCompoundLabel(paramAbstractButton, fontMetrics, str, icon, paramAbstractButton.getVerticalAlignment(), paramAbstractButton.getHorizontalAlignment(), paramAbstractButton.getVerticalTextPosition(), paramAbstractButton.getHorizontalTextPosition(), rectangle3, rectangle1, rectangle2, (str == null) ? 0 : paramInt);
    Rectangle rectangle4 = rectangle1.union(rectangle2);
    Insets insets = paramAbstractButton.getInsets();
    rectangle4.width += insets.left + insets.right;
    rectangle4.height += insets.top + insets.bottom;
    return rectangle4.getSize();
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
  
  static boolean isMenuShortcutKeyDown(InputEvent paramInputEvent) { return ((paramInputEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicGraphicsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */