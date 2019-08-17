package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class MotifDesktopPaneUI extends BasicDesktopPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifDesktopPaneUI(); }
  
  protected void installDesktopManager() {
    this.desktopManager = this.desktop.getDesktopManager();
    if (this.desktopManager == null) {
      this.desktopManager = new MotifDesktopManager(null);
      this.desktop.setDesktopManager(this.desktopManager);
      ((MotifDesktopManager)this.desktopManager).adjustIcons(this.desktop);
    } 
  }
  
  public Insets getInsets(JComponent paramJComponent) { return new Insets(0, 0, 0, 0); }
  
  private class DragPane extends JComponent {
    private DragPane() {}
    
    public void paint(Graphics param1Graphics) {
      param1Graphics.setColor(Color.darkGray);
      param1Graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
  
  private class MotifDesktopManager extends DefaultDesktopManager implements Serializable, UIResource {
    JComponent dragPane;
    
    boolean usingDragPane = false;
    
    private JLayeredPane layeredPaneForDragPane;
    
    int iconWidth;
    
    int iconHeight;
    
    private MotifDesktopManager() {}
    
    public void setBoundsForFrame(JComponent param1JComponent, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!this.usingDragPane) {
        boolean bool = (param1JComponent.getWidth() != param1Int3 || param1JComponent.getHeight() != param1Int4) ? 1 : 0;
        Rectangle rectangle = param1JComponent.getBounds();
        param1JComponent.setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
        SwingUtilities.computeUnion(param1Int1, param1Int2, param1Int3, param1Int4, rectangle);
        param1JComponent.getParent().repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        if (bool)
          param1JComponent.validate(); 
      } else {
        Rectangle rectangle = this.dragPane.getBounds();
        this.dragPane.setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
        SwingUtilities.computeUnion(param1Int1, param1Int2, param1Int3, param1Int4, rectangle);
        this.dragPane.getParent().repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
    }
    
    public void beginDraggingFrame(JComponent param1JComponent) {
      this.usingDragPane = false;
      if (param1JComponent.getParent() instanceof JLayeredPane) {
        if (this.dragPane == null)
          this.dragPane = new MotifDesktopPaneUI.DragPane(MotifDesktopPaneUI.this, null); 
        this.layeredPaneForDragPane = (JLayeredPane)param1JComponent.getParent();
        this.layeredPaneForDragPane.setLayer(this.dragPane, 2147483647);
        this.dragPane.setBounds(param1JComponent.getX(), param1JComponent.getY(), param1JComponent.getWidth(), param1JComponent.getHeight());
        this.layeredPaneForDragPane.add(this.dragPane);
        this.usingDragPane = true;
      } 
    }
    
    public void dragFrame(JComponent param1JComponent, int param1Int1, int param1Int2) { setBoundsForFrame(param1JComponent, param1Int1, param1Int2, param1JComponent.getWidth(), param1JComponent.getHeight()); }
    
    public void endDraggingFrame(JComponent param1JComponent) {
      if (this.usingDragPane) {
        this.layeredPaneForDragPane.remove(this.dragPane);
        this.usingDragPane = false;
        if (param1JComponent instanceof JInternalFrame) {
          setBoundsForFrame(param1JComponent, this.dragPane.getX(), this.dragPane.getY(), this.dragPane.getWidth(), this.dragPane.getHeight());
        } else if (param1JComponent instanceof JInternalFrame.JDesktopIcon) {
          adjustBoundsForIcon((JInternalFrame.JDesktopIcon)param1JComponent, this.dragPane.getX(), this.dragPane.getY());
        } 
      } 
    }
    
    public void beginResizingFrame(JComponent param1JComponent, int param1Int) {
      this.usingDragPane = false;
      if (param1JComponent.getParent() instanceof JLayeredPane) {
        if (this.dragPane == null)
          this.dragPane = new MotifDesktopPaneUI.DragPane(MotifDesktopPaneUI.this, null); 
        JLayeredPane jLayeredPane = (JLayeredPane)param1JComponent.getParent();
        jLayeredPane.setLayer(this.dragPane, 2147483647);
        this.dragPane.setBounds(param1JComponent.getX(), param1JComponent.getY(), param1JComponent.getWidth(), param1JComponent.getHeight());
        jLayeredPane.add(this.dragPane);
        this.usingDragPane = true;
      } 
    }
    
    public void resizeFrame(JComponent param1JComponent, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { setBoundsForFrame(param1JComponent, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void endResizingFrame(JComponent param1JComponent) {
      if (this.usingDragPane) {
        JLayeredPane jLayeredPane = (JLayeredPane)param1JComponent.getParent();
        jLayeredPane.remove(this.dragPane);
        this.usingDragPane = false;
        setBoundsForFrame(param1JComponent, this.dragPane.getX(), this.dragPane.getY(), this.dragPane.getWidth(), this.dragPane.getHeight());
      } 
    }
    
    public void iconifyFrame(JInternalFrame param1JInternalFrame) {
      JInternalFrame.JDesktopIcon jDesktopIcon = param1JInternalFrame.getDesktopIcon();
      Point point = jDesktopIcon.getLocation();
      adjustBoundsForIcon(jDesktopIcon, point.x, point.y);
      super.iconifyFrame(param1JInternalFrame);
    }
    
    protected void adjustIcons(JDesktopPane param1JDesktopPane) {
      JInternalFrame.JDesktopIcon jDesktopIcon = new JInternalFrame.JDesktopIcon(new JInternalFrame());
      Dimension dimension = jDesktopIcon.getPreferredSize();
      this.iconWidth = dimension.width;
      this.iconHeight = dimension.height;
      JInternalFrame[] arrayOfJInternalFrame = param1JDesktopPane.getAllFrames();
      for (byte b = 0; b < arrayOfJInternalFrame.length; b++) {
        jDesktopIcon = arrayOfJInternalFrame[b].getDesktopIcon();
        Point point = jDesktopIcon.getLocation();
        adjustBoundsForIcon(jDesktopIcon, point.x, point.y);
      } 
    }
    
    protected void adjustBoundsForIcon(JInternalFrame.JDesktopIcon param1JDesktopIcon, int param1Int1, int param1Int2) {
      JDesktopPane jDesktopPane = param1JDesktopIcon.getDesktopPane();
      int i = jDesktopPane.getHeight();
      int j = this.iconWidth;
      int k = this.iconHeight;
      jDesktopPane.repaint(param1Int1, param1Int2, j, k);
      param1Int1 = (param1Int1 < 0) ? 0 : param1Int1;
      param1Int2 = (param1Int2 < 0) ? 0 : param1Int2;
      param1Int2 = (param1Int2 >= i) ? (i - 1) : param1Int2;
      int m = param1Int1 / j * j;
      int n = i % k;
      int i1 = (param1Int2 - n) / k * k + n;
      int i2 = param1Int1 - m;
      int i3 = param1Int2 - i1;
      param1Int1 = (i2 < j / 2) ? m : (m + j);
      param1Int2 = (i3 < k / 2) ? i1 : ((i1 + k < i) ? (i1 + k) : i1);
      while (getIconAt(jDesktopPane, param1JDesktopIcon, param1Int1, param1Int2) != null)
        param1Int1 += j; 
      if (param1Int1 > jDesktopPane.getWidth())
        return; 
      if (param1JDesktopIcon.getParent() != null) {
        setBoundsForFrame(param1JDesktopIcon, param1Int1, param1Int2, j, k);
      } else {
        param1JDesktopIcon.setLocation(param1Int1, param1Int2);
      } 
    }
    
    protected JInternalFrame.JDesktopIcon getIconAt(JDesktopPane param1JDesktopPane, JInternalFrame.JDesktopIcon param1JDesktopIcon, int param1Int1, int param1Int2) {
      Object object = null;
      Component[] arrayOfComponent = param1JDesktopPane.getComponents();
      for (byte b = 0; b < arrayOfComponent.length; b++) {
        Component component = arrayOfComponent[b];
        if (component instanceof JInternalFrame.JDesktopIcon && component != param1JDesktopIcon) {
          Point point = component.getLocation();
          if (point.x == param1Int1 && point.y == param1Int2)
            return (JInternalFrame.JDesktopIcon)component; 
        } 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */