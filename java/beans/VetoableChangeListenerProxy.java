package java.beans;

import java.util.EventListenerProxy;

public class VetoableChangeListenerProxy extends EventListenerProxy<VetoableChangeListener> implements VetoableChangeListener {
  private final String propertyName;
  
  public VetoableChangeListenerProxy(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    super(paramVetoableChangeListener);
    this.propertyName = paramString;
  }
  
  public void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent) throws PropertyVetoException { ((VetoableChangeListener)getListener()).vetoableChange(paramPropertyChangeEvent); }
  
  public String getPropertyName() { return this.propertyName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\VetoableChangeListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */