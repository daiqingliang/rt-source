package com.sun.java.swing.plaf.windows;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class WindowsProgressBarUI extends BasicProgressBarUI {
  private Rectangle previousFullBox;
  
  private Insets indeterminateInsets;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsProgressBarUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    if (XPStyle.getXP() != null) {
      LookAndFeel.installProperty(this.progressBar, "opaque", Boolean.FALSE);
      this.progressBar.setBorder(null);
      this.indeterminateInsets = UIManager.getInsets("ProgressBar.indeterminateInsets");
    } 
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    int i = super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if (XPStyle.getXP() != null && this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
      FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
      int j = (this.progressBar.getInsets()).top;
      if (this.progressBar.isIndeterminate()) {
        j = -1;
        paramInt2--;
      } else {
        j = 0;
        paramInt2 -= 3;
      } 
      i = j + (paramInt2 + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
    } 
    return i;
  }
  
  protected Dimension getPreferredInnerHorizontal() {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      XPStyle.Skin skin = xPStyle.getSkin(this.progressBar, TMSchema.Part.PP_BAR);
      return new Dimension((int)super.getPreferredInnerHorizontal().getWidth(), skin.getHeight());
    } 
    return super.getPreferredInnerHorizontal();
  }
  
  protected Dimension getPreferredInnerVertical() {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      XPStyle.Skin skin = xPStyle.getSkin(this.progressBar, TMSchema.Part.PP_BARVERT);
      return new Dimension(skin.getWidth(), (int)super.getPreferredInnerVertical().getHeight());
    } 
    return super.getPreferredInnerVertical();
  }
  
  protected void paintDeterminate(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      boolean bool1 = (this.progressBar.getOrientation() == 1);
      boolean bool2 = WindowsGraphicsUtils.isLeftToRight(paramJComponent);
      int i = this.progressBar.getWidth();
      int j = this.progressBar.getHeight() - 1;
      int k = getAmountFull(null, i, j);
      paintXPBackground(paramGraphics, bool1, i, j);
      if (this.progressBar.isStringPainted()) {
        paramGraphics.setColor(this.progressBar.getForeground());
        j -= 2;
        i -= 2;
        if (i <= 0 || j <= 0)
          return; 
        Graphics2D graphics2D = (Graphics2D)paramGraphics;
        graphics2D.setStroke(new BasicStroke((bool1 ? i : j), 0, 2));
        if (!bool1) {
          if (bool2) {
            graphics2D.drawLine(2, j / 2 + 1, k - 2, j / 2 + 1);
          } else {
            graphics2D.drawLine(2 + i, j / 2 + 1, 2 + i - k - 2, j / 2 + 1);
          } 
          paintString(paramGraphics, 0, 0, i, j, k, null);
        } else {
          graphics2D.drawLine(i / 2 + 1, j + 1, i / 2 + 1, j + 1 - k + 2);
          paintString(paramGraphics, 2, 2, i, j, k, null);
        } 
      } else {
        int m;
        XPStyle.Skin skin = xPStyle.getSkin(this.progressBar, bool1 ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK);
        if (bool1) {
          m = i - 5;
        } else {
          m = j - 5;
        } 
        int n = xPStyle.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSCHUNKSIZE, 2);
        int i1 = xPStyle.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
        int i2 = (k - 4) / (n + i1);
        if (i1 > 0 && i2 * (n + i1) + n < k - 4)
          i2++; 
        for (int i3 = 0; i3 < i2; i3++) {
          if (bool1) {
            skin.paintSkin(paramGraphics, 3, j - i3 * (n + i1) - n - 2, m, n, null);
          } else if (bool2) {
            skin.paintSkin(paramGraphics, 4 + i3 * (n + i1), 2, n, m, null);
          } else {
            skin.paintSkin(paramGraphics, i - 2 + (i3 + 1) * (n + i1), 2, n, m, null);
          } 
        } 
      } 
    } else {
      super.paintDeterminate(paramGraphics, paramJComponent);
    } 
  }
  
  protected void setAnimationIndex(int paramInt) {
    super.setAnimationIndex(paramInt);
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null)
      if (this.boxRect != null) {
        Rectangle rectangle = getFullChunkBounds(this.boxRect);
        if (this.previousFullBox != null)
          rectangle.add(this.previousFullBox); 
        this.progressBar.repaint(rectangle);
      } else {
        this.progressBar.repaint();
      }  
  }
  
  protected int getBoxLength(int paramInt1, int paramInt2) {
    XPStyle xPStyle = XPStyle.getXP();
    return (xPStyle != null) ? 6 : super.getBoxLength(paramInt1, paramInt2);
  }
  
  protected Rectangle getBox(Rectangle paramRectangle) {
    Rectangle rectangle = super.getBox(paramRectangle);
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      boolean bool = (this.progressBar.getOrientation() == 1) ? 1 : 0;
      TMSchema.Part part = bool ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
      Insets insets = this.indeterminateInsets;
      int i = getAnimationIndex();
      int j = getFrameCount() / 2;
      int k = xPStyle.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
      i %= j;
      if (!bool) {
        rectangle.y += insets.top;
        rectangle.height = this.progressBar.getHeight() - insets.top - insets.bottom;
        int m = this.progressBar.getWidth() - insets.left - insets.right;
        m += (rectangle.width + k) * 2;
        double d = m / j;
        rectangle.x = (int)(d * i) + insets.left;
      } else {
        rectangle.x += insets.left;
        rectangle.width = this.progressBar.getWidth() - insets.left - insets.right;
        int m = this.progressBar.getHeight() - insets.top - insets.bottom;
        m += (rectangle.height + k) * 2;
        double d = m / j;
        rectangle.y = (int)(d * i) + insets.top;
      } 
    } 
    return rectangle;
  }
  
  protected void paintIndeterminate(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      boolean bool = (this.progressBar.getOrientation() == 1);
      int i = this.progressBar.getWidth();
      int j = this.progressBar.getHeight();
      paintXPBackground(paramGraphics, bool, i, j);
      this.boxRect = getBox(this.boxRect);
      if (this.boxRect != null) {
        paramGraphics.setColor(this.progressBar.getForeground());
        if (!(paramGraphics instanceof Graphics2D))
          return; 
        paintIndeterminateFrame(this.boxRect, (Graphics2D)paramGraphics, bool, i, j);
        if (this.progressBar.isStringPainted())
          if (!bool) {
            paintString(paramGraphics, -1, -1, i, j, 0, null);
          } else {
            paintString(paramGraphics, 1, 1, i, j, 0, null);
          }  
      } 
    } else {
      super.paintIndeterminate(paramGraphics, paramJComponent);
    } 
  }
  
  private Rectangle getFullChunkBounds(Rectangle paramRectangle) {
    boolean bool = (this.progressBar.getOrientation() == 1) ? 1 : 0;
    XPStyle xPStyle = XPStyle.getXP();
    int i = (xPStyle != null) ? xPStyle.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0) : 0;
    if (!bool) {
      int k = paramRectangle.width + i;
      return new Rectangle(paramRectangle.x - k * 2, paramRectangle.y, k * 3, paramRectangle.height);
    } 
    int j = paramRectangle.height + i;
    return new Rectangle(paramRectangle.x, paramRectangle.y - j * 2, paramRectangle.width, j * 3);
  }
  
  private void paintIndeterminateFrame(Rectangle paramRectangle, Graphics2D paramGraphics2D, boolean paramBoolean, int paramInt1, int paramInt2) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle == null)
      return; 
    Graphics2D graphics2D = (Graphics2D)paramGraphics2D.create();
    TMSchema.Part part1 = paramBoolean ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
    TMSchema.Part part2 = paramBoolean ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK;
    int i = xPStyle.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
    int j = 0;
    int k = 0;
    if (!paramBoolean) {
      j = -paramRectangle.width - i;
      k = 0;
    } else {
      j = 0;
      k = -paramRectangle.height - i;
    } 
    Rectangle rectangle1 = getFullChunkBounds(paramRectangle);
    this.previousFullBox = rectangle1;
    Insets insets = this.indeterminateInsets;
    Rectangle rectangle2 = new Rectangle(insets.left, insets.top, paramInt1 - insets.left - insets.right, paramInt2 - insets.top - insets.bottom);
    Rectangle rectangle3 = rectangle2.intersection(rectangle1);
    graphics2D.clip(rectangle3);
    XPStyle.Skin skin = xPStyle.getSkin(this.progressBar, part2);
    graphics2D.setComposite(AlphaComposite.getInstance(3, 0.8F));
    skin.paintSkin(graphics2D, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, null);
    paramRectangle.translate(j, k);
    graphics2D.setComposite(AlphaComposite.getInstance(3, 0.5F));
    skin.paintSkin(graphics2D, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, null);
    paramRectangle.translate(j, k);
    graphics2D.setComposite(AlphaComposite.getInstance(3, 0.2F));
    skin.paintSkin(graphics2D, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, null);
    graphics2D.dispose();
  }
  
  private void paintXPBackground(Graphics paramGraphics, boolean paramBoolean, int paramInt1, int paramInt2) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle == null)
      return; 
    TMSchema.Part part = paramBoolean ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
    XPStyle.Skin skin = xPStyle.getSkin(this.progressBar, part);
    skin.paintSkin(paramGraphics, 0, 0, paramInt1, paramInt2, null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */