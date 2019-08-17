package java.beans.beancontext;

import java.util.Iterator;
import java.util.TooManyListenersException;

public interface BeanContextServices extends BeanContext, BeanContextServicesListener {
  boolean addService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider);
  
  void revokeService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean);
  
  boolean hasService(Class paramClass);
  
  Object getService(BeanContextChild paramBeanContextChild, Object paramObject1, Class paramClass, Object paramObject2, BeanContextServiceRevokedListener paramBeanContextServiceRevokedListener) throws TooManyListenersException;
  
  void releaseService(BeanContextChild paramBeanContextChild, Object paramObject1, Object paramObject2);
  
  Iterator getCurrentServiceClasses();
  
  Iterator getCurrentServiceSelectors(Class paramClass);
  
  void addBeanContextServicesListener(BeanContextServicesListener paramBeanContextServicesListener);
  
  void removeBeanContextServicesListener(BeanContextServicesListener paramBeanContextServicesListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextServices.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */