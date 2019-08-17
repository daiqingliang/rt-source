package com.sun.jmx.remote.protocol.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

public class ServerProvider implements JMXConnectorServerProvider {
  public JMXConnectorServer newJMXConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer) throws IOException {
    if (!paramJMXServiceURL.getProtocol().equals("rmi"))
      throw new MalformedURLException("Protocol not rmi: " + paramJMXServiceURL.getProtocol()); 
    return new RMIConnectorServer(paramJMXServiceURL, paramMap, paramMBeanServer);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\protocol\rmi\ServerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */