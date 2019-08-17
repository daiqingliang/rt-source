package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPException;

abstract class XMLProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T> {
  protected Packet getResponse(Packet paramPacket, Exception paramException, WSDLPort paramWSDLPort, WSBinding paramWSBinding) {
    Packet packet = super.getResponse(paramPacket, paramException, paramWSDLPort, paramWSBinding);
    if (paramException instanceof HTTPException && packet.supports("javax.xml.ws.http.response.code"))
      packet.put("javax.xml.ws.http.response.code", Integer.valueOf(((HTTPException)paramException).getStatusCode())); 
    return packet;
  }
  
  static XMLProviderArgumentBuilder createBuilder(ProviderEndpointModel paramProviderEndpointModel, WSBinding paramWSBinding) {
    if (paramProviderEndpointModel.mode == Service.Mode.PAYLOAD)
      return new PayloadSource(null); 
    if (paramProviderEndpointModel.datatype == Source.class)
      return new PayloadSource(null); 
    if (paramProviderEndpointModel.datatype == DataSource.class)
      return new DataSourceParameter(paramWSBinding); 
    throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(paramProviderEndpointModel.implClass, paramProviderEndpointModel.datatype));
  }
  
  private static final class DataSourceParameter extends XMLProviderArgumentBuilder<DataSource> {
    private final WSBinding binding;
    
    DataSourceParameter(WSBinding param1WSBinding) { this.binding = param1WSBinding; }
    
    public DataSource getParameter(Packet param1Packet) {
      Message message = param1Packet.getInternalMessage();
      return (message instanceof XMLMessage.MessageDataSource) ? ((XMLMessage.MessageDataSource)message).getDataSource() : XMLMessage.getDataSource(message, this.binding.getFeatures());
    }
    
    public Message getResponseMessage(DataSource param1DataSource) { return XMLMessage.create(param1DataSource, this.binding.getFeatures()); }
    
    protected Message getResponseMessage(Exception param1Exception) { return XMLMessage.create(param1Exception); }
  }
  
  private static final class PayloadSource extends XMLProviderArgumentBuilder<Source> {
    private PayloadSource() {}
    
    public Source getParameter(Packet param1Packet) { return param1Packet.getMessage().readPayloadAsSource(); }
    
    public Message getResponseMessage(Source param1Source) { return Messages.createUsingPayload(param1Source, SOAPVersion.SOAP_11); }
    
    protected Message getResponseMessage(Exception param1Exception) { return XMLMessage.create(param1Exception); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\XMLProviderArgumentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */