package java.beans;

import java.util.EventListenerProxy;

public class PropertyChangeListenerProxy extends EventListenerProxy<PropertyChangeListener> implements PropertyChangeListener {
  private final String propertyName;
  
  public PropertyChangeListenerProxy(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    super(paramPropertyChangeListener);
    this.propertyName = paramString;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { ((PropertyChangeListener)getListener()).propertyChange(paramPropertyChangeEvent); }
  
  public String getPropertyName() { return this.propertyName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PropertyChangeListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */