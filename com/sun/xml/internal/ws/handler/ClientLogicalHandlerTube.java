package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ClientLogicalHandlerTube extends HandlerTube {
  private SEIModel seiModel;
  
  public ClientLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube) {
    super(paramTube, paramWSDLPort, paramWSBinding);
    this.seiModel = paramSEIModel;
  }
  
  public ClientLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, Tube paramTube, HandlerTube paramHandlerTube) {
    super(paramTube, paramHandlerTube, paramWSBinding);
    this.seiModel = paramSEIModel;
  }
  
  private ClientLogicalHandlerTube(ClientLogicalHandlerTube paramClientLogicalHandlerTube, TubeCloner paramTubeCloner) {
    super(paramClientLogicalHandlerTube, paramTubeCloner);
    this.seiModel = paramClientLogicalHandlerTube.seiModel;
  }
  
  protected void initiateClosing(MessageContext paramMessageContext) {
    close(paramMessageContext);
    super.initiateClosing(paramMessageContext);
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner) { return new ClientLogicalHandlerTube(this, paramTubeCloner); }
  
  void setUpProcessor() {
    if (this.handlers == null) {
      this.handlers = new ArrayList();
      WSBinding wSBinding = getBinding();
      List list = ((BindingImpl)wSBinding).getHandlerConfig().getLogicalHandlers();
      if (!list.isEmpty()) {
        this.handlers.addAll(list);
        if (wSBinding.getSOAPVersion() == null) {
          this.processor = new XMLHandlerProcessor(this, wSBinding, this.handlers);
        } else {
          this.processor = new SOAPHandlerProcessor(true, this, wSBinding, this.handlers);
        } 
      } 
    } 
  }
  
  MessageUpdatableContext getContext(Packet paramPacket) { return new LogicalMessageContextImpl(getBinding(), getBindingContext(), paramPacket); }
  
  private BindingContext getBindingContext() { return (this.seiModel != null && this.seiModel instanceof AbstractSEIModelImpl) ? ((AbstractSEIModelImpl)this.seiModel).getBindingContext() : null; }
  
  boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean) {
    boolean bool;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\ClientLogicalHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */