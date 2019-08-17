package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

class MetalSplitPaneDivider extends BasicSplitPaneDivider {
  private MetalBumps bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
  
  private MetalBumps focusBumps = new MetalBumps(10, 10, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), UIManager.getColor("SplitPane.dividerFocusColor"));
  
  private int inset = 2;
  
  private Color controlColor = MetalLookAndFeel.getControl();
  
  private Color primaryControlColor = UIManager.getColor("SplitPane.dividerFocusColor");
  
  public MetalSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI) { super(paramBasicSplitPaneUI); }
  
  public void paint(Graphics paramGraphics) {
    MetalBumps metalBumps;
    if (this.splitPane.hasFocus()) {
      metalBumps = this.focusBumps;
      paramGraphics.setColor(this.primaryControlColor);
    } else {
      metalBumps = this.bumps;
      paramGraphics.setColor(this.controlColor);
    } 
    Rectangle rectangle = paramGraphics.getClipBounds();
    Insets insets = getInsets();
    paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    Dimension dimension = getSize();
    dimension.width -= this.inset * 2;
    dimension.height -= this.inset * 2;
    int i = this.inset;
    int j = this.inset;
    if (insets != null) {
      dimension.width -= insets.left + insets.right;
      dimension.height -= insets.top + insets.bottom;
      i += insets.left;
      j += insets.top;
    } 
    metalBumps.setBumpArea(dimension);
    metalBumps.paintIcon(this, paramGraphics, i, j);
    super.paint(paramGraphics);
  }
  
  protected JButton createLeftOneTouchButton() {
    JButton jButton = new JButton() {
        int[][] buffer = { { 0, 0, 0, 2, 2, 0, 0, 0, 0 }, { 0, 0, 2, 1, 1, 1, 0, 0, 0 }, { 0, 2, 1, 1, 1, 1, 1, 0, 0 }, { 2, 1, 1, 1, 1, 1, 1, 1, 0 }, { 0, 3, 3, 3, 3, 3, 3, 3, 3 } };
        
        public void setBorder(Border param1Border) {}
        
        public void paint(Graphics param1Graphics) {
          JSplitPane jSplitPane = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
          if (jSplitPane != null) {
            int i = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
            int j = MetalSplitPaneDivider.this.getOrientationFromSuper();
            int k = Math.min(MetalSplitPaneDivider.this.getDividerSize(), i);
            Color[] arrayOfColor = { getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };
            param1Graphics.setColor(getBackground());
            if (isOpaque())
              param1Graphics.fillRect(0, 0, getWidth(), getHeight()); 
            if (getModel().isPressed())
              arrayOfColor[1] = arrayOfColor[2]; 
            if (j == 0) {
              for (byte b = 1; b <= this.buffer[0].length; b++) {
                for (byte b1 = 1; b1 < k; b1++) {
                  if (this.buffer[b1 - true][b - true] != 0) {
                    param1Graphics.setColor(arrayOfColor[this.buffer[b1 - true][b - true]]);
                    param1Graphics.drawLine(b, b1, b, b1);
                  } 
                } 
              } 
            } else {
              for (byte b = 1; b <= this.buffer[0].length; b++) {
                for (byte b1 = 1; b1 < k; b1++) {
                  if (this.buffer[b1 - true][b - true] != 0) {
                    param1Graphics.setColor(arrayOfColor[this.buffer[b1 - true][b - true]]);
                    param1Graphics.drawLine(b1, b, b1, b);
                  } 
                } 
              } 
            } 
          } 
        }
        
        public boolean isFocusTraversable() { return false; }
      };
    jButton.setRequestFocusEnabled(false);
    jButton.setCursor(Cursor.getPredefinedCursor(0));
    jButton.setFocusPainted(false);
    jButton.setBorderPainted(false);
    maybeMakeButtonOpaque(jButton);
    return jButton;
  }
  
  private void maybeMakeButtonOpaque(JComponent paramJComponent) {
    Object object = UIManager.get("SplitPane.oneTouchButtonsOpaque");
    if (object != null)
      paramJComponent.setOpaque(((Boolean)object).booleanValue()); 
  }
  
  protected JButton createRightOneTouchButton() {
    JButton jButton = new JButton() {
        int[][] buffer = { { 2, 2, 2, 2, 2, 2, 2, 2 }, { 0, 1, 1, 1, 1, 1, 1, 3 }, { 0, 0, 1, 1, 1, 1, 3, 0 }, { 0, 0, 0, 1, 1, 3, 0, 0 }, { 0, 0, 0, 0, 3, 0, 0, 0 } };
        
        public void setBorder(Border param1Border) {}
        
        public void paint(Graphics param1Graphics) {
          JSplitPane jSplitPane = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
          if (jSplitPane != null) {
            int i = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
            int j = MetalSplitPaneDivider.this.getOrientationFromSuper();
            int k = Math.min(MetalSplitPaneDivider.this.getDividerSize(), i);
            Color[] arrayOfColor = { getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };
            param1Graphics.setColor(getBackground());
            if (isOpaque())
              param1Graphics.fillRect(0, 0, getWidth(), getHeight()); 
            if (getModel().isPressed())
              arrayOfColor[1] = arrayOfColor[2]; 
            if (j == 0) {
              for (byte b = 1; b <= this.buffer[0].length; b++) {
                for (byte b1 = 1; b1 < k; b1++) {
                  if (this.buffer[b1 - true][b - true] != 0) {
                    param1Graphics.setColor(arrayOfColor[this.buffer[b1 - true][b - true]]);
                    param1Graphics.drawLine(b, b1, b, b1);
                  } 
                } 
              } 
            } else {
              for (byte b = 1; b <= this.buffer[0].length; b++) {
                for (byte b1 = 1; b1 < k; b1++) {
                  if (this.buffer[b1 - true][b - true] != 0) {
                    param1Graphics.setColor(arrayOfColor[this.buffer[b1 - true][b - true]]);
                    param1Graphics.drawLine(b1, b, b1, b);
                  } 
                } 
              } 
            } 
          } 
        }
        
        public boolean isFocusTraversable() { return false; }
      };
    jButton.setCursor(Cursor.getPredefinedCursor(0));
    jButton.setFocusPainted(false);
    jButton.setBorderPainted(false);
    jButton.setRequestFocusEnabled(false);
    maybeMakeButtonOpaque(jButton);
    return jButton;
  }
  
  int getOneTouchSizeFromSuper() { return 6; }
  
  int getOneTouchOffsetFromSuper() { return 2; }
  
  int getOrientationFromSuper() { return this.orientation; }
  
  JSplitPane getSplitPaneFromSuper() { return this.splitPane; }
  
  JButton getLeftButtonFromSuper() { return this.leftButton; }
  
  JButton getRightButtonFromSuper() { return this.rightButton; }
  
  public class MetalDividerLayout implements LayoutManager {
    public void layoutContainer(Container param1Container) {
      JButton jButton1 = MetalSplitPaneDivider.this.getLeftButtonFromSuper();
      JButton jButton2 = MetalSplitPaneDivider.this.getRightButtonFromSuper();
      JSplitPane jSplitPane = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
      int i = MetalSplitPaneDivider.this.getOrientationFromSuper();
      int j = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
      int k = MetalSplitPaneDivider.this.getOneTouchOffsetFromSuper();
      Insets insets = MetalSplitPaneDivider.this.getInsets();
      if (jButton1 != null && jButton2 != null && param1Container == MetalSplitPaneDivider.this)
        if (jSplitPane.isOneTouchExpandable()) {
          if (i == 0) {
            int m = (insets != null) ? insets.top : 0;
            int n = MetalSplitPaneDivider.this.getDividerSize();
            if (insets != null)
              n -= insets.top + insets.bottom; 
            n = Math.min(n, j);
            jButton1.setBounds(k, m, n * 2, n);
            jButton2.setBounds(k + j * 2, m, n * 2, n);
          } else {
            int m = MetalSplitPaneDivider.this.getDividerSize();
            int n = (insets != null) ? insets.left : 0;
            if (insets != null)
              m -= insets.left + insets.right; 
            m = Math.min(m, j);
            jButton1.setBounds(n, k, m, m * 2);
            jButton2.setBounds(n, k + j * 2, m, m * 2);
          } 
        } else {
          jButton1.setBounds(-5, -5, 1, 1);
          jButton2.setBounds(-5, -5, 1, 1);
        }  
    }
    
    public Dimension minimumLayoutSize(Container param1Container) { return new Dimension(0, 0); }
    
    public Dimension preferredLayoutSize(Container param1Container) { return new Dimension(0, 0); }
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */