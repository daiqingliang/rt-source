package javax.management.remote;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServer;

public interface JMXConnectorServerProvider {
  JMXConnectorServer newJMXConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXConnectorServerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */