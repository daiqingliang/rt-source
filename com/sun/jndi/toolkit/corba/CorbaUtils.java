package com.sun.jndi.toolkit.corba;

import com.sun.jndi.cosnaming.CNCtx;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;

public class CorbaUtils {
  private static Method toStubMethod = null;
  
  private static Method connectMethod = null;
  
  private static Class<?> corbaStubClass = null;
  
  public static Object remoteToCorba(Remote paramRemote, ORB paramORB) throws ClassNotFoundException, ConfigurationException {
    Object object;
    synchronized (CorbaUtils.class) {
      if (toStubMethod == null)
        initMethodHandles(); 
    } 
    try {
      object = toStubMethod.invoke(null, new Object[] { paramRemote });
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      ConfigurationException configurationException = new ConfigurationException("Problem with PortableRemoteObject.toStub(); object not exported or stub not found");
      configurationException.setRootCause(throwable);
      throw configurationException;
    } catch (IllegalAccessException illegalAccessException) {
      ConfigurationException configurationException = new ConfigurationException("Cannot invoke javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
      configurationException.setRootCause(illegalAccessException);
      throw configurationException;
    } 
    if (!corbaStubClass.isInstance(object))
      return null; 
    try {
      connectMethod.invoke(object, new Object[] { paramORB });
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (!(throwable instanceof java.rmi.RemoteException)) {
        ConfigurationException configurationException = new ConfigurationException("Problem invoking javax.rmi.CORBA.Stub.connect()");
        configurationException.setRootCause(throwable);
        throw configurationException;
      } 
    } catch (IllegalAccessException illegalAccessException) {
      ConfigurationException configurationException = new ConfigurationException("Cannot invoke javax.rmi.CORBA.Stub.connect()");
      configurationException.setRootCause(illegalAccessException);
      throw configurationException;
    } 
    return (Object)object;
  }
  
  public static ORB getOrb(String paramString, int paramInt, Hashtable<?, ?> paramHashtable) {
    Properties properties;
    if (paramHashtable != null) {
      if (paramHashtable instanceof Properties) {
        properties = (Properties)paramHashtable.clone();
      } else {
        properties = new Properties();
        Enumeration enumeration = paramHashtable.keys();
        while (enumeration.hasMoreElements()) {
          String str = (String)enumeration.nextElement();
          Object object = paramHashtable.get(str);
          if (object instanceof String)
            properties.put(str, object); 
        } 
      } 
    } else {
      properties = new Properties();
    } 
    if (paramString != null)
      properties.put("org.omg.CORBA.ORBInitialHost", paramString); 
    if (paramInt >= 0)
      properties.put("org.omg.CORBA.ORBInitialPort", "" + paramInt); 
    if (paramHashtable != null) {
      Object object = paramHashtable.get("java.naming.applet");
      if (object != null)
        return initAppletORB(object, properties); 
    } 
    return ORB.init(new String[0], properties);
  }
  
  public static boolean isObjectFactoryTrusted(Object paramObject) throws NamingException {
    Reference reference = null;
    if (paramObject instanceof Reference) {
      reference = (Reference)paramObject;
    } else if (paramObject instanceof Referenceable) {
      reference = ((Referenceable)paramObject).getReference();
    } 
    if (reference != null && reference.getFactoryClassLocation() != null && !CNCtx.trustURLCodebase)
      throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.cosnaming.object.trustURLCodebase' to 'true'."); 
    return true;
  }
  
  private static ORB initAppletORB(Object paramObject, Properties paramProperties) {
    try {
      Class clazz = Class.forName("java.applet.Applet", true, null);
      if (!clazz.isInstance(paramObject))
        throw new ClassCastException(paramObject.getClass().getName()); 
      Method method = ORB.class.getMethod("init", new Class[] { clazz, Properties.class });
      return (ORB)method.invoke(null, new Object[] { paramObject, paramProperties });
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ClassCastException(paramObject.getClass().getName());
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new AssertionError(noSuchMethodException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new AssertionError(invocationTargetException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new AssertionError(illegalAccessException);
    } 
  }
  
  private static void initMethodHandles() {
    corbaStubClass = Class.forName("javax.rmi.CORBA.Stub");
    try {
      connectMethod = corbaStubClass.getMethod("connect", new Class[] { ORB.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new IllegalStateException("No method definition for javax.rmi.CORBA.Stub.connect(org.omg.CORBA.ORB)");
    } 
    Class clazz = Class.forName("javax.rmi.PortableRemoteObject");
    try {
      toStubMethod = clazz.getMethod("toStub", new Class[] { Remote.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new IllegalStateException("No method definition for javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\corba\CorbaUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */