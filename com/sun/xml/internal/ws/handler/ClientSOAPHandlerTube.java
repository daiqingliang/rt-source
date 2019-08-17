package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
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

public class ClientSOAPHandlerTube extends HandlerTube {
  private Set<String> roles;
  
  public ClientSOAPHandlerTube(WSBinding paramWSBinding, WSDLPort paramWSDLPort, Tube paramTube) {
    super(paramTube, paramWSDLPort, paramWSBinding);
    if (paramWSBinding.getSOAPVersion() != null);
  }
  
  public ClientSOAPHandlerTube(WSBinding paramWSBinding, Tube paramTube, HandlerTube paramHandlerTube) { super(paramTube, paramHandlerTube, paramWSBinding); }
  
  private ClientSOAPHandlerTube(ClientSOAPHandlerTube paramClientSOAPHandlerTube, TubeCloner paramTubeCloner) { super(paramClientSOAPHandlerTube, paramTubeCloner); }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner) { return new ClientSOAPHandlerTube(this, paramTubeCloner); }
  
  void setUpProcessor() {
    if (this.handlers == null) {
      this.handlers = new ArrayList();
      HandlerConfiguration handlerConfiguration = ((BindingImpl)getBinding()).getHandlerConfig();
      List list = handlerConfiguration.getSoapHandlers();
      if (!list.isEmpty()) {
        this.handlers.addAll(list);
        this.roles = new HashSet();
        this.roles.addAll(handlerConfiguration.getRoles());
        this.processor = new SOAPHandlerProcessor(true, this, getBinding(), this.handlers);
      } 
    } 
  }
  
  MessageUpdatableContext getContext(Packet paramPacket) { return new SOAPMessageContextImpl(getBinding(), paramPacket, this.roles); }
  
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
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    try {
      this.processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, paramMessageUpdatableContext, paramBoolean);
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (RuntimeException runtimeException) {
      throw new WebServiceException(runtimeException);
    } 
  }
  
  void closeHandlers(MessageContext paramMessageContext) { closeClientsideHandlers(paramMessageContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\ClientSOAPHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */