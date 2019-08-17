package java.beans.beancontext;

import java.util.Iterator;

public class BeanContextServiceAvailableEvent extends BeanContextEvent {
  private static final long serialVersionUID = -5333985775656400778L;
  
  protected Class serviceClass;
  
  public BeanContextServiceAvailableEvent(BeanContextServices paramBeanContextServices, Class paramClass) {
    super(paramBeanContextServices);
    this.serviceClass = paramClass;
  }
  
  public BeanContextServices getSourceAsBeanContextServices() { return (BeanContextServices)getBeanContext(); }
  
  public Class getServiceClass() { return this.serviceClass; }
  
  public Iterator getCurrentServiceSelectors() { return ((BeanContextServices)getSource()).getCurrentServiceSelectors(this.serviceClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextServiceAvailableEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */