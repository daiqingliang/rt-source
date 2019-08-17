package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Messages;
import java.util.List;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.http.HTTPException;

final class XMLHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C> {
  public XMLHandlerProcessor(HandlerTube paramHandlerTube, WSBinding paramWSBinding, List<? extends Handler> paramList) { super(paramHandlerTube, paramWSBinding, paramList); }
  
  final void insertFaultMessage(C paramC, ProtocolException paramProtocolException) {
    if (paramProtocolException instanceof HTTPException)
      paramC.put("javax.xml.ws.http.response.code", Integer.valueOf(((HTTPException)paramProtocolException).getStatusCode())); 
    if (paramC != null)
      paramC.setPacketMessage(Messages.createEmpty(this.binding.getSOAPVersion())); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\XMLHandlerProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */