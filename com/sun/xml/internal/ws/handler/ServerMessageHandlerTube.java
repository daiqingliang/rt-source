package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ServerMessageHandlerTube extends HandlerTube {
  private SEIModel seiModel;
  
  private Set<String> roles;
  
  public ServerMessageHandlerTube(SEIModel paramSEIModel, WSBinding paramWSBinding, Tube paramTube, HandlerTube paramHandlerTube) {
    super(paramTube, paramHandlerTube, paramWSBinding);
    this.seiModel = paramSEIModel;
    setUpHandlersOnce();
  }
  
  private ServerMessageHandlerTube(ServerMessageHandlerTube paramServerMessageHandlerTube, TubeCloner paramTubeCloner) {
    super(paramServerMessageHandlerTube, paramTubeCloner);
    this.seiModel = paramServerMessageHandlerTube.seiModel;
    this.handlers = paramServerMessageHandlerTube.handlers;
    this.roles = paramServerMessageHandlerTube.roles;
  }
  
  private void setUpHandlersOnce() {
    this.handlers = new ArrayList();
    HandlerConfiguration handlerConfiguration = ((BindingImpl)getBinding()).getHandlerConfig();
    List list = handlerConfiguration.getMessageHandlers();
    if (!list.isEmpty()) {
      this.handlers.addAll(list);
      this.roles = new HashSet();
      this.roles.addAll(handlerConfiguration.getRoles());
    } 
  }
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    Map map = (Map)paramMessageUpdatableContext.get("javax.xml.ws.binding.attachments.outbound");
    AttachmentSet attachmentSet = paramMessageUpdatableContext.packet.getMessage().getAttachments();
    for (Map.Entry entry : map.entrySet()) {
      String str = (String)entry.getKey();
      if (attachmentSet.get(str) == null) {
        DataHandlerAttachment dataHandlerAttachment = new DataHandlerAttachment(str, (DataHandler)map.get(str));
        attachmentSet.add(dataHandlerAttachment);
      } 
    } 
    try {
      this.processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, paramMessageUpdatableContext, paramBoolean);
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } 
  }
  
  boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    boolean bool;
    try {
      bool = this.processor.callHandlersRequest(HandlerProcessor.Direction.INBOUND, paramMessageUpdatableContext, !paramBoolean);
    } catch (RuntimeException runtimeException) {
      this.remedyActionTaken = true;
      throw runtimeException;
    } 
    if (!bool)
      this.remedyActionTaken = true; 
    return bool;
  }
  
  protected void resetProcessor() { this.processor = null; }
  
  void setUpProcessor() {
    if (!this.handlers.isEmpty() && this.processor == null)
      this.processor = new SOAPHandlerProcessor(false, this, getBinding(), this.handlers); 
  }
  
  void closeHandlers(MessageContext paramMessageContext) { closeServersideHandlers(paramMessageContext); }
  
  MessageUpdatableContext getContext(Packet paramPacket) { return new MessageHandlerContextImpl(this.seiModel, getBinding(), this.port, paramPacket, this.roles); }
  
  protected void initiateClosing(MessageContext paramMessageContext) {
    close(paramMessageContext);
    super.initiateClosing(paramMessageContext);
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner) { return new ServerMessageHandlerTube(this, paramTubeCloner); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\ServerMessageHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */