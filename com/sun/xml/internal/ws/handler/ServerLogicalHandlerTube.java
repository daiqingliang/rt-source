package com.sun.xml.internal.ws.handler;

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
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ServerLogicalHandlerTube extends HandlerTube {
  private SEIModel seiModel;
  
  public ServerLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube) {
    super(paramTube, paramWSDLPort, paramWSBinding);
    this.seiModel = paramSEIModel;
    setUpHandlersOnce();
  }
  
  public ServerLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, Tube paramTube, HandlerTube paramHandlerTube) {
    super(paramTube, paramHandlerTube, paramWSBinding);
    this.seiModel = paramSEIModel;
    setUpHandlersOnce();
  }
  
  private ServerLogicalHandlerTube(ServerLogicalHandlerTube paramServerLogicalHandlerTube, TubeCloner paramTubeCloner) {
    super(paramServerLogicalHandlerTube, paramTubeCloner);
    this.seiModel = paramServerLogicalHandlerTube.seiModel;
    this.handlers = paramServerLogicalHandlerTube.handlers;
  }
  
  protected void initiateClosing(MessageContext paramMessageContext) {
    if (getBinding().getSOAPVersion() != null) {
      super.initiateClosing(paramMessageContext);
    } else {
      close(paramMessageContext);
      super.initiateClosing(paramMessageContext);
    } 
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner) { return new ServerLogicalHandlerTube(this, paramTubeCloner); }
  
  private void setUpHandlersOnce() {
    this.handlers = new ArrayList();
    List list = ((BindingImpl)getBinding()).getHandlerConfig().getLogicalHandlers();
    if (!list.isEmpty())
      this.handlers.addAll(list); 
  }
  
  protected void resetProcessor() { this.processor = null; }
  
  void setUpProcessor() {
    if (!this.handlers.isEmpty() && this.processor == null)
      if (getBinding().getSOAPVersion() == null) {
        this.processor = new XMLHandlerProcessor(this, getBinding(), this.handlers);
      } else {
        this.processor = new SOAPHandlerProcessor(false, this, getBinding(), this.handlers);
      }  
  }
  
  MessageUpdatableContext getContext(Packet paramPacket) { return new LogicalMessageContextImpl(getBinding(), getBindingContext(), paramPacket); }
  
  private BindingContext getBindingContext() { return (this.seiModel != null && this.seiModel instanceof AbstractSEIModelImpl) ? ((AbstractSEIModelImpl)this.seiModel).getBindingContext() : null; }
  
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
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    Map map = (Map)paramMessageUpdatableContext.get("javax.xml.ws.binding.attachments.outbound");
    AttachmentSet attachmentSet = paramMessageUpdatableContext.packet.getMessage().getAttachments();
    for (Map.Entry entry : map.entrySet()) {
      String str = (String)entry.getKey();
      DataHandlerAttachment dataHandlerAttachment = new DataHandlerAttachment(str, (DataHandler)map.get(str));
      attachmentSet.add(dataHandlerAttachment);
    } 
    try {
      this.processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, paramMessageUpdatableContext, paramBoolean);
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } 
  }
  
  void closeHandlers(MessageContext paramMessageContext) { closeServersideHandlers(paramMessageContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\ServerLogicalHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */