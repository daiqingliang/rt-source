package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

@Taxonomy(stability = Stability.UNCOMMITTED)
public interface BootAMXMBean {
  public static final String BOOT_AMX_OPERATION_NAME = "bootAMX";
  
  ObjectName bootAMX();
  
  JMXServiceURL[] getJMXServiceURLs();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\amx\BootAMXMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */