package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import sun.swing.SwingUtilities2;

public class MetalScrollBarUI extends BasicScrollBarUI {
  private static Color shadowColor;
  
  private static Color highlightColor;
  
  private static Color darkShadowColor;
  
  private static Color thumbColor;
  
  private static Color thumbShadow;
  
  private static Color thumbHighlightColor;
  
  protected MetalBumps bumps;
  
  protected MetalScrollButton increaseButton;
  
  protected MetalScrollButton decreaseButton;
  
  protected int scrollBarWidth;
  
  public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
  
  protected boolean isFreeStanding = true;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalScrollBarUI(); }
  
  protected void installDefaults() {
    this.scrollBarWidth = ((Integer)UIManager.get("ScrollBar.width")).intValue();
    super.installDefaults();
    this.bumps = new MetalBumps(10, 10, thumbHighlightColor, thumbShadow, thumbColor);
  }
  
  protected void installListeners() {
    super.installListeners();
    ((ScrollBarListener)this.propertyChangeListener).handlePropertyChange(this.scrollbar.getClientProperty("JScrollBar.isFreeStanding"));
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return new ScrollBarListener(); }
  
  protected void configureScrollBarColors() {
    super.configureScrollBarColors();
    shadowColor = UIManager.getColor("ScrollBar.shadow");
    highlightColor = UIManager.getColor("ScrollBar.highlight");
    darkShadowColor = UIManager.getColor("ScrollBar.darkShadow");
    thumbColor = UIManager.getColor("ScrollBar.thumb");
    thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
    thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return (this.scrollbar.getOrientation() == 1) ? new Dimension(this.scrollBarWidth, this.scrollBarWidth * 3 + 10) : new Dimension(this.scrollBarWidth * 3 + 10, this.scrollBarWidth); }
  
  protected JButton createDecreaseButton(int paramInt) {
    this.decreaseButton = new MetalScrollButton(paramInt, this.scrollBarWidth, this.isFreeStanding);
    return this.decreaseButton;
  }
  
  protected JButton createIncreaseButton(int paramInt) {
    this.increaseButton = new MetalScrollButton(paramInt, this.scrollBarWidth, this.isFreeStanding);
    return this.increaseButton;
  }
  
  protected void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    if (this.scrollbar.getOrientation() == 1) {
      if (!this.isFreeStanding) {
        paramRectangle.width += 2;
        if (!bool)
          paramGraphics.translate(-1, 0); 
      } 
      if (paramJComponent.isEnabled()) {
        paramGraphics.setColor(darkShadowColor);
        SwingUtilities2.drawVLine(paramGraphics, 0, 0, paramRectangle.height - 1);
        SwingUtilities2.drawVLine(paramGraphics, paramRectangle.width - 2, 0, paramRectangle.height - 1);
        SwingUtilities2.drawHLine(paramGraphics, 2, paramRectangle.width - 1, paramRectangle.height - 1);
        SwingUtilities2.drawHLine(paramGraphics, 2, paramRectangle.width - 2, 0);
        paramGraphics.setColor(shadowColor);
        SwingUtilities2.drawVLine(paramGraphics, 1, 1, paramRectangle.height - 2);
        SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 3, 1);
        if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
          int i = this.thumbRect.y + this.thumbRect.height - paramRectangle.y;
          SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 1, i);
        } 
        paramGraphics.setColor(highlightColor);
        SwingUtilities2.drawVLine(paramGraphics, paramRectangle.width - 1, 0, paramRectangle.height - 1);
      } else {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, 0, paramRectangle.width, paramRectangle.height);
      } 
      if (!this.isFreeStanding) {
        paramRectangle.width -= 2;
        if (!bool)
          paramGraphics.translate(1, 0); 
      } 
    } else {
      if (!this.isFreeStanding)
        paramRectangle.height += 2; 
      if (paramJComponent.isEnabled()) {
        paramGraphics.setColor(darkShadowColor);
        SwingUtilities2.drawHLine(paramGraphics, 0, paramRectangle.width - 1, 0);
        SwingUtilities2.drawVLine(paramGraphics, 0, 2, paramRectangle.height - 2);
        SwingUtilities2.drawHLine(paramGraphics, 0, paramRectangle.width - 1, paramRectangle.height - 2);
        SwingUtilities2.drawVLine(paramGraphics, paramRectangle.width - 1, 2, paramRectangle.height - 1);
        paramGraphics.setColor(shadowColor);
        SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 2, 1);
        SwingUtilities2.drawVLine(paramGraphics, 1, 1, paramRectangle.height - 3);
        SwingUtilities2.drawHLine(paramGraphics, 0, paramRectangle.width - 1, paramRectangle.height - 1);
        if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
          int i = this.thumbRect.x + this.thumbRect.width - paramRectangle.x;
          SwingUtilities2.drawVLine(paramGraphics, i, 1, paramRectangle.height - 1);
        } 
      } else {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, 0, paramRectangle.width, paramRectangle.height);
      } 
      if (!this.isFreeStanding)
        paramRectangle.height -= 2; 
    } 
    paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
  }
  
  protected void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    if (!paramJComponent.isEnabled())
      return; 
    if (MetalLookAndFeel.usingOcean()) {
      oceanPaintThumb(paramGraphics, paramJComponent, paramRectangle);
      return;
    } 
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    if (this.scrollbar.getOrientation() == 1) {
      if (!this.isFreeStanding) {
        paramRectangle.width += 2;
        if (!bool)
          paramGraphics.translate(-1, 0); 
      } 
      paramGraphics.setColor(thumbColor);
      paramGraphics.fillRect(0, 0, paramRectangle.width - 2, paramRectangle.height - 1);
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, paramRectangle.width - 2, paramRectangle.height - 1);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 3, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, paramRectangle.height - 2);
      this.bumps.setBumpArea(paramRectangle.width - 6, paramRectangle.height - 7);
      this.bumps.paintIcon(paramJComponent, paramGraphics, 3, 4);
      if (!this.isFreeStanding) {
        paramRectangle.width -= 2;
        if (!bool)
          paramGraphics.translate(1, 0); 
      } 
    } else {
      if (!this.isFreeStanding)
        paramRectangle.height += 2; 
      paramGraphics.setColor(thumbColor);
      paramGraphics.fillRect(0, 0, paramRectangle.width - 1, paramRectangle.height - 2);
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, paramRectangle.width - 1, paramRectangle.height - 2);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 3, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, paramRectangle.height - 3);
      this.bumps.setBumpArea(paramRectangle.width - 7, paramRectangle.height - 6);
      this.bumps.paintIcon(paramJComponent, paramGraphics, 4, 3);
      if (!this.isFreeStanding)
        paramRectangle.height -= 2; 
    } 
    paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
  }
  
  private void oceanPaintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    if (this.scrollbar.getOrientation() == 1) {
      if (!this.isFreeStanding) {
        paramRectangle.width += 2;
        if (!bool)
          paramGraphics.translate(-1, 0); 
      } 
      if (thumbColor != null) {
        paramGraphics.setColor(thumbColor);
        paramGraphics.fillRect(0, 0, paramRectangle.width - 2, paramRectangle.height - 1);
      } 
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, paramRectangle.width - 2, paramRectangle.height - 1);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 3, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, paramRectangle.height - 2);
      MetalUtils.drawGradient(paramJComponent, paramGraphics, "ScrollBar.gradient", 2, 2, paramRectangle.width - 4, paramRectangle.height - 3, false);
      int i = paramRectangle.width - 8;
      if (i > 2 && paramRectangle.height >= 10) {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        int j = paramRectangle.height / 2 - 2;
        int k;
        for (k = 0; k < 6; k += 2)
          paramGraphics.fillRect(4, k + j, i, 1); 
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        j++;
        for (k = 0; k < 6; k += 2)
          paramGraphics.fillRect(5, k + j, i, 1); 
      } 
      if (!this.isFreeStanding) {
        paramRectangle.width -= 2;
        if (!bool)
          paramGraphics.translate(1, 0); 
      } 
    } else {
      if (!this.isFreeStanding)
        paramRectangle.height += 2; 
      if (thumbColor != null) {
        paramGraphics.setColor(thumbColor);
        paramGraphics.fillRect(0, 0, paramRectangle.width - 1, paramRectangle.height - 2);
      } 
      paramGraphics.setColor(thumbShadow);
      SwingUtilities2.drawRect(paramGraphics, 0, 0, paramRectangle.width - 1, paramRectangle.height - 2);
      paramGraphics.setColor(thumbHighlightColor);
      SwingUtilities2.drawHLine(paramGraphics, 1, paramRectangle.width - 2, 1);
      SwingUtilities2.drawVLine(paramGraphics, 1, 1, paramRectangle.height - 3);
      MetalUtils.drawGradient(paramJComponent, paramGraphics, "ScrollBar.gradient", 2, 2, paramRectangle.width - 3, paramRectangle.height - 4, true);
      int i = paramRectangle.height - 8;
      if (i > 2 && paramRectangle.width >= 10) {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        int j = paramRectangle.width / 2 - 2;
        int k;
        for (k = 0; k < 6; k += 2)
          paramGraphics.fillRect(j + k, 4, 1, i); 
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        j++;
        for (k = 0; k < 6; k += 2)
          paramGraphics.fillRect(j + k, 5, 1, i); 
      } 
      if (!this.isFreeStanding)
        paramRectangle.height -= 2; 
    } 
    paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
  }
  
  protected Dimension getMinimumThumbSize() { return new Dimension(this.scrollBarWidth, this.scrollBarWidth); }
  
  protected void setThumbBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.thumbRect.x == paramInt1 && this.thumbRect.y == paramInt2 && this.thumbRect.width == paramInt3 && this.thumbRect.height == paramInt4)
      return; 
    int i = Math.min(paramInt1, this.thumbRect.x);
    int j = Math.min(paramInt2, this.thumbRect.y);
    int k = Math.max(paramInt1 + paramInt3, this.thumbRect.x + this.thumbRect.width);
    int m = Math.max(paramInt2 + paramInt4, this.thumbRect.y + this.thumbRect.height);
    this.thumbRect.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.scrollbar.repaint(i, j, k - i + 1, m - j + 1);
  }
  
  class ScrollBarListener extends BasicScrollBarUI.PropertyChangeHandler {
    ScrollBarListener() { super(MetalScrollBarUI.this); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str.equals("JScrollBar.isFreeStanding")) {
        handlePropertyChange(param1PropertyChangeEvent.getNewValue());
      } else {
        super.propertyChange(param1PropertyChangeEvent);
      } 
    }
    
    public void handlePropertyChange(Object param1Object) {
      if (param1Object != null) {
        boolean bool = ((Boolean)param1Object).booleanValue();
        boolean bool1 = (!bool && MetalScrollBarUI.this.isFreeStanding == true) ? 1 : 0;
        boolean bool2 = (bool == true && !MetalScrollBarUI.this.isFreeStanding) ? 1 : 0;
        MetalScrollBarUI.this.isFreeStanding = bool;
        if (bool1) {
          toFlush();
        } else if (bool2) {
          toFreeStanding();
        } 
      } else if (!MetalScrollBarUI.this.isFreeStanding) {
        MetalScrollBarUI.this.isFreeStanding = true;
        toFreeStanding();
      } 
      if (MetalScrollBarUI.this.increaseButton != null)
        MetalScrollBarUI.this.increaseButton.setFreeStanding(MetalScrollBarUI.this.isFreeStanding); 
      if (MetalScrollBarUI.this.decreaseButton != null)
        MetalScrollBarUI.this.decreaseButton.setFreeStanding(MetalScrollBarUI.this.isFreeStanding); 
    }
    
    protected void toFlush() { MetalScrollBarUI.this.scrollBarWidth -= 2; }
    
    protected void toFreeStanding() { MetalScrollBarUI.this.scrollBarWidth += 2; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */