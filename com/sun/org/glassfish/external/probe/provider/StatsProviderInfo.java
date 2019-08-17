package com.sun.org.glassfish.external.probe.provider;

public class StatsProviderInfo {
  private String configElement;
  
  private PluginPoint pp;
  
  private String subTreeRoot;
  
  private Object statsProvider;
  
  private String configLevelStr = null;
  
  private final String invokerId;
  
  public StatsProviderInfo(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject) { this(paramString1, paramPluginPoint, paramString2, paramObject, null); }
  
  public StatsProviderInfo(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject, String paramString3) {
    this.configElement = paramString1;
    this.pp = paramPluginPoint;
    this.subTreeRoot = paramString2;
    this.statsProvider = paramObject;
    this.invokerId = paramString3;
  }
  
  public String getConfigElement() { return this.configElement; }
  
  public PluginPoint getPluginPoint() { return this.pp; }
  
  public String getSubTreeRoot() { return this.subTreeRoot; }
  
  public Object getStatsProvider() { return this.statsProvider; }
  
  public String getConfigLevel() { return this.configLevelStr; }
  
  public void setConfigLevel(String paramString) { this.configLevelStr = paramString; }
  
  public String getInvokerId() { return this.invokerId; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\probe\provider\StatsProviderInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */