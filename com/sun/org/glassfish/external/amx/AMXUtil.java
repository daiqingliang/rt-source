package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.ObjectName;

@Taxonomy(stability = Stability.UNCOMMITTED)
public final class AMXUtil {
  public static ObjectName newObjectName(String paramString) {
    try {
      return new ObjectName(paramString);
    } catch (Exception exception) {
      throw new RuntimeException("bad ObjectName", exception);
    } 
  }
  
  public static ObjectName newObjectName(String paramString1, String paramString2) { return newObjectName(paramString1 + ":" + paramString2); }
  
  public static ObjectName getMBeanServerDelegateObjectName() { return newObjectName("JMImplementation:type=MBeanServerDelegate"); }
  
  public static String prop(String paramString1, String paramString2) { return paramString1 + "=" + paramString2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\amx\AMXUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */