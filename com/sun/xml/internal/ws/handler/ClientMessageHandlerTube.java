package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
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

public class ClientMessageHandlerTube extends HandlerTube {
  private SEIModel seiModel;
  
  private Set<String> roles;
  
  public ClientMessageHandlerTube(@Nullable SEIModel paramSEIModel, WSBinding paramWSBinding, WSDLPort paramWSDLPort, Tube paramTube) {
    super(paramTube, paramWSDLPort, paramWSBinding);
    this.seiModel = paramSEIModel;
  }
  
  private ClientMessageHandlerTube(ClientMessageHandlerTube paramClientMessageHandlerTube, TubeCloner paramTubeCloner) {
    super(paramClientMessageHandlerTube, paramTubeCloner);
    this.seiModel = paramClientMessageHandlerTube.seiModel;
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner) { return new ClientMessageHandlerTube(this, paramTubeCloner); }
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    try {
      this.processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, paramMessageUpdatableContext, paramBoolean);
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (RuntimeException runtimeException) {
      throw new WebServiceException(runtimeException);
    } 
  }
  
  boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    boolean bool;
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
      bool = this.processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, paramMessageUpdatableContext, !paramBoolean);
    } catch (WebServiceException webServiceException) {
      this.remedyActionTaken = true;
      throw webServiceException;
    } catch (RuntimeException runtimeException) {
      this.remedyActionTaken = true;
      throw new WebServiceException(runtimeException);
    } 
    if (!bool)
      this.remedyActionTaken = true; 
    return bool;
  }
  
  void closeHandlers(MessageContext paramMessageContext) { closeClientsideHandlers(paramMessageContext); }
  
  void setUpProcessor() {
    if (this.handlers == null) {
      this.handlers = new ArrayList();
      HandlerConfiguration handlerConfiguration = ((BindingImpl)getBinding()).getHandlerConfig();
      List list = handlerConfiguration.getMessageHandlers();
      if (!list.isEmpty()) {
        this.handlers.addAll(list);
        this.roles = new HashSet();
        this.roles.addAll(handlerConfiguration.getRoles());
        this.processor = new SOAPHandlerProcessor(true, this, getBinding(), this.handlers);
      } 
    } 
  }
  
  MessageUpdatableContext getContext(Packet paramPacket) { return new MessageHandlerContextImpl(this.seiModel, getBinding(), this.port, paramPacket, this.roles); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\ClientMessageHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */