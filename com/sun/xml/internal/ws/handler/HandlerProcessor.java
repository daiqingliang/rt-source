package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

abstract class HandlerProcessor<C extends MessageUpdatableContext> extends Object {
  boolean isClient;
  
  static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
  
  private List<? extends Handler> handlers;
  
  WSBinding binding;
  
  private int index = -1;
  
  private HandlerTube owner;
  
  protected HandlerProcessor(HandlerTube paramHandlerTube, WSBinding paramWSBinding, List<? extends Handler> paramList) {
    this.owner = paramHandlerTube;
    if (paramList == null)
      paramList = new ArrayList<? extends Handler>(); 
    this.handlers = paramList;
    this.binding = paramWSBinding;
  }
  
  int getIndex() { return this.index; }
  
  void setIndex(int paramInt) { this.index = paramInt; }
  
  public boolean callHandlersRequest(Direction paramDirection, C paramC, boolean paramBoolean) {
    boolean bool;
    setDirection(paramDirection, paramC);
    try {
      if (paramDirection == Direction.OUTBOUND) {
        bool = callHandleMessage(paramC, 0, this.handlers.size() - 1);
      } else {
        bool = callHandleMessage(paramC, this.handlers.size() - 1, 0);
      } 
    } catch (ProtocolException protocolException) {
      logger.log(Level.FINER, "exception in handler chain", protocolException);
      if (paramBoolean) {
        insertFaultMessage(paramC, protocolException);
        reverseDirection(paramDirection, paramC);
        setHandleFaultProperty();
        if (paramDirection == Direction.OUTBOUND) {
          callHandleFault(paramC, getIndex() - 1, 0);
        } else {
          callHandleFault(paramC, getIndex() + 1, this.handlers.size() - 1);
        } 
        return false;
      } 
      throw protocolException;
    } catch (RuntimeException runtimeException) {
      logger.log(Level.FINER, "exception in handler chain", runtimeException);
      throw runtimeException;
    } 
    if (!bool) {
      if (paramBoolean) {
        reverseDirection(paramDirection, paramC);
        if (paramDirection == Direction.OUTBOUND) {
          callHandleMessageReverse(paramC, getIndex() - 1, 0);
        } else {
          callHandleMessageReverse(paramC, getIndex() + 1, this.handlers.size() - 1);
        } 
      } else {
        setHandleFalseProperty();
      } 
      return false;
    } 
    return bool;
  }
  
  public void callHandlersResponse(Direction paramDirection, C paramC, boolean paramBoolean) {
    setDirection(paramDirection, paramC);
    try {
      if (paramBoolean) {
        if (paramDirection == Direction.OUTBOUND) {
          callHandleFault(paramC, 0, this.handlers.size() - 1);
        } else {
          callHandleFault(paramC, this.handlers.size() - 1, 0);
        } 
      } else if (paramDirection == Direction.OUTBOUND) {
        callHandleMessageReverse(paramC, 0, this.handlers.size() - 1);
      } else {
        callHandleMessageReverse(paramC, this.handlers.size() - 1, 0);
      } 
    } catch (RuntimeException runtimeException) {
      logger.log(Level.FINER, "exception in handler chain", runtimeException);
      throw runtimeException;
    } 
  }
  
  private void reverseDirection(Direction paramDirection, C paramC) {
    if (paramDirection == Direction.OUTBOUND) {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(false));
    } else {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(true));
    } 
  }
  
  private void setDirection(Direction paramDirection, C paramC) {
    if (paramDirection == Direction.OUTBOUND) {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(true));
    } else {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(false));
    } 
  }
  
  private void setHandleFaultProperty() { this.owner.setHandleFault(); }
  
  private void setHandleFalseProperty() { this.owner.setHandleFalse(); }
  
  abstract void insertFaultMessage(C paramC, ProtocolException paramProtocolException);
  
  private boolean callHandleMessage(C paramC, int paramInt1, int paramInt2) {
    int i = paramInt1;
    try {
      if (paramInt1 > paramInt2) {
        while (i >= paramInt2) {
          if (!((Handler)this.handlers.get(i)).handleMessage(paramC)) {
            setIndex(i);
            return false;
          } 
          i--;
        } 
      } else {
        while (i <= paramInt2) {
          if (!((Handler)this.handlers.get(i)).handleMessage(paramC)) {
            setIndex(i);
            return false;
          } 
          i++;
        } 
      } 
    } catch (RuntimeException runtimeException) {
      setIndex(i);
      throw runtimeException;
    } 
    return true;
  }
  
  private boolean callHandleMessageReverse(C paramC, int paramInt1, int paramInt2) {
    if (this.handlers.isEmpty() || paramInt1 == -1 || paramInt1 == this.handlers.size())
      return false; 
    int i = paramInt1;
    if (paramInt1 > paramInt2) {
      while (i >= paramInt2) {
        if (!((Handler)this.handlers.get(i)).handleMessage(paramC)) {
          setHandleFalseProperty();
          return false;
        } 
        i--;
      } 
    } else {
      while (i <= paramInt2) {
        if (!((Handler)this.handlers.get(i)).handleMessage(paramC)) {
          setHandleFalseProperty();
          return false;
        } 
        i++;
      } 
    } 
    return true;
  }
  
  private boolean callHandleFault(C paramC, int paramInt1, int paramInt2) {
    if (this.handlers.isEmpty() || paramInt1 == -1 || paramInt1 == this.handlers.size())
      return false; 
    int i = paramInt1;
    if (paramInt1 > paramInt2) {
      try {
        while (i >= paramInt2) {
          if (!((Handler)this.handlers.get(i)).handleFault(paramC))
            return false; 
          i--;
        } 
      } catch (RuntimeException runtimeException) {
        logger.log(Level.FINER, "exception in handler chain", runtimeException);
        throw runtimeException;
      } 
    } else {
      try {
        while (i <= paramInt2) {
          if (!((Handler)this.handlers.get(i)).handleFault(paramC))
            return false; 
          i++;
        } 
      } catch (RuntimeException runtimeException) {
        logger.log(Level.FINER, "exception in handler chain", runtimeException);
        throw runtimeException;
      } 
    } 
    return true;
  }
  
  void closeHandlers(MessageContext paramMessageContext, int paramInt1, int paramInt2) {
    if (this.handlers.isEmpty() || paramInt1 == -1)
      return; 
    if (paramInt1 > paramInt2) {
      for (int i = paramInt1; i >= paramInt2; i--) {
        try {
          ((Handler)this.handlers.get(i)).close(paramMessageContext);
        } catch (RuntimeException runtimeException) {
          logger.log(Level.INFO, "Exception ignored during close", runtimeException);
        } 
      } 
    } else {
      for (int i = paramInt1; i <= paramInt2; i++) {
        try {
          ((Handler)this.handlers.get(i)).close(paramMessageContext);
        } catch (RuntimeException runtimeException) {
          logger.log(Level.INFO, "Exception ignored during close", runtimeException);
        } 
      } 
    } 
  }
  
  public enum Direction {
    OUTBOUND, INBOUND;
  }
  
  public enum RequestOrResponse {
    REQUEST, RESPONSE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\HandlerProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */