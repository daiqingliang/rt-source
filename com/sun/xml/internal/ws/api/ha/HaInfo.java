package com.sun.xml.internal.ws.api.ha;

public class HaInfo {
  private final String replicaInstance;
  
  private final String key;
  
  private final boolean failOver;
  
  public HaInfo(String paramString1, String paramString2, boolean paramBoolean) {
    this.key = paramString1;
    this.replicaInstance = paramString2;
    this.failOver = paramBoolean;
  }
  
  public String getReplicaInstance() { return this.replicaInstance; }
  
  public String getKey() { return this.key; }
  
  public boolean isFailOver() { return this.failOver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\ha\HaInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */