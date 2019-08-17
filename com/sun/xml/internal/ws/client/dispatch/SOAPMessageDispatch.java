package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class SOAPMessageDispatch extends DispatchImpl<SOAPMessage> {
  @Deprecated
  public SOAPMessageDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) { super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference); }
  
  public SOAPMessageDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) { super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference); }
  
  Packet createPacket(SOAPMessage paramSOAPMessage) {
    Iterator iterator = paramSOAPMessage.getMimeHeaders().getAllHeaders();
    Headers headers = new Headers();
    while (iterator.hasNext()) {
      MimeHeader mimeHeader = (MimeHeader)iterator.next();
      headers.add(mimeHeader.getName(), mimeHeader.getValue());
    } 
    Packet packet = new Packet(SAAJFactory.create(paramSOAPMessage));
    packet.invocationProperties.put("javax.xml.ws.http.request.headers", headers);
    return packet;
  }
  
  SOAPMessage toReturnValue(Packet paramPacket) {
    try {
      if (paramPacket == null || paramPacket.getMessage() == null)
        throw new WebServiceException(DispatchMessages.INVALID_RESPONSE()); 
      return paramPacket.getMessage().readAsSOAPMessage();
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\dispatch\SOAPMessageDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */