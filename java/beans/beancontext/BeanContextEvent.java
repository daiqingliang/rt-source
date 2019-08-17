package java.beans.beancontext;

import java.util.EventObject;

public abstract class BeanContextEvent extends EventObject {
  private static final long serialVersionUID = 7267998073569045052L;
  
  protected BeanContext propagatedFrom;
  
  protected BeanContextEvent(BeanContext paramBeanContext) { super(paramBeanContext); }
  
  public BeanContext getBeanContext() { return (BeanContext)getSource(); }
  
  public void setPropagatedFrom(BeanContext paramBeanContext) { this.propagatedFrom = paramBeanContext; }
  
  public BeanContext getPropagatedFrom() { return this.propagatedFrom; }
  
  public boolean isPropagated() { return (this.propagatedFrom != null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */