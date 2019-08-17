package java.beans.beancontext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BeanContextChildSupport implements BeanContextChild, BeanContextServicesListener, Serializable {
  static final long serialVersionUID = 6328947014421475877L;
  
  public BeanContextChild beanContextChildPeer = this;
  
  protected PropertyChangeSupport pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
  
  protected VetoableChangeSupport vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
  
  protected BeanContext beanContext;
  
  protected boolean rejectedSetBCOnce;
  
  public BeanContextChildSupport() {}
  
  public BeanContextChildSupport(BeanContextChild paramBeanContextChild) {}
  
  public void setBeanContext(BeanContext paramBeanContext) throws PropertyVetoException {
    if (paramBeanContext == this.beanContext)
      return; 
    BeanContext beanContext1 = this.beanContext;
    BeanContext beanContext2 = paramBeanContext;
    if (!this.rejectedSetBCOnce) {
      if (this.rejectedSetBCOnce = !validatePendingSetBeanContext(paramBeanContext))
        throw new PropertyVetoException("setBeanContext() change rejected:", new PropertyChangeEvent(this.beanContextChildPeer, "beanContext", beanContext1, beanContext2)); 
      try {
        fireVetoableChange("beanContext", beanContext1, beanContext2);
      } catch (PropertyVetoException propertyVetoException) {
        this.rejectedSetBCOnce = true;
        throw propertyVetoException;
      } 
    } 
    if (this.beanContext != null)
      releaseBeanContextResources(); 
    this.beanContext = beanContext2;
    this.rejectedSetBCOnce = false;
    firePropertyChange("beanContext", beanContext1, beanContext2);
    if (this.beanContext != null)
      initializeBeanContextResources(); 
  }
  
  public BeanContext getBeanContext() { return this.beanContext; }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.pcSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.pcSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) { this.vcSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener); }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) { this.vcSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener); }
  
  public void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent) {}
  
  public void serviceAvailable(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent) {}
  
  public BeanContextChild getBeanContextChildPeer() { return this.beanContextChildPeer; }
  
  public boolean isDelegated() { return !equals(this.beanContextChildPeer); }
  
  public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) { this.pcSupport.firePropertyChange(paramString, paramObject1, paramObject2); }
  
  public void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2) { this.vcSupport.fireVetoableChange(paramString, paramObject1, paramObject2); }
  
  public boolean validatePendingSetBeanContext(BeanContext paramBeanContext) { return true; }
  
  protected void releaseBeanContextResources() {}
  
  protected void initializeBeanContextResources() {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (!equals(this.beanContextChildPeer) && !(this.beanContextChildPeer instanceof Serializable))
      throw new IOException("BeanContextChildSupport beanContextChildPeer not Serializable"); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { paramObjectInputStream.defaultReadObject(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextChildSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */