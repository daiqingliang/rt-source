package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.handler.MessageHandlerContext;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.Set;

public class MessageHandlerContextImpl extends MessageUpdatableContext implements MessageHandlerContext {
  @Nullable
  private SEIModel seiModel;
  
  private Set<String> roles;
  
  private WSBinding binding;
  
  @Nullable
  private WSDLPort wsdlModel;
  
  public MessageHandlerContextImpl(@Nullable SEIModel paramSEIModel, WSBinding paramWSBinding, @Nullable WSDLPort paramWSDLPort, Packet paramPacket, Set<String> paramSet) {
    super(paramPacket);
    this.seiModel = paramSEIModel;
    this.binding = paramWSBinding;
    this.wsdlModel = paramWSDLPort;
    this.roles = paramSet;
  }
  
  public Message getMessage() { return this.packet.getMessage(); }
  
  public void setMessage(Message paramMessage) { this.packet.setMessage(paramMessage); }
  
  public Set<String> getRoles() { return this.roles; }
  
  public WSBinding getWSBinding() { return this.binding; }
  
  @Nullable
  public SEIModel getSEIModel() { return this.seiModel; }
  
  @Nullable
  public WSDLPort getPort() { return this.wsdlModel; }
  
  void updateMessage() {}
  
  void setPacketMessage(Message paramMessage) { setMessage(paramMessage); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\MessageHandlerContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */