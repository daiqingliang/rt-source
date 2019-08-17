package javax.imageio.spi;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class SubRegistry {
  ServiceRegistry registry;
  
  Class category;
  
  final PartiallyOrderedSet poset = new PartiallyOrderedSet();
  
  final Map<Class<?>, Object> map = new HashMap();
  
  final Map<Class<?>, AccessControlContext> accMap = new HashMap();
  
  public SubRegistry(ServiceRegistry paramServiceRegistry, Class paramClass) {
    this.registry = paramServiceRegistry;
    this.category = paramClass;
  }
  
  public boolean registerServiceProvider(Object paramObject) {
    Object object = this.map.get(paramObject.getClass());
    boolean bool = (object != null) ? 1 : 0;
    if (bool)
      deregisterServiceProvider(object); 
    this.map.put(paramObject.getClass(), paramObject);
    this.accMap.put(paramObject.getClass(), AccessController.getContext());
    this.poset.add(paramObject);
    if (paramObject instanceof RegisterableService) {
      RegisterableService registerableService = (RegisterableService)paramObject;
      registerableService.onRegistration(this.registry, this.category);
    } 
    return !bool;
  }
  
  public boolean deregisterServiceProvider(Object paramObject) {
    Object object = this.map.get(paramObject.getClass());
    if (paramObject == object) {
      this.map.remove(paramObject.getClass());
      this.accMap.remove(paramObject.getClass());
      this.poset.remove(paramObject);
      if (paramObject instanceof RegisterableService) {
        RegisterableService registerableService = (RegisterableService)paramObject;
        registerableService.onDeregistration(this.registry, this.category);
      } 
      return true;
    } 
    return false;
  }
  
  public boolean contains(Object paramObject) {
    Object object = this.map.get(paramObject.getClass());
    return (object == paramObject);
  }
  
  public boolean setOrdering(Object paramObject1, Object paramObject2) { return this.poset.setOrdering(paramObject1, paramObject2); }
  
  public boolean unsetOrdering(Object paramObject1, Object paramObject2) { return this.poset.unsetOrdering(paramObject1, paramObject2); }
  
  public Iterator getServiceProviders(boolean paramBoolean) { return paramBoolean ? this.poset.iterator() : this.map.values().iterator(); }
  
  public <T> T getServiceProviderByClass(Class<T> paramClass) { return (T)this.map.get(paramClass); }
  
  public void clear() {
    Iterator iterator = this.map.values().iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      iterator.remove();
      if (object instanceof RegisterableService) {
        RegisterableService registerableService = (RegisterableService)object;
        AccessControlContext accessControlContext = (AccessControlContext)this.accMap.get(object.getClass());
        if (accessControlContext != null || System.getSecurityManager() == null)
          AccessController.doPrivileged(() -> {
                paramRegisterableService.onDeregistration(this.registry, this.category);
                return null;
              }accessControlContext); 
      } 
    } 
    this.poset.clear();
    this.accMap.clear();
  }
  
  public void finalize() { clear(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\SubRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */