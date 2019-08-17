package java.beans.beancontext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TooManyListenersException;

public class BeanContextServicesSupport extends BeanContextSupport implements BeanContextServices {
  private static final long serialVersionUID = -8494482757288719206L;
  
  protected HashMap services;
  
  protected int serializable = 0;
  
  protected BCSSProxyServiceProvider proxy;
  
  protected ArrayList bcsListeners;
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices, Locale paramLocale, boolean paramBoolean1, boolean paramBoolean2) { super(paramBeanContextServices, paramLocale, paramBoolean1, paramBoolean2); }
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices, Locale paramLocale, boolean paramBoolean) { this(paramBeanContextServices, paramLocale, paramBoolean, true); }
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices, Locale paramLocale) { this(paramBeanContextServices, paramLocale, false, true); }
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices) { this(paramBeanContextServices, null, false, true); }
  
  public BeanContextServicesSupport() { this(null, null, false, true); }
  
  public void initialize() {
    super.initialize();
    this.services = new HashMap(this.serializable + 1);
    this.bcsListeners = new ArrayList(1);
  }
  
  public BeanContextServices getBeanContextServicesPeer() { return (BeanContextServices)getBeanContextChildPeer(); }
  
  protected BeanContextSupport.BCSChild createBCSChild(Object paramObject1, Object paramObject2) { return new BCSSChild(paramObject1, paramObject2); }
  
  protected BCSSServiceProvider createBCSSServiceProvider(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider) { return new BCSSServiceProvider(paramClass, paramBeanContextServiceProvider); }
  
  public void addBeanContextServicesListener(BeanContextServicesListener paramBeanContextServicesListener) {
    if (paramBeanContextServicesListener == null)
      throw new NullPointerException("bcsl"); 
    synchronized (this.bcsListeners) {
      if (this.bcsListeners.contains(paramBeanContextServicesListener))
        return; 
      this.bcsListeners.add(paramBeanContextServicesListener);
    } 
  }
  
  public void removeBeanContextServicesListener(BeanContextServicesListener paramBeanContextServicesListener) {
    if (paramBeanContextServicesListener == null)
      throw new NullPointerException("bcsl"); 
    synchronized (this.bcsListeners) {
      if (!this.bcsListeners.contains(paramBeanContextServicesListener))
        return; 
      this.bcsListeners.remove(paramBeanContextServicesListener);
    } 
  }
  
  public boolean addService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider) { return addService(paramClass, paramBeanContextServiceProvider, true); }
  
  protected boolean addService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean) {
    if (paramClass == null)
      throw new NullPointerException("serviceClass"); 
    if (paramBeanContextServiceProvider == null)
      throw new NullPointerException("bcsp"); 
    synchronized (BeanContext.globalHierarchyLock) {
      if (this.services.containsKey(paramClass))
        return false; 
      this.services.put(paramClass, createBCSSServiceProvider(paramClass, paramBeanContextServiceProvider));
      if (paramBeanContextServiceProvider instanceof Serializable)
        this.serializable++; 
      if (!paramBoolean)
        return true; 
      BeanContextServiceAvailableEvent beanContextServiceAvailableEvent = new BeanContextServiceAvailableEvent(getBeanContextServicesPeer(), paramClass);
      fireServiceAdded(beanContextServiceAvailableEvent);
      synchronized (this.children) {
        for (Object object : this.children.keySet()) {
          if (object instanceof BeanContextServices)
            ((BeanContextServicesListener)object).serviceAvailable(beanContextServiceAvailableEvent); 
        } 
      } 
      return true;
    } 
  }
  
  public void revokeService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean) {
    if (paramClass == null)
      throw new NullPointerException("serviceClass"); 
    if (paramBeanContextServiceProvider == null)
      throw new NullPointerException("bcsp"); 
    synchronized (BeanContext.globalHierarchyLock) {
      if (!this.services.containsKey(paramClass))
        return; 
      BCSSServiceProvider bCSSServiceProvider = (BCSSServiceProvider)this.services.get(paramClass);
      if (!bCSSServiceProvider.getServiceProvider().equals(paramBeanContextServiceProvider))
        throw new IllegalArgumentException("service provider mismatch"); 
      this.services.remove(paramClass);
      if (paramBeanContextServiceProvider instanceof Serializable)
        this.serializable--; 
      Iterator iterator = bcsChildren();
      while (iterator.hasNext())
        ((BCSSChild)iterator.next()).revokeService(paramClass, false, paramBoolean); 
      fireServiceRevoked(paramClass, paramBoolean);
    } 
  }
  
  public boolean hasService(Class paramClass) {
    if (paramClass == null)
      throw new NullPointerException("serviceClass"); 
    synchronized (BeanContext.globalHierarchyLock) {
      if (this.services.containsKey(paramClass))
        return true; 
      BeanContextServices beanContextServices = null;
      try {
        beanContextServices = (BeanContextServices)getBeanContext();
      } catch (ClassCastException classCastException) {
        return false;
      } 
      return (beanContextServices == null) ? false : beanContextServices.hasService(paramClass);
    } 
  }
  
  public Object getService(BeanContextChild paramBeanContextChild, Object paramObject1, Class paramClass, Object paramObject2, BeanContextServiceRevokedListener paramBeanContextServiceRevokedListener) throws TooManyListenersException {
    if (paramBeanContextChild == null)
      throw new NullPointerException("child"); 
    if (paramClass == null)
      throw new NullPointerException("serviceClass"); 
    if (paramObject1 == null)
      throw new NullPointerException("requestor"); 
    if (paramBeanContextServiceRevokedListener == null)
      throw new NullPointerException("bcsrl"); 
    Object object = null;
    BeanContextServices beanContextServices = getBeanContextServicesPeer();
    synchronized (BeanContext.globalHierarchyLock) {
      BCSSChild bCSSChild;
      synchronized (this.children) {
        bCSSChild = (BCSSChild)this.children.get(paramBeanContextChild);
      } 
      if (bCSSChild == null)
        throw new IllegalArgumentException("not a child of this context"); 
      BCSSServiceProvider bCSSServiceProvider = (BCSSServiceProvider)this.services.get(paramClass);
      if (bCSSServiceProvider != null) {
        BeanContextServiceProvider beanContextServiceProvider = bCSSServiceProvider.getServiceProvider();
        object = beanContextServiceProvider.getService(beanContextServices, paramObject1, paramClass, paramObject2);
        if (object != null) {
          try {
            bCSSChild.usingService(paramObject1, object, paramClass, beanContextServiceProvider, false, paramBeanContextServiceRevokedListener);
          } catch (TooManyListenersException tooManyListenersException) {
            beanContextServiceProvider.releaseService(beanContextServices, paramObject1, object);
            throw tooManyListenersException;
          } catch (UnsupportedOperationException unsupportedOperationException) {
            beanContextServiceProvider.releaseService(beanContextServices, paramObject1, object);
            throw unsupportedOperationException;
          } 
          return object;
        } 
      } 
      if (this.proxy != null) {
        object = this.proxy.getService(beanContextServices, paramObject1, paramClass, paramObject2);
        if (object != null) {
          try {
            bCSSChild.usingService(paramObject1, object, paramClass, this.proxy, true, paramBeanContextServiceRevokedListener);
          } catch (TooManyListenersException tooManyListenersException) {
            this.proxy.releaseService(beanContextServices, paramObject1, object);
            throw tooManyListenersException;
          } catch (UnsupportedOperationException unsupportedOperationException) {
            this.proxy.releaseService(beanContextServices, paramObject1, object);
            throw unsupportedOperationException;
          } 
          return object;
        } 
      } 
    } 
    return null;
  }
  
  public void releaseService(BeanContextChild paramBeanContextChild, Object paramObject1, Object paramObject2) {
    if (paramBeanContextChild == null)
      throw new NullPointerException("child"); 
    if (paramObject1 == null)
      throw new NullPointerException("requestor"); 
    if (paramObject2 == null)
      throw new NullPointerException("service"); 
    synchronized (BeanContext.globalHierarchyLock) {
      BCSSChild bCSSChild;
      synchronized (this.children) {
        bCSSChild = (BCSSChild)this.children.get(paramBeanContextChild);
      } 
      if (bCSSChild != null) {
        bCSSChild.releaseService(paramObject1, paramObject2);
      } else {
        throw new IllegalArgumentException("child actual is not a child of this BeanContext");
      } 
    } 
  }
  
  public Iterator getCurrentServiceClasses() { return new BeanContextSupport.BCSIterator(this.services.keySet().iterator()); }
  
  public Iterator getCurrentServiceSelectors(Class paramClass) {
    BCSSServiceProvider bCSSServiceProvider = (BCSSServiceProvider)this.services.get(paramClass);
    return (bCSSServiceProvider != null) ? new BeanContextSupport.BCSIterator(bCSSServiceProvider.getServiceProvider().getCurrentServiceSelectors(getBeanContextServicesPeer(), paramClass)) : null;
  }
  
  public void serviceAvailable(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent) {
    synchronized (BeanContext.globalHierarchyLock) {
      Iterator iterator;
      if (this.services.containsKey(paramBeanContextServiceAvailableEvent.getServiceClass()))
        return; 
      fireServiceAdded(paramBeanContextServiceAvailableEvent);
      synchronized (this.children) {
        iterator = this.children.keySet().iterator();
      } 
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof BeanContextServices)
          ((BeanContextServicesListener)object).serviceAvailable(paramBeanContextServiceAvailableEvent); 
      } 
    } 
  }
  
  public void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent) {
    synchronized (BeanContext.globalHierarchyLock) {
      Iterator iterator;
      if (this.services.containsKey(paramBeanContextServiceRevokedEvent.getServiceClass()))
        return; 
      fireServiceRevoked(paramBeanContextServiceRevokedEvent);
      synchronized (this.children) {
        iterator = this.children.keySet().iterator();
      } 
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof BeanContextServices)
          ((BeanContextServicesListener)object).serviceRevoked(paramBeanContextServiceRevokedEvent); 
      } 
    } 
  }
  
  protected static final BeanContextServicesListener getChildBeanContextServicesListener(Object paramObject) {
    try {
      return (BeanContextServicesListener)paramObject;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  protected void childJustRemovedHook(Object paramObject, BeanContextSupport.BCSChild paramBCSChild) {
    BCSSChild bCSSChild = (BCSSChild)paramBCSChild;
    bCSSChild.cleanupReferences();
  }
  
  protected void releaseBeanContextResources() {
    Object[] arrayOfObject;
    super.releaseBeanContextResources();
    synchronized (this.children) {
      if (this.children.isEmpty())
        return; 
      arrayOfObject = this.children.values().toArray();
    } 
    for (byte b = 0; b < arrayOfObject.length; b++)
      ((BCSSChild)arrayOfObject[b]).revokeAllDelegatedServicesNow(); 
    this.proxy = null;
  }
  
  protected void initializeBeanContextResources() {
    super.initializeBeanContextResources();
    BeanContext beanContext = getBeanContext();
    if (beanContext == null)
      return; 
    try {
      BeanContextServices beanContextServices = (BeanContextServices)beanContext;
      this.proxy = new BCSSProxyServiceProvider(beanContextServices);
    } catch (ClassCastException classCastException) {}
  }
  
  protected final void fireServiceAdded(Class paramClass) {
    BeanContextServiceAvailableEvent beanContextServiceAvailableEvent = new BeanContextServiceAvailableEvent(getBeanContextServicesPeer(), paramClass);
    fireServiceAdded(beanContextServiceAvailableEvent);
  }
  
  protected final void fireServiceAdded(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent) {
    Object[] arrayOfObject;
    synchronized (this.bcsListeners) {
      arrayOfObject = this.bcsListeners.toArray();
    } 
    for (byte b = 0; b < arrayOfObject.length; b++)
      ((BeanContextServicesListener)arrayOfObject[b]).serviceAvailable(paramBeanContextServiceAvailableEvent); 
  }
  
  protected final void fireServiceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent) {
    Object[] arrayOfObject;
    synchronized (this.bcsListeners) {
      arrayOfObject = this.bcsListeners.toArray();
    } 
    for (byte b = 0; b < arrayOfObject.length; b++)
      ((BeanContextServiceRevokedListener)arrayOfObject[b]).serviceRevoked(paramBeanContextServiceRevokedEvent); 
  }
  
  protected final void fireServiceRevoked(Class paramClass, boolean paramBoolean) {
    Object[] arrayOfObject;
    BeanContextServiceRevokedEvent beanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(getBeanContextServicesPeer(), paramClass, paramBoolean);
    synchronized (this.bcsListeners) {
      arrayOfObject = this.bcsListeners.toArray();
    } 
    for (byte b = 0; b < arrayOfObject.length; b++)
      ((BeanContextServicesListener)arrayOfObject[b]).serviceRevoked(beanContextServiceRevokedEvent); 
  }
  
  protected void bcsPreSerializationHook(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeInt(this.serializable);
    if (this.serializable <= 0)
      return; 
    byte b = 0;
    Iterator iterator = this.services.entrySet().iterator();
    while (iterator.hasNext() && b < this.serializable) {
      Map.Entry entry = (Map.Entry)iterator.next();
      BCSSServiceProvider bCSSServiceProvider = null;
      try {
        bCSSServiceProvider = (BCSSServiceProvider)entry.getValue();
      } catch (ClassCastException classCastException) {
        continue;
      } 
      if (bCSSServiceProvider.getServiceProvider() instanceof Serializable) {
        paramObjectOutputStream.writeObject(entry.getKey());
        paramObjectOutputStream.writeObject(bCSSServiceProvider);
        b++;
      } 
    } 
    if (b != this.serializable)
      throw new IOException("wrote different number of service providers than expected"); 
  }
  
  protected void bcsPreDeserializationHook(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.serializable = paramObjectInputStream.readInt();
    for (int i = this.serializable; i > 0; i--)
      this.services.put(paramObjectInputStream.readObject(), paramObjectInputStream.readObject()); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    serialize(paramObjectOutputStream, this.bcsListeners);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    deserialize(paramObjectInputStream, this.bcsListeners);
  }
  
  protected class BCSSChild extends BeanContextSupport.BCSChild {
    private static final long serialVersionUID = -3263851306889194873L;
    
    private HashMap serviceClasses;
    
    private HashMap serviceRequestors;
    
    BCSSChild(Object param1Object1, Object param1Object2) { super(BeanContextServicesSupport.this, param1Object1, param1Object2); }
    
    void usingService(Object param1Object1, Object param1Object2, Class param1Class, BeanContextServiceProvider param1BeanContextServiceProvider, boolean param1Boolean, BeanContextServiceRevokedListener param1BeanContextServiceRevokedListener) throws TooManyListenersException, UnsupportedOperationException {
      BCSSCServiceClassRef bCSSCServiceClassRef = null;
      if (this.serviceClasses == null) {
        this.serviceClasses = new HashMap(1);
      } else {
        bCSSCServiceClassRef = (BCSSCServiceClassRef)this.serviceClasses.get(param1Class);
      } 
      if (bCSSCServiceClassRef == null) {
        bCSSCServiceClassRef = new BCSSCServiceClassRef(param1Class, param1BeanContextServiceProvider, param1Boolean);
        this.serviceClasses.put(param1Class, bCSSCServiceClassRef);
      } else {
        bCSSCServiceClassRef.verifyAndMaybeSetProvider(param1BeanContextServiceProvider, param1Boolean);
        bCSSCServiceClassRef.verifyRequestor(param1Object1, param1BeanContextServiceRevokedListener);
      } 
      bCSSCServiceClassRef.addRequestor(param1Object1, param1BeanContextServiceRevokedListener);
      bCSSCServiceClassRef.addRef(param1Boolean);
      BCSSCServiceRef bCSSCServiceRef = null;
      Map map = null;
      if (this.serviceRequestors == null) {
        this.serviceRequestors = new HashMap(1);
      } else {
        map = (Map)this.serviceRequestors.get(param1Object1);
      } 
      if (map == null) {
        map = new HashMap(1);
        this.serviceRequestors.put(param1Object1, map);
      } else {
        bCSSCServiceRef = (BCSSCServiceRef)map.get(param1Object2);
      } 
      if (bCSSCServiceRef == null) {
        bCSSCServiceRef = new BCSSCServiceRef(bCSSCServiceClassRef, param1Boolean);
        map.put(param1Object2, bCSSCServiceRef);
      } else {
        bCSSCServiceRef.addRef();
      } 
    }
    
    void releaseService(Object param1Object1, Object param1Object2) {
      if (this.serviceRequestors == null)
        return; 
      Map map = (Map)this.serviceRequestors.get(param1Object1);
      if (map == null)
        return; 
      BCSSCServiceRef bCSSCServiceRef = (BCSSCServiceRef)map.get(param1Object2);
      if (bCSSCServiceRef == null)
        return; 
      BCSSCServiceClassRef bCSSCServiceClassRef = bCSSCServiceRef.getServiceClassRef();
      boolean bool = bCSSCServiceRef.isDelegated();
      BeanContextServiceProvider beanContextServiceProvider = bool ? bCSSCServiceClassRef.getDelegateProvider() : bCSSCServiceClassRef.getServiceProvider();
      beanContextServiceProvider.releaseService(BeanContextServicesSupport.this.getBeanContextServicesPeer(), param1Object1, param1Object2);
      bCSSCServiceClassRef.releaseRef(bool);
      bCSSCServiceClassRef.removeRequestor(param1Object1);
      if (bCSSCServiceRef.release() == 0) {
        map.remove(param1Object2);
        if (map.isEmpty()) {
          this.serviceRequestors.remove(param1Object1);
          bCSSCServiceClassRef.removeRequestor(param1Object1);
        } 
        if (this.serviceRequestors.isEmpty())
          this.serviceRequestors = null; 
        if (bCSSCServiceClassRef.isEmpty())
          this.serviceClasses.remove(bCSSCServiceClassRef.getServiceClass()); 
        if (this.serviceClasses.isEmpty())
          this.serviceClasses = null; 
      } 
    }
    
    void revokeService(Class param1Class, boolean param1Boolean1, boolean param1Boolean2) {
      if (this.serviceClasses == null)
        return; 
      BCSSCServiceClassRef bCSSCServiceClassRef = (BCSSCServiceClassRef)this.serviceClasses.get(param1Class);
      if (bCSSCServiceClassRef == null)
        return; 
      Iterator iterator = bCSSCServiceClassRef.cloneOfEntries();
      BeanContextServiceRevokedEvent beanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(BeanContextServicesSupport.this.getBeanContextServicesPeer(), param1Class, param1Boolean2);
      boolean bool = false;
      while (iterator.hasNext() && this.serviceRequestors != null) {
        Map.Entry entry = (Map.Entry)iterator.next();
        BeanContextServiceRevokedListener beanContextServiceRevokedListener = (BeanContextServiceRevokedListener)entry.getValue();
        if (param1Boolean2) {
          Object object = entry.getKey();
          Map map = (Map)this.serviceRequestors.get(object);
          if (map != null) {
            Iterator iterator1 = map.entrySet().iterator();
            while (iterator1.hasNext()) {
              Map.Entry entry1 = (Map.Entry)iterator1.next();
              BCSSCServiceRef bCSSCServiceRef = (BCSSCServiceRef)entry1.getValue();
              if (bCSSCServiceRef.getServiceClassRef().equals(bCSSCServiceClassRef) && param1Boolean1 == bCSSCServiceRef.isDelegated())
                iterator1.remove(); 
            } 
            if (bool = map.isEmpty())
              this.serviceRequestors.remove(object); 
          } 
          if (bool)
            bCSSCServiceClassRef.removeRequestor(object); 
        } 
        beanContextServiceRevokedListener.serviceRevoked(beanContextServiceRevokedEvent);
      } 
      if (param1Boolean2 && this.serviceClasses != null) {
        if (bCSSCServiceClassRef.isEmpty())
          this.serviceClasses.remove(param1Class); 
        if (this.serviceClasses.isEmpty())
          this.serviceClasses = null; 
      } 
      if (this.serviceRequestors != null && this.serviceRequestors.isEmpty())
        this.serviceRequestors = null; 
    }
    
    void cleanupReferences() {
      if (this.serviceRequestors == null)
        return; 
      Iterator iterator = this.serviceRequestors.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        Object object = entry.getKey();
        Iterator iterator1 = ((Map)entry.getValue()).entrySet().iterator();
        iterator.remove();
        while (iterator1.hasNext()) {
          Map.Entry entry1 = (Map.Entry)iterator1.next();
          Object object1 = entry1.getKey();
          BCSSCServiceRef bCSSCServiceRef = (BCSSCServiceRef)entry1.getValue();
          BCSSCServiceClassRef bCSSCServiceClassRef = bCSSCServiceRef.getServiceClassRef();
          BeanContextServiceProvider beanContextServiceProvider = bCSSCServiceRef.isDelegated() ? bCSSCServiceClassRef.getDelegateProvider() : bCSSCServiceClassRef.getServiceProvider();
          bCSSCServiceClassRef.removeRequestor(object);
          iterator1.remove();
          while (bCSSCServiceRef.release() >= 0)
            beanContextServiceProvider.releaseService(BeanContextServicesSupport.this.getBeanContextServicesPeer(), object, object1); 
        } 
      } 
      this.serviceRequestors = null;
      this.serviceClasses = null;
    }
    
    void revokeAllDelegatedServicesNow() {
      if (this.serviceClasses == null)
        return; 
      for (BCSSCServiceClassRef bCSSCServiceClassRef : new HashSet(this.serviceClasses.values())) {
        if (!bCSSCServiceClassRef.isDelegated())
          continue; 
        Iterator iterator = bCSSCServiceClassRef.cloneOfEntries();
        BeanContextServiceRevokedEvent beanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(BeanContextServicesSupport.this.getBeanContextServicesPeer(), bCSSCServiceClassRef.getServiceClass(), true);
        boolean bool = false;
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry)iterator.next();
          BeanContextServiceRevokedListener beanContextServiceRevokedListener = (BeanContextServiceRevokedListener)entry.getValue();
          Object object = entry.getKey();
          Map map = (Map)this.serviceRequestors.get(object);
          if (map != null) {
            Iterator iterator1 = map.entrySet().iterator();
            while (iterator1.hasNext()) {
              Map.Entry entry1 = (Map.Entry)iterator1.next();
              BCSSCServiceRef bCSSCServiceRef = (BCSSCServiceRef)entry1.getValue();
              if (bCSSCServiceRef.getServiceClassRef().equals(bCSSCServiceClassRef) && bCSSCServiceRef.isDelegated())
                iterator1.remove(); 
            } 
            if (bool = map.isEmpty())
              this.serviceRequestors.remove(object); 
          } 
          if (bool)
            bCSSCServiceClassRef.removeRequestor(object); 
          beanContextServiceRevokedListener.serviceRevoked(beanContextServiceRevokedEvent);
          if (bCSSCServiceClassRef.isEmpty())
            this.serviceClasses.remove(bCSSCServiceClassRef.getServiceClass()); 
        } 
      } 
      if (this.serviceClasses.isEmpty())
        this.serviceClasses = null; 
      if (this.serviceRequestors != null && this.serviceRequestors.isEmpty())
        this.serviceRequestors = null; 
    }
    
    class BCSSCServiceClassRef {
      Class serviceClass;
      
      BeanContextServiceProvider serviceProvider;
      
      int serviceRefs;
      
      BeanContextServiceProvider delegateProvider;
      
      int delegateRefs;
      
      HashMap requestors = new HashMap(1);
      
      BCSSCServiceClassRef(Class param2Class, BeanContextServiceProvider param2BeanContextServiceProvider, boolean param2Boolean) {
        this.serviceClass = param2Class;
        if (param2Boolean) {
          this.delegateProvider = param2BeanContextServiceProvider;
        } else {
          this.serviceProvider = param2BeanContextServiceProvider;
        } 
      }
      
      void addRequestor(Object param2Object, BeanContextServiceRevokedListener param2BeanContextServiceRevokedListener) throws TooManyListenersException {
        BeanContextServiceRevokedListener beanContextServiceRevokedListener = (BeanContextServiceRevokedListener)this.requestors.get(param2Object);
        if (beanContextServiceRevokedListener != null && !beanContextServiceRevokedListener.equals(param2BeanContextServiceRevokedListener))
          throw new TooManyListenersException(); 
        this.requestors.put(param2Object, param2BeanContextServiceRevokedListener);
      }
      
      void removeRequestor(Object param2Object) { this.requestors.remove(param2Object); }
      
      void verifyRequestor(Object param2Object, BeanContextServiceRevokedListener param2BeanContextServiceRevokedListener) throws TooManyListenersException {
        BeanContextServiceRevokedListener beanContextServiceRevokedListener = (BeanContextServiceRevokedListener)this.requestors.get(param2Object);
        if (beanContextServiceRevokedListener != null && !beanContextServiceRevokedListener.equals(param2BeanContextServiceRevokedListener))
          throw new TooManyListenersException(); 
      }
      
      void verifyAndMaybeSetProvider(BeanContextServiceProvider param2BeanContextServiceProvider, boolean param2Boolean) {
        BeanContextServiceProvider beanContextServiceProvider;
        if (param2Boolean) {
          beanContextServiceProvider = this.delegateProvider;
          if (beanContextServiceProvider == null || param2BeanContextServiceProvider == null) {
            this.delegateProvider = param2BeanContextServiceProvider;
            return;
          } 
        } else {
          beanContextServiceProvider = this.serviceProvider;
          if (beanContextServiceProvider == null || param2BeanContextServiceProvider == null) {
            this.serviceProvider = param2BeanContextServiceProvider;
            return;
          } 
        } 
        if (!beanContextServiceProvider.equals(param2BeanContextServiceProvider))
          throw new UnsupportedOperationException("existing service reference obtained from different BeanContextServiceProvider not supported"); 
      }
      
      Iterator cloneOfEntries() { return ((HashMap)this.requestors.clone()).entrySet().iterator(); }
      
      Iterator entries() { return this.requestors.entrySet().iterator(); }
      
      boolean isEmpty() { return this.requestors.isEmpty(); }
      
      Class getServiceClass() { return this.serviceClass; }
      
      BeanContextServiceProvider getServiceProvider() { return this.serviceProvider; }
      
      BeanContextServiceProvider getDelegateProvider() { return this.delegateProvider; }
      
      boolean isDelegated() { return (this.delegateProvider != null); }
      
      void addRef(boolean param2Boolean) {
        if (param2Boolean) {
          this.delegateRefs++;
        } else {
          this.serviceRefs++;
        } 
      }
      
      void releaseRef(boolean param2Boolean) {
        if (param2Boolean) {
          if (--this.delegateRefs == 0)
            this.delegateProvider = null; 
        } else if (--this.serviceRefs <= 0) {
          this.serviceProvider = null;
        } 
      }
      
      int getRefs() { return this.serviceRefs + this.delegateRefs; }
      
      int getDelegateRefs() { return this.delegateRefs; }
      
      int getServiceRefs() { return this.serviceRefs; }
    }
    
    class BCSSCServiceRef {
      BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef serviceClassRef;
      
      int refCnt = 1;
      
      boolean delegated = false;
      
      BCSSCServiceRef(BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef param2BCSSCServiceClassRef, boolean param2Boolean) {
        this.serviceClassRef = param2BCSSCServiceClassRef;
        this.delegated = param2Boolean;
      }
      
      void addRef() { this.refCnt++; }
      
      int release() { return --this.refCnt; }
      
      BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef getServiceClassRef() { return this.serviceClassRef; }
      
      boolean isDelegated() { return this.delegated; }
    }
  }
  
  protected class BCSSProxyServiceProvider implements BeanContextServiceProvider, BeanContextServiceRevokedListener {
    private BeanContextServices nestingCtxt;
    
    BCSSProxyServiceProvider(BeanContextServices param1BeanContextServices) { this.nestingCtxt = param1BeanContextServices; }
    
    public Object getService(BeanContextServices param1BeanContextServices, Object param1Object1, Class param1Class, Object param1Object2) {
      Object object = null;
      try {
        object = this.nestingCtxt.getService(param1BeanContextServices, param1Object1, param1Class, param1Object2, this);
      } catch (TooManyListenersException tooManyListenersException) {
        return null;
      } 
      return object;
    }
    
    public void releaseService(BeanContextServices param1BeanContextServices, Object param1Object1, Object param1Object2) { this.nestingCtxt.releaseService(param1BeanContextServices, param1Object1, param1Object2); }
    
    public Iterator getCurrentServiceSelectors(BeanContextServices param1BeanContextServices, Class param1Class) { return this.nestingCtxt.getCurrentServiceSelectors(param1Class); }
    
    public void serviceRevoked(BeanContextServiceRevokedEvent param1BeanContextServiceRevokedEvent) {
      Iterator iterator = BeanContextServicesSupport.this.bcsChildren();
      while (iterator.hasNext())
        ((BeanContextServicesSupport.BCSSChild)iterator.next()).revokeService(param1BeanContextServiceRevokedEvent.getServiceClass(), true, param1BeanContextServiceRevokedEvent.isCurrentServiceInvalidNow()); 
    }
  }
  
  protected static class BCSSServiceProvider implements Serializable {
    private static final long serialVersionUID = 861278251667444782L;
    
    protected BeanContextServiceProvider serviceProvider;
    
    BCSSServiceProvider(Class param1Class, BeanContextServiceProvider param1BeanContextServiceProvider) { this.serviceProvider = param1BeanContextServiceProvider; }
    
    protected BeanContextServiceProvider getServiceProvider() { return this.serviceProvider; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextServicesSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */