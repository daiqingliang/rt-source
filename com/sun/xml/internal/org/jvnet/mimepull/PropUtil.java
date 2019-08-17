package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.Properties;

final class PropUtil {
  public static boolean getBooleanSystemProperty(String paramString, boolean paramBoolean) {
    try {
      return getBoolean(getProp(System.getProperties(), paramString), paramBoolean);
    } catch (SecurityException securityException) {
      try {
        String str = System.getProperty(paramString);
        return (str == null) ? paramBoolean : (paramBoolean ? (!str.equalsIgnoreCase("false") ? 1 : 0) : str.equalsIgnoreCase("true"));
      } catch (SecurityException securityException) {
        return paramBoolean;
      } 
    } 
  }
  
  private static Object getProp(Properties paramProperties, String paramString) {
    Object object = paramProperties.get(paramString);
    return (object != null) ? object : paramProperties.getProperty(paramString);
  }
  
  private static boolean getBoolean(Object paramObject, boolean paramBoolean) { return (paramObject == null) ? paramBoolean : ((paramObject instanceof String) ? (paramBoolean ? (!((String)paramObject).equalsIgnoreCase("false") ? 1 : 0) : ((String)paramObject).equalsIgnoreCase("true")) : ((paramObject instanceof Boolean) ? ((Boolean)paramObject).booleanValue() : paramBoolean)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\PropUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */