package com.sun.xml.internal.ws.message;

public abstract class Util {
  public static boolean parseBool(String paramString) {
    if (paramString.length() == 0)
      return false; 
    char c = paramString.charAt(0);
    return (c == 't' || c == '1');
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */