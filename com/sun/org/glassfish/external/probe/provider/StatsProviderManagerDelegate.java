package com.sun.org.glassfish.external.probe.provider;

public interface StatsProviderManagerDelegate {
  void register(StatsProviderInfo paramStatsProviderInfo);
  
  void unregister(Object paramObject);
  
  boolean hasListeners(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\probe\provider\StatsProviderManagerDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */