package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;

public abstract class ProviderArgumentsBuilder<T> extends Object {
  protected abstract Message getResponseMessage(Exception paramException);
  
  protected Packet getResponse(Packet paramPacket, Exception paramException, WSDLPort paramWSDLPort, WSBinding paramWSBinding) {
    Message message = getResponseMessage(paramException);
    return paramPacket.createServerResponse(message, paramWSDLPort, null, paramWSBinding);
  }
  
  public abstract T getParameter(Packet paramPacket);
  
  protected abstract Message getResponseMessage(T paramT);
  
  protected Packet getResponse(Packet paramPacket, @Nullable T paramT, WSDLPort paramWSDLPort, WSBinding paramWSBinding) {
    Message message = null;
    if (paramT != null)
      message = getResponseMessage(paramT); 
    return paramPacket.createServerResponse(message, paramWSDLPort, null, paramWSBinding);
  }
  
  public static ProviderArgumentsBuilder<?> create(ProviderEndpointModel paramProviderEndpointModel, WSBinding paramWSBinding) { return (paramProviderEndpointModel.datatype == Packet.class) ? new PacketProviderArgumentsBuilder(paramWSBinding.getSOAPVersion()) : ((paramWSBinding instanceof javax.xml.ws.soap.SOAPBinding) ? SOAPProviderArgumentBuilder.create(paramProviderEndpointModel, paramWSBinding.getSOAPVersion()) : XMLProviderArgumentBuilder.createBuilder(paramProviderEndpointModel, paramWSBinding)); }
  
  private static class PacketProviderArgumentsBuilder extends ProviderArgumentsBuilder<Packet> {
    private final SOAPVersion soapVersion;
    
    public PacketProviderArgumentsBuilder(SOAPVersion param1SOAPVersion) { this.soapVersion = param1SOAPVersion; }
    
    protected Message getResponseMessage(Exception param1Exception) { return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, param1Exception); }
    
    public Packet getParameter(Packet param1Packet) { return param1Packet; }
    
    protected Message getResponseMessage(Packet param1Packet) { throw new IllegalStateException(); }
    
    protected Packet getResponse(Packet param1Packet1, @Nullable Packet param1Packet2, WSDLPort param1WSDLPort, WSBinding param1WSBinding) { return param1Packet2; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\ProviderArgumentsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */