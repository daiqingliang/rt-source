package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;

public final class JmxMBeanServer implements SunJmxMBeanServer {
  public static final boolean DEFAULT_FAIR_LOCK_POLICY = true;
  
  private final MBeanInstantiator instantiator;
  
  private final SecureClassLoaderRepository secureClr;
  
  private final boolean interceptorsEnabled;
  
  private final MBeanServer outerShell;
  
  private final MBeanServerDelegate mBeanServerDelegateObject;
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate) { this(paramString, paramMBeanServer, paramMBeanServerDelegate, null, false); }
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, boolean paramBoolean) { this(paramString, paramMBeanServer, paramMBeanServerDelegate, null, false); }
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, boolean paramBoolean) { this(paramString, paramMBeanServer, paramMBeanServerDelegate, paramMBeanInstantiator, paramBoolean, true); }
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramMBeanInstantiator == null) {
      ClassLoaderRepositorySupport classLoaderRepositorySupport = new ClassLoaderRepositorySupport();
      paramMBeanInstantiator = new MBeanInstantiator(classLoaderRepositorySupport);
    } 
    MBeanInstantiator mBeanInstantiator = paramMBeanInstantiator;
    this.secureClr = new SecureClassLoaderRepository((ClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ClassLoaderRepository>(this, mBeanInstantiator) {
            public ClassLoaderRepository run() { return fInstantiator.getClassLoaderRepository(); }
          }));
    if (paramMBeanServerDelegate == null)
      paramMBeanServerDelegate = new MBeanServerDelegateImpl(); 
    if (paramMBeanServer == null)
      paramMBeanServer = this; 
    this.instantiator = paramMBeanInstantiator;
    this.mBeanServerDelegateObject = paramMBeanServerDelegate;
    this.outerShell = paramMBeanServer;
    Repository repository = new Repository(paramString);
    this.mbsInterceptor = new DefaultMBeanServerInterceptor(paramMBeanServer, paramMBeanServerDelegate, paramMBeanInstantiator, repository);
    this.interceptorsEnabled = paramBoolean1;
    initialize();
  }
  
  public boolean interceptorsEnabled() { return this.interceptorsEnabled; }
  
  public MBeanInstantiator getMBeanInstantiator() {
    if (this.interceptorsEnabled)
      return this.instantiator; 
    throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException { return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName), (Object[])null, (String[])null); }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException { return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName1), paramObjectName2, (Object[])null, (String[])null); }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException { return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName), paramArrayOfObject, paramArrayOfString); }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException { return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName1), paramObjectName2, paramArrayOfObject, paramArrayOfString); }
  
  public ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException { return this.mbsInterceptor.registerMBean(paramObject, cloneObjectName(paramObjectName)); }
  
  public void unregisterMBean(ObjectName paramObjectName) throws InstanceNotFoundException, MBeanRegistrationException { this.mbsInterceptor.unregisterMBean(cloneObjectName(paramObjectName)); }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName) throws InstanceNotFoundException { return this.mbsInterceptor.getObjectInstance(cloneObjectName(paramObjectName)); }
  
  public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp) { return this.mbsInterceptor.queryMBeans(cloneObjectName(paramObjectName), paramQueryExp); }
  
  public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp) { return this.mbsInterceptor.queryNames(cloneObjectName(paramObjectName), paramQueryExp); }
  
  public boolean isRegistered(ObjectName paramObjectName) { return this.mbsInterceptor.isRegistered(paramObjectName); }
  
  public Integer getMBeanCount() { return this.mbsInterceptor.getMBeanCount(); }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException { return this.mbsInterceptor.getAttribute(cloneObjectName(paramObjectName), paramString); }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString) throws InstanceNotFoundException, ReflectionException { return this.mbsInterceptor.getAttributes(cloneObjectName(paramObjectName), paramArrayOfString); }
  
  public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException { this.mbsInterceptor.setAttribute(cloneObjectName(paramObjectName), cloneAttribute(paramAttribute)); }
  
  public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList) throws InstanceNotFoundException, ReflectionException { return this.mbsInterceptor.setAttributes(cloneObjectName(paramObjectName), cloneAttributeList(paramAttributeList)); }
  
  public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws InstanceNotFoundException, MBeanException, ReflectionException { return this.mbsInterceptor.invoke(cloneObjectName(paramObjectName), paramString, paramArrayOfObject, paramArrayOfString); }
  
  public String getDefaultDomain() { return this.mbsInterceptor.getDefaultDomain(); }
  
  public String[] getDomains() { return this.mbsInterceptor.getDomains(); }
  
  public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException { this.mbsInterceptor.addNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener, paramNotificationFilter, paramObject); }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException { this.mbsInterceptor.addNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2, paramNotificationFilter, paramObject); }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener) throws InstanceNotFoundException, ListenerNotFoundException { this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener); }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException { this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener, paramNotificationFilter, paramObject); }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2) throws InstanceNotFoundException, ListenerNotFoundException { this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2); }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException { this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2, paramNotificationFilter, paramObject); }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException { return this.mbsInterceptor.getMBeanInfo(cloneObjectName(paramObjectName)); }
  
  public Object instantiate(String paramString) throws ReflectionException, MBeanException {
    checkMBeanPermission(paramString, null, null, "instantiate");
    return this.instantiator.instantiate(paramString);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName) throws ReflectionException, MBeanException, InstanceNotFoundException {
    checkMBeanPermission(paramString, null, null, "instantiate");
    ClassLoader classLoader = this.outerShell.getClass().getClassLoader();
    return this.instantiator.instantiate(paramString, paramObjectName, classLoader);
  }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException {
    checkMBeanPermission(paramString, null, null, "instantiate");
    ClassLoader classLoader = this.outerShell.getClass().getClassLoader();
    return this.instantiator.instantiate(paramString, paramArrayOfObject, paramArrayOfString, classLoader);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException, InstanceNotFoundException {
    checkMBeanPermission(paramString, null, null, "instantiate");
    ClassLoader classLoader = this.outerShell.getClass().getClassLoader();
    return this.instantiator.instantiate(paramString, paramObjectName, paramArrayOfObject, paramArrayOfString, classLoader);
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException { return this.mbsInterceptor.isInstanceOf(cloneObjectName(paramObjectName), paramString); }
  
  @Deprecated
  public ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte) throws InstanceNotFoundException, OperationsException {
    ClassLoader classLoader = getClassLoaderFor(paramObjectName);
    return this.instantiator.deserialize(classLoader, paramArrayOfByte);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte) throws OperationsException, ReflectionException {
    Class clazz;
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter"); 
    ClassLoaderRepository classLoaderRepository = getClassLoaderRepository();
    try {
      if (classLoaderRepository == null)
        throw new ClassNotFoundException(paramString); 
      clazz = classLoaderRepository.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ReflectionException(classNotFoundException, "The given class could not be loaded by the default loader repository");
    } 
    return this.instantiator.deserialize(clazz.getClassLoader(), paramArrayOfByte);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte) throws InstanceNotFoundException, OperationsException, ReflectionException {
    paramObjectName = cloneObjectName(paramObjectName);
    try {
      getClassLoader(paramObjectName);
    } catch (SecurityException securityException) {
      throw securityException;
    } catch (Exception exception) {}
    ClassLoader classLoader = this.outerShell.getClass().getClassLoader();
    return this.instantiator.deserialize(paramString, paramObjectName, paramArrayOfByte, classLoader);
  }
  
  private void initialize() {
    if (this.instantiator == null)
      throw new IllegalStateException("instantiator must not be null."); 
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() {
              JmxMBeanServer.this.mbsInterceptor.registerMBean(JmxMBeanServer.this.mBeanServerDelegateObject, MBeanServerDelegate.DELEGATE_NAME);
              return null;
            }
          });
    } catch (SecurityException securityException) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected security exception occurred", securityException); 
      throw securityException;
    } catch (Exception exception) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected exception occurred", exception); 
      throw new IllegalStateException("Can't register delegate.", exception);
    } 
    ClassLoader classLoader = this.outerShell.getClass().getClassLoader();
    ModifiableClassLoaderRepository modifiableClassLoaderRepository = (ModifiableClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ModifiableClassLoaderRepository>() {
          public ModifiableClassLoaderRepository run() { return JmxMBeanServer.this.instantiator.getClassLoaderRepository(); }
        });
    if (modifiableClassLoaderRepository != null) {
      modifiableClassLoaderRepository.addClassLoader(classLoader);
      ClassLoader classLoader1 = ClassLoader.getSystemClassLoader();
      if (classLoader1 != classLoader)
        modifiableClassLoaderRepository.addClassLoader(classLoader1); 
    } 
  }
  
  public MBeanServer getMBeanServerInterceptor() {
    if (this.interceptorsEnabled)
      return this.mbsInterceptor; 
    throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
  }
  
  public void setMBeanServerInterceptor(MBeanServer paramMBeanServer) {
    if (!this.interceptorsEnabled)
      throw new UnsupportedOperationException("MBeanServerInterceptors are disabled."); 
    if (paramMBeanServer == null)
      throw new IllegalArgumentException("MBeanServerInterceptor is null"); 
    this.mbsInterceptor = paramMBeanServer;
  }
  
  public ClassLoader getClassLoaderFor(ObjectName paramObjectName) throws InstanceNotFoundException { return this.mbsInterceptor.getClassLoaderFor(cloneObjectName(paramObjectName)); }
  
  public ClassLoader getClassLoader(ObjectName paramObjectName) throws InstanceNotFoundException { return this.mbsInterceptor.getClassLoader(cloneObjectName(paramObjectName)); }
  
  public ClassLoaderRepository getClassLoaderRepository() {
    checkMBeanPermission(null, null, null, "getClassLoaderRepository");
    return this.secureClr;
  }
  
  public MBeanServerDelegate getMBeanServerDelegate() {
    if (!this.interceptorsEnabled)
      throw new UnsupportedOperationException("MBeanServerInterceptors are disabled."); 
    return this.mBeanServerDelegateObject;
  }
  
  public static MBeanServerDelegate newMBeanServerDelegate() { return new MBeanServerDelegateImpl(); }
  
  public static MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, boolean paramBoolean) {
    checkNewMBeanServerPermission();
    return new JmxMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, null, paramBoolean, true);
  }
  
  private ObjectName cloneObjectName(ObjectName paramObjectName) { return (paramObjectName != null) ? ObjectName.getInstance(paramObjectName) : paramObjectName; }
  
  private Attribute cloneAttribute(Attribute paramAttribute) { return (paramAttribute != null && !paramAttribute.getClass().equals(Attribute.class)) ? new Attribute(paramAttribute.getName(), paramAttribute.getValue()) : paramAttribute; }
  
  private AttributeList cloneAttributeList(AttributeList paramAttributeList) {
    if (paramAttributeList != null) {
      List list = paramAttributeList.asList();
      if (!paramAttributeList.getClass().equals(AttributeList.class)) {
        AttributeList attributeList = new AttributeList(list.size());
        for (Attribute attribute : list)
          attributeList.add(cloneAttribute(attribute)); 
        return attributeList;
      } 
      for (byte b = 0; b < list.size(); b++) {
        Attribute attribute = (Attribute)list.get(b);
        if (!attribute.getClass().equals(Attribute.class))
          paramAttributeList.set(b, cloneAttribute(attribute)); 
      } 
      return paramAttributeList;
    } 
    return paramAttributeList;
  }
  
  private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanPermission mBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
      securityManager.checkPermission(mBeanPermission);
    } 
  }
  
  private static void checkNewMBeanServerPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanServerPermission mBeanServerPermission = new MBeanServerPermission("newMBeanServer");
      securityManager.checkPermission(mBeanServerPermission);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\JmxMBeanServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */