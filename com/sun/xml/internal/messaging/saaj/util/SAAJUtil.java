package com.sun.xml.internal.messaging.saaj.util;

import java.security.AccessControlException;

public final class SAAJUtil {
  public static boolean getSystemBoolean(String paramString) {
    try {
      return Boolean.getBoolean(paramString);
    } catch (AccessControlException accessControlException) {
      return false;
    } 
  }
  
  public static String getSystemProperty(String paramString) {
    try {
      return System.getProperty(paramString);
    } catch (SecurityException securityException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\SAAJUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */