package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class WindowsScrollBarUI extends BasicScrollBarUI {
  private Grid thumbGrid;
  
  private Grid highlightGrid;
  
  private Dimension horizontalThumbSize;
  
  private Dimension verticalThumbSize;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsScrollBarUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      this.scrollbar.setBorder(null);
      this.horizontalThumbSize = getSize(this.scrollbar, xPStyle, TMSchema.Part.SBP_THUMBBTNHORZ);
      this.verticalThumbSize = getSize(this.scrollbar, xPStyle, TMSchema.Part.SBP_THUMBBTNVERT);
    } else {
      this.horizontalThumbSize = null;
      this.verticalThumbSize = null;
    } 
  }
  
  private static Dimension getSize(Component paramComponent, XPStyle paramXPStyle, TMSchema.Part paramPart) {
    XPStyle.Skin skin = paramXPStyle.getSkin(paramComponent, paramPart);
    return new Dimension(skin.getWidth(), skin.getHeight());
  }
  
  protected Dimension getMinimumThumbSize() { return (this.horizontalThumbSize == null || this.verticalThumbSize == null) ? super.getMinimumThumbSize() : ((0 == this.scrollbar.getOrientation()) ? this.horizontalThumbSize : this.verticalThumbSize); }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    this.thumbGrid = this.highlightGrid = null;
  }
  
  protected void configureScrollBarColors() {
    super.configureScrollBarColors();
    Color color = UIManager.getColor("ScrollBar.trackForeground");
    if (color != null && this.trackColor != null)
      this.thumbGrid = Grid.getGrid(color, this.trackColor); 
    color = UIManager.getColor("ScrollBar.trackHighlightForeground");
    if (color != null && this.trackHighlightColor != null)
      this.highlightGrid = Grid.getGrid(color, this.trackHighlightColor); 
  }
  
  protected JButton createDecreaseButton(int paramInt) { return new WindowsArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight")); }
  
  protected JButton createIncreaseButton(int paramInt) { return new WindowsArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight")); }
  
  protected BasicScrollBarUI.ArrowButtonListener createArrowButtonListener() { return XPStyle.isVista() ? new BasicScrollBarUI.ArrowButtonListener() {
        public void mouseEntered(MouseEvent param1MouseEvent) {
          repaint();
          super.mouseEntered(param1MouseEvent);
        }
        
        public void mouseExited(MouseEvent param1MouseEvent) {
          repaint();
          super.mouseExited(param1MouseEvent);
        }
        
        private void repaint() { WindowsScrollBarUI.this.scrollbar.repaint(); }
      } : super.createArrowButtonListener(); }
  
  protected void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    boolean bool = (this.scrollbar.getOrientation() == 1) ? 1 : 0;
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      JScrollBar jScrollBar = (JScrollBar)paramJComponent;
      TMSchema.State state = TMSchema.State.NORMAL;
      if (!jScrollBar.isEnabled())
        state = TMSchema.State.DISABLED; 
      TMSchema.Part part = bool ? TMSchema.Part.SBP_LOWERTRACKVERT : TMSchema.Part.SBP_LOWERTRACKHORZ;
      xPStyle.getSkin(jScrollBar, part).paintSkin(paramGraphics, paramRectangle, state);
    } else if (this.thumbGrid == null) {
      super.paintTrack(paramGraphics, paramJComponent, paramRectangle);
    } else {
      this.thumbGrid.paint(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
      if (this.trackHighlight == 1) {
        paintDecreaseHighlight(paramGraphics);
      } else if (this.trackHighlight == 2) {
        paintIncreaseHighlight(paramGraphics);
      } 
    } 
  }
  
  protected void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    boolean bool = (this.scrollbar.getOrientation() == 1) ? 1 : 0;
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      JScrollBar jScrollBar = (JScrollBar)paramJComponent;
      TMSchema.State state = TMSchema.State.NORMAL;
      if (!jScrollBar.isEnabled()) {
        state = TMSchema.State.DISABLED;
      } else if (this.isDragging) {
        state = TMSchema.State.PRESSED;
      } else if (isThumbRollover()) {
        state = TMSchema.State.HOT;
      } else if (XPStyle.isVista() && ((this.incrButton != null && this.incrButton.getModel().isRollover()) || (this.decrButton != null && this.decrButton.getModel().isRollover()))) {
        state = TMSchema.State.HOVER;
      } 
      TMSchema.Part part1 = bool ? TMSchema.Part.SBP_THUMBBTNVERT : TMSchema.Part.SBP_THUMBBTNHORZ;
      xPStyle.getSkin(jScrollBar, part1).paintSkin(paramGraphics, paramRectangle, state);
      TMSchema.Part part2 = bool ? TMSchema.Part.SBP_GRIPPERVERT : TMSchema.Part.SBP_GRIPPERHORZ;
      XPStyle.Skin skin = xPStyle.getSkin(jScrollBar, part2);
      Insets insets = xPStyle.getMargin(paramJComponent, part1, null, TMSchema.Prop.CONTENTMARGINS);
      if (insets == null || (bool && paramRectangle.height - insets.top - insets.bottom >= skin.getHeight()) || (!bool && paramRectangle.width - insets.left - insets.right >= skin.getWidth()))
        skin.paintSkin(paramGraphics, paramRectangle.x + (paramRectangle.width - skin.getWidth()) / 2, paramRectangle.y + (paramRectangle.height - skin.getHeight()) / 2, skin.getWidth(), skin.getHeight(), state); 
    } else {
      super.paintThumb(paramGraphics, paramJComponent, paramRectangle);
    } 
  }
  
  protected void paintDecreaseHighlight(Graphics paramGraphics) {
    if (this.highlightGrid == null) {
      super.paintDecreaseHighlight(paramGraphics);
    } else {
      int m;
      int k;
      int j;
      int i;
      Insets insets = this.scrollbar.getInsets();
      Rectangle rectangle = getThumbBounds();
      if (this.scrollbar.getOrientation() == 1) {
        i = insets.left;
        j = this.decrButton.getY() + this.decrButton.getHeight();
        k = this.scrollbar.getWidth() - insets.left + insets.right;
        m = rectangle.y - j;
      } else {
        i = this.decrButton.getX() + this.decrButton.getHeight();
        j = insets.top;
        k = rectangle.x - i;
        m = this.scrollbar.getHeight() - insets.top + insets.bottom;
      } 
      this.highlightGrid.paint(paramGraphics, i, j, k, m);
    } 
  }
  
  protected void paintIncreaseHighlight(Graphics paramGraphics) {
    if (this.highlightGrid == null) {
      super.paintDecreaseHighlight(paramGraphics);
    } else {
      int m;
      int k;
      int j;
      int i;
      Insets insets = this.scrollbar.getInsets();
      Rectangle rectangle = getThumbBounds();
      if (this.scrollbar.getOrientation() == 1) {
        i = insets.left;
        j = rectangle.y + rectangle.height;
        k = this.scrollbar.getWidth() - insets.left + insets.right;
        m = this.incrButton.getY() - j;
      } else {
        i = rectangle.x + rectangle.width;
        j = insets.top;
        k = this.incrButton.getX() - i;
        m = this.scrollbar.getHeight() - insets.top + insets.bottom;
      } 
      this.highlightGrid.paint(paramGraphics, i, j, k, m);
    } 
  }
  
  protected void setThumbRollover(boolean paramBoolean) {
    boolean bool = isThumbRollover();
    super.setThumbRollover(paramBoolean);
    if (XPStyle.isVista() && paramBoolean != bool)
      this.scrollbar.repaint(); 
  }
  
  private static class Grid {
    private static final int BUFFER_SIZE = 64;
    
    private static HashMap<String, WeakReference<Grid>> map = new HashMap();
    
    private BufferedImage image;
    
    public static Grid getGrid(Color param1Color1, Color param1Color2) {
      String str = param1Color1.getRGB() + " " + param1Color2.getRGB();
      WeakReference weakReference = (WeakReference)map.get(str);
      Grid grid = (weakReference == null) ? null : (Grid)weakReference.get();
      if (grid == null) {
        grid = new Grid(param1Color1, param1Color2);
        map.put(str, new WeakReference(grid));
      } 
      return grid;
    }
    
    public Grid(Color param1Color1, Color param1Color2) {
      int[] arrayOfInt = { param1Color1.getRGB(), param1Color2.getRGB() };
      IndexColorModel indexColorModel = new IndexColorModel(8, 2, arrayOfInt, 0, false, -1, 0);
      this.image = new BufferedImage(64, 64, 13, indexColorModel);
      graphics = this.image.getGraphics();
      try {
        graphics.setClip(0, 0, 64, 64);
        paintGrid(graphics, param1Color1, param1Color2);
      } finally {
        graphics.dispose();
      } 
    }
    
    public void paint(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = param1Graphics.getClipBounds();
      int i = Math.max(param1Int1, rectangle.x);
      int j = Math.max(param1Int2, rectangle.y);
      int k = Math.min(rectangle.x + rectangle.width, param1Int1 + param1Int3);
      int m = Math.min(rectangle.y + rectangle.height, param1Int2 + param1Int4);
      if (k <= i || m <= j)
        return; 
      int n = (i - param1Int1) % 2;
      for (int i1 = i; i1 < k; i1 += 64) {
        int i2 = (j - param1Int2) % 2;
        int i3 = Math.min(64 - n, k - i1);
        for (int i4 = j; i4 < m; i4 += 64) {
          int i5 = Math.min(64 - i2, m - i4);
          param1Graphics.drawImage(this.image, i1, i4, i1 + i3, i4 + i5, n, i2, n + i3, i2 + i5, null);
          if (i2 != 0) {
            i4 -= i2;
            i2 = 0;
          } 
        } 
        if (n != 0) {
          i1 -= n;
          n = 0;
        } 
      } 
    }
    
    private void paintGrid(Graphics param1Graphics, Color param1Color1, Color param1Color2) {
      Rectangle rectangle = param1Graphics.getClipBounds();
      param1Graphics.setColor(param1Color2);
      param1Graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      param1Graphics.setColor(param1Color1);
      param1Graphics.translate(rectangle.x, rectangle.y);
      int i = rectangle.width;
      int j = rectangle.height;
      int k = rectangle.x % 2;
      int m = i - j;
      while (k < m) {
        param1Graphics.drawLine(k, 0, k + j, j);
        k += 2;
      } 
      m = i;
      while (k < m) {
        param1Graphics.drawLine(k, 0, i, i - k);
        k += 2;
      } 
      m = (rectangle.x % 2 == 0) ? 2 : 1;
      int n = j - i;
      while (m < n) {
        param1Graphics.drawLine(0, m, i, m + i);
        m += 2;
      } 
      n = j;
      while (m < n) {
        param1Graphics.drawLine(0, m, j - m, j);
        m += 2;
      } 
      param1Graphics.translate(-rectangle.x, -rectangle.y);
    }
  }
  
  private class WindowsArrowButton extends BasicArrowButton {
    public WindowsArrowButton(int param1Int, Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Int, param1Color1, param1Color2, param1Color3, param1Color4); }
    
    public WindowsArrowButton(int param1Int) { super(param1Int); }
    
    public void paint(Graphics param1Graphics) {
      XPStyle xPStyle = XPStyle.getXP();
      if (xPStyle != null) {
        ButtonModel buttonModel = getModel();
        XPStyle.Skin skin = xPStyle.getSkin(this, TMSchema.Part.SBP_ARROWBTN);
        TMSchema.State state = null;
        boolean bool = (XPStyle.isVista() && (WindowsScrollBarUI.this.isThumbRollover() || (this == WindowsScrollBarUI.this.incrButton && WindowsScrollBarUI.this.decrButton.getModel().isRollover()) || (this == WindowsScrollBarUI.this.decrButton && WindowsScrollBarUI.this.incrButton.getModel().isRollover()))) ? 1 : 0;
        if (buttonModel.isArmed() && buttonModel.isPressed()) {
          switch (this.direction) {
            case 1:
              state = TMSchema.State.UPPRESSED;
              break;
            case 5:
              state = TMSchema.State.DOWNPRESSED;
              break;
            case 7:
              state = TMSchema.State.LEFTPRESSED;
              break;
            case 3:
              state = TMSchema.State.RIGHTPRESSED;
              break;
          } 
        } else if (!buttonModel.isEnabled()) {
          switch (this.direction) {
            case 1:
              state = TMSchema.State.UPDISABLED;
              break;
            case 5:
              state = TMSchema.State.DOWNDISABLED;
              break;
            case 7:
              state = TMSchema.State.LEFTDISABLED;
              break;
            case 3:
              state = TMSchema.State.RIGHTDISABLED;
              break;
          } 
        } else if (buttonModel.isRollover() || buttonModel.isPressed()) {
          switch (this.direction) {
            case 1:
              state = TMSchema.State.UPHOT;
              break;
            case 5:
              state = TMSchema.State.DOWNHOT;
              break;
            case 7:
              state = TMSchema.State.LEFTHOT;
              break;
            case 3:
              state = TMSchema.State.RIGHTHOT;
              break;
          } 
        } else if (bool) {
          switch (this.direction) {
            case 1:
              state = TMSchema.State.UPHOVER;
              break;
            case 5:
              state = TMSchema.State.DOWNHOVER;
              break;
            case 7:
              state = TMSchema.State.LEFTHOVER;
              break;
            case 3:
              state = TMSchema.State.RIGHTHOVER;
              break;
          } 
        } else {
          switch (this.direction) {
            case 1:
              state = TMSchema.State.UPNORMAL;
              break;
            case 5:
              state = TMSchema.State.DOWNNORMAL;
              break;
            case 7:
              state = TMSchema.State.LEFTNORMAL;
              break;
            case 3:
              state = TMSchema.State.RIGHTNORMAL;
              break;
          } 
        } 
        skin.paintSkin(param1Graphics, 0, 0, getWidth(), getHeight(), state);
      } else {
        super.paint(param1Graphics);
      } 
    }
    
    public Dimension getPreferredSize() {
      int i = 16;
      if (WindowsScrollBarUI.this.scrollbar != null) {
        switch (WindowsScrollBarUI.this.scrollbar.getOrientation()) {
          case 1:
            i = WindowsScrollBarUI.this.scrollbar.getWidth();
            break;
          case 0:
            i = WindowsScrollBarUI.this.scrollbar.getHeight();
            break;
        } 
        i = Math.max(i, 5);
      } 
      return new Dimension(i, i);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */