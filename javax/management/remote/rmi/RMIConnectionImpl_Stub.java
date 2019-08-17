package javax.management.remote.rmi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.MarshalledObject;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
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

public final class RMIConnectionImpl_Stub extends RemoteStub implements RMIConnection {
  private static final long serialVersionUID = 2L;
  
  private static Method $method_addNotificationListener_0;
  
  private static Method $method_addNotificationListeners_1;
  
  private static Method $method_close_2;
  
  private static Method $method_createMBean_3;
  
  private static Method $method_createMBean_4;
  
  private static Method $method_createMBean_5;
  
  private static Method $method_createMBean_6;
  
  private static Method $method_fetchNotifications_7;
  
  private static Method $method_getAttribute_8;
  
  private static Method $method_getAttributes_9;
  
  private static Method $method_getConnectionId_10;
  
  private static Method $method_getDefaultDomain_11;
  
  private static Method $method_getDomains_12;
  
  private static Method $method_getMBeanCount_13;
  
  private static Method $method_getMBeanInfo_14;
  
  private static Method $method_getObjectInstance_15;
  
  private static Method $method_invoke_16;
  
  private static Method $method_isInstanceOf_17;
  
  private static Method $method_isRegistered_18;
  
  private static Method $method_queryMBeans_19;
  
  private static Method $method_queryNames_20;
  
  private static Method $method_removeNotificationListener_21;
  
  private static Method $method_removeNotificationListener_22;
  
  private static Method $method_removeNotificationListeners_23;
  
  private static Method $method_setAttribute_24;
  
  private static Method $method_setAttributes_25;
  
  private static Method $method_unregisterMBean_26;
  
  static Class array$Ljavax$management$ObjectName;
  
  static Class array$Ljava$rmi$MarshalledObject;
  
  static Class array$Ljavax$security$auth$Subject;
  
  static Class array$Ljava$lang$String;
  
  static Class array$Ljava$lang$Integer;
  
  static  {
    try {
      $method_addNotificationListener_0 = RMIConnection.class.getMethod("addNotificationListener", new Class[] { ObjectName.class, ObjectName.class, MarshalledObject.class, MarshalledObject.class, Subject.class });
      $method_addNotificationListeners_1 = RMIConnection.class.getMethod("addNotificationListeners", new Class[] { (array$Ljavax$management$ObjectName != null) ? array$Ljavax$management$ObjectName : (array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")), (array$Ljava$rmi$MarshalledObject != null) ? array$Ljava$rmi$MarshalledObject : (array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")), (array$Ljavax$security$auth$Subject != null) ? array$Ljavax$security$auth$Subject : (array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")) });
      $method_close_2 = AutoCloseable.class.getMethod("close", new Class[0]);
      $method_createMBean_3 = RMIConnection.class.getMethod("createMBean", new Class[] { String.class, ObjectName.class, MarshalledObject.class, (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), Subject.class });
      $method_createMBean_4 = RMIConnection.class.getMethod("createMBean", new Class[] { String.class, ObjectName.class, ObjectName.class, MarshalledObject.class, (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), Subject.class });
      $method_createMBean_5 = RMIConnection.class.getMethod("createMBean", new Class[] { String.class, ObjectName.class, ObjectName.class, Subject.class });
      $method_createMBean_6 = RMIConnection.class.getMethod("createMBean", new Class[] { String.class, ObjectName.class, Subject.class });
      $method_fetchNotifications_7 = RMIConnection.class.getMethod("fetchNotifications", new Class[] { long.class, int.class, long.class });
      $method_getAttribute_8 = RMIConnection.class.getMethod("getAttribute", new Class[] { ObjectName.class, String.class, Subject.class });
      $method_getAttributes_9 = RMIConnection.class.getMethod("getAttributes", new Class[] { ObjectName.class, (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), Subject.class });
      $method_getConnectionId_10 = RMIConnection.class.getMethod("getConnectionId", new Class[0]);
      $method_getDefaultDomain_11 = RMIConnection.class.getMethod("getDefaultDomain", new Class[] { Subject.class });
      $method_getDomains_12 = RMIConnection.class.getMethod("getDomains", new Class[] { Subject.class });
      $method_getMBeanCount_13 = RMIConnection.class.getMethod("getMBeanCount", new Class[] { Subject.class });
      $method_getMBeanInfo_14 = RMIConnection.class.getMethod("getMBeanInfo", new Class[] { ObjectName.class, Subject.class });
      $method_getObjectInstance_15 = RMIConnection.class.getMethod("getObjectInstance", new Class[] { ObjectName.class, Subject.class });
      $method_invoke_16 = RMIConnection.class.getMethod("invoke", new Class[] { ObjectName.class, String.class, MarshalledObject.class, (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), Subject.class });
      $method_isInstanceOf_17 = RMIConnection.class.getMethod("isInstanceOf", new Class[] { ObjectName.class, String.class, Subject.class });
      $method_isRegistered_18 = RMIConnection.class.getMethod("isRegistered", new Class[] { ObjectName.class, Subject.class });
      $method_queryMBeans_19 = RMIConnection.class.getMethod("queryMBeans", new Class[] { ObjectName.class, MarshalledObject.class, Subject.class });
      $method_queryNames_20 = RMIConnection.class.getMethod("queryNames", new Class[] { ObjectName.class, MarshalledObject.class, Subject.class });
      $method_removeNotificationListener_21 = RMIConnection.class.getMethod("removeNotificationListener", new Class[] { ObjectName.class, ObjectName.class, MarshalledObject.class, MarshalledObject.class, Subject.class });
      $method_removeNotificationListener_22 = RMIConnection.class.getMethod("removeNotificationListener", new Class[] { ObjectName.class, ObjectName.class, Subject.class });
      $method_removeNotificationListeners_23 = RMIConnection.class.getMethod("removeNotificationListeners", new Class[] { ObjectName.class, (array$Ljava$lang$Integer != null) ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")), Subject.class });
      $method_setAttribute_24 = RMIConnection.class.getMethod("setAttribute", new Class[] { ObjectName.class, MarshalledObject.class, Subject.class });
      $method_setAttributes_25 = RMIConnection.class.getMethod("setAttributes", new Class[] { ObjectName.class, MarshalledObject.class, Subject.class });
      $method_unregisterMBean_26 = RMIConnection.class.getMethod("unregisterMBean", new Class[] { ObjectName.class, Subject.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError("stub class initialization failed");
    } 
  }
  
  public RMIConnectionImpl_Stub(RemoteRef paramRemoteRef) { super(paramRemoteRef); }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws IOException, InstanceNotFoundException {
    try {
      this.ref.invoke(this, $method_addNotificationListener_0, new Object[] { paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject }, -8578317696269497109L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Integer[] addNotificationListeners(ObjectName[] paramArrayOfObjectName, MarshalledObject[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject) throws IOException, InstanceNotFoundException {
    try {
      Object object = this.ref.invoke(this, $method_addNotificationListeners_1, new Object[] { paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject }, -5321691879380783377L);
      return (Integer[])object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void close() throws IOException {
    try {
      this.ref.invoke(this, $method_close_2, null, -4742752445160157748L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws IOException, InstanceAlreadyExistsException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_createMBean_3, new Object[] { paramString, paramObjectName, paramMarshalledObject, paramArrayOfString, paramSubject }, 4867822117947806114L);
      return (ObjectInstance)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
      throw instanceAlreadyExistsException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw notCompliantMBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws IOException, InstanceAlreadyExistsException, InstanceNotFoundException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_createMBean_4, new Object[] { paramString, paramObjectName1, paramObjectName2, paramMarshalledObject, paramArrayOfString, paramSubject }, -6604955182088909937L);
      return (ObjectInstance)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
      throw instanceAlreadyExistsException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw notCompliantMBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws IOException, InstanceAlreadyExistsException, InstanceNotFoundException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_createMBean_5, new Object[] { paramString, paramObjectName1, paramObjectName2, paramSubject }, -8679469989872508324L);
      return (ObjectInstance)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
      throw instanceAlreadyExistsException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw notCompliantMBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Subject paramSubject) throws IOException, InstanceAlreadyExistsException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_createMBean_6, new Object[] { paramString, paramObjectName, paramSubject }, 2510753813974665446L);
      return (ObjectInstance)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
      throw instanceAlreadyExistsException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw notCompliantMBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public NotificationResult fetchNotifications(long paramLong1, int paramInt, long paramLong2) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_fetchNotifications_7, new Object[] { new Long(paramLong1), new Integer(paramInt), new Long(paramLong2) }, -5037523307973544478L);
      return (NotificationResult)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString, Subject paramSubject) throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    try {
      return this.ref.invoke(this, $method_getAttribute_8, new Object[] { paramObjectName, paramString, paramSubject }, -1089783104982388203L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString, Subject paramSubject) throws IOException, InstanceNotFoundException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_getAttributes_9, new Object[] { paramObjectName, paramArrayOfString, paramSubject }, 6285293806596348999L);
      return (AttributeList)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public String getConnectionId() throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_getConnectionId_10, null, -67907180346059933L);
      return (String)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public String getDefaultDomain(Subject paramSubject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_getDefaultDomain_11, new Object[] { paramSubject }, 6047668923998658472L);
      return (String)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public String[] getDomains(Subject paramSubject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_getDomains_12, new Object[] { paramSubject }, -6662314179953625551L);
      return (String[])object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Integer getMBeanCount(Subject paramSubject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_getMBeanCount_13, new Object[] { paramSubject }, -2042362057335820635L);
      return (Integer)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName, Subject paramSubject) throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_getMBeanInfo_14, new Object[] { paramObjectName, paramSubject }, -7404813916326233354L);
      return (MBeanInfo)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (IntrospectionException introspectionException) {
      throw introspectionException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName, Subject paramSubject) throws IOException, InstanceNotFoundException {
    try {
      Object object = this.ref.invoke(this, $method_getObjectInstance_15, new Object[] { paramObjectName, paramSubject }, 6950095694996159938L);
      return (ObjectInstance)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws IOException, InstanceNotFoundException, MBeanException, ReflectionException {
    try {
      return this.ref.invoke(this, $method_invoke_16, new Object[] { paramObjectName, paramString, paramMarshalledObject, paramArrayOfString, paramSubject }, 1434350937885235744L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString, Subject paramSubject) throws IOException, InstanceNotFoundException {
    try {
      Object object = this.ref.invoke(this, $method_isInstanceOf_17, new Object[] { paramObjectName, paramString, paramSubject }, -2147516868461740814L);
      return ((Boolean)object).booleanValue();
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public boolean isRegistered(ObjectName paramObjectName, Subject paramSubject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_isRegistered_18, new Object[] { paramObjectName, paramSubject }, 8325683335228268564L);
      return ((Boolean)object).booleanValue();
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Set queryMBeans(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_queryMBeans_19, new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, 2915881009400597976L);
      return (Set)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Set queryNames(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_queryNames_20, new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, 9152567528369059802L);
      return (Set)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws IOException, InstanceNotFoundException {
    try {
      this.ref.invoke(this, $method_removeNotificationListener_21, new Object[] { paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject }, 2578029900065214857L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (ListenerNotFoundException listenerNotFoundException) {
      throw listenerNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
    try {
      this.ref.invoke(this, $method_removeNotificationListener_22, new Object[] { paramObjectName1, paramObjectName2, paramSubject }, 6604721169198089513L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (ListenerNotFoundException listenerNotFoundException) {
      throw listenerNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeNotificationListeners(ObjectName paramObjectName, Integer[] paramArrayOfInteger, Subject paramSubject) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
    try {
      this.ref.invoke(this, $method_removeNotificationListeners_23, new Object[] { paramObjectName, paramArrayOfInteger, paramSubject }, 2549120024456183446L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (ListenerNotFoundException listenerNotFoundException) {
      throw listenerNotFoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void setAttribute(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException, AttributeNotFoundException, InstanceNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    try {
      this.ref.invoke(this, $method_setAttribute_24, new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, 6738606893952597516L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (InvalidAttributeValueException invalidAttributeValueException) {
      throw invalidAttributeValueException;
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException, InstanceNotFoundException, ReflectionException {
    try {
      Object object = this.ref.invoke(this, $method_setAttributes_25, new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, -230470228399681820L);
      return (AttributeList)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void unregisterMBean(ObjectName paramObjectName, Subject paramSubject) throws IOException, InstanceNotFoundException, MBeanRegistrationException {
    try {
      this.ref.invoke(this, $method_unregisterMBean_26, new Object[] { paramObjectName, paramSubject }, -159498580868721452L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw instanceNotFoundException;
    } catch (MBeanRegistrationException mBeanRegistrationException) {
      throw mBeanRegistrationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIConnectionImpl_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */