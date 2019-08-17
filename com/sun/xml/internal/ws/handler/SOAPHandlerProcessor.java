package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;

final class SOAPHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C> {
  public SOAPHandlerProcessor(boolean paramBoolean, HandlerTube paramHandlerTube, WSBinding paramWSBinding, List<? extends Handler> paramList) {
    super(paramHandlerTube, paramWSBinding, paramList);
    this.isClient = paramBoolean;
  }
  
  final void insertFaultMessage(C paramC, ProtocolException paramProtocolException) {
    try {
      if (!paramC.getPacketMessage().isFault()) {
        Message message = Messages.create(this.binding.getSOAPVersion(), paramProtocolException, determineFaultCode(this.binding.getSOAPVersion()));
        paramC.setPacketMessage(message);
      } 
    } catch (Exception exception) {
      logger.log(Level.SEVERE, "exception while creating fault message in handler chain", exception);
      throw new RuntimeException(exception);
    } 
  }
  
  private QName determineFaultCode(SOAPVersion paramSOAPVersion) { return this.isClient ? paramSOAPVersion.faultCodeClient : paramSOAPVersion.faultCodeServer; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\SOAPHandlerProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */