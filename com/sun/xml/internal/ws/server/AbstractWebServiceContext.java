package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import java.security.Principal;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;

public abstract class AbstractWebServiceContext implements WSWebServiceContext {
  private final WSEndpoint endpoint;
  
  public AbstractWebServiceContext(@NotNull WSEndpoint paramWSEndpoint) { this.endpoint = paramWSEndpoint; }
  
  public MessageContext getMessageContext() {
    Packet packet = getRequestPacket();
    if (packet == null)
      throw new IllegalStateException("getMessageContext() can only be called while servicing a request"); 
    return new EndpointMessageContextImpl(packet);
  }
  
  public Principal getUserPrincipal() {
    Packet packet = getRequestPacket();
    if (packet == null)
      throw new IllegalStateException("getUserPrincipal() can only be called while servicing a request"); 
    return packet.webServiceContextDelegate.getUserPrincipal(packet);
  }
  
  public boolean isUserInRole(String paramString) {
    Packet packet = getRequestPacket();
    if (packet == null)
      throw new IllegalStateException("isUserInRole() can only be called while servicing a request"); 
    return packet.webServiceContextDelegate.isUserInRole(packet, paramString);
  }
  
  public EndpointReference getEndpointReference(Element... paramVarArgs) { return getEndpointReference(javax.xml.ws.wsaddressing.W3CEndpointReference.class, paramVarArgs); }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs) {
    Packet packet = getRequestPacket();
    if (packet == null)
      throw new IllegalStateException("getEndpointReference() can only be called while servicing a request"); 
    String str1 = packet.webServiceContextDelegate.getEPRAddress(packet, this.endpoint);
    String str2 = null;
    if (this.endpoint.getServiceDefinition() != null)
      str2 = packet.webServiceContextDelegate.getWSDLAddress(packet, this.endpoint); 
    return (T)(EndpointReference)paramClass.cast(this.endpoint.getEndpointReference(paramClass, str1, str2, paramVarArgs));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\AbstractWebServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */