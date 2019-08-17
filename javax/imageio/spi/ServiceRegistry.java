package javax.imageio.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class ServiceRegistry {
  private Map categoryMap = new HashMap();
  
  public ServiceRegistry(Iterator<Class<?>> paramIterator) {
    if (paramIterator == null)
      throw new IllegalArgumentException("categories == null!"); 
    while (paramIterator.hasNext()) {
      Class clazz = (Class)paramIterator.next();
      SubRegistry subRegistry = new SubRegistry(this, clazz);
      this.categoryMap.put(clazz, subRegistry);
    } 
  }
  
  public static <T> Iterator<T> lookupProviders(Class<T> paramClass, ClassLoader paramClassLoader) {
    if (paramClass == null)
      throw new IllegalArgumentException("providerClass == null!"); 
    return ServiceLoader.load(paramClass, paramClassLoader).iterator();
  }
  
  public static <T> Iterator<T> lookupProviders(Class<T> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("providerClass == null!"); 
    return ServiceLoader.load(paramClass).iterator();
  }
  
  public Iterator<Class<?>> getCategories() {
    Set set = this.categoryMap.keySet();
    return set.iterator();
  }
  
  private Iterator getSubRegistries(Object paramObject) {
    ArrayList arrayList = new ArrayList();
    for (Class clazz : this.categoryMap.keySet()) {
      if (clazz.isAssignableFrom(paramObject.getClass()))
        arrayList.add((SubRegistry)this.categoryMap.get(clazz)); 
    } 
    return arrayList.iterator();
  }
  
  public <T> boolean registerServiceProvider(T paramT, Class<T> paramClass) {
    if (paramT == null)
      throw new IllegalArgumentException("provider == null!"); 
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    if (!paramClass.isAssignableFrom(paramT.getClass()))
      throw new ClassCastException(); 
    return subRegistry.registerServiceProvider(paramT);
  }
  
  public void registerServiceProvider(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("provider == null!"); 
    Iterator iterator = getSubRegistries(paramObject);
    while (iterator.hasNext()) {
      SubRegistry subRegistry = (SubRegistry)iterator.next();
      subRegistry.registerServiceProvider(paramObject);
    } 
  }
  
  public void registerServiceProviders(Iterator<?> paramIterator) {
    if (paramIterator == null)
      throw new IllegalArgumentException("provider == null!"); 
    while (paramIterator.hasNext())
      registerServiceProvider(paramIterator.next()); 
  }
  
  public <T> boolean deregisterServiceProvider(T paramT, Class<T> paramClass) {
    if (paramT == null)
      throw new IllegalArgumentException("provider == null!"); 
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    if (!paramClass.isAssignableFrom(paramT.getClass()))
      throw new ClassCastException(); 
    return subRegistry.deregisterServiceProvider(paramT);
  }
  
  public void deregisterServiceProvider(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("provider == null!"); 
    Iterator iterator = getSubRegistries(paramObject);
    while (iterator.hasNext()) {
      SubRegistry subRegistry = (SubRegistry)iterator.next();
      subRegistry.deregisterServiceProvider(paramObject);
    } 
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("provider == null!"); 
    Iterator iterator = getSubRegistries(paramObject);
    while (iterator.hasNext()) {
      SubRegistry subRegistry = (SubRegistry)iterator.next();
      if (subRegistry.contains(paramObject))
        return true; 
    } 
    return false;
  }
  
  public <T> Iterator<T> getServiceProviders(Class<T> paramClass, boolean paramBoolean) {
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    return subRegistry.getServiceProviders(paramBoolean);
  }
  
  public <T> Iterator<T> getServiceProviders(Class<T> paramClass, Filter paramFilter, boolean paramBoolean) {
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    Iterator iterator = getServiceProviders(paramClass, paramBoolean);
    return new FilterIterator(iterator, paramFilter);
  }
  
  public <T> T getServiceProviderByClass(Class<T> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("providerClass == null!"); 
    for (Class clazz : this.categoryMap.keySet()) {
      if (clazz.isAssignableFrom(paramClass)) {
        SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(clazz);
        Object object = subRegistry.getServiceProviderByClass(paramClass);
        if (object != null)
          return (T)object; 
      } 
    } 
    return null;
  }
  
  public <T> boolean setOrdering(Class<T> paramClass, T paramT1, T paramT2) {
    if (paramT1 == null || paramT2 == null)
      throw new IllegalArgumentException("provider is null!"); 
    if (paramT1 == paramT2)
      throw new IllegalArgumentException("providers are the same!"); 
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    return (subRegistry.contains(paramT1) && subRegistry.contains(paramT2)) ? subRegistry.setOrdering(paramT1, paramT2) : 0;
  }
  
  public <T> boolean unsetOrdering(Class<T> paramClass, T paramT1, T paramT2) {
    if (paramT1 == null || paramT2 == null)
      throw new IllegalArgumentException("provider is null!"); 
    if (paramT1 == paramT2)
      throw new IllegalArgumentException("providers are the same!"); 
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    return (subRegistry.contains(paramT1) && subRegistry.contains(paramT2)) ? subRegistry.unsetOrdering(paramT1, paramT2) : 0;
  }
  
  public void deregisterAll(Class<?> paramClass) {
    SubRegistry subRegistry = (SubRegistry)this.categoryMap.get(paramClass);
    if (subRegistry == null)
      throw new IllegalArgumentException("category unknown!"); 
    subRegistry.clear();
  }
  
  public void deregisterAll() {
    for (SubRegistry subRegistry : this.categoryMap.values())
      subRegistry.clear(); 
  }
  
  public void finalize() {
    deregisterAll();
    super.finalize();
  }
  
  public static interface Filter {
    boolean filter(Object param1Object);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\ServiceRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */