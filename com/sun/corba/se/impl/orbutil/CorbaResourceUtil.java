package com.sun.corba.se.impl.orbutil;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CorbaResourceUtil {
  private static boolean resourcesInitialized = false;
  
  private static ResourceBundle resources;
  
  public static String getString(String paramString) {
    if (!resourcesInitialized)
      initResources(); 
    try {
      return resources.getString(paramString);
    } catch (MissingResourceException missingResourceException) {
      return null;
    } 
  }
  
  public static String getText(String paramString) {
    String str = getString(paramString);
    if (str == null)
      str = "no text found: \"" + paramString + "\""; 
    return str;
  }
  
  public static String getText(String paramString, int paramInt) { return getText(paramString, Integer.toString(paramInt), null, null); }
  
  public static String getText(String paramString1, String paramString2) { return getText(paramString1, paramString2, null, null); }
  
  public static String getText(String paramString1, String paramString2, String paramString3) { return getText(paramString1, paramString2, paramString3, null); }
  
  public static String getText(String paramString1, String paramString2, String paramString3, String paramString4) {
    String str = getString(paramString1);
    if (str == null)
      str = "no text found: key = \"" + paramString1 + "\", arguments = \"{0}\", \"{1}\", \"{2}\""; 
    String[] arrayOfString = new String[3];
    arrayOfString[0] = (paramString2 != null) ? paramString2.toString() : "null";
    arrayOfString[1] = (paramString3 != null) ? paramString3.toString() : "null";
    arrayOfString[2] = (paramString4 != null) ? paramString4.toString() : "null";
    return MessageFormat.format(str, (Object[])arrayOfString);
  }
  
  private static void initResources() {
    try {
      resources = ResourceBundle.getBundle("com.sun.corba.se.impl.orbutil.resources.sunorb");
      resourcesInitialized = true;
    } catch (MissingResourceException missingResourceException) {
      throw new Error("fatal: missing resource bundle: " + missingResourceException.getClassName());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\CorbaResourceUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */