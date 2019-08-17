package com.sun.jmx.remote.protocol.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;

public class ClientProvider implements JMXConnectorProvider {
  public JMXConnector newJMXConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) throws IOException {
    if (!paramJMXServiceURL.getProtocol().equals("rmi"))
      throw new MalformedURLException("Protocol not rmi: " + paramJMXServiceURL.getProtocol()); 
    return new RMIConnector(paramJMXServiceURL, paramMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\protocol\rmi\ClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */