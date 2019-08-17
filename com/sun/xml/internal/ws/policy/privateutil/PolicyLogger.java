package com.sun.xml.internal.ws.policy.privateutil;

import com.sun.istack.internal.logging.Logger;
import java.lang.reflect.Field;

public final class PolicyLogger extends Logger {
  private static final String POLICY_PACKAGE_ROOT = "com.sun.xml.internal.ws.policy";
  
  private PolicyLogger(String paramString1, String paramString2) { super(paramString1, paramString2); }
  
  public static PolicyLogger getLogger(Class<?> paramClass) {
    String str = paramClass.getName();
    return str.startsWith("com.sun.xml.internal.ws.policy") ? new PolicyLogger(getLoggingSubsystemName() + str.substring("com.sun.xml.internal.ws.policy".length()), str) : new PolicyLogger(getLoggingSubsystemName() + "." + str, str);
  }
  
  private static String getLoggingSubsystemName() {
    String str = "wspolicy";
    try {
      Class clazz = Class.forName("com.sun.xml.internal.ws.util.Constants");
      Field field = clazz.getField("LoggingDomain");
      Object object = field.get(null);
      str = object.toString().concat(".wspolicy");
    } catch (RuntimeException runtimeException) {
    
    } catch (Exception exception) {}
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\PolicyLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */