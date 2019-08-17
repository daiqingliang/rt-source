package javax.management.remote.rmi;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.util.Set;
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
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;

public interface RMIConnection extends Closeable, Remote {
  String getConnectionId() throws IOException;
  
  void close() throws IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
  
  ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
  
  void unregisterMBean(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, MBeanRegistrationException, IOException;
  
  ObjectInstance getObjectInstance(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, IOException;
  
  Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException;
  
  Set<ObjectName> queryNames(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException;
  
  boolean isRegistered(ObjectName paramObjectName, Subject paramSubject) throws IOException;
  
  Integer getMBeanCount(Subject paramSubject) throws IOException;
  
  Object getAttribute(ObjectName paramObjectName, String paramString, Subject paramSubject) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException;
  
  AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString, Subject paramSubject) throws InstanceNotFoundException, ReflectionException, IOException;
  
  void setAttribute(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException;
  
  AttributeList setAttributes(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws InstanceNotFoundException, ReflectionException, IOException;
  
  Object invoke(ObjectName paramObjectName, String paramString, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
  
  String getDefaultDomain(Subject paramSubject) throws IOException;
  
  String[] getDomains(Subject paramSubject) throws IOException;
  
  MBeanInfo getMBeanInfo(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;
  
  boolean isInstanceOf(ObjectName paramObjectName, String paramString, Subject paramSubject) throws InstanceNotFoundException, IOException;
  
  void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws InstanceNotFoundException, IOException;
  
  void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
  
  void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws InstanceNotFoundException, IOException;
  
  Integer[] addNotificationListeners(ObjectName[] paramArrayOfObjectName, MarshalledObject[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject) throws InstanceNotFoundException, IOException;
  
  void removeNotificationListeners(ObjectName paramObjectName, Integer[] paramArrayOfInteger, Subject paramSubject) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
  
  NotificationResult fetchNotifications(long paramLong1, int paramInt, long paramLong2) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */