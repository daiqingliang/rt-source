package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class MessageDispatch extends DispatchImpl<Message> {
  @Deprecated
  public MessageDispatch(QName paramQName, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) { super(paramQName, Service.Mode.MESSAGE, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference); }
  
  public MessageDispatch(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference) { super(paramWSPortInfo, Service.Mode.MESSAGE, paramBindingImpl, paramWSEndpointReference, true); }
  
  Message toReturnValue(Packet paramPacket) { return paramPacket.getMessage(); }
  
  Packet createPacket(Message paramMessage) { return new Packet(paramMessage); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\dispatch\MessageDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */