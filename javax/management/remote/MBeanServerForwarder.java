package javax.management.remote;

import javax.management.MBeanServer;

public interface MBeanServerForwarder extends MBeanServer {
  MBeanServer getMBeanServer();
  
  void setMBeanServer(MBeanServer paramMBeanServer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\MBeanServerForwarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */