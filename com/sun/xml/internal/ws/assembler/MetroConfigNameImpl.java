package com.sun.xml.internal.ws.assembler;

public class MetroConfigNameImpl implements MetroConfigName {
  private final String defaultFileName;
  
  private final String appFileName;
  
  public MetroConfigNameImpl(String paramString1, String paramString2) {
    this.defaultFileName = paramString1;
    this.appFileName = paramString2;
  }
  
  public String getDefaultFileName() { return this.defaultFileName; }
  
  public String getAppFileName() { return this.appFileName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\MetroConfigNameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */