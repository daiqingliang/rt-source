package java.lang.management;

import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.OperatingSystemMXBean;
import com.sun.management.UnixOperatingSystemMXBean;
import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformComponent;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import sun.management.ManagementFactoryHelper;
import sun.management.Util;

static enum PlatformComponent {
  CLASS_LOADING,
  COMPILATION,
  MEMORY,
  GARBAGE_COLLECTOR,
  MEMORY_MANAGER,
  MEMORY_POOL,
  OPERATING_SYSTEM,
  RUNTIME,
  THREADING,
  LOGGING,
  BUFFER_POOL,
  SUN_GARBAGE_COLLECTOR,
  SUN_OPERATING_SYSTEM,
  SUN_UNIX_OPERATING_SYSTEM,
  HOTSPOT_DIAGNOSTIC("com.sun.management.HotSpotDiagnosticMXBean", "com.sun.management", "HotSpotDiagnostic", (SUN_UNIX_OPERATING_SYSTEM = new PlatformComponent("SUN_UNIX_OPERATING_SYSTEM", 13, "com.sun.management.UnixOperatingSystemMXBean", "java.lang", "OperatingSystem", (SUN_OPERATING_SYSTEM = new PlatformComponent("SUN_OPERATING_SYSTEM", 12, "com.sun.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", (SUN_GARBAGE_COLLECTOR = new PlatformComponent("SUN_GARBAGE_COLLECTOR", 11, "com.sun.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", (BUFFER_POOL = new PlatformComponent("BUFFER_POOL", 10, "java.lang.management.BufferPoolMXBean", "java.nio", "BufferPool", (LOGGING = new PlatformComponent("LOGGING", 9, "java.lang.management.PlatformLoggingMXBean", "java.util.logging", "Logging", (THREADING = new PlatformComponent("THREADING", 8, "java.lang.management.ThreadMXBean", "java.lang", "Threading", (RUNTIME = new PlatformComponent("RUNTIME", 7, "java.lang.management.RuntimeMXBean", "java.lang", "Runtime", (OPERATING_SYSTEM = new PlatformComponent("OPERATING_SYSTEM", 6, "java.lang.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", (MEMORY_POOL = new PlatformComponent("MEMORY_POOL", 5, "java.lang.management.MemoryPoolMXBean", "java.lang", "MemoryPool", (MEMORY_MANAGER = new PlatformComponent("MEMORY_MANAGER", 4, "java.lang.management.MemoryManagerMXBean", "java.lang", "MemoryManager", (GARBAGE_COLLECTOR = new PlatformComponent("GARBAGE_COLLECTOR", 3, "java.lang.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", (MEMORY = new PlatformComponent("MEMORY", 2, "java.lang.management.MemoryMXBean", "java.lang", "Memory", (COMPILATION = new PlatformComponent("COMPILATION", 1, "java.lang.management.CompilationMXBean", "java.lang", "Compilation", (CLASS_LOADING = new PlatformComponent("CLASS_LOADING", 0, "java.lang.management.ClassLoadingMXBean", "java.lang", "ClassLoading", defaultKeyProperties(), true, new MXBeanFetcher<ClassLoadingMXBean>() {
                                  public List<ClassLoadingMXBean> getMXBeans() { return Collections.singletonList(ManagementFactoryHelper.getClassLoadingMXBean()); }
                                },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<CompilationMXBean>() {
                                public List<CompilationMXBean> getMXBeans() {
                                  CompilationMXBean compilationMXBean = ManagementFactoryHelper.getCompilationMXBean();
                                  return (compilationMXBean == null) ? Collections.emptyList() : Collections.singletonList(compilationMXBean);
                                }
                              },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<MemoryMXBean>() {
                              public List<MemoryMXBean> getMXBeans() { return Collections.singletonList(ManagementFactoryHelper.getMemoryMXBean()); }
                            },  new PlatformComponent[0])).keyProperties(new String[] { "name" }), false, new MXBeanFetcher<GarbageCollectorMXBean>() {
                            public List<GarbageCollectorMXBean> getMXBeans() { return ManagementFactoryHelper.getGarbageCollectorMXBeans(); }
                          },  new PlatformComponent[0])).keyProperties(new String[] { "name" }), false, new MXBeanFetcher<MemoryManagerMXBean>() {
                          public List<MemoryManagerMXBean> getMXBeans() { return ManagementFactoryHelper.getMemoryManagerMXBeans(); }
                        },  new PlatformComponent[] { GARBAGE_COLLECTOR })).keyProperties(new String[] { "name" }), false, new MXBeanFetcher<MemoryPoolMXBean>() {
                        public List<MemoryPoolMXBean> getMXBeans() { return ManagementFactoryHelper.getMemoryPoolMXBeans(); }
                      },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<OperatingSystemMXBean>() {
                      public List<OperatingSystemMXBean> getMXBeans() { return Collections.singletonList(ManagementFactoryHelper.getOperatingSystemMXBean()); }
                    },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<RuntimeMXBean>() {
                    public List<RuntimeMXBean> getMXBeans() { return Collections.singletonList(ManagementFactoryHelper.getRuntimeMXBean()); }
                  },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<ThreadMXBean>() {
                  public List<ThreadMXBean> getMXBeans() { return Collections.singletonList(ManagementFactoryHelper.getThreadMXBean()); }
                },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<PlatformLoggingMXBean>() {
                public List<PlatformLoggingMXBean> getMXBeans() {
                  PlatformLoggingMXBean platformLoggingMXBean = ManagementFactoryHelper.getPlatformLoggingMXBean();
                  return (platformLoggingMXBean == null) ? Collections.emptyList() : Collections.singletonList(platformLoggingMXBean);
                }
              },  new PlatformComponent[0])).keyProperties(new String[] { "name" }), false, new MXBeanFetcher<BufferPoolMXBean>() {
              public List<BufferPoolMXBean> getMXBeans() { return ManagementFactoryHelper.getBufferPoolMXBeans(); }
            },  new PlatformComponent[0])).keyProperties(new String[] { "name" }), false, new MXBeanFetcher<GarbageCollectorMXBean>() {
            public List<GarbageCollectorMXBean> getMXBeans() { return PlatformComponent.getGcMXBeanList(GarbageCollectorMXBean.class); }
          },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<OperatingSystemMXBean>() {
          public List<OperatingSystemMXBean> getMXBeans() { return PlatformComponent.getOSMXBeanList(OperatingSystemMXBean.class); }
        },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<UnixOperatingSystemMXBean>() {
        public List<UnixOperatingSystemMXBean> getMXBeans() { return PlatformComponent.getOSMXBeanList(UnixOperatingSystemMXBean.class); }
      },  new PlatformComponent[0])).defaultKeyProperties(), true, new MXBeanFetcher<HotSpotDiagnosticMXBean>() {
      public List<HotSpotDiagnosticMXBean> getMXBeans() { return Collections.singletonList(ManagementFactoryHelper.getDiagnosticMXBean()); }
    },  new PlatformComponent[0]);
  
  private final String mxbeanInterfaceName;
  
  private final String domain;
  
  private final String type;
  
  private final Set<String> keyProperties;
  
  private final MXBeanFetcher<?> fetcher;
  
  private final PlatformComponent[] subComponents;
  
  private final boolean singleton;
  
  private static Set<String> defaultKeyProps;
  
  private static Map<String, PlatformComponent> enumMap;
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  private static <T extends GarbageCollectorMXBean> List<T> getGcMXBeanList(Class<T> paramClass) {
    List list = ManagementFactoryHelper.getGarbageCollectorMXBeans();
    ArrayList arrayList = new ArrayList(list.size());
    for (GarbageCollectorMXBean garbageCollectorMXBean : list) {
      if (paramClass.isInstance(garbageCollectorMXBean))
        arrayList.add(paramClass.cast(garbageCollectorMXBean)); 
    } 
    return arrayList;
  }
  
  private static <T extends OperatingSystemMXBean> List<T> getOSMXBeanList(Class<T> paramClass) {
    OperatingSystemMXBean operatingSystemMXBean = ManagementFactoryHelper.getOperatingSystemMXBean();
    return paramClass.isInstance(operatingSystemMXBean) ? Collections.singletonList(paramClass.cast(operatingSystemMXBean)) : Collections.emptyList();
  }
  
  PlatformComponent(String paramString1, String paramString2, Set<String> paramSet, boolean paramBoolean, MXBeanFetcher<?> paramMXBeanFetcher, PlatformComponent[] paramArrayOfPlatformComponent1, PlatformComponent... paramVarArgs1) {
    this.mxbeanInterfaceName = paramString1;
    this.domain = paramString2;
    this.type = paramSet;
    this.keyProperties = paramBoolean;
    this.singleton = paramMXBeanFetcher;
    this.fetcher = paramArrayOfPlatformComponent1;
    this.subComponents = paramVarArgs1;
  }
  
  private static Set<String> defaultKeyProperties() {
    if (defaultKeyProps == null)
      defaultKeyProps = Collections.singleton("type"); 
    return defaultKeyProps;
  }
  
  private static Set<String> keyProperties(String... paramVarArgs) {
    HashSet hashSet = new HashSet();
    hashSet.add("type");
    for (String str : paramVarArgs)
      hashSet.add(str); 
    return hashSet;
  }
  
  boolean isSingleton() { return this.singleton; }
  
  String getMXBeanInterfaceName() { return this.mxbeanInterfaceName; }
  
  Class<? extends PlatformManagedObject> getMXBeanInterface() {
    try {
      return Class.forName(this.mxbeanInterfaceName, false, PlatformManagedObject.class.getClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new AssertionError(classNotFoundException);
    } 
  }
  
  <T extends PlatformManagedObject> List<T> getMXBeans(Class<T> paramClass) { return this.fetcher.getMXBeans(); }
  
  <T extends PlatformManagedObject> T getSingletonMXBean(Class<T> paramClass) {
    if (!this.singleton)
      throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances"); 
    List list = getMXBeans(paramClass);
    assert list.size() == 1;
    return (T)(list.isEmpty() ? null : (PlatformManagedObject)list.get(0));
  }
  
  <T extends PlatformManagedObject> T getSingletonMXBean(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass) throws IOException {
    if (!this.singleton)
      throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances"); 
    assert this.keyProperties.size() == 1;
    String str = this.domain + ":type=" + this.type;
    return (T)(PlatformManagedObject)ManagementFactory.newPlatformMXBeanProxy(paramMBeanServerConnection, str, paramClass);
  }
  
  <T extends PlatformManagedObject> List<T> getMXBeans(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass) throws IOException {
    ArrayList arrayList = new ArrayList();
    for (ObjectName objectName : getObjectNames(paramMBeanServerConnection))
      arrayList.add(ManagementFactory.newPlatformMXBeanProxy(paramMBeanServerConnection, objectName.getCanonicalName(), paramClass)); 
    return arrayList;
  }
  
  private Set<ObjectName> getObjectNames(MBeanServerConnection paramMBeanServerConnection) throws IOException {
    String str = this.domain + ":type=" + this.type;
    if (this.keyProperties.size() > 1)
      str = str + ",*"; 
    ObjectName objectName = Util.newObjectName(str);
    Set set = paramMBeanServerConnection.queryNames(objectName, null);
    for (PlatformComponent platformComponent : this.subComponents)
      set.addAll(platformComponent.getObjectNames(paramMBeanServerConnection)); 
    return set;
  }
  
  private static void ensureInitialized() {
    if (enumMap == null) {
      enumMap = new HashMap();
      for (PlatformComponent platformComponent : values())
        enumMap.put(platformComponent.getMXBeanInterfaceName(), platformComponent); 
    } 
  }
  
  static boolean isPlatformMXBean(String paramString) {
    ensureInitialized();
    return enumMap.containsKey(paramString);
  }
  
  static <T extends PlatformManagedObject> PlatformComponent getPlatformComponent(Class<T> paramClass) {
    ensureInitialized();
    String str = paramClass.getName();
    PlatformComponent platformComponent = (PlatformComponent)enumMap.get(str);
    return (platformComponent != null && platformComponent.getMXBeanInterface() == paramClass) ? platformComponent : null;
  }
  
  static interface MXBeanFetcher<T extends PlatformManagedObject> {
    List<T> getMXBeans();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\PlatformComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */