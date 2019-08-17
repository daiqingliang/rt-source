package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class NormalDataCollector extends DataCollectorBase {
  private String[] args;
  
  public NormalDataCollector(String[] paramArrayOfString, Properties paramProperties, String paramString1, String paramString2) {
    super(paramProperties, paramString1, paramString2);
    this.args = paramArrayOfString;
  }
  
  public boolean isApplet() { return false; }
  
  protected void collect() {
    checkPropertyDefaults();
    findPropertiesFromFile();
    findPropertiesFromSystem();
    findPropertiesFromProperties();
    findPropertiesFromArgs(this.args);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\NormalDataCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */