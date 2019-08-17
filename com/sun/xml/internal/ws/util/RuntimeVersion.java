package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;

public final class RuntimeVersion {
  public static final Version VERSION;
  
  public String getVersion() { return VERSION.toString(); }
  
  static  {
    Version version = null;
    inputStream = RuntimeVersion.class.getResourceAsStream("version.properties");
    try {
      version = Version.create(inputStream);
    } finally {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (IOException iOException) {} 
    } 
    VERSION = (version == null) ? Version.create(null) : version;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\RuntimeVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */