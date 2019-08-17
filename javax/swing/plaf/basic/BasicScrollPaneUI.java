package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicScrollPaneUI extends ScrollPaneUI implements ScrollPaneConstants {
  protected JScrollPane scrollpane;
  
  protected ChangeListener vsbChangeListener;
  
  protected ChangeListener hsbChangeListener;
  
  protected ChangeListener viewportChangeListener;
  
  protected PropertyChangeListener spPropertyChangeListener;
  
  private MouseWheelListener mouseScrollListener;
  
  private int oldExtent = Integer.MIN_VALUE;
  
  private PropertyChangeListener vsbPropertyChangeListener;
  
  private PropertyChangeListener hsbPropertyChangeListener;
  
  private Handler handler;
  
  private boolean setValueCalled = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicScrollPaneUI(); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("scrollUp"));
    paramLazyActionMap.put(new Actions("scrollDown"));
    paramLazyActionMap.put(new Actions("scrollHome"));
    paramLazyActionMap.put(new Actions("scrollEnd"));
    paramLazyActionMap.put(new Actions("unitScrollUp"));
    paramLazyActionMap.put(new Actions("unitScrollDown"));
    paramLazyActionMap.put(new Actions("scrollLeft"));
    paramLazyActionMap.put(new Actions("scrollRight"));
    paramLazyActionMap.put(new Actions("unitScrollRight"));
    paramLazyActionMap.put(new Actions("unitScrollLeft"));
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Border border = this.scrollpane.getViewportBorder();
    if (border != null) {
      Rectangle rectangle = this.scrollpane.getViewportBorderBounds();
      border.paintBorder(this.scrollpane, paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(32767, 32767); }
  
  protected void installDefaults(JScrollPane paramJScrollPane) {
    LookAndFeel.installBorder(paramJScrollPane, "ScrollPane.border");
    LookAndFeel.installColorsAndFont(paramJScrollPane, "ScrollPane.background", "ScrollPane.foreground", "ScrollPane.font");
    Border border = paramJScrollPane.getViewportBorder();
    if (border == null || border instanceof javax.swing.plaf.UIResource) {
      border = UIManager.getBorder("ScrollPane.viewportBorder");
      paramJScrollPane.setViewportBorder(border);
    } 
    LookAndFeel.installProperty(paramJScrollPane, "opaque", Boolean.TRUE);
  }
  
  protected void installListeners(JScrollPane paramJScrollPane) {
    this.vsbChangeListener = createVSBChangeListener();
    this.vsbPropertyChangeListener = createVSBPropertyChangeListener();
    this.hsbChangeListener = createHSBChangeListener();
    this.hsbPropertyChangeListener = createHSBPropertyChangeListener();
    this.viewportChangeListener = createViewportChangeListener();
    this.spPropertyChangeListener = createPropertyChangeListener();
    JViewport jViewport = this.scrollpane.getViewport();
    JScrollBar jScrollBar1 = this.scrollpane.getVerticalScrollBar();
    JScrollBar jScrollBar2 = this.scrollpane.getHorizontalScrollBar();
    if (jViewport != null)
      jViewport.addChangeListener(this.viewportChangeListener); 
    if (jScrollBar1 != null) {
      jScrollBar1.getModel().addChangeListener(this.vsbChangeListener);
      jScrollBar1.addPropertyChangeListener(this.vsbPropertyChangeListener);
    } 
    if (jScrollBar2 != null) {
      jScrollBar2.getModel().addChangeListener(this.hsbChangeListener);
      jScrollBar2.addPropertyChangeListener(this.hsbPropertyChangeListener);
    } 
    this.scrollpane.addPropertyChangeListener(this.spPropertyChangeListener);
    this.mouseScrollListener = createMouseWheelListener();
    this.scrollpane.addMouseWheelListener(this.mouseScrollListener);
  }
  
  protected void installKeyboardActions(JScrollPane paramJScrollPane) {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(paramJScrollPane, 1, inputMap);
    LazyActionMap.installLazyActionMap(paramJScrollPane, BasicScrollPaneUI.class, "ScrollPane.actionMap");
  }
  
  InputMap getInputMap(int paramInt) {
    if (paramInt == 1) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(this.scrollpane, this, "ScrollPane.ancestorInputMap");
      InputMap inputMap2;
      if (this.scrollpane.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(this.scrollpane, this, "ScrollPane.ancestorInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    return null;
  }
  
  public void installUI(JComponent paramJComponent) {
    this.scrollpane = (JScrollPane)paramJComponent;
    installDefaults(this.scrollpane);
    installListeners(this.scrollpane);
    installKeyboardActions(this.scrollpane);
  }
  
  protected void uninstallDefaults(JScrollPane paramJScrollPane) {
    LookAndFeel.uninstallBorder(this.scrollpane);
    if (this.scrollpane.getViewportBorder() instanceof javax.swing.plaf.UIResource)
      this.scrollpane.setViewportBorder(null); 
  }
  
  protected void uninstallListeners(JComponent paramJComponent) {
    JViewport jViewport = this.scrollpane.getViewport();
    JScrollBar jScrollBar1 = this.scrollpane.getVerticalScrollBar();
    JScrollBar jScrollBar2 = this.scrollpane.getHorizontalScrollBar();
    if (jViewport != null)
      jViewport.removeChangeListener(this.viewportChangeListener); 
    if (jScrollBar1 != null) {
      jScrollBar1.getModel().removeChangeListener(this.vsbChangeListener);
      jScrollBar1.removePropertyChangeListener(this.vsbPropertyChangeListener);
    } 
    if (jScrollBar2 != null) {
      jScrollBar2.getModel().removeChangeListener(this.hsbChangeListener);
      jScrollBar2.removePropertyChangeListener(this.hsbPropertyChangeListener);
    } 
    this.scrollpane.removePropertyChangeListener(this.spPropertyChangeListener);
    if (this.mouseScrollListener != null)
      this.scrollpane.removeMouseWheelListener(this.mouseScrollListener); 
    this.vsbChangeListener = null;
    this.hsbChangeListener = null;
    this.viewportChangeListener = null;
    this.spPropertyChangeListener = null;
    this.mouseScrollListener = null;
    this.handler = null;
  }
  
  protected void uninstallKeyboardActions(JScrollPane paramJScrollPane) {
    SwingUtilities.replaceUIActionMap(paramJScrollPane, null);
    SwingUtilities.replaceUIInputMap(paramJScrollPane, 1, null);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults(this.scrollpane);
    uninstallListeners(this.scrollpane);
    uninstallKeyboardActions(this.scrollpane);
    this.scrollpane = null;
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(); 
    return this.handler;
  }
  
  protected void syncScrollPaneWithViewport() {
    JViewport jViewport1 = this.scrollpane.getViewport();
    JScrollBar jScrollBar1 = this.scrollpane.getVerticalScrollBar();
    JScrollBar jScrollBar2 = this.scrollpane.getHorizontalScrollBar();
    JViewport jViewport2 = this.scrollpane.getRowHeader();
    JViewport jViewport3 = this.scrollpane.getColumnHeader();
    boolean bool = this.scrollpane.getComponentOrientation().isLeftToRight();
    if (jViewport1 != null) {
      Dimension dimension1 = jViewport1.getExtentSize();
      Dimension dimension2 = jViewport1.getViewSize();
      Point point = jViewport1.getViewPosition();
      if (jScrollBar1 != null) {
        int i = dimension1.height;
        int j = dimension2.height;
        int k = Math.max(0, Math.min(point.y, j - i));
        jScrollBar1.setValues(k, i, 0, j);
      } 
      if (jScrollBar2 != null) {
        int k;
        int i = dimension1.width;
        int j = dimension2.width;
        if (bool) {
          k = Math.max(0, Math.min(point.x, j - i));
        } else {
          int m = jScrollBar2.getValue();
          if (this.setValueCalled && j - m == point.x) {
            k = Math.max(0, Math.min(j - i, m));
            if (i != 0)
              this.setValueCalled = false; 
          } else if (i > j) {
            point.x = j - i;
            jViewport1.setViewPosition(point);
            k = 0;
          } else {
            k = Math.max(0, Math.min(j - i, j - i - point.x));
            if (this.oldExtent > i)
              k -= this.oldExtent - i; 
          } 
        } 
        this.oldExtent = i;
        jScrollBar2.setValues(k, i, 0, j);
      } 
      if (jViewport2 != null) {
        Point point1 = jViewport2.getViewPosition();
        point1.y = (jViewport1.getViewPosition()).y;
        point1.x = 0;
        jViewport2.setViewPosition(point1);
      } 
      if (jViewport3 != null) {
        Point point1 = jViewport3.getViewPosition();
        if (bool) {
          point1.x = (jViewport1.getViewPosition()).x;
        } else {
          point1.x = Math.max(0, (jViewport1.getViewPosition()).x);
        } 
        point1.y = 0;
        jViewport3.setViewPosition(point1);
      } 
    } 
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    if (paramJComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    JViewport jViewport1 = this.scrollpane.getViewport();
    Insets insets = this.scrollpane.getInsets();
    int i = insets.top;
    paramInt2 = paramInt2 - insets.top - insets.bottom;
    paramInt1 = paramInt1 - insets.left - insets.right;
    JViewport jViewport2 = this.scrollpane.getColumnHeader();
    if (jViewport2 != null && jViewport2.isVisible()) {
      Component component1 = jViewport2.getView();
      if (component1 != null && component1.isVisible()) {
        Dimension dimension1 = component1.getPreferredSize();
        int j = component1.getBaseline(dimension1.width, dimension1.height);
        if (j >= 0)
          return i + j; 
      } 
      Dimension dimension = jViewport2.getPreferredSize();
      paramInt2 -= dimension.height;
      i += dimension.height;
    } 
    Component component = (jViewport1 == null) ? null : jViewport1.getView();
    if (component != null && component.isVisible() && component.getBaselineResizeBehavior() == Component.BaselineResizeBehavior.CONSTANT_ASCENT) {
      Border border = this.scrollpane.getViewportBorder();
      if (border != null) {
        Insets insets1 = border.getBorderInsets(this.scrollpane);
        i += insets1.top;
        paramInt2 = paramInt2 - insets1.top - insets1.bottom;
        paramInt1 = paramInt1 - insets1.left - insets1.right;
      } 
      if (component.getWidth() > 0 && component.getHeight() > 0) {
        Dimension dimension = component.getMinimumSize();
        paramInt1 = Math.max(dimension.width, component.getWidth());
        paramInt2 = Math.max(dimension.height, component.getHeight());
      } 
      if (paramInt1 > 0 && paramInt2 > 0) {
        int j = component.getBaseline(paramInt1, paramInt2);
        if (j > 0)
          return i + j; 
      } 
    } 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  protected ChangeListener createViewportChangeListener() { return getHandler(); }
  
  private PropertyChangeListener createHSBPropertyChangeListener() { return getHandler(); }
  
  protected ChangeListener createHSBChangeListener() { return getHandler(); }
  
  private PropertyChangeListener createVSBPropertyChangeListener() { return getHandler(); }
  
  protected ChangeListener createVSBChangeListener() { return getHandler(); }
  
  protected MouseWheelListener createMouseWheelListener() { return getHandler(); }
  
  protected void updateScrollBarDisplayPolicy(PropertyChangeEvent paramPropertyChangeEvent) {
    this.scrollpane.revalidate();
    this.scrollpane.repaint();
  }
  
  protected void updateViewport(PropertyChangeEvent paramPropertyChangeEvent) {
    JViewport jViewport1 = (JViewport)paramPropertyChangeEvent.getOldValue();
    JViewport jViewport2 = (JViewport)paramPropertyChangeEvent.getNewValue();
    if (jViewport1 != null)
      jViewport1.removeChangeListener(this.viewportChangeListener); 
    if (jViewport2 != null) {
      Point point = jViewport2.getViewPosition();
      if (this.scrollpane.getComponentOrientation().isLeftToRight()) {
        point.x = Math.max(point.x, 0);
      } else {
        int i = (jViewport2.getViewSize()).width;
        int j = (jViewport2.getExtentSize()).width;
        if (j > i) {
          point.x = i - j;
        } else {
          point.x = Math.max(0, Math.min(i - j, point.x));
        } 
      } 
      point.y = Math.max(point.y, 0);
      jViewport2.setViewPosition(point);
      jViewport2.addChangeListener(this.viewportChangeListener);
    } 
  }
  
  protected void updateRowHeader(PropertyChangeEvent paramPropertyChangeEvent) {
    JViewport jViewport = (JViewport)paramPropertyChangeEvent.getNewValue();
    if (jViewport != null) {
      JViewport jViewport1 = this.scrollpane.getViewport();
      Point point = jViewport.getViewPosition();
      point.y = (jViewport1 != null) ? (jViewport1.getViewPosition()).y : 0;
      jViewport.setViewPosition(point);
    } 
  }
  
  protected void updateColumnHeader(PropertyChangeEvent paramPropertyChangeEvent) {
    JViewport jViewport = (JViewport)paramPropertyChangeEvent.getNewValue();
    if (jViewport != null) {
      JViewport jViewport1 = this.scrollpane.getViewport();
      Point point = jViewport.getViewPosition();
      if (jViewport1 == null) {
        point.x = 0;
      } else if (this.scrollpane.getComponentOrientation().isLeftToRight()) {
        point.x = (jViewport1.getViewPosition()).x;
      } else {
        point.x = Math.max(0, (jViewport1.getViewPosition()).x);
      } 
      jViewport.setViewPosition(point);
      this.scrollpane.add(jViewport, "COLUMN_HEADER");
    } 
  }
  
  private void updateHorizontalScrollBar(PropertyChangeEvent paramPropertyChangeEvent) { updateScrollBar(paramPropertyChangeEvent, this.hsbChangeListener, this.hsbPropertyChangeListener); }
  
  private void updateVerticalScrollBar(PropertyChangeEvent paramPropertyChangeEvent) { updateScrollBar(paramPropertyChangeEvent, this.vsbChangeListener, this.vsbPropertyChangeListener); }
  
  private void updateScrollBar(PropertyChangeEvent paramPropertyChangeEvent, ChangeListener paramChangeListener, PropertyChangeListener paramPropertyChangeListener) {
    JScrollBar jScrollBar = (JScrollBar)paramPropertyChangeEvent.getOldValue();
    if (jScrollBar != null) {
      if (paramChangeListener != null)
        jScrollBar.getModel().removeChangeListener(paramChangeListener); 
      if (paramPropertyChangeListener != null)
        jScrollBar.removePropertyChangeListener(paramPropertyChangeListener); 
    } 
    jScrollBar = (JScrollBar)paramPropertyChangeEvent.getNewValue();
    if (jScrollBar != null) {
      if (paramChangeListener != null)
        jScrollBar.getModel().addChangeListener(paramChangeListener); 
      if (paramPropertyChangeListener != null)
        jScrollBar.addPropertyChangeListener(paramPropertyChangeListener); 
    } 
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private static class Actions extends UIAction {
    private static final String SCROLL_UP = "scrollUp";
    
    private static final String SCROLL_DOWN = "scrollDown";
    
    private static final String SCROLL_HOME = "scrollHome";
    
    private static final String SCROLL_END = "scrollEnd";
    
    private static final String UNIT_SCROLL_UP = "unitScrollUp";
    
    private static final String UNIT_SCROLL_DOWN = "unitScrollDown";
    
    private static final String SCROLL_LEFT = "scrollLeft";
    
    private static final String SCROLL_RIGHT = "scrollRight";
    
    private static final String UNIT_SCROLL_LEFT = "unitScrollLeft";
    
    private static final String UNIT_SCROLL_RIGHT = "unitScrollRight";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JScrollPane jScrollPane = (JScrollPane)param1ActionEvent.getSource();
      boolean bool = jScrollPane.getComponentOrientation().isLeftToRight();
      String str = getName();
      if (str == "scrollUp") {
        scroll(jScrollPane, 1, -1, true);
      } else if (str == "scrollDown") {
        scroll(jScrollPane, 1, 1, true);
      } else if (str == "scrollHome") {
        scrollHome(jScrollPane);
      } else if (str == "scrollEnd") {
        scrollEnd(jScrollPane);
      } else if (str == "unitScrollUp") {
        scroll(jScrollPane, 1, -1, false);
      } else if (str == "unitScrollDown") {
        scroll(jScrollPane, 1, 1, false);
      } else if (str == "scrollLeft") {
        scroll(jScrollPane, 0, bool ? -1 : 1, true);
      } else if (str == "scrollRight") {
        scroll(jScrollPane, 0, bool ? 1 : -1, true);
      } else if (str == "unitScrollLeft") {
        scroll(jScrollPane, 0, bool ? -1 : 1, false);
      } else if (str == "unitScrollRight") {
        scroll(jScrollPane, 0, bool ? 1 : -1, false);
      } 
    }
    
    private void scrollEnd(JScrollPane param1JScrollPane) {
      JViewport jViewport = param1JScrollPane.getViewport();
      Component component;
      if (jViewport != null && (component = jViewport.getView()) != null) {
        Rectangle rectangle1 = jViewport.getViewRect();
        Rectangle rectangle2 = component.getBounds();
        if (param1JScrollPane.getComponentOrientation().isLeftToRight()) {
          jViewport.setViewPosition(new Point(rectangle2.width - rectangle1.width, rectangle2.height - rectangle1.height));
        } else {
          jViewport.setViewPosition(new Point(0, rectangle2.height - rectangle1.height));
        } 
      } 
    }
    
    private void scrollHome(JScrollPane param1JScrollPane) {
      JViewport jViewport = param1JScrollPane.getViewport();
      Component component;
      if (jViewport != null && (component = jViewport.getView()) != null)
        if (param1JScrollPane.getComponentOrientation().isLeftToRight()) {
          jViewport.setViewPosition(new Point(0, 0));
        } else {
          Rectangle rectangle1 = jViewport.getViewRect();
          Rectangle rectangle2 = component.getBounds();
          jViewport.setViewPosition(new Point(rectangle2.width - rectangle1.width, 0));
        }  
    }
    
    private void scroll(JScrollPane param1JScrollPane, int param1Int1, int param1Int2, boolean param1Boolean) {
      JViewport jViewport = param1JScrollPane.getViewport();
      Component component;
      if (jViewport != null && (component = jViewport.getView()) != null) {
        int i;
        Rectangle rectangle = jViewport.getViewRect();
        Dimension dimension = component.getSize();
        if (component instanceof Scrollable) {
          if (param1Boolean) {
            i = ((Scrollable)component).getScrollableBlockIncrement(rectangle, param1Int1, param1Int2);
          } else {
            i = ((Scrollable)component).getScrollableUnitIncrement(rectangle, param1Int1, param1Int2);
          } 
        } else if (param1Boolean) {
          if (param1Int1 == 1) {
            i = rectangle.height;
          } else {
            i = rectangle.width;
          } 
        } else {
          i = 10;
        } 
        if (param1Int1 == 1) {
          rectangle.y += i * param1Int2;
          if (rectangle.y + rectangle.height > dimension.height) {
            rectangle.y = Math.max(0, dimension.height - rectangle.height);
          } else if (rectangle.y < 0) {
            rectangle.y = 0;
          } 
        } else if (param1JScrollPane.getComponentOrientation().isLeftToRight()) {
          rectangle.x += i * param1Int2;
          if (rectangle.x + rectangle.width > dimension.width) {
            rectangle.x = Math.max(0, dimension.width - rectangle.width);
          } else if (rectangle.x < 0) {
            rectangle.x = 0;
          } 
        } else {
          rectangle.x -= i * param1Int2;
          if (rectangle.width > dimension.width) {
            rectangle.x = dimension.width - rectangle.width;
          } else {
            rectangle.x = Math.max(0, Math.min(dimension.width - rectangle.width, rectangle.x));
          } 
        } 
        jViewport.setViewPosition(rectangle.getLocation());
      } 
    }
  }
  
  public class HSBChangeListener implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicScrollPaneUI.this.getHandler().stateChanged(param1ChangeEvent); }
  }
  
  class Handler implements ChangeListener, PropertyChangeListener, MouseWheelListener {
    public void mouseWheelMoved(MouseWheelEvent param1MouseWheelEvent) {
      if (BasicScrollPaneUI.this.scrollpane.isWheelScrollingEnabled() && param1MouseWheelEvent.getWheelRotation() != 0) {
        JScrollBar jScrollBar = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
        byte b = (param1MouseWheelEvent.getWheelRotation() < 0) ? -1 : 1;
        byte b1 = 1;
        if (jScrollBar == null || !jScrollBar.isVisible() || param1MouseWheelEvent.isShiftDown()) {
          jScrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
          if (jScrollBar == null || !jScrollBar.isVisible())
            return; 
          b1 = 0;
        } 
        param1MouseWheelEvent.consume();
        if (param1MouseWheelEvent.getScrollType() == 0) {
          JViewport jViewport = BasicScrollPaneUI.this.scrollpane.getViewport();
          if (jViewport == null)
            return; 
          Component component = jViewport.getView();
          int i = Math.abs(param1MouseWheelEvent.getUnitsToScroll());
          boolean bool = (Math.abs(param1MouseWheelEvent.getWheelRotation()) == 1);
          Object object = jScrollBar.getClientProperty("JScrollBar.fastWheelScrolling");
          if (Boolean.TRUE == object && component instanceof Scrollable) {
            Scrollable scrollable = (Scrollable)component;
            Rectangle rectangle = jViewport.getViewRect();
            int j = rectangle.x;
            boolean bool1 = component.getComponentOrientation().isLeftToRight();
            int k = jScrollBar.getMinimum();
            int m = jScrollBar.getMaximum() - jScrollBar.getModel().getExtent();
            if (bool) {
              int i1 = scrollable.getScrollableBlockIncrement(rectangle, b1, b);
              if (b < 0) {
                k = Math.max(k, jScrollBar.getValue() - i1);
              } else {
                m = Math.min(m, jScrollBar.getValue() + i1);
              } 
            } 
            int n;
            for (n = 0; n < i; n++) {
              int i1 = scrollable.getScrollableUnitIncrement(rectangle, b1, b);
              if (b1 == 1) {
                if (b < 0) {
                  rectangle.y -= i1;
                  if (rectangle.y <= k) {
                    rectangle.y = k;
                    break;
                  } 
                } else {
                  rectangle.y += i1;
                  if (rectangle.y >= m) {
                    rectangle.y = m;
                    break;
                  } 
                } 
              } else if ((bool1 && b < 0) || (!bool1 && b > 0)) {
                rectangle.x -= i1;
                if (bool1 && rectangle.x < k) {
                  rectangle.x = k;
                  break;
                } 
              } else if ((bool1 && b > 0) || (!bool1 && b < 0)) {
                rectangle.x += i1;
                if (bool1 && rectangle.x > m) {
                  rectangle.x = m;
                  break;
                } 
              } else {
                assert false : "Non-sensical ComponentOrientation / scroll direction";
              } 
            } 
            if (b1 == 1) {
              jScrollBar.setValue(rectangle.y);
            } else if (bool1) {
              jScrollBar.setValue(rectangle.x);
            } else {
              n = jScrollBar.getValue() - rectangle.x - j;
              if (n < k) {
                n = k;
              } else if (n > m) {
                n = m;
              } 
              jScrollBar.setValue(n);
            } 
          } else {
            BasicScrollBarUI.scrollByUnits(jScrollBar, b, i, bool);
          } 
        } else if (param1MouseWheelEvent.getScrollType() == 1) {
          BasicScrollBarUI.scrollByBlock(jScrollBar, b);
        } 
      } 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      JViewport jViewport = BasicScrollPaneUI.this.scrollpane.getViewport();
      if (jViewport != null)
        if (param1ChangeEvent.getSource() == jViewport) {
          BasicScrollPaneUI.this.syncScrollPaneWithViewport();
        } else {
          JScrollBar jScrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
          if (jScrollBar != null && param1ChangeEvent.getSource() == jScrollBar.getModel()) {
            hsbStateChanged(jViewport, param1ChangeEvent);
          } else {
            JScrollBar jScrollBar1 = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
            if (jScrollBar1 != null && param1ChangeEvent.getSource() == jScrollBar1.getModel())
              vsbStateChanged(jViewport, param1ChangeEvent); 
          } 
        }  
    }
    
    private void vsbStateChanged(JViewport param1JViewport, ChangeEvent param1ChangeEvent) {
      BoundedRangeModel boundedRangeModel = (BoundedRangeModel)param1ChangeEvent.getSource();
      Point point = param1JViewport.getViewPosition();
      point.y = boundedRangeModel.getValue();
      param1JViewport.setViewPosition(point);
    }
    
    private void hsbStateChanged(JViewport param1JViewport, ChangeEvent param1ChangeEvent) {
      BoundedRangeModel boundedRangeModel = (BoundedRangeModel)param1ChangeEvent.getSource();
      Point point = param1JViewport.getViewPosition();
      int i = boundedRangeModel.getValue();
      if (BasicScrollPaneUI.this.scrollpane.getComponentOrientation().isLeftToRight()) {
        point.x = i;
      } else {
        int j = (param1JViewport.getViewSize()).width;
        int k = (param1JViewport.getExtentSize()).width;
        int m = point.x;
        point.x = j - k - i;
        if (k == 0 && i != 0 && m == j) {
          BasicScrollPaneUI.this.setValueCalled = true;
        } else if (k != 0 && m < 0 && point.x == 0) {
          point.x += i;
        } 
      } 
      param1JViewport.setViewPosition(point);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getSource() == BasicScrollPaneUI.this.scrollpane) {
        scrollPanePropertyChange(param1PropertyChangeEvent);
      } else {
        sbPropertyChange(param1PropertyChangeEvent);
      } 
    }
    
    private void scrollPanePropertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "verticalScrollBarDisplayPolicy") {
        BasicScrollPaneUI.this.updateScrollBarDisplayPolicy(param1PropertyChangeEvent);
      } else if (str == "horizontalScrollBarDisplayPolicy") {
        BasicScrollPaneUI.this.updateScrollBarDisplayPolicy(param1PropertyChangeEvent);
      } else if (str == "viewport") {
        BasicScrollPaneUI.this.updateViewport(param1PropertyChangeEvent);
      } else if (str == "rowHeader") {
        BasicScrollPaneUI.this.updateRowHeader(param1PropertyChangeEvent);
      } else if (str == "columnHeader") {
        BasicScrollPaneUI.this.updateColumnHeader(param1PropertyChangeEvent);
      } else if (str == "verticalScrollBar") {
        BasicScrollPaneUI.this.updateVerticalScrollBar(param1PropertyChangeEvent);
      } else if (str == "horizontalScrollBar") {
        BasicScrollPaneUI.this.updateHorizontalScrollBar(param1PropertyChangeEvent);
      } else if (str == "componentOrientation") {
        BasicScrollPaneUI.this.scrollpane.revalidate();
        BasicScrollPaneUI.this.scrollpane.repaint();
      } 
    }
    
    private void sbPropertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Object object = param1PropertyChangeEvent.getSource();
      if ("model" == str) {
        JScrollBar jScrollBar = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
        BoundedRangeModel boundedRangeModel = (BoundedRangeModel)param1PropertyChangeEvent.getOldValue();
        ChangeListener changeListener = null;
        if (object == jScrollBar) {
          changeListener = BasicScrollPaneUI.this.vsbChangeListener;
        } else if (object == BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar()) {
          jScrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
          changeListener = BasicScrollPaneUI.this.hsbChangeListener;
        } 
        if (changeListener != null) {
          if (boundedRangeModel != null)
            boundedRangeModel.removeChangeListener(changeListener); 
          if (jScrollBar.getModel() != null)
            jScrollBar.getModel().addChangeListener(changeListener); 
        } 
      } else if ("componentOrientation" == str && object == BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar()) {
        JScrollBar jScrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
        JViewport jViewport = BasicScrollPaneUI.this.scrollpane.getViewport();
        Point point = jViewport.getViewPosition();
        if (BasicScrollPaneUI.this.scrollpane.getComponentOrientation().isLeftToRight()) {
          point.x = jScrollBar.getValue();
        } else {
          point.x = (jViewport.getViewSize()).width - (jViewport.getExtentSize()).width - jScrollBar.getValue();
        } 
        jViewport.setViewPosition(point);
      } 
    }
  }
  
  protected class MouseWheelHandler implements MouseWheelListener {
    public void mouseWheelMoved(MouseWheelEvent param1MouseWheelEvent) { BasicScrollPaneUI.this.getHandler().mouseWheelMoved(param1MouseWheelEvent); }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicScrollPaneUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  public class VSBChangeListener implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicScrollPaneUI.this.getHandler().stateChanged(param1ChangeEvent); }
  }
  
  public class ViewportChangeHandler implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicScrollPaneUI.this.getHandler().stateChanged(param1ChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */