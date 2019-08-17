package javax.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.plaf.LayerUI;
import sun.awt.AWTAccessor;

public final class JLayer<V extends Component> extends JComponent implements Scrollable, PropertyChangeListener, Accessible {
  private V view;
  
  private LayerUI<? super V> layerUI;
  
  private JPanel glassPane;
  
  private long eventMask;
  
  private boolean isPainting;
  
  private boolean isPaintingImmediately;
  
  private static final LayerEventController eventController = new LayerEventController(null);
  
  public JLayer() { this(null); }
  
  public JLayer(V paramV) { this(paramV, new LayerUI()); }
  
  public JLayer(V paramV, LayerUI<V> paramLayerUI) {
    setGlassPane(createGlassPane());
    setView(paramV);
    setUI(paramLayerUI);
  }
  
  public V getView() { return (V)this.view; }
  
  public void setView(V paramV) {
    Component component = getView();
    if (component != null)
      super.remove(component); 
    if (paramV != null)
      super.addImpl(paramV, null, getComponentCount()); 
    this.view = paramV;
    firePropertyChange("view", component, paramV);
    revalidate();
    repaint();
  }
  
  public void setUI(LayerUI<? super V> paramLayerUI) {
    this.layerUI = paramLayerUI;
    setUI(paramLayerUI);
  }
  
  public LayerUI<? super V> getUI() { return this.layerUI; }
  
  public JPanel getGlassPane() { return this.glassPane; }
  
  public void setGlassPane(JPanel paramJPanel) {
    JPanel jPanel = getGlassPane();
    boolean bool = false;
    if (jPanel != null) {
      bool = jPanel.isVisible();
      super.remove(jPanel);
    } 
    if (paramJPanel != null) {
      AWTAccessor.getComponentAccessor().setMixingCutoutShape(paramJPanel, new Rectangle());
      paramJPanel.setVisible(bool);
      super.addImpl(paramJPanel, null, 0);
    } 
    this.glassPane = paramJPanel;
    firePropertyChange("glassPane", jPanel, paramJPanel);
    revalidate();
    repaint();
  }
  
  public JPanel createGlassPane() { return new DefaultLayerGlassPane(); }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    if (paramLayoutManager != null)
      throw new IllegalArgumentException("JLayer.setLayout() not supported"); 
  }
  
  public void setBorder(Border paramBorder) {
    if (paramBorder != null)
      throw new IllegalArgumentException("JLayer.setBorder() not supported"); 
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) { throw new UnsupportedOperationException("Adding components to JLayer is not supported, use setView() or setGlassPane() instead"); }
  
  public void remove(Component paramComponent) {
    if (paramComponent == null) {
      super.remove(paramComponent);
    } else if (paramComponent == getView()) {
      setView(null);
    } else if (paramComponent == getGlassPane()) {
      setGlassPane(null);
    } else {
      super.remove(paramComponent);
    } 
  }
  
  public void removeAll() {
    if (this.view != null)
      setView(null); 
    if (this.glassPane != null)
      setGlassPane(null); 
  }
  
  protected boolean isPaintingOrigin() { return true; }
  
  public void paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (!this.isPaintingImmediately && getUI() != null) {
      this.isPaintingImmediately = true;
      try {
        getUI().paintImmediately(paramInt1, paramInt2, paramInt3, paramInt4, this);
      } finally {
        this.isPaintingImmediately = false;
      } 
    } else {
      super.paintImmediately(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public void paint(Graphics paramGraphics) {
    if (!this.isPainting) {
      this.isPainting = true;
      try {
        super.paintComponent(paramGraphics);
      } finally {
        this.isPainting = false;
      } 
    } else {
      super.paint(paramGraphics);
    } 
  }
  
  protected void paintComponent(Graphics paramGraphics) {}
  
  public boolean isOptimizedDrawingEnabled() { return false; }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getUI() != null)
      getUI().applyPropertyChange(paramPropertyChangeEvent, this); 
  }
  
  public void setLayerEventMask(long paramLong) {
    long l = getLayerEventMask();
    this.eventMask = paramLong;
    firePropertyChange("layerEventMask", l, paramLong);
    if (paramLong != l) {
      disableEvents(l);
      enableEvents(this.eventMask);
      if (isDisplayable())
        eventController.updateAWTEventListener(l, paramLong); 
    } 
  }
  
  public long getLayerEventMask() { return this.eventMask; }
  
  public void updateUI() {
    if (getUI() != null)
      getUI().updateUI(this); 
  }
  
  public Dimension getPreferredScrollableViewportSize() { return (getView() instanceof Scrollable) ? ((Scrollable)getView()).getPreferredScrollableViewportSize() : getPreferredSize(); }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) { return (getView() instanceof Scrollable) ? ((Scrollable)getView()).getScrollableBlockIncrement(paramRectangle, paramInt1, paramInt2) : ((paramInt1 == 1) ? paramRectangle.height : paramRectangle.width); }
  
  public boolean getScrollableTracksViewportHeight() { return (getView() instanceof Scrollable) ? ((Scrollable)getView()).getScrollableTracksViewportHeight() : 0; }
  
  public boolean getScrollableTracksViewportWidth() { return (getView() instanceof Scrollable) ? ((Scrollable)getView()).getScrollableTracksViewportWidth() : 0; }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) { return (getView() instanceof Scrollable) ? ((Scrollable)getView()).getScrollableUnitIncrement(paramRectangle, paramInt1, paramInt2) : 1; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.layerUI != null)
      setUI(this.layerUI); 
    if (this.eventMask != 0L)
      eventController.updateAWTEventListener(0L, this.eventMask); 
  }
  
  public void addNotify() {
    super.addNotify();
    eventController.updateAWTEventListener(0L, this.eventMask);
  }
  
  public void removeNotify() {
    super.removeNotify();
    eventController.updateAWTEventListener(this.eventMask, 0L);
  }
  
  public void doLayout() {
    if (getUI() != null)
      getUI().doLayout(this); 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new JComponent.AccessibleJComponent() {
          public AccessibleRole getAccessibleRole() { return AccessibleRole.PANEL; }
        }; 
    return this.accessibleContext;
  }
  
  private static class DefaultLayerGlassPane extends JPanel {
    public DefaultLayerGlassPane() { setOpaque(false); }
    
    public boolean contains(int param1Int1, int param1Int2) {
      for (byte b = 0; b < getComponentCount(); b++) {
        Component component = getComponent(b);
        Point point = SwingUtilities.convertPoint(this, new Point(param1Int1, param1Int2), component);
        if (component.isVisible() && component.contains(point))
          return true; 
      } 
      return (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0 && getMouseWheelListeners().length == 0 && !isCursorSet()) ? false : super.contains(param1Int1, param1Int2);
    }
  }
  
  private static class LayerEventController implements AWTEventListener {
    private ArrayList<Long> layerMaskList = new ArrayList();
    
    private long currentEventMask;
    
    private static final long ACCEPTED_EVENTS = 231487L;
    
    private LayerEventController() {}
    
    public void eventDispatched(AWTEvent param1AWTEvent) {
      Object object = param1AWTEvent.getSource();
      if (object instanceof Component)
        for (Component component = (Component)object; component != null; component = component.getParent()) {
          if (component instanceof JLayer) {
            JLayer jLayer = (JLayer)component;
            LayerUI layerUI = jLayer.getUI();
            if (layerUI != null && isEventEnabled(jLayer.getLayerEventMask(), param1AWTEvent.getID()) && (!(param1AWTEvent instanceof InputEvent) || !((InputEvent)param1AWTEvent).isConsumed()))
              layerUI.eventDispatched(param1AWTEvent, jLayer); 
          } 
        }  
    }
    
    private void updateAWTEventListener(long param1Long1, long param1Long2) {
      if (param1Long1 != 0L)
        this.layerMaskList.remove(Long.valueOf(param1Long1)); 
      if (param1Long2 != 0L)
        this.layerMaskList.add(Long.valueOf(param1Long2)); 
      long l = 0L;
      for (Long long : this.layerMaskList)
        l |= long.longValue(); 
      l &= 0x3883FL;
      if (l == 0L) {
        removeAWTEventListener();
      } else if (getCurrentEventMask() != l) {
        removeAWTEventListener();
        addAWTEventListener(l);
      } 
      this.currentEventMask = l;
    }
    
    private long getCurrentEventMask() { return this.currentEventMask; }
    
    private void addAWTEventListener(final long eventMask) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              Toolkit.getDefaultToolkit().addAWTEventListener(JLayer.LayerEventController.this, eventMask);
              return null;
            }
          }); }
    
    private void removeAWTEventListener() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              Toolkit.getDefaultToolkit().removeAWTEventListener(JLayer.LayerEventController.this);
              return null;
            }
          }); }
    
    private boolean isEventEnabled(long param1Long, int param1Int) { return (((param1Long & 0x1L) != 0L && param1Int >= 100 && param1Int <= 103) || ((param1Long & 0x2L) != 0L && param1Int >= 300 && param1Int <= 301) || ((param1Long & 0x4L) != 0L && param1Int >= 1004 && param1Int <= 1005) || ((param1Long & 0x8L) != 0L && param1Int >= 400 && param1Int <= 402) || ((param1Long & 0x20000L) != 0L && param1Int == 507) || ((param1Long & 0x20L) != 0L && (param1Int == 503 || param1Int == 506)) || ((param1Long & 0x10L) != 0L && param1Int != 503 && param1Int != 506 && param1Int != 507 && param1Int >= 500 && param1Int <= 507) || ((param1Long & 0x800L) != 0L && param1Int >= 1100 && param1Int <= 1101) || ((param1Long & 0x8000L) != 0L && param1Int == 1400) || ((param1Long & 0x10000L) != 0L && (param1Int == 1401 || param1Int == 1402))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */