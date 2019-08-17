package com.sun.naming.internal;

import java.util.List;
import javax.naming.NamingException;

public final class FactoryEnumeration {
  private List<NamedWeakReference<Object>> factories;
  
  private int posn = 0;
  
  private ClassLoader loader;
  
  FactoryEnumeration(List<NamedWeakReference<Object>> paramList, ClassLoader paramClassLoader) {
    this.factories = paramList;
    this.loader = paramClassLoader;
  }
  
  public Object next() throws NamingException {
    synchronized (this.factories) {
      NamedWeakReference namedWeakReference = (NamedWeakReference)this.factories.get(this.posn++);
      Object object = namedWeakReference.get();
      if (object != null && !(object instanceof Class))
        return object; 
      String str = namedWeakReference.getName();
      try {
        if (object == null) {
          Class clazz = Class.forName(str, true, this.loader);
          object = clazz;
        } 
        object = ((Class)object).newInstance();
        namedWeakReference = new NamedWeakReference(object, str);
        this.factories.set(this.posn - 1, namedWeakReference);
        return object;
      } catch (ClassNotFoundException classNotFoundException) {
        NamingException namingException = new NamingException("No longer able to load " + str);
        namingException.setRootCause(classNotFoundException);
        throw namingException;
      } catch (InstantiationException instantiationException) {
        NamingException namingException = new NamingException("Cannot instantiate " + object);
        namingException.setRootCause(instantiationException);
        throw namingException;
      } catch (IllegalAccessException illegalAccessException) {
        NamingException namingException = new NamingException("Cannot access " + object);
        namingException.setRootCause(illegalAccessException);
        throw namingException;
      } 
    } 
  }
  
  public boolean hasMore() {
    synchronized (this.factories) {
      return (this.posn < this.factories.size());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\naming\internal\FactoryEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */