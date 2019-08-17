package java.lang.management;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformComponent;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import javax.management.StandardEmitterMBean;
import javax.management.StandardMBean;
import sun.management.ExtendedPlatformComponent;
import sun.management.ManagementFactoryHelper;
import sun.misc.VM;

public class ManagementFactory {
  public static final String CLASS_LOADING_MXBEAN_NAME = "java.lang:type=ClassLoading";
  
  public static final String COMPILATION_MXBEAN_NAME = "java.lang:type=Compilation";
  
  public static final String MEMORY_MXBEAN_NAME = "java.lang:type=Memory";
  
  public static final String OPERATING_SYSTEM_MXBEAN_NAME = "java.lang:type=OperatingSystem";
  
  public static final String RUNTIME_MXBEAN_NAME = "java.lang:type=Runtime";
  
  public static final String THREAD_MXBEAN_NAME = "java.lang:type=Threading";
  
  public static final String GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE = "java.lang:type=GarbageCollector";
  
  public static final String MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryManager";
  
  public static final String MEMORY_POOL_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryPool";
  
  private static MBeanServer platformMBeanServer;
  
  private static final String NOTIF_EMITTER = "javax.management.NotificationEmitter";
  
  public static ClassLoadingMXBean getClassLoadingMXBean() { return ManagementFactoryHelper.getClassLoadingMXBean(); }
  
  public static MemoryMXBean getMemoryMXBean() { return ManagementFactoryHelper.getMemoryMXBean(); }
  
  public static ThreadMXBean getThreadMXBean() { return ManagementFactoryHelper.getThreadMXBean(); }
  
  public static RuntimeMXBean getRuntimeMXBean() { return ManagementFactoryHelper.getRuntimeMXBean(); }
  
  public static CompilationMXBean getCompilationMXBean() { return ManagementFactoryHelper.getCompilationMXBean(); }
  
  public static OperatingSystemMXBean getOperatingSystemMXBean() { return ManagementFactoryHelper.getOperatingSystemMXBean(); }
  
  public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() { return ManagementFactoryHelper.getMemoryPoolMXBeans(); }
  
  public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() { return ManagementFactoryHelper.getMemoryManagerMXBeans(); }
  
  public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() { return ManagementFactoryHelper.getGarbageCollectorMXBeans(); }
  
  public static MBeanServer getPlatformMBeanServer() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanServerPermission mBeanServerPermission = new MBeanServerPermission("createMBeanServer");
      securityManager.checkPermission(mBeanServerPermission);
    } 
    if (platformMBeanServer == null) {
      platformMBeanServer = MBeanServerFactory.createMBeanServer();
      for (PlatformComponent platformComponent : PlatformComponent.values()) {
        List list = platformComponent.getMXBeans(platformComponent.getMXBeanInterface());
        for (PlatformManagedObject platformManagedObject : list) {
          if (!platformMBeanServer.isRegistered(platformManagedObject.getObjectName()))
            addMXBean(platformMBeanServer, platformManagedObject); 
        } 
      } 
      HashMap hashMap = ManagementFactoryHelper.getPlatformDynamicMBeans();
      for (Map.Entry entry : hashMap.entrySet())
        addDynamicMBean(platformMBeanServer, (DynamicMBean)entry.getValue(), (ObjectName)entry.getKey()); 
      for (PlatformManagedObject platformManagedObject : ExtendedPlatformComponent.getMXBeans()) {
        if (!platformMBeanServer.isRegistered(platformManagedObject.getObjectName()))
          addMXBean(platformMBeanServer, platformManagedObject); 
      } 
    } 
    return platformMBeanServer;
  }
  
  public static <T> T newPlatformMXBeanProxy(MBeanServerConnection paramMBeanServerConnection, String paramString, Class<T> paramClass) throws IOException {
    final Class<T> cls = paramClass;
    ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return cls.getClassLoader(); }
        });
    if (!VM.isSystemDomainLoader(classLoader))
      throw new IllegalArgumentException(paramString + " is not a platform MXBean"); 
    try {
      ObjectName objectName = new ObjectName(paramString);
      String str = paramClass.getName();
      if (!paramMBeanServerConnection.isInstanceOf(objectName, str))
        throw new IllegalArgumentException(paramString + " is not an instance of " + paramClass); 
      boolean bool = paramMBeanServerConnection.isInstanceOf(objectName, "javax.management.NotificationEmitter");
      return (T)JMX.newMXBeanProxy(paramMBeanServerConnection, objectName, paramClass, bool);
    } catch (InstanceNotFoundException|javax.management.MalformedObjectNameException instanceNotFoundException) {
      throw new IllegalArgumentException(instanceNotFoundException);
    } 
  }
  
  public static <T extends PlatformManagedObject> T getPlatformMXBean(Class<T> paramClass) {
    PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (platformComponent == null) {
      PlatformManagedObject platformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (platformManagedObject != null)
        return (T)platformManagedObject; 
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    } 
    if (!platformComponent.isSingleton())
      throw new IllegalArgumentException(paramClass.getName() + " can have zero or more than one instances"); 
    return (T)platformComponent.getSingletonMXBean(paramClass);
  }
  
  public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(Class<T> paramClass) {
    PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (platformComponent == null) {
      PlatformManagedObject platformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (platformManagedObject != null)
        return Collections.singletonList(platformManagedObject); 
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    } 
    return Collections.unmodifiableList(platformComponent.getMXBeans(paramClass));
  }
  
  public static <T extends PlatformManagedObject> T getPlatformMXBean(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass) throws IOException {
    PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (platformComponent == null) {
      PlatformManagedObject platformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (platformManagedObject != null) {
        ObjectName objectName = platformManagedObject.getObjectName();
        return (T)(PlatformManagedObject)newPlatformMXBeanProxy(paramMBeanServerConnection, objectName.getCanonicalName(), paramClass);
      } 
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    } 
    if (!platformComponent.isSingleton())
      throw new IllegalArgumentException(paramClass.getName() + " can have zero or more than one instances"); 
    return (T)platformComponent.getSingletonMXBean(paramMBeanServerConnection, paramClass);
  }
  
  public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass) throws IOException {
    PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (platformComponent == null) {
      PlatformManagedObject platformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (platformManagedObject != null) {
        ObjectName objectName = platformManagedObject.getObjectName();
        PlatformManagedObject platformManagedObject1 = (PlatformManagedObject)newPlatformMXBeanProxy(paramMBeanServerConnection, objectName.getCanonicalName(), paramClass);
        return Collections.singletonList(platformManagedObject1);
      } 
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    } 
    return Collections.unmodifiableList(platformComponent.getMXBeans(paramMBeanServerConnection, paramClass));
  }
  
  public static Set<Class<? extends PlatformManagedObject>> getPlatformManagementInterfaces() {
    HashSet hashSet = new HashSet();
    for (PlatformComponent platformComponent : PlatformComponent.values())
      hashSet.add(platformComponent.getMXBeanInterface()); 
    return Collections.unmodifiableSet(hashSet);
  }
  
  private static void addMXBean(final MBeanServer mbs, final PlatformManagedObject pmo) {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
              StandardMBean standardMBean;
              if (pmo instanceof DynamicMBean) {
                standardMBean = (DynamicMBean)DynamicMBean.class.cast(pmo);
              } else if (pmo instanceof NotificationEmitter) {
                standardMBean = new StandardEmitterMBean(pmo, null, true, (NotificationEmitter)pmo);
              } else {
                standardMBean = new StandardMBean(pmo, null, true);
              } 
              mbs.registerMBean(standardMBean, pmo.getObjectName());
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new RuntimeException(privilegedActionException.getException());
    } 
  }
  
  private static void addDynamicMBean(final MBeanServer mbs, final DynamicMBean dmbean, final ObjectName on) {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
              mbs.registerMBean(dmbean, on);
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new RuntimeException(privilegedActionException.getException());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\ManagementFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */