package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;

final class RESTSourceDispatch extends DispatchImpl<Source> {
  @Deprecated
  public RESTSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    assert isXMLHttp(paramBindingImpl);
  }
  
  public RESTSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    assert isXMLHttp(paramBindingImpl);
  }
  
  Source toReturnValue(Packet paramPacket) {
    Message message = paramPacket.getMessage();
    try {
      return new StreamSource(XMLMessage.getDataSource(message, this.binding.getFeatures()).getInputStream());
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  Packet createPacket(Source paramSource) {
    PayloadSourceMessage payloadSourceMessage;
    if (paramSource == null) {
      payloadSourceMessage = Messages.createEmpty(this.soapVersion);
    } else {
      payloadSourceMessage = new PayloadSourceMessage(null, paramSource, setOutboundAttachments(), this.soapVersion);
    } 
    return new Packet(payloadSourceMessage);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\dispatch\RESTSourceDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */