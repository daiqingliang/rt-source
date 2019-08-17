package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.DataCollector;
import java.applet.Applet;
import java.net.URL;
import java.util.Properties;

public abstract class DataCollectorFactory {
  public static DataCollector create(Applet paramApplet, Properties paramProperties, String paramString) {
    String str = paramString;
    if (paramApplet != null) {
      URL uRL = paramApplet.getCodeBase();
      if (uRL != null)
        str = uRL.getHost(); 
    } 
    return new AppletDataCollector(paramApplet, paramProperties, paramString, str);
  }
  
  public static DataCollector create(String[] paramArrayOfString, Properties paramProperties, String paramString) { return new NormalDataCollector(paramArrayOfString, paramProperties, paramString, paramString); }
  
  public static DataCollector create(Properties paramProperties, String paramString) { return new PropertyOnlyDataCollector(paramProperties, paramString, paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\DataCollectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */