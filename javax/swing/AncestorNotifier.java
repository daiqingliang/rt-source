package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

class AncestorNotifier implements ComponentListener, PropertyChangeListener, Serializable {
  Component firstInvisibleAncestor;
  
  EventListenerList listenerList = new EventListenerList();
  
  JComponent root;
  
  AncestorNotifier(JComponent paramJComponent) {
    this.root = paramJComponent;
    addListeners(paramJComponent, true);
  }
  
  void addAncestorListener(AncestorListener paramAncestorListener) { this.listenerList.add(AncestorListener.class, paramAncestorListener); }
  
  void removeAncestorListener(AncestorListener paramAncestorListener) { this.listenerList.remove(AncestorListener.class, paramAncestorListener); }
  
  AncestorListener[] getAncestorListeners() { return (AncestorListener[])this.listenerList.getListeners(AncestorListener.class); }
  
  protected void fireAncestorAdded(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AncestorListener.class) {
        AncestorEvent ancestorEvent = new AncestorEvent(paramJComponent, paramInt, paramContainer1, paramContainer2);
        ((AncestorListener)arrayOfObject[i + 1]).ancestorAdded(ancestorEvent);
      } 
    } 
  }
  
  protected void fireAncestorRemoved(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AncestorListener.class) {
        AncestorEvent ancestorEvent = new AncestorEvent(paramJComponent, paramInt, paramContainer1, paramContainer2);
        ((AncestorListener)arrayOfObject[i + 1]).ancestorRemoved(ancestorEvent);
      } 
    } 
  }
  
  protected void fireAncestorMoved(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AncestorListener.class) {
        AncestorEvent ancestorEvent = new AncestorEvent(paramJComponent, paramInt, paramContainer1, paramContainer2);
        ((AncestorListener)arrayOfObject[i + 1]).ancestorMoved(ancestorEvent);
      } 
    } 
  }
  
  void removeAllListeners() { removeListeners(this.root); }
  
  void addListeners(Component paramComponent, boolean paramBoolean) {
    this.firstInvisibleAncestor = null;
    for (Component component = paramComponent; this.firstInvisibleAncestor == null; component = component.getParent()) {
      if (paramBoolean || component != paramComponent) {
        component.addComponentListener(this);
        if (component instanceof JComponent) {
          JComponent jComponent = (JComponent)component;
          jComponent.addPropertyChangeListener(this);
        } 
      } 
      if (!component.isVisible() || component.getParent() == null || component instanceof java.awt.Window)
        this.firstInvisibleAncestor = component; 
    } 
    if (this.firstInvisibleAncestor instanceof java.awt.Window && this.firstInvisibleAncestor.isVisible())
      this.firstInvisibleAncestor = null; 
  }
  
  void removeListeners(Component paramComponent) {
    for (Component component = paramComponent; component != null; component = component.getParent()) {
      component.removeComponentListener(this);
      if (component instanceof JComponent) {
        JComponent jComponent = (JComponent)component;
        jComponent.removePropertyChangeListener(this);
      } 
      if (component == this.firstInvisibleAncestor || component instanceof java.awt.Window)
        break; 
    } 
  }
  
  public void componentResized(ComponentEvent paramComponentEvent) {}
  
  public void componentMoved(ComponentEvent paramComponentEvent) {
    Component component = paramComponentEvent.getComponent();
    fireAncestorMoved(this.root, 3, (Container)component, component.getParent());
  }
  
  public void componentShown(ComponentEvent paramComponentEvent) {
    Component component = paramComponentEvent.getComponent();
    if (component == this.firstInvisibleAncestor) {
      addListeners(component, false);
      if (this.firstInvisibleAncestor == null)
        fireAncestorAdded(this.root, 1, (Container)component, component.getParent()); 
    } 
  }
  
  public void componentHidden(ComponentEvent paramComponentEvent) {
    Component component = paramComponentEvent.getComponent();
    boolean bool = (this.firstInvisibleAncestor == null) ? 1 : 0;
    if (!(component instanceof java.awt.Window))
      removeListeners(component.getParent()); 
    this.firstInvisibleAncestor = component;
    if (bool)
      fireAncestorRemoved(this.root, 2, (Container)component, component.getParent()); 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str != null && (str.equals("parent") || str.equals("ancestor"))) {
      JComponent jComponent = (JComponent)paramPropertyChangeEvent.getSource();
      if (paramPropertyChangeEvent.getNewValue() != null) {
        if (jComponent == this.firstInvisibleAncestor) {
          addListeners(jComponent, false);
          if (this.firstInvisibleAncestor == null)
            fireAncestorAdded(this.root, 1, jComponent, jComponent.getParent()); 
        } 
      } else {
        boolean bool = (this.firstInvisibleAncestor == null) ? 1 : 0;
        Container container = (Container)paramPropertyChangeEvent.getOldValue();
        removeListeners(container);
        this.firstInvisibleAncestor = jComponent;
        if (bool)
          fireAncestorRemoved(this.root, 2, jComponent, container); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\AncestorNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */