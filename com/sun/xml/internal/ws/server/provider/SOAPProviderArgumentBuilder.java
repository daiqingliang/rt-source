package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

abstract class SOAPProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T> {
  protected final SOAPVersion soapVersion;
  
  private SOAPProviderArgumentBuilder(SOAPVersion paramSOAPVersion) { this.soapVersion = paramSOAPVersion; }
  
  static ProviderArgumentsBuilder create(ProviderEndpointModel paramProviderEndpointModel, SOAPVersion paramSOAPVersion) {
    if (paramProviderEndpointModel.mode == Service.Mode.PAYLOAD)
      return new PayloadSource(paramSOAPVersion); 
    if (paramProviderEndpointModel.datatype == Source.class)
      return new MessageSource(paramSOAPVersion); 
    if (paramProviderEndpointModel.datatype == SOAPMessage.class)
      return new SOAPMessageParameter(paramSOAPVersion); 
    if (paramProviderEndpointModel.datatype == Message.class)
      return new MessageProviderArgumentBuilder(paramSOAPVersion); 
    throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(paramProviderEndpointModel.implClass, paramProviderEndpointModel.datatype));
  }
  
  private static final class MessageSource extends SOAPProviderArgumentBuilder<Source> {
    MessageSource(SOAPVersion param1SOAPVersion) { super(param1SOAPVersion, null); }
    
    public Source getParameter(Packet param1Packet) { return param1Packet.getMessage().readEnvelopeAsSource(); }
    
    protected Message getResponseMessage(Source param1Source) { return Messages.create(param1Source, this.soapVersion); }
    
    protected Message getResponseMessage(Exception param1Exception) { return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, param1Exception); }
  }
  
  private static final class PayloadSource extends SOAPProviderArgumentBuilder<Source> {
    PayloadSource(SOAPVersion param1SOAPVersion) { super(param1SOAPVersion, null); }
    
    public Source getParameter(Packet param1Packet) { return param1Packet.getMessage().readPayloadAsSource(); }
    
    protected Message getResponseMessage(Source param1Source) { return Messages.createUsingPayload(param1Source, this.soapVersion); }
    
    protected Message getResponseMessage(Exception param1Exception) { return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, param1Exception); }
  }
  
  private static final class SOAPMessageParameter extends SOAPProviderArgumentBuilder<SOAPMessage> {
    SOAPMessageParameter(SOAPVersion param1SOAPVersion) { super(param1SOAPVersion, null); }
    
    public SOAPMessage getParameter(Packet param1Packet) {
      try {
        return param1Packet.getMessage().readAsSOAPMessage(param1Packet, true);
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    protected Message getResponseMessage(SOAPMessage param1SOAPMessage) { return Messages.create(param1SOAPMessage); }
    
    protected Message getResponseMessage(Exception param1Exception) { return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, param1Exception); }
    
    protected Packet getResponse(Packet param1Packet, @Nullable SOAPMessage param1SOAPMessage, WSDLPort param1WSDLPort, WSBinding param1WSBinding) {
      Packet packet = super.getResponse(param1Packet, param1SOAPMessage, param1WSDLPort, param1WSBinding);
      if (param1SOAPMessage != null && packet.supports("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers")) {
        MimeHeaders mimeHeaders = param1SOAPMessage.getMimeHeaders();
        HashMap hashMap = new HashMap();
        Iterator iterator = mimeHeaders.getAllHeaders();
        while (iterator.hasNext()) {
          MimeHeader mimeHeader = (MimeHeader)iterator.next();
          if (mimeHeader.getName().equalsIgnoreCase("SOAPAction"))
            continue; 
          List list = (List)hashMap.get(mimeHeader.getName());
          if (list == null) {
            list = new ArrayList();
            hashMap.put(mimeHeader.getName(), list);
          } 
          list.add(mimeHeader.getValue());
        } 
        packet.put("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers", hashMap);
      } 
      return packet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\SOAPProviderArgumentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */