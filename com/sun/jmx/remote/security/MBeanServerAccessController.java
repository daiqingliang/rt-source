package com.sun.jmx.remote.security;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.util.Set;
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
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;
import javax.management.remote.MBeanServerForwarder;

public abstract class MBeanServerAccessController implements MBeanServerForwarder {
  private MBeanServer mbs;
  
  public MBeanServer getMBeanServer() { return this.mbs; }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) {
    if (paramMBeanServer == null)
      throw new IllegalArgumentException("Null MBeanServer"); 
    if (this.mbs != null)
      throw new IllegalArgumentException("MBeanServer object already initialized"); 
    this.mbs = paramMBeanServer;
  }
  
  protected abstract void checkRead();
  
  protected abstract void checkWrite();
  
  protected void checkCreate(String paramString) { checkWrite(); }
  
  protected void checkUnregister(ObjectName paramObjectName) { checkWrite(); }
  
  public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException {
    checkRead();
    getMBeanServer().addNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException {
    checkRead();
    getMBeanServer().addNotificationListener(paramObjectName1, paramObjectName2, paramNotificationFilter, paramObject);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
    checkCreate(paramString);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      Object object = getMBeanServer().instantiate(paramString);
      checkClassLoader(object);
      return getMBeanServer().registerMBean(object, paramObjectName);
    } 
    return getMBeanServer().createMBean(paramString, paramObjectName);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
    checkCreate(paramString);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      Object object = getMBeanServer().instantiate(paramString, paramArrayOfObject, paramArrayOfString);
      checkClassLoader(object);
      return getMBeanServer().registerMBean(object, paramObjectName);
    } 
    return getMBeanServer().createMBean(paramString, paramObjectName, paramArrayOfObject, paramArrayOfString);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
    checkCreate(paramString);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      Object object = getMBeanServer().instantiate(paramString, paramObjectName2);
      checkClassLoader(object);
      return getMBeanServer().registerMBean(object, paramObjectName1);
    } 
    return getMBeanServer().createMBean(paramString, paramObjectName1, paramObjectName2);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
    checkCreate(paramString);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      Object object = getMBeanServer().instantiate(paramString, paramObjectName2, paramArrayOfObject, paramArrayOfString);
      checkClassLoader(object);
      return getMBeanServer().registerMBean(object, paramObjectName1);
    } 
    return getMBeanServer().createMBean(paramString, paramObjectName1, paramObjectName2, paramArrayOfObject, paramArrayOfString);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte) throws InstanceNotFoundException, OperationsException {
    checkRead();
    return getMBeanServer().deserialize(paramObjectName, paramArrayOfByte);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte) throws OperationsException, ReflectionException {
    checkRead();
    return getMBeanServer().deserialize(paramString, paramArrayOfByte);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte) throws InstanceNotFoundException, OperationsException, ReflectionException {
    checkRead();
    return getMBeanServer().deserialize(paramString, paramObjectName, paramArrayOfByte);
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
    checkRead();
    return getMBeanServer().getAttribute(paramObjectName, paramString);
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString) throws InstanceNotFoundException, ReflectionException {
    checkRead();
    return getMBeanServer().getAttributes(paramObjectName, paramArrayOfString);
  }
  
  public ClassLoader getClassLoader(ObjectName paramObjectName) throws InstanceNotFoundException {
    checkRead();
    return getMBeanServer().getClassLoader(paramObjectName);
  }
  
  public ClassLoader getClassLoaderFor(ObjectName paramObjectName) throws InstanceNotFoundException {
    checkRead();
    return getMBeanServer().getClassLoaderFor(paramObjectName);
  }
  
  public ClassLoaderRepository getClassLoaderRepository() {
    checkRead();
    return getMBeanServer().getClassLoaderRepository();
  }
  
  public String getDefaultDomain() {
    checkRead();
    return getMBeanServer().getDefaultDomain();
  }
  
  public String[] getDomains() {
    checkRead();
    return getMBeanServer().getDomains();
  }
  
  public Integer getMBeanCount() {
    checkRead();
    return getMBeanServer().getMBeanCount();
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
    checkRead();
    return getMBeanServer().getMBeanInfo(paramObjectName);
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName) throws InstanceNotFoundException {
    checkRead();
    return getMBeanServer().getObjectInstance(paramObjectName);
  }
  
  public Object instantiate(String paramString) throws ReflectionException, MBeanException {
    checkCreate(paramString);
    return getMBeanServer().instantiate(paramString);
  }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException {
    checkCreate(paramString);
    return getMBeanServer().instantiate(paramString, paramArrayOfObject, paramArrayOfString);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName) throws ReflectionException, MBeanException, InstanceNotFoundException {
    checkCreate(paramString);
    return getMBeanServer().instantiate(paramString, paramObjectName);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException, InstanceNotFoundException {
    checkCreate(paramString);
    return getMBeanServer().instantiate(paramString, paramObjectName, paramArrayOfObject, paramArrayOfString);
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws InstanceNotFoundException, MBeanException, ReflectionException {
    checkWrite();
    checkMLetMethods(paramObjectName, paramString);
    return getMBeanServer().invoke(paramObjectName, paramString, paramArrayOfObject, paramArrayOfString);
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException {
    checkRead();
    return getMBeanServer().isInstanceOf(paramObjectName, paramString);
  }
  
  public boolean isRegistered(ObjectName paramObjectName) {
    checkRead();
    return getMBeanServer().isRegistered(paramObjectName);
  }
  
  public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp) {
    checkRead();
    return getMBeanServer().queryMBeans(paramObjectName, paramQueryExp);
  }
  
  public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp) {
    checkRead();
    return getMBeanServer().queryNames(paramObjectName, paramQueryExp);
  }
  
  public ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
    checkWrite();
    return getMBeanServer().registerMBean(paramObject, paramObjectName);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener) throws InstanceNotFoundException, ListenerNotFoundException {
    checkRead();
    getMBeanServer().removeNotificationListener(paramObjectName, paramNotificationListener);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException {
    checkRead();
    getMBeanServer().removeNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2) throws InstanceNotFoundException, ListenerNotFoundException {
    checkRead();
    getMBeanServer().removeNotificationListener(paramObjectName1, paramObjectName2);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException {
    checkRead();
    getMBeanServer().removeNotificationListener(paramObjectName1, paramObjectName2, paramNotificationFilter, paramObject);
  }
  
  public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    checkWrite();
    getMBeanServer().setAttribute(paramObjectName, paramAttribute);
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList) throws InstanceNotFoundException, ReflectionException {
    checkWrite();
    return getMBeanServer().setAttributes(paramObjectName, paramAttributeList);
  }
  
  public void unregisterMBean(ObjectName paramObjectName) {
    checkUnregister(paramObjectName);
    getMBeanServer().unregisterMBean(paramObjectName);
  }
  
  private void checkClassLoader(Object paramObject) {
    if (paramObject instanceof ClassLoader)
      throw new SecurityException("Access denied! Creating an MBean that is a ClassLoader is forbidden unless a security manager is installed."); 
  }
  
  private void checkMLetMethods(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      return; 
    if (!paramString.equals("addURL") && !paramString.equals("getMBeansFromURL"))
      return; 
    if (!getMBeanServer().isInstanceOf(paramObjectName, "javax.management.loading.MLet"))
      return; 
    if (paramString.equals("addURL"))
      throw new SecurityException("Access denied! MLet method addURL cannot be invoked unless a security manager is installed."); 
    GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.remote.x.mlet.allow.getMBeansFromURL");
    String str = (String)AccessController.doPrivileged(getPropertyAction);
    boolean bool = "true".equalsIgnoreCase(str);
    if (!bool)
      throw new SecurityException("Access denied! MLet method getMBeansFromURL cannot be invoked unless a security manager is installed or the system property -Djmx.remote.x.mlet.allow.getMBeansFromURL=true is specified."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\security\MBeanServerAccessController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */