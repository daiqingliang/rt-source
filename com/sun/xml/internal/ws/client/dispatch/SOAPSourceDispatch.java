package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

final class SOAPSourceDispatch extends DispatchImpl<Source> {
  @Deprecated
  public SOAPSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    assert !isXMLHttp(paramBindingImpl);
  }
  
  public SOAPSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    assert !isXMLHttp(paramBindingImpl);
  }
  
  Source toReturnValue(Packet paramPacket) {
    Message message = paramPacket.getMessage();
    switch (this.mode) {
      case PAYLOAD:
        return message.readPayloadAsSource();
      case MESSAGE:
        return message.readEnvelopeAsSource();
    } 
    throw new WebServiceException("Unrecognized dispatch mode");
  }
  
  Packet createPacket(Source paramSource) {
    Message message;
    if (paramSource == null) {
      message = Messages.createEmpty(this.soapVersion);
    } else {
      PayloadSourceMessage payloadSourceMessage;
      switch (this.mode) {
        case PAYLOAD:
          payloadSourceMessage = new PayloadSourceMessage(null, paramSource, setOutboundAttachments(), this.soapVersion);
          return new Packet(payloadSourceMessage);
        case MESSAGE:
          message = Messages.create(paramSource, this.soapVersion);
          return new Packet(message);
      } 
      throw new WebServiceException("Unrecognized message mode");
    } 
    return new Packet(message);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\dispatch\SOAPSourceDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */