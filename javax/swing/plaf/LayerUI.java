package javax.swing.plaf;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

public class LayerUI<V extends Component> extends ComponentUI implements Serializable {
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) { paramJComponent.paint(paramGraphics); }
  
  public void eventDispatched(AWTEvent paramAWTEvent, JLayer<? extends V> paramJLayer) {
    if (paramAWTEvent instanceof FocusEvent) {
      processFocusEvent((FocusEvent)paramAWTEvent, paramJLayer);
    } else if (paramAWTEvent instanceof MouseEvent) {
      switch (paramAWTEvent.getID()) {
        case 500:
        case 501:
        case 502:
        case 504:
        case 505:
          processMouseEvent((MouseEvent)paramAWTEvent, paramJLayer);
          break;
        case 503:
        case 506:
          processMouseMotionEvent((MouseEvent)paramAWTEvent, paramJLayer);
          break;
        case 507:
          processMouseWheelEvent((MouseWheelEvent)paramAWTEvent, paramJLayer);
          break;
      } 
    } else if (paramAWTEvent instanceof KeyEvent) {
      processKeyEvent((KeyEvent)paramAWTEvent, paramJLayer);
    } else if (paramAWTEvent instanceof ComponentEvent) {
      processComponentEvent((ComponentEvent)paramAWTEvent, paramJLayer);
    } else if (paramAWTEvent instanceof InputMethodEvent) {
      processInputMethodEvent((InputMethodEvent)paramAWTEvent, paramJLayer);
    } else if (paramAWTEvent instanceof HierarchyEvent) {
      switch (paramAWTEvent.getID()) {
        case 1400:
          processHierarchyEvent((HierarchyEvent)paramAWTEvent, paramJLayer);
          break;
        case 1401:
        case 1402:
          processHierarchyBoundsEvent((HierarchyEvent)paramAWTEvent, paramJLayer);
          break;
      } 
    } 
  }
  
  protected void processComponentEvent(ComponentEvent paramComponentEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processFocusEvent(FocusEvent paramFocusEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processKeyEvent(KeyEvent paramKeyEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processMouseEvent(MouseEvent paramMouseEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processMouseMotionEvent(MouseEvent paramMouseEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processHierarchyEvent(HierarchyEvent paramHierarchyEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processHierarchyBoundsEvent(HierarchyEvent paramHierarchyEvent, JLayer<? extends V> paramJLayer) {}
  
  public void updateUI(JLayer<? extends V> paramJLayer) {}
  
  public void installUI(JComponent paramJComponent) { addPropertyChangeListener((JLayer)paramJComponent); }
  
  public void uninstallUI(JComponent paramJComponent) { removePropertyChangeListener((JLayer)paramJComponent); }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.propertyChangeSupport.addPropertyChangeListener(paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.propertyChangeSupport.removePropertyChangeListener(paramPropertyChangeListener); }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return this.propertyChangeSupport.getPropertyChangeListeners(); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.propertyChangeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.propertyChangeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) { return this.propertyChangeSupport.getPropertyChangeListeners(paramString); }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) { this.propertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2); }
  
  public void applyPropertyChange(PropertyChangeEvent paramPropertyChangeEvent, JLayer<? extends V> paramJLayer) {}
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    JLayer jLayer = (JLayer)paramJComponent;
    return (jLayer.getView() != null) ? jLayer.getView().getBaseline(paramInt1, paramInt2) : super.getBaseline(paramJComponent, paramInt1, paramInt2);
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    JLayer jLayer = (JLayer)paramJComponent;
    return (jLayer.getView() != null) ? jLayer.getView().getBaselineResizeBehavior() : super.getBaselineResizeBehavior(paramJComponent);
  }
  
  public void doLayout(JLayer<? extends V> paramJLayer) {
    Component component = paramJLayer.getView();
    if (component != null)
      component.setBounds(0, 0, paramJLayer.getWidth(), paramJLayer.getHeight()); 
    JPanel jPanel = paramJLayer.getGlassPane();
    if (jPanel != null)
      jPanel.setBounds(0, 0, paramJLayer.getWidth(), paramJLayer.getHeight()); 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    JLayer jLayer = (JLayer)paramJComponent;
    Component component = jLayer.getView();
    return (component != null) ? component.getPreferredSize() : super.getPreferredSize(paramJComponent);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    JLayer jLayer = (JLayer)paramJComponent;
    Component component = jLayer.getView();
    return (component != null) ? component.getMinimumSize() : super.getMinimumSize(paramJComponent);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    JLayer jLayer = (JLayer)paramJComponent;
    Component component = jLayer.getView();
    return (component != null) ? component.getMaximumSize() : super.getMaximumSize(paramJComponent);
  }
  
  public void paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4, JLayer<? extends V> paramJLayer) { paramJLayer.paintImmediately(paramInt1, paramInt2, paramInt3, paramInt4); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\LayerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */