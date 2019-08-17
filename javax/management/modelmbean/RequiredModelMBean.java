package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;
import javax.management.ServiceNotFoundException;
import javax.management.loading.ClassLoaderRepository;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class RequiredModelMBean implements ModelMBean, MBeanRegistration, NotificationEmitter {
  ModelMBeanInfo modelMBeanInfo;
  
  private NotificationBroadcasterSupport generalBroadcaster = null;
  
  private NotificationBroadcasterSupport attributeBroadcaster = null;
  
  private Object managedResource = null;
  
  private boolean registered = false;
  
  private MBeanServer server = null;
  
  private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
  
  private final AccessControlContext acc = AccessController.getContext();
  
  private static final Class<?>[] primitiveClasses = { int.class, long.class, boolean.class, double.class, float.class, short.class, byte.class, char.class };
  
  private static final Map<String, Class<?>> primitiveClassMap = new HashMap();
  
  private static Set<String> rmmbMethodNames;
  
  private static final String[] primitiveTypes;
  
  private static final String[] primitiveWrappers;
  
  public RequiredModelMBean() throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Entry"); 
    this.modelMBeanInfo = createDefaultModelMBeanInfo();
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Exit"); 
  }
  
  public RequiredModelMBean(ModelMBeanInfo paramModelMBeanInfo) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Entry"); 
    setModelMBeanInfo(paramModelMBeanInfo);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Exit"); 
  }
  
  public void setModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Entry"); 
    if (paramModelMBeanInfo == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo is null: Raising exception."); 
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("ModelMBeanInfo must not be null");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to initialize the ModelMBeanInfo of the RequiredModelMBean");
    } 
    if (this.registered) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "RequiredMBean is registered: Raising exception."); 
      IllegalStateException illegalStateException = new IllegalStateException("cannot call setModelMBeanInfo while ModelMBean is registered");
      throw new RuntimeOperationsException(illegalStateException, "Exception occurred trying to set the ModelMBeanInfo of the RequiredModelMBean");
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Setting ModelMBeanInfo to " + printModelMBeanInfo(paramModelMBeanInfo));
      int i = 0;
      if (paramModelMBeanInfo.getNotifications() != null)
        i = paramModelMBeanInfo.getNotifications().length; 
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo notifications has " + i + " elements");
    } 
    this.modelMBeanInfo = (ModelMBeanInfo)paramModelMBeanInfo.clone();
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "set mbeanInfo to: " + printModelMBeanInfo(this.modelMBeanInfo));
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Exit");
    } 
  }
  
  public void setManagedResource(Object paramObject, String paramString) throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Entry"); 
    if (paramString == null || !paramString.equalsIgnoreCase("objectReference")) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource Type is not supported: " + paramString); 
      throw new InvalidTargetObjectTypeException(paramString);
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource is valid"); 
    this.managedResource = paramObject;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object, String)", "Exit"); 
  }
  
  public void load() throws MBeanException, RuntimeOperationsException {
    ServiceNotFoundException serviceNotFoundException = new ServiceNotFoundException("Persistence not supported for this MBean");
    throw new MBeanException(serviceNotFoundException, serviceNotFoundException.getMessage());
  }
  
  public void store() throws MBeanException, RuntimeOperationsException {
    ServiceNotFoundException serviceNotFoundException = new ServiceNotFoundException("Persistence not supported for this MBean");
    throw new MBeanException(serviceNotFoundException, serviceNotFoundException.getMessage());
  }
  
  private Object resolveForCacheValue(Descriptor paramDescriptor) throws MBeanException, RuntimeOperationsException {
    String str;
    boolean bool1 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool1)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Entry"); 
    Object object1 = null;
    boolean bool2 = false;
    boolean bool3 = true;
    long l = 0L;
    if (paramDescriptor == null) {
      if (bool1)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Input Descriptor is null"); 
      return object1;
    } 
    if (bool1)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "descriptor is " + paramDescriptor); 
    Descriptor descriptor = this.modelMBeanInfo.getMBeanDescriptor();
    if (descriptor == null && bool1)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "MBean Descriptor is null"); 
    Object object2 = paramDescriptor.getFieldValue("currencyTimeLimit");
    if (object2 != null) {
      str = object2.toString();
    } else {
      str = null;
    } 
    if (str == null && descriptor != null) {
      object2 = descriptor.getFieldValue("currencyTimeLimit");
      if (object2 != null) {
        str = object2.toString();
      } else {
        str = null;
      } 
    } 
    if (str != null) {
      if (bool1)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyTimeLimit: " + str); 
      l = (new Long(str)).longValue() * 1000L;
      if (l < 0L) {
        bool3 = false;
        bool2 = true;
        if (bool1)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", l + ": never Cached"); 
      } else if (l == 0L) {
        bool3 = true;
        bool2 = false;
        if (bool1)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "always valid Cache"); 
      } else {
        String str1;
        Object object = paramDescriptor.getFieldValue("lastUpdatedTimeStamp");
        if (object != null) {
          str1 = object.toString();
        } else {
          str1 = null;
        } 
        if (bool1)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "lastUpdatedTimeStamp: " + str1); 
        if (str1 == null)
          str1 = "0"; 
        long l1 = (new Long(str1)).longValue();
        if (bool1)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyPeriod:" + l + " lastUpdatedTimeStamp:" + l1); 
        long l2 = (new Date()).getTime();
        if (l2 < l1 + l) {
          bool3 = true;
          bool2 = false;
          if (bool1)
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", " timed valid Cache for " + l2 + " < " + (l1 + l)); 
        } else {
          bool3 = false;
          bool2 = true;
          if (bool1)
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "timed expired cache for " + l2 + " > " + (l1 + l)); 
        } 
      } 
      if (bool1)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "returnCachedValue:" + bool3 + " resetValue: " + bool2); 
      if (bool3 == true) {
        Object object = paramDescriptor.getFieldValue("value");
        if (object != null) {
          object1 = object;
          if (bool1)
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "valid Cache value: " + object); 
        } else {
          object1 = null;
          if (bool1)
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "no Cached value"); 
        } 
      } 
      if (bool2 == true) {
        paramDescriptor.removeField("lastUpdatedTimeStamp");
        paramDescriptor.removeField("value");
        object1 = null;
        this.modelMBeanInfo.setDescriptor(paramDescriptor, null);
        if (bool1)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "reset cached value to null"); 
      } 
    } 
    if (bool1)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Exit"); 
    return object1;
  }
  
  public MBeanInfo getMBeanInfo() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "Entry"); 
    if (this.modelMBeanInfo == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "modelMBeanInfo is null"); 
      this.modelMBeanInfo = createDefaultModelMBeanInfo();
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "ModelMBeanInfo is " + this.modelMBeanInfo.getClassName() + " for " + this.modelMBeanInfo.getDescription());
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", printModelMBeanInfo(this.modelMBeanInfo));
    } 
    return (MBeanInfo)this.modelMBeanInfo.clone();
  }
  
  private String printModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo) {
    StringBuilder stringBuilder = new StringBuilder();
    if (paramModelMBeanInfo == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "printModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo to print is null, printing local ModelMBeanInfo"); 
      paramModelMBeanInfo = this.modelMBeanInfo;
    } 
    stringBuilder.append("\nMBeanInfo for ModelMBean is:");
    stringBuilder.append("\nCLASSNAME: \t" + paramModelMBeanInfo.getClassName());
    stringBuilder.append("\nDESCRIPTION: \t" + paramModelMBeanInfo.getDescription());
    try {
      stringBuilder.append("\nMBEAN DESCRIPTOR: \t" + paramModelMBeanInfo.getMBeanDescriptor());
    } catch (Exception exception) {
      stringBuilder.append("\nMBEAN DESCRIPTOR: \t is invalid");
    } 
    stringBuilder.append("\nATTRIBUTES");
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = paramModelMBeanInfo.getAttributes();
    if (arrayOfMBeanAttributeInfo != null && arrayOfMBeanAttributeInfo.length > 0) {
      for (byte b = 0; b < arrayOfMBeanAttributeInfo.length; b++) {
        ModelMBeanAttributeInfo modelMBeanAttributeInfo = (ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[b];
        stringBuilder.append(" ** NAME: \t" + modelMBeanAttributeInfo.getName());
        stringBuilder.append("    DESCR: \t" + modelMBeanAttributeInfo.getDescription());
        stringBuilder.append("    TYPE: \t" + modelMBeanAttributeInfo.getType() + "    READ: \t" + modelMBeanAttributeInfo.isReadable() + "    WRITE: \t" + modelMBeanAttributeInfo.isWritable());
        stringBuilder.append("    DESCRIPTOR: " + modelMBeanAttributeInfo.getDescriptor().toString());
      } 
    } else {
      stringBuilder.append(" ** No attributes **");
    } 
    stringBuilder.append("\nCONSTRUCTORS");
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = paramModelMBeanInfo.getConstructors();
    if (arrayOfMBeanConstructorInfo != null && arrayOfMBeanConstructorInfo.length > 0) {
      for (byte b = 0; b < arrayOfMBeanConstructorInfo.length; b++) {
        ModelMBeanConstructorInfo modelMBeanConstructorInfo = (ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[b];
        stringBuilder.append(" ** NAME: \t" + modelMBeanConstructorInfo.getName());
        stringBuilder.append("    DESCR: \t" + modelMBeanConstructorInfo.getDescription());
        stringBuilder.append("    PARAM: \t" + modelMBeanConstructorInfo.getSignature().length + " parameter(s)");
        stringBuilder.append("    DESCRIPTOR: " + modelMBeanConstructorInfo.getDescriptor().toString());
      } 
    } else {
      stringBuilder.append(" ** No Constructors **");
    } 
    stringBuilder.append("\nOPERATIONS");
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = paramModelMBeanInfo.getOperations();
    if (arrayOfMBeanOperationInfo != null && arrayOfMBeanOperationInfo.length > 0) {
      for (byte b = 0; b < arrayOfMBeanOperationInfo.length; b++) {
        ModelMBeanOperationInfo modelMBeanOperationInfo = (ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[b];
        stringBuilder.append(" ** NAME: \t" + modelMBeanOperationInfo.getName());
        stringBuilder.append("    DESCR: \t" + modelMBeanOperationInfo.getDescription());
        stringBuilder.append("    PARAM: \t" + modelMBeanOperationInfo.getSignature().length + " parameter(s)");
        stringBuilder.append("    DESCRIPTOR: " + modelMBeanOperationInfo.getDescriptor().toString());
      } 
    } else {
      stringBuilder.append(" ** No operations ** ");
    } 
    stringBuilder.append("\nNOTIFICATIONS");
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramModelMBeanInfo.getNotifications();
    if (arrayOfMBeanNotificationInfo != null && arrayOfMBeanNotificationInfo.length > 0) {
      for (byte b = 0; b < arrayOfMBeanNotificationInfo.length; b++) {
        ModelMBeanNotificationInfo modelMBeanNotificationInfo = (ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[b];
        stringBuilder.append(" ** NAME: \t" + modelMBeanNotificationInfo.getName());
        stringBuilder.append("    DESCR: \t" + modelMBeanNotificationInfo.getDescription());
        stringBuilder.append("    DESCRIPTOR: " + modelMBeanNotificationInfo.getDescriptor().toString());
      } 
    } else {
      stringBuilder.append(" ** No notifications **");
    } 
    stringBuilder.append(" ** ModelMBean: End of MBeanInfo ** ");
    return stringBuilder.toString();
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException {
    Object object3;
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Entry"); 
    if (paramString == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Method name must not be null");
      throw new RuntimeOperationsException(illegalArgumentException, "An exception occurred while trying to invoke a method on a RequiredModelMBean");
    } 
    String str1 = null;
    int i = paramString.lastIndexOf(".");
    if (i > 0) {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } else {
      str2 = paramString;
    } 
    i = str2.indexOf("(");
    if (i > 0)
      str2 = str2.substring(0, i); 
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Finding operation " + paramString + " as " + str2); 
    ModelMBeanOperationInfo modelMBeanOperationInfo = this.modelMBeanInfo.getOperation(str2);
    if (modelMBeanOperationInfo == null) {
      final String className = "Operation " + paramString + " not in ModelMBeanInfo";
      throw new MBeanException(new ServiceNotFoundException(str), str);
    } 
    Descriptor descriptor = modelMBeanOperationInfo.getDescriptor();
    if (descriptor == null)
      throw new MBeanException(new ServiceNotFoundException("Operation descriptor null"), "Operation descriptor null"); 
    Object object1 = resolveForCacheValue(descriptor);
    if (object1 != null) {
      if (bool)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Returning cached value"); 
      return object1;
    } 
    if (str1 == null)
      str1 = (String)descriptor.getFieldValue("class"); 
    String str2 = (String)descriptor.getFieldValue("name");
    if (str2 == null)
      throw new MBeanException(new ServiceNotFoundException("Method descriptor must include `name' field"), "Method descriptor must include `name' field"); 
    String str3 = (String)descriptor.getFieldValue("targetType");
    if (str3 != null && !str3.equalsIgnoreCase("objectReference")) {
      final String className = "Target type must be objectReference: " + str3;
      throw new MBeanException(new InvalidTargetObjectTypeException(str), str);
    } 
    Object object2 = descriptor.getFieldValue("targetObject");
    if (bool && object2 != null)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Found target object in descriptor"); 
    Method method = findRMMBMethod(str2, object2, str1, paramArrayOfString);
    if (method != null) {
      object3 = this;
    } else {
      Class clazz;
      if (bool)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in managedResource class"); 
      if (object2 != null) {
        object3 = object2;
      } else {
        object3 = this.managedResource;
        if (object3 == null) {
          clazz = "managedResource for invoke " + paramString + " is null";
          ServiceNotFoundException serviceNotFoundException = new ServiceNotFoundException(clazz);
          throw new MBeanException(serviceNotFoundException);
        } 
      } 
      if (str1 != null) {
        try {
          AccessControlContext accessControlContext = AccessController.getContext();
          final Object obj = object3;
          final String className = str1;
          final ClassNotFoundException[] caughtException = new ClassNotFoundException[1];
          clazz = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
                public Class<?> run() {
                  try {
                    ReflectUtil.checkPackageAccess(className);
                    ClassLoader classLoader = obj.getClass().getClassLoader();
                    return Class.forName(className, false, classLoader);
                  } catch (ClassNotFoundException classNotFoundException) {
                    caughtException[0] = classNotFoundException;
                    return null;
                  } 
                }
              }accessControlContext, this.acc);
          if (arrayOfClassNotFoundException[false] != null)
            throw arrayOfClassNotFoundException[0]; 
        } catch (ClassNotFoundException classNotFoundException) {
          final String className = "class for invoke " + paramString + " not found";
          throw new ReflectionException(classNotFoundException, str);
        } 
      } else {
        clazz = object3.getClass();
      } 
      method = resolveMethod(clazz, str2, paramArrayOfString);
    } 
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "found " + str2 + ", now invoking"); 
    Object object4 = invokeMethod(paramString, method, object3, paramArrayOfObject);
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "successfully invoked method"); 
    if (object4 != null)
      cacheResult(modelMBeanOperationInfo, descriptor, object4); 
    return object4;
  }
  
  private Method resolveMethod(Class<?> paramClass, String paramString, final String[] sig) throws ReflectionException {
    final Class[] argClasses;
    final boolean tracing = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolving " + paramClass.getName() + "." + paramString); 
    if (paramArrayOfString == null) {
      arrayOfClass = null;
    } else {
      AccessControlContext accessControlContext = AccessController.getContext();
      final ReflectionException[] caughtException = new ReflectionException[1];
      final ClassLoader targetClassLoader = paramClass.getClassLoader();
      arrayOfClass = new Class[paramArrayOfString.length];
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
              for (byte b = 0; b < sig.length; b++) {
                if (tracing)
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolve type " + sig[b]); 
                argClasses[b] = (Class)primitiveClassMap.get(sig[b]);
                if (argClasses[b] == null)
                  try {
                    ReflectUtil.checkPackageAccess(sig[b]);
                    argClasses[b] = Class.forName(sig[b], false, targetClassLoader);
                  } catch (ClassNotFoundException classNotFoundException) {
                    if (tracing)
                      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "class not found"); 
                    caughtException[0] = new ReflectionException(classNotFoundException, "Parameter class not found");
                  }  
              } 
              return null;
            }
          }accessControlContext, this.acc);
      if (arrayOfReflectionException[false] != null)
        throw arrayOfReflectionException[0]; 
    } 
    try {
      return paramClass.getMethod(paramString, arrayOfClass);
    } catch (NoSuchMethodException noSuchMethodException) {
      String str = "Target method not found: " + paramClass.getName() + "." + paramString;
      throw new ReflectionException(noSuchMethodException, str);
    } 
  }
  
  private Method findRMMBMethod(String paramString1, Object paramObject, String paramString2, String[] paramArrayOfString) {
    Class clazz2;
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in RequiredModelMBean class"); 
    if (!isRMMBMethodName(paramString1))
      return null; 
    if (paramObject != null)
      return null; 
    final Class rmmbClass = RequiredModelMBean.class;
    if (paramString2 == null) {
      clazz2 = clazz1;
    } else {
      AccessControlContext accessControlContext = AccessController.getContext();
      final String className = paramString2;
      clazz2 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
            public Class<?> run() {
              try {
                ReflectUtil.checkPackageAccess(className);
                ClassLoader classLoader = rmmbClass.getClassLoader();
                Class clazz = Class.forName(className, false, classLoader);
                return !rmmbClass.isAssignableFrom(clazz) ? null : clazz;
              } catch (ClassNotFoundException classNotFoundException) {
                return null;
              } 
            }
          }accessControlContext, this.acc);
    } 
    try {
      return (clazz2 != null) ? resolveMethod(clazz2, paramString1, paramArrayOfString) : null;
    } catch (ReflectionException reflectionException) {
      return null;
    } 
  }
  
  private Object invokeMethod(String paramString, final Method method, final Object targetObject, final Object[] opArgs) throws MBeanException, ReflectionException {
    try {
      final Throwable[] caughtException = new Throwable[1];
      AccessControlContext accessControlContext = AccessController.getContext();
      Object object = javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Object>() {
            public Object run() {
              try {
                ReflectUtil.checkPackageAccess(method.getDeclaringClass());
                return MethodUtil.invoke(method, targetObject, opArgs);
              } catch (InvocationTargetException invocationTargetException) {
                caughtException[0] = invocationTargetException;
              } catch (IllegalAccessException illegalAccessException) {
                caughtException[0] = illegalAccessException;
              } 
              return null;
            }
          }accessControlContext, this.acc);
      if (arrayOfThrowable[false] != null) {
        if (arrayOfThrowable[0] instanceof Exception)
          throw (Exception)arrayOfThrowable[0]; 
        if (arrayOfThrowable[0] instanceof Error)
          throw (Error)arrayOfThrowable[0]; 
      } 
      return object;
    } catch (RuntimeErrorException runtimeErrorException) {
      throw new RuntimeOperationsException(runtimeErrorException, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    } catch (RuntimeException runtimeException) {
      throw new RuntimeOperationsException(runtimeException, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionException(illegalAccessException, "IllegalAccessException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof RuntimeException)
        throw new MBeanException((RuntimeException)throwable, "RuntimeException thrown in RequiredModelMBean while trying to invoke operation " + paramString); 
      if (throwable instanceof Error)
        throw new RuntimeErrorException((Error)throwable, "Error occurred in RequiredModelMBean while trying to invoke operation " + paramString); 
      if (throwable instanceof ReflectionException)
        throw (ReflectionException)throwable; 
      throw new MBeanException((Exception)throwable, "Exception thrown in RequiredModelMBean while trying to invoke operation " + paramString);
    } catch (Error error) {
      throw new RuntimeErrorException(error, "Error occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    } catch (Exception exception) {
      throw new ReflectionException(exception, "Exception occurred in RequiredModelMBean while trying to invoke operation " + paramString);
    } 
  }
  
  private void cacheResult(ModelMBeanOperationInfo paramModelMBeanOperationInfo, Descriptor paramDescriptor, Object paramObject) throws MBeanException {
    String str;
    Descriptor descriptor = this.modelMBeanInfo.getMBeanDescriptor();
    Object object = paramDescriptor.getFieldValue("currencyTimeLimit");
    if (object != null) {
      str = object.toString();
    } else {
      str = null;
    } 
    if (str == null && descriptor != null) {
      object = descriptor.getFieldValue("currencyTimeLimit");
      if (object != null) {
        str = object.toString();
      } else {
        str = null;
      } 
    } 
    if (str != null && !str.equals("-1")) {
      paramDescriptor.setField("value", paramObject);
      paramDescriptor.setField("lastUpdatedTimeStamp", String.valueOf((new Date()).getTime()));
      this.modelMBeanInfo.setDescriptor(paramDescriptor, "operation");
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String,Object[],Object[])", "new descriptor is " + paramDescriptor); 
    } 
  }
  
  private static boolean isRMMBMethodName(String paramString) {
    if (rmmbMethodNames == null)
      try {
        HashSet hashSet = new HashSet();
        Method[] arrayOfMethod = RequiredModelMBean.class.getMethods();
        for (byte b = 0; b < arrayOfMethod.length; b++)
          hashSet.add(arrayOfMethod[b].getName()); 
        rmmbMethodNames = hashSet;
      } catch (Exception exception) {
        return true;
      }  
    return rmmbMethodNames.contains(paramString);
  }
  
  public Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException {
    Object object;
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a RequiredModelMBean"); 
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Entry with " + paramString); 
    try {
      if (this.modelMBeanInfo == null)
        throw new AttributeNotFoundException("getAttribute failed: ModelMBeanInfo not found for " + paramString); 
      ModelMBeanAttributeInfo modelMBeanAttributeInfo = this.modelMBeanInfo.getAttribute(paramString);
      Descriptor descriptor1 = this.modelMBeanInfo.getMBeanDescriptor();
      if (modelMBeanAttributeInfo == null)
        throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + paramString); 
      Descriptor descriptor2 = modelMBeanAttributeInfo.getDescriptor();
      if (descriptor2 != null) {
        if (!modelMBeanAttributeInfo.isReadable())
          throw new AttributeNotFoundException("getAttribute failed: " + paramString + " is not readable "); 
        object = resolveForCacheValue(descriptor2);
        if (bool)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "*** cached value is " + object); 
        if (object == null) {
          if (bool)
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "**** cached value is null - getting getMethod"); 
          String str1 = (String)descriptor2.getFieldValue("getMethod");
          if (str1 != null) {
            if (bool)
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "invoking a getMethod for " + paramString); 
            Object object1 = invoke(str1, new Object[0], new String[0]);
            if (object1 != null) {
              String str2;
              if (bool)
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a non-null response from getMethod\n"); 
              object = object1;
              Object object2 = descriptor2.getFieldValue("currencyTimeLimit");
              if (object2 != null) {
                str2 = object2.toString();
              } else {
                str2 = null;
              } 
              if (str2 == null && descriptor1 != null) {
                object2 = descriptor1.getFieldValue("currencyTimeLimit");
                if (object2 != null) {
                  str2 = object2.toString();
                } else {
                  str2 = null;
                } 
              } 
              if (str2 != null && !str2.equals("-1")) {
                if (bool)
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "setting cached value and lastUpdatedTime in descriptor"); 
                descriptor2.setField("value", object);
                String str3 = String.valueOf((new Date()).getTime());
                descriptor2.setField("lastUpdatedTimeStamp", str3);
                modelMBeanAttributeInfo.setDescriptor(descriptor2);
                this.modelMBeanInfo.setDescriptor(descriptor2, "attribute");
                if (bool) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "new descriptor is " + descriptor2);
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "AttributeInfo descriptor is " + modelMBeanAttributeInfo.getDescriptor());
                  String str4 = this.modelMBeanInfo.getDescriptor(paramString, "attribute").toString();
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "modelMBeanInfo: AttributeInfo descriptor is " + str4);
                } 
              } 
            } else {
              if (bool)
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a null response from getMethod\n"); 
              object = null;
            } 
          } else {
            String str2 = "";
            object = descriptor2.getFieldValue("value");
            if (object == null) {
              str2 = "default ";
              object = descriptor2.getFieldValue("default");
            } 
            if (bool)
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "could not find getMethod for " + paramString + ", returning descriptor " + str2 + "value"); 
          } 
        } 
        final String respType = modelMBeanAttributeInfo.getType();
        if (object != null) {
          String str1 = object.getClass().getName();
          if (!str.equals(str1)) {
            boolean bool1 = false;
            boolean bool2 = false;
            boolean bool3 = false;
            byte b;
            for (b = 0; b < primitiveTypes.length; b++) {
              if (str.equals(primitiveTypes[b])) {
                bool2 = true;
                if (str1.equals(primitiveWrappers[b]))
                  bool3 = true; 
                break;
              } 
            } 
            if (bool2) {
              if (!bool3)
                bool1 = true; 
            } else {
              try {
                final Class respClass = object.getClass();
                final Exception[] caughException = new Exception[1];
                AccessControlContext accessControlContext = AccessController.getContext();
                Class clazz2 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
                      public Class<?> run() {
                        try {
                          ReflectUtil.checkPackageAccess(respType);
                          ClassLoader classLoader = respClass.getClassLoader();
                          return Class.forName(respType, true, classLoader);
                        } catch (Exception exception) {
                          caughException[0] = exception;
                          return null;
                        } 
                      }
                    }accessControlContext, this.acc);
                if (arrayOfException[false] != null)
                  throw arrayOfException[0]; 
                boolean bool4 = clazz2.isInstance(object);
              } catch (Exception exception) {
                b = 0;
                if (bool)
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exception: ", exception); 
              } 
              if (b == 0)
                bool1 = true; 
            } 
            if (bool1) {
              if (bool)
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Wrong response type '" + str + "'"); 
              throw new MBeanException(new InvalidAttributeValueException("Wrong value type received for get attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
            } 
          } 
        } 
      } else {
        if (bool)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed " + paramString + " not in attributeDescriptor\n"); 
        throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, no getMethod defined in descriptor for attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
      } 
    } catch (MBeanException mBeanException) {
      throw mBeanException;
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (Exception exception) {
      if (bool)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed with " + exception.getMessage() + " exception type " + exception.getClass().toString()); 
      throw new MBeanException(exception, "An exception occurred while trying to get an attribute value: " + exception.getMessage());
    } 
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exit"); 
    return object;
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Entry"); 
    if (paramArrayOfString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"), "Exception occurred trying to get attributes of a RequiredModelMBean"); 
    AttributeList attributeList = new AttributeList();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      try {
        attributeList.add(new Attribute(paramArrayOfString[b], getAttribute(paramArrayOfString[b])));
      } catch (Exception exception) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Failed to get \"" + paramArrayOfString[b] + "\": ", exception); 
      } 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Exit"); 
    return attributeList;
  }
  
  public void setAttribute(Attribute paramAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute()", "Entry"); 
    if (paramAttribute == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"), "Exception occurred trying to set an attribute of a RequiredModelMBean"); 
    String str = paramAttribute.getName();
    Object object = paramAttribute.getValue();
    boolean bool1 = false;
    ModelMBeanAttributeInfo modelMBeanAttributeInfo = this.modelMBeanInfo.getAttribute(str);
    if (modelMBeanAttributeInfo == null)
      throw new AttributeNotFoundException("setAttribute failed: " + str + " is not found "); 
    Descriptor descriptor1 = this.modelMBeanInfo.getMBeanDescriptor();
    Descriptor descriptor2 = modelMBeanAttributeInfo.getDescriptor();
    if (descriptor2 != null) {
      String str4;
      if (!modelMBeanAttributeInfo.isWritable())
        throw new AttributeNotFoundException("setAttribute failed: " + str + " is not writable "); 
      String str1 = (String)descriptor2.getFieldValue("setMethod");
      String str2 = (String)descriptor2.getFieldValue("getMethod");
      String str3 = modelMBeanAttributeInfo.getType();
      Object object1 = "Unknown";
      try {
        object1 = getAttribute(str);
      } catch (Throwable throwable) {}
      Attribute attribute = new Attribute(str, object1);
      if (str1 == null) {
        if (object != null)
          try {
            Class clazz = loadClass(str3);
            if (!clazz.isInstance(object))
              throw new InvalidAttributeValueException(clazz.getName() + " expected, " + object.getClass().getName() + " received."); 
          } catch (ClassNotFoundException classNotFoundException) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Class " + str3 + " for attribute " + str + " not found: ", classNotFoundException); 
          }  
        bool1 = true;
      } else {
        invoke(str1, new Object[] { object }, new String[] { str3 });
      } 
      Object object2 = descriptor2.getFieldValue("currencyTimeLimit");
      if (object2 != null) {
        str4 = object2.toString();
      } else {
        str4 = null;
      } 
      if (str4 == null && descriptor1 != null) {
        object2 = descriptor1.getFieldValue("currencyTimeLimit");
        if (object2 != null) {
          str4 = object2.toString();
        } else {
          str4 = null;
        } 
      } 
      boolean bool2 = (str4 != null && !str4.equals("-1")) ? 1 : 0;
      if (str1 == null && !bool2 && str2 != null)
        throw new MBeanException(new ServiceNotFoundException("No setMethod field is defined in the descriptor for " + str + " attribute and caching is not enabled for it")); 
      if (bool2 || bool1) {
        if (bool)
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setting cached value of " + str + " to " + object); 
        descriptor2.setField("value", object);
        if (bool2) {
          String str5 = String.valueOf((new Date()).getTime());
          descriptor2.setField("lastUpdatedTimeStamp", str5);
        } 
        modelMBeanAttributeInfo.setDescriptor(descriptor2);
        this.modelMBeanInfo.setDescriptor(descriptor2, "attribute");
        if (bool) {
          StringBuilder stringBuilder = (new StringBuilder()).append("new descriptor is ").append(descriptor2).append(". AttributeInfo descriptor is ").append(modelMBeanAttributeInfo.getDescriptor()).append(". AttributeInfo descriptor is ").append(this.modelMBeanInfo.getDescriptor(str, "attribute"));
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", stringBuilder.toString());
        } 
      } 
      if (bool)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "sending sendAttributeNotification"); 
      sendAttributeChangeNotification(attribute, paramAttribute);
    } else {
      if (bool)
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setMethod failed " + str + " not in attributeDescriptor\n"); 
      throw new InvalidAttributeValueException("Unable to resolve attribute value, no defined in descriptor for attribute");
    } 
    if (bool)
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Exit"); 
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Entry"); 
    if (paramAttributeList == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"), "Exception occurred trying to set attributes of a RequiredModelMBean"); 
    AttributeList attributeList = new AttributeList();
    for (Attribute attribute : paramAttributeList.asList()) {
      try {
        setAttribute(attribute);
        attributeList.add(attribute);
      } catch (Exception exception) {
        attributeList.remove(attribute);
      } 
    } 
    return attributeList;
  }
  
  private ModelMBeanInfo createDefaultModelMBeanInfo() { return new ModelMBeanInfoSupport(getClass().getName(), "Default ModelMBean", null, null, null, null); }
  
  private void writeToLog(String paramString1, String paramString2) throws Exception {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Notification Logging to " + paramString1 + ": " + paramString2); 
    if (paramString1 == null || paramString2 == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Bad input parameters, will not log this entry."); 
      return;
    } 
    fileOutputStream = new FileOutputStream(paramString1, true);
    try {
      PrintStream printStream = new PrintStream(fileOutputStream);
      printStream.println(paramString2);
      printStream.close();
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Successfully opened log " + paramString1); 
    } catch (Exception exception) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Exception " + exception.toString() + " trying to write to the Notification log file " + paramString1); 
      throw exception;
    } finally {
      fileOutputStream.close();
    } 
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry"); 
    if (paramNotificationListener == null)
      throw new IllegalArgumentException("notification listener must not be null"); 
    if (this.generalBroadcaster == null)
      this.generalBroadcaster = new NotificationBroadcasterSupport(); 
    this.generalBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "NotificationListener added");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
    } 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    if (paramNotificationListener == null)
      throw new ListenerNotFoundException("Notification listener is null"); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Entry"); 
    if (this.generalBroadcaster == null)
      throw new ListenerNotFoundException("No notification listeners registered"); 
    this.generalBroadcaster.removeNotificationListener(paramNotificationListener);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Exit"); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException {
    if (paramNotificationListener == null)
      throw new ListenerNotFoundException("Notification listener is null"); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry"); 
    if (this.generalBroadcaster == null)
      throw new ListenerNotFoundException("No notification listeners registered"); 
    this.generalBroadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit"); 
  }
  
  public void sendNotification(Notification paramNotification) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Entry"); 
    if (paramNotification == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("notification object must not be null"), "Exception occurred trying to send a notification from a RequiredModelMBean"); 
    Descriptor descriptor1 = this.modelMBeanInfo.getDescriptor(paramNotification.getType(), "notification");
    Descriptor descriptor2 = this.modelMBeanInfo.getMBeanDescriptor();
    if (descriptor1 != null) {
      String str = (String)descriptor1.getFieldValue("log");
      if (str == null && descriptor2 != null)
        str = (String)descriptor2.getFieldValue("log"); 
      if (str != null && (str.equalsIgnoreCase("t") || str.equalsIgnoreCase("true"))) {
        String str1 = (String)descriptor1.getFieldValue("logfile");
        if (str1 == null && descriptor2 != null)
          str1 = (String)descriptor2.getFieldValue("logfile"); 
        if (str1 != null)
          try {
            writeToLog(str1, "LogMsg: " + (new Date(paramNotification.getTimeStamp())).toString() + " " + paramNotification.getType() + " " + paramNotification.getMessage() + " Severity = " + (String)descriptor1.getFieldValue("severity"));
          } catch (Exception exception) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE))
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Failed to log " + paramNotification.getType() + " notification: ", exception); 
          }  
      } 
    } 
    if (this.generalBroadcaster != null)
      this.generalBroadcaster.sendNotification(paramNotification); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "sendNotification sent provided notification object");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", " Exit");
    } 
  }
  
  public void sendNotification(String paramString) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Entry"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("notification message must not be null"), "Exception occurred trying to send a text notification from a ModelMBean"); 
    Notification notification = new Notification("jmx.modelmbean.generic", this, 1L, paramString);
    sendNotification(notification);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Notification sent");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Exit");
    } 
  }
  
  private static final boolean hasNotification(ModelMBeanInfo paramModelMBeanInfo, String paramString) {
    try {
      return (paramModelMBeanInfo == null) ? false : ((paramModelMBeanInfo.getNotification(paramString) != null));
    } catch (MBeanException mBeanException) {
      return false;
    } catch (RuntimeOperationsException runtimeOperationsException) {
      return false;
    } 
  }
  
  private static final ModelMBeanNotificationInfo makeGenericInfo() {
    DescriptorSupport descriptorSupport = new DescriptorSupport(new String[] { "name=GENERIC", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.modelmbean.generic" });
    return new ModelMBeanNotificationInfo(new String[] { "jmx.modelmbean.generic" }, "GENERIC", "A text notification has been issued by the managed resource", descriptorSupport);
  }
  
  private static final ModelMBeanNotificationInfo makeAttributeChangeInfo() {
    DescriptorSupport descriptorSupport = new DescriptorSupport(new String[] { "name=ATTRIBUTE_CHANGE", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.attribute.change" });
    return new ModelMBeanNotificationInfo(new String[] { "jmx.attribute.change" }, "ATTRIBUTE_CHANGE", "Signifies that an observed MBean attribute value has changed", descriptorSupport);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Entry"); 
    boolean bool1 = hasNotification(this.modelMBeanInfo, "GENERIC");
    boolean bool2 = hasNotification(this.modelMBeanInfo, "ATTRIBUTE_CHANGE");
    ModelMBeanNotificationInfo[] arrayOfModelMBeanNotificationInfo1 = (ModelMBeanNotificationInfo[])this.modelMBeanInfo.getNotifications();
    boolean bool = ((arrayOfModelMBeanNotificationInfo1 == null) ? 0 : arrayOfModelMBeanNotificationInfo1.length) + (bool1 ? 0 : 1) + (bool2 ? 0 : 1);
    ModelMBeanNotificationInfo[] arrayOfModelMBeanNotificationInfo2 = new ModelMBeanNotificationInfo[bool];
    byte b1 = 0;
    if (!bool1)
      arrayOfModelMBeanNotificationInfo2[b1++] = makeGenericInfo(); 
    if (!bool2)
      arrayOfModelMBeanNotificationInfo2[b1++] = makeAttributeChangeInfo(); 
    int i = arrayOfModelMBeanNotificationInfo1.length;
    byte b2 = b1;
    for (byte b3 = 0; b3 < i; b3++)
      arrayOfModelMBeanNotificationInfo2[b2 + b3] = arrayOfModelMBeanNotificationInfo1[b3]; 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Exit"); 
    return arrayOfModelMBeanNotificationInfo2;
  }
  
  public void addAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString, Object paramObject) throws MBeanException, RuntimeOperationsException, IllegalArgumentException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Entry"); 
    if (paramNotificationListener == null)
      throw new IllegalArgumentException("Listener to be registered must not be null"); 
    if (this.attributeBroadcaster == null)
      this.attributeBroadcaster = new NotificationBroadcasterSupport(); 
    AttributeChangeNotificationFilter attributeChangeNotificationFilter = new AttributeChangeNotificationFilter();
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanInfo.getAttributes();
    boolean bool = false;
    if (paramString == null) {
      if (arrayOfMBeanAttributeInfo != null && arrayOfMBeanAttributeInfo.length > 0)
        for (byte b = 0; b < arrayOfMBeanAttributeInfo.length; b++)
          attributeChangeNotificationFilter.enableAttribute(arrayOfMBeanAttributeInfo[b].getName());  
    } else {
      if (arrayOfMBeanAttributeInfo != null && arrayOfMBeanAttributeInfo.length > 0)
        for (byte b = 0; b < arrayOfMBeanAttributeInfo.length; b++) {
          if (paramString.equals(arrayOfMBeanAttributeInfo[b].getName())) {
            bool = true;
            attributeChangeNotificationFilter.enableAttribute(paramString);
            break;
          } 
        }  
      if (!bool)
        throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name does not exist"), "Exception occurred trying to add an AttributeChangeNotification listener"); 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      Vector vector = attributeChangeNotificationFilter.getEnabledAttributes();
      String str = (vector.size() > 1) ? ("[" + (String)vector.firstElement() + ", ...]") : vector.toString();
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Set attribute change filter to " + str);
    } 
    this.attributeBroadcaster.addNotificationListener(paramNotificationListener, attributeChangeNotificationFilter, paramObject);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Notification listener added for " + paramString);
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Exit");
    } 
  }
  
  public void removeAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString) throws MBeanException, RuntimeOperationsException, ListenerNotFoundException {
    if (paramNotificationListener == null)
      throw new ListenerNotFoundException("Notification listener is null"); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Entry"); 
    if (this.attributeBroadcaster == null)
      throw new ListenerNotFoundException("No attribute change notification listeners registered"); 
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanInfo.getAttributes();
    boolean bool = false;
    if (arrayOfMBeanAttributeInfo != null && arrayOfMBeanAttributeInfo.length > 0)
      for (byte b = 0; b < arrayOfMBeanAttributeInfo.length; b++) {
        if (arrayOfMBeanAttributeInfo[b].getName().equals(paramString)) {
          bool = true;
          break;
        } 
      }  
    if (!bool && paramString != null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid attribute name"), "Exception occurred trying to remove attribute change notification listener"); 
    this.attributeBroadcaster.removeNotificationListener(paramNotificationListener);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Exit"); 
  }
  
  public void sendAttributeChangeNotification(AttributeChangeNotification paramAttributeChangeNotification) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Entry"); 
    if (paramAttributeChangeNotification == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("attribute change notification object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean"); 
    Object object1 = paramAttributeChangeNotification.getOldValue();
    Object object2 = paramAttributeChangeNotification.getNewValue();
    if (object1 == null)
      object1 = "null"; 
    if (object2 == null)
      object2 = "null"; 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Sending AttributeChangeNotification with " + paramAttributeChangeNotification.getAttributeName() + paramAttributeChangeNotification.getAttributeType() + paramAttributeChangeNotification.getNewValue() + paramAttributeChangeNotification.getOldValue()); 
    Descriptor descriptor1 = this.modelMBeanInfo.getDescriptor(paramAttributeChangeNotification.getType(), "notification");
    Descriptor descriptor2 = this.modelMBeanInfo.getMBeanDescriptor();
    if (descriptor1 != null) {
      String str = (String)descriptor1.getFieldValue("log");
      if (str == null && descriptor2 != null)
        str = (String)descriptor2.getFieldValue("log"); 
      if (str != null && (str.equalsIgnoreCase("t") || str.equalsIgnoreCase("true"))) {
        String str1 = (String)descriptor1.getFieldValue("logfile");
        if (str1 == null && descriptor2 != null)
          str1 = (String)descriptor2.getFieldValue("logfile"); 
        if (str1 != null)
          try {
            writeToLog(str1, "LogMsg: " + (new Date(paramAttributeChangeNotification.getTimeStamp())).toString() + " " + paramAttributeChangeNotification.getType() + " " + paramAttributeChangeNotification.getMessage() + " Name = " + paramAttributeChangeNotification.getAttributeName() + " Old value = " + object1 + " New value = " + object2);
          } catch (Exception exception) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE))
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + paramAttributeChangeNotification.getType() + " notification: ", exception); 
          }  
      } 
    } else if (descriptor2 != null) {
      String str = (String)descriptor2.getFieldValue("log");
      if (str != null && (str.equalsIgnoreCase("t") || str.equalsIgnoreCase("true"))) {
        String str1 = (String)descriptor2.getFieldValue("logfile");
        if (str1 != null)
          try {
            writeToLog(str1, "LogMsg: " + (new Date(paramAttributeChangeNotification.getTimeStamp())).toString() + " " + paramAttributeChangeNotification.getType() + " " + paramAttributeChangeNotification.getMessage() + " Name = " + paramAttributeChangeNotification.getAttributeName() + " Old value = " + object1 + " New value = " + object2);
          } catch (Exception exception) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE))
              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + paramAttributeChangeNotification.getType() + " notification: ", exception); 
          }  
      } 
    } 
    if (this.attributeBroadcaster != null)
      this.attributeBroadcaster.sendNotification(paramAttributeChangeNotification); 
    if (this.generalBroadcaster != null)
      this.generalBroadcaster.sendNotification(paramAttributeChangeNotification); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "sent notification");
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Exit");
    } 
  }
  
  public void sendAttributeChangeNotification(Attribute paramAttribute1, Attribute paramAttribute2) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Entry"); 
    if (paramAttribute1 == null || paramAttribute2 == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean"); 
    if (!paramAttribute1.getName().equals(paramAttribute2.getName()))
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute names are not the same"), "Exception occurred trying to send attribute change notification of a ModelMBean"); 
    Object object1 = paramAttribute2.getValue();
    Object object2 = paramAttribute1.getValue();
    String str = "unknown";
    if (object1 != null)
      str = object1.getClass().getName(); 
    if (object2 != null)
      str = object2.getClass().getName(); 
    AttributeChangeNotification attributeChangeNotification = new AttributeChangeNotification(this, 1L, (new Date()).getTime(), "AttributeChangeDetected", paramAttribute1.getName(), str, paramAttribute1.getValue(), paramAttribute2.getValue());
    sendAttributeChangeNotification(attributeChangeNotification);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Exit"); 
  }
  
  protected ClassLoaderRepository getClassLoaderRepository() { return MBeanServerFactory.getClassLoaderRepository(this.server); }
  
  private Class<?> loadClass(final String className) throws ClassNotFoundException {
    AccessControlContext accessControlContext = AccessController.getContext();
    final ClassNotFoundException[] caughtException = new ClassNotFoundException[1];
    Class clazz = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
          public Class<?> run() {
            try {
              ReflectUtil.checkPackageAccess(className);
              return Class.forName(className);
            } catch (ClassNotFoundException classNotFoundException) {
              ClassLoaderRepository classLoaderRepository = RequiredModelMBean.this.getClassLoaderRepository();
              try {
                if (classLoaderRepository == null)
                  throw new ClassNotFoundException(className); 
                return classLoaderRepository.loadClass(className);
              } catch (ClassNotFoundException classNotFoundException1) {
                caughtException[0] = classNotFoundException1;
                return null;
              } 
            } 
          }
        },  accessControlContext, this.acc);
    if (arrayOfClassNotFoundException[false] != null)
      throw arrayOfClassNotFoundException[0]; 
    return clazz;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    if (paramObjectName == null)
      throw new NullPointerException("name of RequiredModelMBean to registered is null"); 
    this.server = paramMBeanServer;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) { this.registered = paramBoolean.booleanValue(); }
  
  public void preDeregister() throws MBeanException, RuntimeOperationsException {}
  
  public void postDeregister() throws MBeanException, RuntimeOperationsException {
    this.registered = false;
    this.server = null;
  }
  
  static  {
    for (byte b = 0; b < primitiveClasses.length; b++) {
      Class clazz = primitiveClasses[b];
      primitiveClassMap.put(clazz.getName(), clazz);
    } 
    primitiveTypes = new String[] { boolean.class.getName(), byte.class.getName(), char.class.getName(), short.class.getName(), int.class.getName(), long.class.getName(), float.class.getName(), double.class.getName(), void.class.getName() };
    primitiveWrappers = new String[] { Boolean.class.getName(), Byte.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), Void.class.getName() };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\RequiredModelMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */