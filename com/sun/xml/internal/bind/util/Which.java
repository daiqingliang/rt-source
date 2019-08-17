package com.sun.xml.internal.bind.util;

import java.net.URL;

public class Which {
  public static String which(Class paramClass) { return which(paramClass.getName(), SecureLoader.getClassClassLoader(paramClass)); }
  
  public static String which(String paramString, ClassLoader paramClassLoader) {
    String str = paramString.replace('.', '/') + ".class";
    if (paramClassLoader == null)
      paramClassLoader = SecureLoader.getSystemClassLoader(); 
    URL uRL = paramClassLoader.getResource(str);
    return (uRL != null) ? uRL.toString() : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bin\\util\Which.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */