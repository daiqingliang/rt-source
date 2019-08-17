package java.beans.beancontext;

import java.util.Iterator;

public interface BeanContextServiceProvider {
  Object getService(BeanContextServices paramBeanContextServices, Object paramObject1, Class paramClass, Object paramObject2);
  
  void releaseService(BeanContextServices paramBeanContextServices, Object paramObject1, Object paramObject2);
  
  Iterator getCurrentServiceSelectors(BeanContextServices paramBeanContextServices, Class paramClass);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */