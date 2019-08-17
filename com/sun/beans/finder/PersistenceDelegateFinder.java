package com.sun.beans.finder;

import java.beans.PersistenceDelegate;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceDelegateFinder extends InstanceFinder<PersistenceDelegate> {
  private final Map<Class<?>, PersistenceDelegate> registry = new HashMap();
  
  public PersistenceDelegateFinder() { super(PersistenceDelegate.class, true, "PersistenceDelegate", new String[0]); }
  
  public void register(Class<?> paramClass, PersistenceDelegate paramPersistenceDelegate) {
    synchronized (this.registry) {
      if (paramPersistenceDelegate != null) {
        this.registry.put(paramClass, paramPersistenceDelegate);
      } else {
        this.registry.remove(paramClass);
      } 
    } 
  }
  
  public PersistenceDelegate find(Class<?> paramClass) {
    PersistenceDelegate persistenceDelegate;
    synchronized (this.registry) {
      persistenceDelegate = (PersistenceDelegate)this.registry.get(paramClass);
    } 
    return (persistenceDelegate != null) ? persistenceDelegate : (PersistenceDelegate)super.find(paramClass);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\PersistenceDelegateFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */