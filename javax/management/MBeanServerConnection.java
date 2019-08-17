package javax.management;

import java.io.IOException;
import java.util.Set;

public interface MBeanServerConnection {
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
  
  void unregisterMBean(ObjectName paramObjectName) throws InstanceNotFoundException, MBeanRegistrationException, IOException;
  
  ObjectInstance getObjectInstance(ObjectName paramObjectName) throws InstanceNotFoundException, IOException;
  
  Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp) throws IOException;
  
  Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp) throws IOException;
  
  boolean isRegistered(ObjectName paramObjectName) throws IOException;
  
  Integer getMBeanCount() throws IOException;
  
  Object getAttribute(ObjectName paramObjectName, String paramString) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException;
  
  AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString) throws InstanceNotFoundException, ReflectionException, IOException;
  
  void setAttribute(ObjectName paramObjectName, Attribute paramAttribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException;
  
  AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList) throws InstanceNotFoundException, ReflectionException, IOException;
  
  Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
  
  String getDefaultDomain() throws IOException;
  
  String[] getDomains() throws IOException;
  
  void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException, IOException;
  
  void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException, IOException;
  
  void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
  
  void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException, IOException;
  
  void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
  
  void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException, IOException;
  
  MBeanInfo getMBeanInfo(ObjectName paramObjectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;
  
  boolean isInstanceOf(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */