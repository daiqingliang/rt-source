package javax.management;

import java.io.ObjectInputStream;
import java.util.Set;
import javax.management.loading.ClassLoaderRepository;

public interface MBeanServer extends MBeanServerConnection {
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException;
  
  ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException;
  
  void unregisterMBean(ObjectName paramObjectName) throws InstanceNotFoundException, MBeanRegistrationException;
  
  ObjectInstance getObjectInstance(ObjectName paramObjectName) throws InstanceNotFoundException;
  
  Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp);
  
  Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp);
  
  boolean isRegistered(ObjectName paramObjectName);
  
  Integer getMBeanCount();
  
  Object getAttribute(ObjectName paramObjectName, String paramString) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException;
  
  AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString) throws InstanceNotFoundException, ReflectionException;
  
  void setAttribute(ObjectName paramObjectName, Attribute paramAttribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;
  
  AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList) throws InstanceNotFoundException, ReflectionException;
  
  Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws InstanceNotFoundException, MBeanException, ReflectionException;
  
  String getDefaultDomain();
  
  String[] getDomains();
  
  void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException;
  
  void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException;
  
  void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2) throws InstanceNotFoundException, ListenerNotFoundException;
  
  void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException;
  
  void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener) throws InstanceNotFoundException, ListenerNotFoundException;
  
  void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException;
  
  MBeanInfo getMBeanInfo(ObjectName paramObjectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException;
  
  boolean isInstanceOf(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException;
  
  Object instantiate(String paramString) throws ReflectionException, MBeanException;
  
  Object instantiate(String paramString, ObjectName paramObjectName) throws ReflectionException, MBeanException, InstanceNotFoundException;
  
  Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException;
  
  Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException, InstanceNotFoundException;
  
  @Deprecated
  ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte) throws InstanceNotFoundException, OperationsException;
  
  @Deprecated
  ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte) throws OperationsException, ReflectionException;
  
  @Deprecated
  ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte) throws InstanceNotFoundException, OperationsException, ReflectionException;
  
  ClassLoader getClassLoaderFor(ObjectName paramObjectName) throws InstanceNotFoundException;
  
  ClassLoader getClassLoader(ObjectName paramObjectName) throws InstanceNotFoundException;
  
  ClassLoaderRepository getClassLoaderRepository();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */