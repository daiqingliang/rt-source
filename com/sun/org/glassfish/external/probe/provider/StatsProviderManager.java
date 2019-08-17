package com.sun.org.glassfish.external.probe.provider;

import java.util.Vector;

public class StatsProviderManager {
  static StatsProviderManagerDelegate spmd;
  
  static Vector<StatsProviderInfo> toBeRegistered = new Vector();
  
  public static boolean register(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject) { return register(paramPluginPoint, paramString1, paramString2, paramObject, null); }
  
  public static boolean register(PluginPoint paramPluginPoint, String paramString1, String paramString2, Object paramObject, String paramString3) {
    StatsProviderInfo statsProviderInfo = new StatsProviderInfo(paramString1, paramPluginPoint, paramString2, paramObject, paramString3);
    return registerStatsProvider(statsProviderInfo);
  }
  
  public static boolean register(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject, String paramString3) { return register(paramString1, paramPluginPoint, paramString2, paramObject, paramString3, null); }
  
  public static boolean register(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject, String paramString3, String paramString4) {
    StatsProviderInfo statsProviderInfo = new StatsProviderInfo(paramString1, paramPluginPoint, paramString2, paramObject, paramString4);
    statsProviderInfo.setConfigLevel(paramString3);
    return registerStatsProvider(statsProviderInfo);
  }
  
  private static boolean registerStatsProvider(StatsProviderInfo paramStatsProviderInfo) {
    if (spmd == null) {
      toBeRegistered.add(paramStatsProviderInfo);
    } else {
      spmd.register(paramStatsProviderInfo);
      return true;
    } 
    return false;
  }
  
  public static boolean unregister(Object paramObject) {
    if (spmd == null) {
      for (StatsProviderInfo statsProviderInfo : toBeRegistered) {
        if (statsProviderInfo.getStatsProvider() == paramObject) {
          toBeRegistered.remove(statsProviderInfo);
          break;
        } 
      } 
    } else {
      spmd.unregister(paramObject);
      return true;
    } 
    return false;
  }
  
  public static boolean hasListeners(String paramString) { return (spmd == null) ? false : spmd.hasListeners(paramString); }
  
  public static void setStatsProviderManagerDelegate(StatsProviderManagerDelegate paramStatsProviderManagerDelegate) {
    if (paramStatsProviderManagerDelegate == null)
      return; 
    spmd = paramStatsProviderManagerDelegate;
    for (StatsProviderInfo statsProviderInfo : toBeRegistered)
      spmd.register(statsProviderInfo); 
    toBeRegistered.clear();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\probe\provider\StatsProviderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */