package com.sun.xml.internal.ws.client;

import com.sun.org.glassfish.gmbal.AMXMetadata;
import com.sun.org.glassfish.gmbal.Description;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedObject;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.server.MonitorBase;
import java.net.URL;
import java.util.Map;
import javax.xml.namespace.QName;

@ManagedObject
@Description("Metro Web Service client")
@AMXMetadata(type = "WSClient")
public final class MonitorRootClient extends MonitorBase {
  private final Stub stub;
  
  MonitorRootClient(Stub paramStub) { this.stub = paramStub; }
  
  @ManagedAttribute
  private Container getContainer() { return this.stub.owner.getContainer(); }
  
  @ManagedAttribute
  private Map<QName, PortInfo> qnameToPortInfoMap() { return this.stub.owner.getQNameToPortInfoMap(); }
  
  @ManagedAttribute
  private QName serviceName() { return this.stub.owner.getServiceName(); }
  
  @ManagedAttribute
  private Class serviceClass() { return this.stub.owner.getServiceClass(); }
  
  @ManagedAttribute
  private URL wsdlDocumentLocation() { return this.stub.owner.getWSDLDocumentLocation(); }
  
  @ManagedAttribute
  private WSDLService wsdlService() { return this.stub.owner.getWsdlService(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\MonitorRootClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */