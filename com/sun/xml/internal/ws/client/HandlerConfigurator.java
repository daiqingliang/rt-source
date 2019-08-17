package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.soap.SOAPBinding;

abstract class HandlerConfigurator {
  abstract void configureHandlers(@NotNull WSPortInfo paramWSPortInfo, @NotNull BindingImpl paramBindingImpl);
  
  abstract HandlerResolver getResolver();
  
  static final class AnnotationConfigurator extends HandlerConfigurator {
    private final HandlerChainsModel handlerModel;
    
    private final Map<WSPortInfo, HandlerAnnotationInfo> chainMap = new HashMap();
    
    private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
    
    AnnotationConfigurator(WSServiceDelegate param1WSServiceDelegate) {
      this.handlerModel = HandlerAnnotationProcessor.buildHandlerChainsModel(param1WSServiceDelegate.getServiceClass());
      assert this.handlerModel != null;
    }
    
    void configureHandlers(WSPortInfo param1WSPortInfo, BindingImpl param1BindingImpl) {
      HandlerAnnotationInfo handlerAnnotationInfo = (HandlerAnnotationInfo)this.chainMap.get(param1WSPortInfo);
      if (handlerAnnotationInfo == null) {
        logGetChain(param1WSPortInfo);
        handlerAnnotationInfo = this.handlerModel.getHandlersForPortInfo(param1WSPortInfo);
        this.chainMap.put(param1WSPortInfo, handlerAnnotationInfo);
      } 
      if (param1BindingImpl instanceof SOAPBinding)
        ((SOAPBinding)param1BindingImpl).setRoles(handlerAnnotationInfo.getRoles()); 
      logSetChain(param1WSPortInfo, handlerAnnotationInfo);
      param1BindingImpl.setHandlerChain(handlerAnnotationInfo.getHandlers());
    }
    
    HandlerResolver getResolver() { return new HandlerResolver() {
          public List<Handler> getHandlerChain(PortInfo param2PortInfo) { return new ArrayList(HandlerConfigurator.AnnotationConfigurator.this.handlerModel.getHandlersForPortInfo(param2PortInfo).getHandlers()); }
        }; }
    
    private void logSetChain(WSPortInfo param1WSPortInfo, HandlerAnnotationInfo param1HandlerAnnotationInfo) {
      logger.finer("Setting chain of length " + param1HandlerAnnotationInfo.getHandlers().size() + " for port info");
      logPortInfo(param1WSPortInfo, Level.FINER);
    }
    
    private void logGetChain(WSPortInfo param1WSPortInfo) {
      logger.fine("No handler chain found for port info:");
      logPortInfo(param1WSPortInfo, Level.FINE);
      logger.fine("Existing handler chains:");
      if (this.chainMap.isEmpty()) {
        logger.fine("none");
      } else {
        for (WSPortInfo wSPortInfo : this.chainMap.keySet()) {
          logger.fine(((HandlerAnnotationInfo)this.chainMap.get(wSPortInfo)).getHandlers().size() + " handlers for port info ");
          logPortInfo(wSPortInfo, Level.FINE);
        } 
      } 
    }
    
    private void logPortInfo(WSPortInfo param1WSPortInfo, Level param1Level) { logger.log(param1Level, "binding: " + param1WSPortInfo.getBindingID() + "\nservice: " + param1WSPortInfo.getServiceName() + "\nport: " + param1WSPortInfo.getPortName()); }
  }
  
  static final class HandlerResolverImpl extends HandlerConfigurator {
    @Nullable
    private final HandlerResolver resolver;
    
    public HandlerResolverImpl(HandlerResolver param1HandlerResolver) { this.resolver = param1HandlerResolver; }
    
    void configureHandlers(@NotNull WSPortInfo param1WSPortInfo, @NotNull BindingImpl param1BindingImpl) {
      if (this.resolver != null)
        param1BindingImpl.setHandlerChain(this.resolver.getHandlerChain(param1WSPortInfo)); 
    }
    
    HandlerResolver getResolver() { return this.resolver; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\HandlerConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */