package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.Invoker;
import org.xml.sax.EntityResolver;

public class EndpointCreationAttributes {
  private final boolean processHandlerAnnotation;
  
  private final Invoker invoker;
  
  private final EntityResolver entityResolver;
  
  private final boolean isTransportSynchronous;
  
  public EndpointCreationAttributes(boolean paramBoolean1, Invoker paramInvoker, EntityResolver paramEntityResolver, boolean paramBoolean2) {
    this.processHandlerAnnotation = paramBoolean1;
    this.invoker = paramInvoker;
    this.entityResolver = paramEntityResolver;
    this.isTransportSynchronous = paramBoolean2;
  }
  
  public boolean isProcessHandlerAnnotation() { return this.processHandlerAnnotation; }
  
  public Invoker getInvoker() { return this.invoker; }
  
  public EntityResolver getEntityResolver() { return this.entityResolver; }
  
  public boolean isTransportSynchronous() { return this.isTransportSynchronous; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\config\management\EndpointCreationAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */