package javax.management;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

public class MBeanServerBuilder {
  public MBeanServerDelegate newMBeanServerDelegate() { return JmxMBeanServer.newMBeanServerDelegate(); }
  
  public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate) { return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, false); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */