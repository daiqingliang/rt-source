package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;

public class MXBeanLookup {
  private static final ThreadLocal<MXBeanLookup> currentLookup = new ThreadLocal();
  
  private final MBeanServerConnection mbsc;
  
  private final WeakIdentityHashMap<Object, ObjectName> mxbeanToObjectName = WeakIdentityHashMap.make();
  
  private final Map<ObjectName, WeakReference<Object>> objectNameToProxy = Util.newMap();
  
  private static final WeakIdentityHashMap<MBeanServerConnection, WeakReference<MXBeanLookup>> mbscToLookup = WeakIdentityHashMap.make();
  
  private MXBeanLookup(MBeanServerConnection paramMBeanServerConnection) { this.mbsc = paramMBeanServerConnection; }
  
  static MXBeanLookup lookupFor(MBeanServerConnection paramMBeanServerConnection) {
    synchronized (mbscToLookup) {
      WeakReference weakReference = (WeakReference)mbscToLookup.get(paramMBeanServerConnection);
      MXBeanLookup mXBeanLookup = (weakReference == null) ? null : (MXBeanLookup)weakReference.get();
      if (mXBeanLookup == null) {
        mXBeanLookup = new MXBeanLookup(paramMBeanServerConnection);
        mbscToLookup.put(paramMBeanServerConnection, new WeakReference(mXBeanLookup));
      } 
      return mXBeanLookup;
    } 
  }
  
  <T> T objectNameToMXBean(ObjectName paramObjectName, Class<T> paramClass) {
    WeakReference weakReference = (WeakReference)this.objectNameToProxy.get(paramObjectName);
    if (weakReference != null) {
      Object object1 = weakReference.get();
      if (paramClass.isInstance(object1))
        return (T)paramClass.cast(object1); 
    } 
    Object object = JMX.newMXBeanProxy(this.mbsc, paramObjectName, paramClass);
    this.objectNameToProxy.put(paramObjectName, new WeakReference(object));
    return (T)object;
  }
  
  ObjectName mxbeanToObjectName(Object paramObject) throws OpenDataException {
    String str1;
    if (paramObject instanceof Proxy) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject);
      if (invocationHandler instanceof MBeanServerInvocationHandler) {
        MBeanServerInvocationHandler mBeanServerInvocationHandler = (MBeanServerInvocationHandler)invocationHandler;
        if (mBeanServerInvocationHandler.getMBeanServerConnection().equals(this.mbsc))
          return mBeanServerInvocationHandler.getObjectName(); 
        str1 = "proxy for a different MBeanServer";
      } else {
        str1 = "not a JMX proxy";
      } 
    } else {
      ObjectName objectName = (ObjectName)this.mxbeanToObjectName.get(paramObject);
      if (objectName != null)
        return objectName; 
      str1 = "not an MXBean registered in this MBeanServer";
    } 
    String str2 = (paramObject == null) ? "null" : ("object of type " + paramObject.getClass().getName());
    throw new OpenDataException("Could not convert " + str2 + " to an ObjectName: " + str1);
  }
  
  void addReference(ObjectName paramObjectName, Object paramObject) throws InstanceAlreadyExistsException {
    ObjectName objectName = (ObjectName)this.mxbeanToObjectName.get(paramObject);
    if (objectName != null) {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("jmx.mxbean.multiname"));
      if (!"true".equalsIgnoreCase(str))
        throw new InstanceAlreadyExistsException("MXBean already registered with name " + objectName); 
    } 
    this.mxbeanToObjectName.put(paramObject, paramObjectName);
  }
  
  boolean removeReference(ObjectName paramObjectName, Object paramObject) {
    if (paramObjectName.equals(this.mxbeanToObjectName.get(paramObject))) {
      this.mxbeanToObjectName.remove(paramObject);
      return true;
    } 
    return false;
  }
  
  static MXBeanLookup getLookup() { return (MXBeanLookup)currentLookup.get(); }
  
  static void setLookup(MXBeanLookup paramMXBeanLookup) { currentLookup.set(paramMXBeanLookup); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MXBeanLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */