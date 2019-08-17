package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.handler.HandlerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;

public class HandlerConfiguration {
  private final Set<String> roles;
  
  private final List<Handler> handlerChain;
  
  private final List<LogicalHandler> logicalHandlers;
  
  private final List<SOAPHandler> soapHandlers;
  
  private final List<MessageHandler> messageHandlers;
  
  private final Set<QName> handlerKnownHeaders;
  
  public HandlerConfiguration(Set<String> paramSet, List<Handler> paramList) {
    this.roles = paramSet;
    this.handlerChain = paramList;
    this.logicalHandlers = new ArrayList();
    this.soapHandlers = new ArrayList();
    this.messageHandlers = new ArrayList();
    HashSet hashSet = new HashSet();
    for (Handler handler : paramList) {
      if (handler instanceof LogicalHandler) {
        this.logicalHandlers.add((LogicalHandler)handler);
        continue;
      } 
      if (handler instanceof SOAPHandler) {
        this.soapHandlers.add((SOAPHandler)handler);
        Set set = ((SOAPHandler)handler).getHeaders();
        if (set != null)
          hashSet.addAll(set); 
        continue;
      } 
      if (handler instanceof MessageHandler) {
        this.messageHandlers.add((MessageHandler)handler);
        Set set = ((MessageHandler)handler).getHeaders();
        if (set != null)
          hashSet.addAll(set); 
        continue;
      } 
      throw new HandlerException("handler.not.valid.type", new Object[] { handler.getClass() });
    } 
    this.handlerKnownHeaders = Collections.unmodifiableSet(hashSet);
  }
  
  public HandlerConfiguration(Set<String> paramSet, HandlerConfiguration paramHandlerConfiguration) {
    this.roles = paramSet;
    this.handlerChain = paramHandlerConfiguration.handlerChain;
    this.logicalHandlers = paramHandlerConfiguration.logicalHandlers;
    this.soapHandlers = paramHandlerConfiguration.soapHandlers;
    this.messageHandlers = paramHandlerConfiguration.messageHandlers;
    this.handlerKnownHeaders = paramHandlerConfiguration.handlerKnownHeaders;
  }
  
  public Set<String> getRoles() { return this.roles; }
  
  public List<Handler> getHandlerChain() { return (this.handlerChain == null) ? Collections.emptyList() : new ArrayList(this.handlerChain); }
  
  public List<LogicalHandler> getLogicalHandlers() { return this.logicalHandlers; }
  
  public List<SOAPHandler> getSoapHandlers() { return this.soapHandlers; }
  
  public List<MessageHandler> getMessageHandlers() { return this.messageHandlers; }
  
  public Set<QName> getHandlerKnownHeaders() { return this.handlerKnownHeaders; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\HandlerConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */