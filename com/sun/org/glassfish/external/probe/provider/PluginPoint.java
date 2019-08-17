package com.sun.org.glassfish.external.probe.provider;

public static enum PluginPoint {
  SERVER("server", "server"),
  APPLICATIONS("applications", "server/applications");
  
  String name;
  
  String path;
  
  PluginPoint(String paramString1, String paramString2) {
    this.name = paramString1;
    this.path = paramString2;
  }
  
  public String getName() { return this.name; }
  
  public String getPath() { return this.path; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\probe\provider\PluginPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */