package com.sun.management.jmx;

import com.sun.jmx.mbeanserver.Introspector;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;

@Deprecated
public class Introspector {
  @Deprecated
  public static MBeanInfo testCompliance(Class paramClass) throws NotCompliantMBeanException { return Introspector.testCompliance(paramClass); }
  
  @Deprecated
  public static Class getMBeanInterface(Class paramClass) { return Introspector.getMBeanInterface(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\jmx\Introspector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */