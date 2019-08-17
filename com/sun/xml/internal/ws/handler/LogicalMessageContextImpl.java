package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalMessageContext;

class LogicalMessageContextImpl extends MessageUpdatableContext implements LogicalMessageContext {
  private LogicalMessageImpl lm;
  
  private WSBinding binding;
  
  private BindingContext defaultJaxbContext;
  
  public LogicalMessageContextImpl(WSBinding paramWSBinding, BindingContext paramBindingContext, Packet paramPacket) {
    super(paramPacket);
    this.binding = paramWSBinding;
    this.defaultJaxbContext = paramBindingContext;
  }
  
  public LogicalMessage getMessage() {
    if (this.lm == null)
      this.lm = new LogicalMessageImpl(this.defaultJaxbContext, this.packet); 
    return this.lm;
  }
  
  void setPacketMessage(Message paramMessage) {
    if (paramMessage != null) {
      this.packet.setMessage(paramMessage);
      this.lm = null;
    } 
  }
  
  protected void updateMessage() {
    if (this.lm != null) {
      if (this.lm.isPayloadModifed()) {
        Message message1 = this.packet.getMessage();
        Message message2 = this.lm.getMessage(message1.getHeaders(), message1.getAttachments(), this.binding);
        this.packet.setMessage(message2);
      } 
      this.lm = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\LogicalMessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */