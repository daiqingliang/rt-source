package javax.management.remote;

import java.io.IOException;
import java.util.Map;

public interface JMXConnectorServerMBean {
  void start() throws IOException;
  
  void stop() throws IOException;
  
  boolean isActive();
  
  void setMBeanServerForwarder(MBeanServerForwarder paramMBeanServerForwarder);
  
  String[] getConnectionIds();
  
  JMXServiceURL getAddress();
  
  Map<String, ?> getAttributes();
  
  JMXConnector toJMXConnector(Map<String, ?> paramMap) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXConnectorServerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */