package com.sun.org.apache.xml.internal.serializer;

import java.util.Properties;

public final class OutputPropertyUtils {
  public static boolean getBooleanProperty(String paramString, Properties paramProperties) {
    String str = paramProperties.getProperty(paramString);
    return !(null == str || !str.equals("yes"));
  }
  
  public static int getIntProperty(String paramString, Properties paramProperties) {
    String str = paramProperties.getProperty(paramString);
    return (null == str) ? 0 : Integer.parseInt(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\OutputPropertyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */