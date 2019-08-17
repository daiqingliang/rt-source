package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

public abstract class HandlerTube extends AbstractFilterTubeImpl {
  HandlerTube cousinTube;
  
  protected List<Handler> handlers;
  
  HandlerProcessor processor;
  
  boolean remedyActionTaken = false;
  
  @Nullable
  protected final WSDLPort port;
  
  boolean requestProcessingSucessful = false;
  
  private WSBinding binding;
  
  private HandlerConfiguration hc;
  
  private HandlerTubeExchange exchange;
  
  public HandlerTube(Tube paramTube, WSDLPort paramWSDLPort, WSBinding paramWSBinding) {
    super(paramTube);
    this.port = paramWSDLPort;
    this.binding = paramWSBinding;
  }
  
  public HandlerTube(Tube paramTube, HandlerTube paramHandlerTube, WSBinding paramWSBinding) {
    super(paramTube);
    this.cousinTube = paramHandlerTube;
    this.binding = paramWSBinding;
    if (paramHandlerTube != null) {
      this.port = paramHandlerTube.port;
    } else {
      this.port = null;
    } 
  }
  
  protected HandlerTube(HandlerTube paramHandlerTube, TubeCloner paramTubeCloner) {
    super(paramHandlerTube, paramTubeCloner);
    if (paramHandlerTube.cousinTube != null)
      this.cousinTube = (HandlerTube)paramTubeCloner.copy(paramHandlerTube.cousinTube); 
    this.port = paramHandlerTube.port;
    this.binding = paramHandlerTube.binding;
  }
  
  protected WSBinding getBinding() { return this.binding; }
  
  public NextAction processRequest(Packet paramPacket) {
    setupExchange();
    if (isHandleFalse()) {
      this.remedyActionTaken = true;
      return doInvoke(this.next, paramPacket);
    } 
    setUpProcessorInternal();
    messageUpdatableContext = getContext(paramPacket);
    boolean bool = checkOneWay(paramPacket);
    try {
      if (!isHandlerChainEmpty()) {
        boolean bool1 = callHandlersOnRequest(messageUpdatableContext, bool);
        messageUpdatableContext.updatePacket();
        if (!bool && !bool1)
          return doReturnWith(paramPacket); 
      } 
      this.requestProcessingSucessful = true;
      return doInvoke(this.next, paramPacket);
    } catch (RuntimeException runtimeException) {
      if (bool) {
        if (paramPacket.transportBackChannel != null)
          paramPacket.transportBackChannel.close(); 
        paramPacket.setMessage(null);
        return doReturnWith(paramPacket);
      } 
      throw runtimeException;
    } finally {
      if (!this.requestProcessingSucessful)
        initiateClosing(messageUpdatableContext.getMessageContext()); 
    } 
  }
  
  public NextAction processResponse(Packet paramPacket) {
    setupExchange();
    messageUpdatableContext = getContext(paramPacket);
    try {
      if (isHandleFalse() || paramPacket.getMessage() == null)
        return doReturnWith(paramPacket); 
      setUpProcessorInternal();
      boolean bool = isHandleFault(paramPacket);
      if (!isHandlerChainEmpty())
        callHandlersOnResponse(messageUpdatableContext, bool); 
    } finally {
      initiateClosing(messageUpdatableContext.getMessageContext());
    } 
    messageUpdatableContext.updatePacket();
    return doReturnWith(paramPacket);
  }
  
  public NextAction processException(Throwable paramThrowable) {
    try {
      return doThrow(paramThrowable);
    } finally {
      Packet packet = Fiber.current().getPacket();
      MessageUpdatableContext messageUpdatableContext = getContext(packet);
      initiateClosing(messageUpdatableContext.getMessageContext());
    } 
  }
  
  protected void initiateClosing(MessageContext paramMessageContext) {}
  
  public final void close(MessageContext paramMessageContext) {
    if (this.requestProcessingSucessful && this.cousinTube != null)
      this.cousinTube.close(paramMessageContext); 
    if (this.processor != null)
      closeHandlers(paramMessageContext); 
    this.exchange = null;
    this.requestProcessingSucessful = false;
  }
  
  abstract void closeHandlers(MessageContext paramMessageContext);
  
  protected void closeClientsideHandlers(MessageContext paramMessageContext) {
    if (this.processor == null)
      return; 
    if (this.remedyActionTaken) {
      this.processor.closeHandlers(paramMessageContext, this.processor.getIndex(), 0);
      this.processor.setIndex(-1);
      this.remedyActionTaken = false;
    } else {
      this.processor.closeHandlers(paramMessageContext, this.handlers.size() - 1, 0);
    } 
  }
  
  protected void closeServersideHandlers(MessageContext paramMessageContext) {
    if (this.processor == null)
      return; 
    if (this.remedyActionTaken) {
      this.processor.closeHandlers(paramMessageContext, this.processor.getIndex(), this.handlers.size() - 1);
      this.processor.setIndex(-1);
      this.remedyActionTaken = false;
    } else {
      this.processor.closeHandlers(paramMessageContext, 0, this.handlers.size() - 1);
    } 
  }
  
  abstract void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean);
  
  abstract boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean);
  
  private boolean checkOneWay(Packet paramPacket) { return (this.port != null) ? paramPacket.getMessage().isOneWay(this.port) : ((paramPacket.expectReply == null || !paramPacket.expectReply.booleanValue()) ? 1 : 0); }
  
  private void setUpProcessorInternal() {
    HandlerConfiguration handlerConfiguration = ((BindingImpl)this.binding).getHandlerConfig();
    if (handlerConfiguration != this.hc)
      resetProcessor(); 
    this.hc = handlerConfiguration;
    setUpProcessor();
  }
  
  abstract void setUpProcessor();
  
  protected void resetProcessor() { this.handlers = null; }
  
  public final boolean isHandlerChainEmpty() { return this.handlers.isEmpty(); }
  
  abstract MessageUpdatableContext getContext(Packet paramPacket);
  
  private boolean isHandleFault(Packet paramPacket) {
    if (this.cousinTube != null)
      return this.exchange.isHandleFault(); 
    boolean bool = paramPacket.getMessage().isFault();
    this.exchange.setHandleFault(bool);
    return bool;
  }
  
  final void setHandleFault() { this.exchange.setHandleFault(true); }
  
  private boolean isHandleFalse() { return this.exchange.isHandleFalse(); }
  
  final void setHandleFalse() { this.exchange.setHandleFalse(); }
  
  private void setupExchange() {
    if (this.exchange == null) {
      this.exchange = new HandlerTubeExchange();
      if (this.cousinTube != null)
        this.cousinTube.exchange = this.exchange; 
    } else if (this.cousinTube != null) {
      this.cousinTube.exchange = this.exchange;
    } 
  }
  
  static final class HandlerTubeExchange {
    private boolean handleFalse;
    
    private boolean handleFault;
    
    boolean isHandleFault() { return this.handleFault; }
    
    void setHandleFault(boolean param1Boolean) { this.handleFault = param1Boolean; }
    
    public boolean isHandleFalse() { return this.handleFalse; }
    
    void setHandleFalse() { this.handleFalse = true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\HandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */