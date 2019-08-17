package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class MetalRootPaneUI extends BasicRootPaneUI {
  private static final String[] borderKeys = { null, "RootPane.frameBorder", "RootPane.plainDialogBorder", "RootPane.informationDialogBorder", "RootPane.errorDialogBorder", "RootPane.colorChooserDialogBorder", "RootPane.fileChooserDialogBorder", "RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };
  
  private static final int CORNER_DRAG_WIDTH = 16;
  
  private static final int BORDER_DRAG_THICKNESS = 5;
  
  private Window window;
  
  private JComponent titlePane;
  
  private MouseInputListener mouseInputListener;
  
  private LayoutManager layoutManager;
  
  private LayoutManager savedOldLayout;
  
  private JRootPane root;
  
  private Cursor lastCursor = Cursor.getPredefinedCursor(0);
  
  private static final int[] cursorMapping = { 
      6, 6, 8, 7, 7, 6, 0, 0, 0, 7, 
      10, 0, 0, 0, 11, 4, 0, 0, 0, 5, 
      4, 4, 9, 5, 5 };
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalRootPaneUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    this.root = (JRootPane)paramJComponent;
    int i = this.root.getWindowDecorationStyle();
    if (i != 0)
      installClientDecorations(this.root); 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    uninstallClientDecorations(this.root);
    this.layoutManager = null;
    this.mouseInputListener = null;
    this.root = null;
  }
  
  void installBorder(JRootPane paramJRootPane) {
    int i = paramJRootPane.getWindowDecorationStyle();
    if (i == 0) {
      LookAndFeel.uninstallBorder(paramJRootPane);
    } else {
      LookAndFeel.installBorder(paramJRootPane, borderKeys[i]);
    } 
  }
  
  private void uninstallBorder(JRootPane paramJRootPane) { LookAndFeel.uninstallBorder(paramJRootPane); }
  
  private void installWindowListeners(JRootPane paramJRootPane, Component paramComponent) {
    if (paramComponent instanceof Window) {
      this.window = (Window)paramComponent;
    } else {
      this.window = SwingUtilities.getWindowAncestor(paramComponent);
    } 
    if (this.window != null) {
      if (this.mouseInputListener == null)
        this.mouseInputListener = createWindowMouseInputListener(paramJRootPane); 
      this.window.addMouseListener(this.mouseInputListener);
      this.window.addMouseMotionListener(this.mouseInputListener);
    } 
  }
  
  private void uninstallWindowListeners(JRootPane paramJRootPane) {
    if (this.window != null) {
      this.window.removeMouseListener(this.mouseInputListener);
      this.window.removeMouseMotionListener(this.mouseInputListener);
    } 
  }
  
  private void installLayout(JRootPane paramJRootPane) {
    if (this.layoutManager == null)
      this.layoutManager = createLayoutManager(); 
    this.savedOldLayout = paramJRootPane.getLayout();
    paramJRootPane.setLayout(this.layoutManager);
  }
  
  private void uninstallLayout(JRootPane paramJRootPane) {
    if (this.savedOldLayout != null) {
      paramJRootPane.setLayout(this.savedOldLayout);
      this.savedOldLayout = null;
    } 
  }
  
  private void installClientDecorations(JRootPane paramJRootPane) {
    installBorder(paramJRootPane);
    JComponent jComponent = createTitlePane(paramJRootPane);
    setTitlePane(paramJRootPane, jComponent);
    installWindowListeners(paramJRootPane, paramJRootPane.getParent());
    installLayout(paramJRootPane);
    if (this.window != null) {
      paramJRootPane.revalidate();
      paramJRootPane.repaint();
    } 
  }
  
  private void uninstallClientDecorations(JRootPane paramJRootPane) {
    uninstallBorder(paramJRootPane);
    uninstallWindowListeners(paramJRootPane);
    setTitlePane(paramJRootPane, null);
    uninstallLayout(paramJRootPane);
    int i = paramJRootPane.getWindowDecorationStyle();
    if (i == 0) {
      paramJRootPane.repaint();
      paramJRootPane.revalidate();
    } 
    if (this.window != null)
      this.window.setCursor(Cursor.getPredefinedCursor(0)); 
    this.window = null;
  }
  
  private JComponent createTitlePane(JRootPane paramJRootPane) { return new MetalTitlePane(paramJRootPane, this); }
  
  private MouseInputListener createWindowMouseInputListener(JRootPane paramJRootPane) { return new MouseInputHandler(null); }
  
  private LayoutManager createLayoutManager() { return new MetalRootLayout(null); }
  
  private void setTitlePane(JRootPane paramJRootPane, JComponent paramJComponent) {
    JLayeredPane jLayeredPane = paramJRootPane.getLayeredPane();
    JComponent jComponent = getTitlePane();
    if (jComponent != null) {
      jComponent.setVisible(false);
      jLayeredPane.remove(jComponent);
    } 
    if (paramJComponent != null) {
      jLayeredPane.add(paramJComponent, JLayeredPane.FRAME_CONTENT_LAYER);
      paramJComponent.setVisible(true);
    } 
    this.titlePane = paramJComponent;
  }
  
  private JComponent getTitlePane() { return this.titlePane; }
  
  private JRootPane getRootPane() { return this.root; }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == null)
      return; 
    if (str.equals("windowDecorationStyle")) {
      JRootPane jRootPane = (JRootPane)paramPropertyChangeEvent.getSource();
      int i = jRootPane.getWindowDecorationStyle();
      uninstallClientDecorations(jRootPane);
      if (i != 0)
        installClientDecorations(jRootPane); 
    } else if (str.equals("ancestor")) {
      uninstallWindowListeners(this.root);
      if (((JRootPane)paramPropertyChangeEvent.getSource()).getWindowDecorationStyle() != 0)
        installWindowListeners(this.root, this.root.getParent()); 
    } 
  }
  
  private static class MetalRootLayout implements LayoutManager2 {
    private MetalRootLayout() {}
    
    public Dimension preferredLayoutSize(Container param1Container) {
      Dimension dimension;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      Insets insets = param1Container.getInsets();
      JRootPane jRootPane = (JRootPane)param1Container;
      if (jRootPane.getContentPane() != null) {
        dimension = jRootPane.getContentPane().getPreferredSize();
      } else {
        dimension = jRootPane.getSize();
      } 
      if (dimension != null) {
        i = dimension.width;
        j = dimension.height;
      } 
      if (jRootPane.getMenuBar() != null) {
        Dimension dimension1 = jRootPane.getMenuBar().getPreferredSize();
        if (dimension1 != null) {
          k = dimension1.width;
          m = dimension1.height;
        } 
      } 
      if (jRootPane.getWindowDecorationStyle() != 0 && jRootPane.getUI() instanceof MetalRootPaneUI) {
        JComponent jComponent = ((MetalRootPaneUI)jRootPane.getUI()).getTitlePane();
        if (jComponent != null) {
          Dimension dimension1 = jComponent.getPreferredSize();
          if (dimension1 != null) {
            n = dimension1.width;
            i1 = dimension1.height;
          } 
        } 
      } 
      return new Dimension(Math.max(Math.max(i, k), n) + insets.left + insets.right, j + m + n + insets.top + insets.bottom);
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      Dimension dimension;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      Insets insets = param1Container.getInsets();
      JRootPane jRootPane = (JRootPane)param1Container;
      if (jRootPane.getContentPane() != null) {
        dimension = jRootPane.getContentPane().getMinimumSize();
      } else {
        dimension = jRootPane.getSize();
      } 
      if (dimension != null) {
        i = dimension.width;
        j = dimension.height;
      } 
      if (jRootPane.getMenuBar() != null) {
        Dimension dimension1 = jRootPane.getMenuBar().getMinimumSize();
        if (dimension1 != null) {
          k = dimension1.width;
          m = dimension1.height;
        } 
      } 
      if (jRootPane.getWindowDecorationStyle() != 0 && jRootPane.getUI() instanceof MetalRootPaneUI) {
        JComponent jComponent = ((MetalRootPaneUI)jRootPane.getUI()).getTitlePane();
        if (jComponent != null) {
          Dimension dimension1 = jComponent.getMinimumSize();
          if (dimension1 != null) {
            n = dimension1.width;
            i1 = dimension1.height;
          } 
        } 
      } 
      return new Dimension(Math.max(Math.max(i, k), n) + insets.left + insets.right, j + m + n + insets.top + insets.bottom);
    }
    
    public Dimension maximumLayoutSize(Container param1Container) {
      int i = Integer.MAX_VALUE;
      int j = Integer.MAX_VALUE;
      int k = Integer.MAX_VALUE;
      int m = Integer.MAX_VALUE;
      int n = Integer.MAX_VALUE;
      int i1 = Integer.MAX_VALUE;
      Insets insets = param1Container.getInsets();
      JRootPane jRootPane = (JRootPane)param1Container;
      if (jRootPane.getContentPane() != null) {
        Dimension dimension = jRootPane.getContentPane().getMaximumSize();
        if (dimension != null) {
          i = dimension.width;
          j = dimension.height;
        } 
      } 
      if (jRootPane.getMenuBar() != null) {
        Dimension dimension = jRootPane.getMenuBar().getMaximumSize();
        if (dimension != null) {
          k = dimension.width;
          m = dimension.height;
        } 
      } 
      if (jRootPane.getWindowDecorationStyle() != 0 && jRootPane.getUI() instanceof MetalRootPaneUI) {
        JComponent jComponent = ((MetalRootPaneUI)jRootPane.getUI()).getTitlePane();
        if (jComponent != null) {
          Dimension dimension = jComponent.getMaximumSize();
          if (dimension != null) {
            n = dimension.width;
            i1 = dimension.height;
          } 
        } 
      } 
      int i2 = Math.max(Math.max(j, m), i1);
      if (i2 != Integer.MAX_VALUE)
        i2 = j + m + i1 + insets.top + insets.bottom; 
      int i3 = Math.max(Math.max(i, k), n);
      if (i3 != Integer.MAX_VALUE)
        i3 += insets.left + insets.right; 
      return new Dimension(i3, i2);
    }
    
    public void layoutContainer(Container param1Container) {
      JRootPane jRootPane = (JRootPane)param1Container;
      Rectangle rectangle = jRootPane.getBounds();
      Insets insets = jRootPane.getInsets();
      int i = 0;
      int j = rectangle.width - insets.right - insets.left;
      int k = rectangle.height - insets.top - insets.bottom;
      if (jRootPane.getLayeredPane() != null)
        jRootPane.getLayeredPane().setBounds(insets.left, insets.top, j, k); 
      if (jRootPane.getGlassPane() != null)
        jRootPane.getGlassPane().setBounds(insets.left, insets.top, j, k); 
      if (jRootPane.getWindowDecorationStyle() != 0 && jRootPane.getUI() instanceof MetalRootPaneUI) {
        JComponent jComponent = ((MetalRootPaneUI)jRootPane.getUI()).getTitlePane();
        if (jComponent != null) {
          Dimension dimension = jComponent.getPreferredSize();
          if (dimension != null) {
            int m = dimension.height;
            jComponent.setBounds(0, 0, j, m);
            i += m;
          } 
        } 
      } 
      if (jRootPane.getMenuBar() != null) {
        Dimension dimension = jRootPane.getMenuBar().getPreferredSize();
        jRootPane.getMenuBar().setBounds(0, i, j, dimension.height);
        i += dimension.height;
      } 
      if (jRootPane.getContentPane() != null) {
        Dimension dimension = jRootPane.getContentPane().getPreferredSize();
        jRootPane.getContentPane().setBounds(0, i, j, (k < i) ? 0 : (k - i));
      } 
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public void addLayoutComponent(Component param1Component, Object param1Object) {}
    
    public float getLayoutAlignmentX(Container param1Container) { return 0.0F; }
    
    public float getLayoutAlignmentY(Container param1Container) { return 0.0F; }
    
    public void invalidateLayout(Container param1Container) {}
  }
  
  private class MouseInputHandler implements MouseInputListener {
    private boolean isMovingWindow;
    
    private int dragCursor;
    
    private int dragOffsetX;
    
    private int dragOffsetY;
    
    private int dragWidth;
    
    private int dragHeight;
    
    private MouseInputHandler() {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      JRootPane jRootPane = MetalRootPaneUI.this.getRootPane();
      if (jRootPane.getWindowDecorationStyle() == 0)
        return; 
      Point point1 = param1MouseEvent.getPoint();
      Window window = (Window)param1MouseEvent.getSource();
      if (window != null)
        window.toFront(); 
      Point point2 = SwingUtilities.convertPoint(window, point1, MetalRootPaneUI.this.getTitlePane());
      Frame frame = null;
      Dialog dialog = null;
      if (window instanceof Frame) {
        frame = (Frame)window;
      } else if (window instanceof Dialog) {
        dialog = (Dialog)window;
      } 
      int i = (frame != null) ? frame.getExtendedState() : 0;
      if (MetalRootPaneUI.this.getTitlePane() != null && MetalRootPaneUI.this.getTitlePane().contains(point2)) {
        if (((frame != null && (i & 0x6) == 0) || dialog != null) && point1.y >= 5 && point1.x >= 5 && point1.x < window.getWidth() - 5) {
          this.isMovingWindow = true;
          this.dragOffsetX = point1.x;
          this.dragOffsetY = point1.y;
        } 
      } else if ((frame != null && frame.isResizable() && (i & 0x6) == 0) || (dialog != null && dialog.isResizable())) {
        this.dragOffsetX = point1.x;
        this.dragOffsetY = point1.y;
        this.dragWidth = window.getWidth();
        this.dragHeight = window.getHeight();
        this.dragCursor = getCursor(calculateCorner(window, point1.x, point1.y));
      } 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (this.dragCursor != 0 && MetalRootPaneUI.this.window != null && !MetalRootPaneUI.this.window.isValid()) {
        MetalRootPaneUI.this.window.validate();
        MetalRootPaneUI.this.getRootPane().repaint();
      } 
      this.isMovingWindow = false;
      this.dragCursor = 0;
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      JRootPane jRootPane = MetalRootPaneUI.this.getRootPane();
      if (jRootPane.getWindowDecorationStyle() == 0)
        return; 
      Window window = (Window)param1MouseEvent.getSource();
      Frame frame = null;
      Dialog dialog = null;
      if (window instanceof Frame) {
        frame = (Frame)window;
      } else if (window instanceof Dialog) {
        dialog = (Dialog)window;
      } 
      int i = getCursor(calculateCorner(window, param1MouseEvent.getX(), param1MouseEvent.getY()));
      if (i != 0 && ((frame != null && frame.isResizable() && (frame.getExtendedState() & 0x6) == 0) || (dialog != null && dialog.isResizable()))) {
        window.setCursor(Cursor.getPredefinedCursor(i));
      } else {
        window.setCursor(MetalRootPaneUI.this.lastCursor);
      } 
    }
    
    private void adjust(Rectangle param1Rectangle, Dimension param1Dimension, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Rectangle.x += param1Int1;
      param1Rectangle.y += param1Int2;
      param1Rectangle.width += param1Int3;
      param1Rectangle.height += param1Int4;
      if (param1Dimension != null) {
        if (param1Rectangle.width < param1Dimension.width) {
          int i = param1Dimension.width - param1Rectangle.width;
          if (param1Int1 != 0)
            param1Rectangle.x -= i; 
          param1Rectangle.width = param1Dimension.width;
        } 
        if (param1Rectangle.height < param1Dimension.height) {
          int i = param1Dimension.height - param1Rectangle.height;
          if (param1Int2 != 0)
            param1Rectangle.y -= i; 
          param1Rectangle.height = param1Dimension.height;
        } 
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      Window window = (Window)param1MouseEvent.getSource();
      Point point = param1MouseEvent.getPoint();
      if (this.isMovingWindow) {
        Point point1 = param1MouseEvent.getLocationOnScreen();
        window.setLocation(point1.x - this.dragOffsetX, point1.y - this.dragOffsetY);
      } else if (this.dragCursor != 0) {
        Rectangle rectangle1 = window.getBounds();
        Rectangle rectangle2 = new Rectangle(rectangle1);
        Dimension dimension = window.getMinimumSize();
        switch (this.dragCursor) {
          case 11:
            adjust(rectangle1, dimension, 0, 0, point.x + this.dragWidth - this.dragOffsetX - rectangle1.width, 0);
            break;
          case 9:
            adjust(rectangle1, dimension, 0, 0, 0, point.y + this.dragHeight - this.dragOffsetY - rectangle1.height);
            break;
          case 8:
            adjust(rectangle1, dimension, 0, point.y - this.dragOffsetY, 0, -(point.y - this.dragOffsetY));
            break;
          case 10:
            adjust(rectangle1, dimension, point.x - this.dragOffsetX, 0, -(point.x - this.dragOffsetX), 0);
            break;
          case 7:
            adjust(rectangle1, dimension, 0, point.y - this.dragOffsetY, point.x + this.dragWidth - this.dragOffsetX - rectangle1.width, -(point.y - this.dragOffsetY));
            break;
          case 5:
            adjust(rectangle1, dimension, 0, 0, point.x + this.dragWidth - this.dragOffsetX - rectangle1.width, point.y + this.dragHeight - this.dragOffsetY - rectangle1.height);
            break;
          case 6:
            adjust(rectangle1, dimension, point.x - this.dragOffsetX, point.y - this.dragOffsetY, -(point.x - this.dragOffsetX), -(point.y - this.dragOffsetY));
            break;
          case 4:
            adjust(rectangle1, dimension, point.x - this.dragOffsetX, 0, -(point.x - this.dragOffsetX), point.y + this.dragHeight - this.dragOffsetY - rectangle1.height);
            break;
        } 
        if (!rectangle1.equals(rectangle2)) {
          window.setBounds(rectangle1);
          if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
            window.validate();
            MetalRootPaneUI.this.getRootPane().repaint();
          } 
        } 
      } 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      Window window = (Window)param1MouseEvent.getSource();
      MetalRootPaneUI.this.lastCursor = window.getCursor();
      mouseMoved(param1MouseEvent);
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      Window window = (Window)param1MouseEvent.getSource();
      window.setCursor(MetalRootPaneUI.this.lastCursor);
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      Window window = (Window)param1MouseEvent.getSource();
      Frame frame = null;
      if (window instanceof Frame) {
        frame = (Frame)window;
      } else {
        return;
      } 
      Point point = SwingUtilities.convertPoint(window, param1MouseEvent.getPoint(), MetalRootPaneUI.this.getTitlePane());
      int i = frame.getExtendedState();
      if (MetalRootPaneUI.this.getTitlePane() != null && MetalRootPaneUI.this.getTitlePane().contains(point) && param1MouseEvent.getClickCount() % 2 == 0 && (param1MouseEvent.getModifiers() & 0x10) != 0 && frame.isResizable()) {
        if ((i & 0x6) != 0) {
          frame.setExtendedState(i & 0xFFFFFFF9);
        } else {
          frame.setExtendedState(i | 0x6);
        } 
        return;
      } 
    }
    
    private int calculateCorner(Window param1Window, int param1Int1, int param1Int2) {
      Insets insets = param1Window.getInsets();
      int i = calculatePosition(param1Int1 - insets.left, param1Window.getWidth() - insets.left - insets.right);
      int j = calculatePosition(param1Int2 - insets.top, param1Window.getHeight() - insets.top - insets.bottom);
      return (i == -1 || j == -1) ? -1 : (j * 5 + i);
    }
    
    private int getCursor(int param1Int) { return (param1Int == -1) ? 0 : cursorMapping[param1Int]; }
    
    private int calculatePosition(int param1Int1, int param1Int2) { return (param1Int1 < 5) ? 0 : ((param1Int1 < 16) ? 1 : ((param1Int1 >= param1Int2 - 5) ? 4 : ((param1Int1 >= param1Int2 - 16) ? 3 : 2))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalRootPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */