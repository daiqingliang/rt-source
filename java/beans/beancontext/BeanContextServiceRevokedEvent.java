package java.beans.beancontext;

public class BeanContextServiceRevokedEvent extends BeanContextEvent {
  private static final long serialVersionUID = -1295543154724961754L;
  
  protected Class serviceClass;
  
  private boolean invalidateRefs;
  
  public BeanContextServiceRevokedEvent(BeanContextServices paramBeanContextServices, Class paramClass, boolean paramBoolean) {
    super(paramBeanContextServices);
    this.serviceClass = paramClass;
    this.invalidateRefs = paramBoolean;
  }
  
  public BeanContextServices getSourceAsBeanContextServices() { return (BeanContextServices)getBeanContext(); }
  
  public Class getServiceClass() { return this.serviceClass; }
  
  public boolean isServiceClass(Class paramClass) { return this.serviceClass.equals(paramClass); }
  
  public boolean isCurrentServiceInvalidNow() { return this.invalidateRefs; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextServiceRevokedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */