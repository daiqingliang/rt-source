package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import java.lang.reflect.Method;

public abstract class AbstractMultiInstanceResolver<T> extends AbstractInstanceResolver<T> {
  protected final Class<T> clazz;
  
  private WSWebServiceContext webServiceContext;
  
  protected WSEndpoint owner;
  
  private final Method postConstructMethod;
  
  private final Method preDestroyMethod;
  
  private ResourceInjector resourceInjector;
  
  public AbstractMultiInstanceResolver(Class<T> paramClass) {
    this.clazz = paramClass;
    this.postConstructMethod = findAnnotatedMethod(paramClass, javax.annotation.PostConstruct.class);
    this.preDestroyMethod = findAnnotatedMethod(paramClass, javax.annotation.PreDestroy.class);
  }
  
  protected final void prepare(T paramT) {
    assert this.webServiceContext != null;
    this.resourceInjector.inject(this.webServiceContext, paramT);
    invokeMethod(this.postConstructMethod, paramT, new Object[0]);
  }
  
  protected final T create() {
    Object object = createNewInstance(this.clazz);
    prepare(object);
    return (T)object;
  }
  
  public void start(WSWebServiceContext paramWSWebServiceContext, WSEndpoint paramWSEndpoint) {
    this.resourceInjector = getResourceInjector(paramWSEndpoint);
    this.webServiceContext = paramWSWebServiceContext;
    this.owner = paramWSEndpoint;
  }
  
  protected final void dispose(T paramT) { invokeMethod(this.preDestroyMethod, paramT, new Object[0]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\AbstractMultiInstanceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */