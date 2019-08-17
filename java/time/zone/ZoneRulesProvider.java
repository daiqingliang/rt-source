package java.time.zone;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ZoneRulesProvider {
  private static final CopyOnWriteArrayList<ZoneRulesProvider> PROVIDERS = new CopyOnWriteArrayList();
  
  private static final ConcurrentMap<String, ZoneRulesProvider> ZONES = new ConcurrentHashMap(512, 0.75F, 2);
  
  public static Set<String> getAvailableZoneIds() { return new HashSet(ZONES.keySet()); }
  
  public static ZoneRules getRules(String paramString, boolean paramBoolean) {
    Objects.requireNonNull(paramString, "zoneId");
    return getProvider(paramString).provideRules(paramString, paramBoolean);
  }
  
  public static NavigableMap<String, ZoneRules> getVersions(String paramString) {
    Objects.requireNonNull(paramString, "zoneId");
    return getProvider(paramString).provideVersions(paramString);
  }
  
  private static ZoneRulesProvider getProvider(String paramString) {
    ZoneRulesProvider zoneRulesProvider = (ZoneRulesProvider)ZONES.get(paramString);
    if (zoneRulesProvider == null) {
      if (ZONES.isEmpty())
        throw new ZoneRulesException("No time-zone data files registered"); 
      throw new ZoneRulesException("Unknown time-zone ID: " + paramString);
    } 
    return zoneRulesProvider;
  }
  
  public static void registerProvider(ZoneRulesProvider paramZoneRulesProvider) {
    Objects.requireNonNull(paramZoneRulesProvider, "provider");
    registerProvider0(paramZoneRulesProvider);
    PROVIDERS.add(paramZoneRulesProvider);
  }
  
  private static void registerProvider0(ZoneRulesProvider paramZoneRulesProvider) {
    for (String str : paramZoneRulesProvider.provideZoneIds()) {
      Objects.requireNonNull(str, "zoneId");
      ZoneRulesProvider zoneRulesProvider = (ZoneRulesProvider)ZONES.putIfAbsent(str, paramZoneRulesProvider);
      if (zoneRulesProvider != null)
        throw new ZoneRulesException("Unable to register zone as one already registered with that ID: " + str + ", currently loading from provider: " + paramZoneRulesProvider); 
    } 
  }
  
  public static boolean refresh() {
    boolean bool = false;
    for (ZoneRulesProvider zoneRulesProvider : PROVIDERS)
      bool |= zoneRulesProvider.provideRefresh(); 
    return bool;
  }
  
  protected abstract Set<String> provideZoneIds();
  
  protected abstract ZoneRules provideRules(String paramString, boolean paramBoolean);
  
  protected abstract NavigableMap<String, ZoneRules> provideVersions(String paramString);
  
  protected boolean provideRefresh() { return false; }
  
  static  {
    final ArrayList loaded = new ArrayList();
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            String str = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
            if (str != null) {
              try {
                Class clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
                ZoneRulesProvider zoneRulesProvider;
                (zoneRulesProvider = (ZoneRulesProvider)ZoneRulesProvider.class.cast(clazz.newInstance())).registerProvider(zoneRulesProvider);
                loaded.add(zoneRulesProvider);
              } catch (Exception exception) {
                throw new Error(exception);
              } 
            } else {
              ZoneRulesProvider.registerProvider(new TzdbZoneRulesProvider());
            } 
            return null;
          }
        });
    ServiceLoader serviceLoader = ServiceLoader.load(ZoneRulesProvider.class, ClassLoader.getSystemClassLoader());
    Iterator iterator = serviceLoader.iterator();
    while (iterator.hasNext()) {
      ZoneRulesProvider zoneRulesProvider;
      try {
        zoneRulesProvider = (ZoneRulesProvider)iterator.next();
      } catch (ServiceConfigurationError serviceConfigurationError) {
        if (serviceConfigurationError.getCause() instanceof SecurityException)
          continue; 
        throw serviceConfigurationError;
      } 
      boolean bool = false;
      for (ZoneRulesProvider zoneRulesProvider1 : arrayList) {
        if (zoneRulesProvider1.getClass() == zoneRulesProvider.getClass())
          bool = true; 
      } 
      if (!bool) {
        registerProvider0(zoneRulesProvider);
        arrayList.add(zoneRulesProvider);
      } 
    } 
    PROVIDERS.addAll(arrayList);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\zone\ZoneRulesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */