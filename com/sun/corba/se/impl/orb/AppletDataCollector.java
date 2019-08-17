package com.sun.corba.se.impl.orb;

import java.applet.Applet;
import java.util.Properties;

public class AppletDataCollector extends DataCollectorBase {
  private Applet applet;
  
  AppletDataCollector(Applet paramApplet, Properties paramProperties, String paramString1, String paramString2) {
    super(paramProperties, paramString1, paramString2);
    this.applet = paramApplet;
  }
  
  public boolean isApplet() { return true; }
  
  protected void collect() {
    checkPropertyDefaults();
    findPropertiesFromFile();
    findPropertiesFromProperties();
    findPropertiesFromApplet(this.applet);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\AppletDataCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */