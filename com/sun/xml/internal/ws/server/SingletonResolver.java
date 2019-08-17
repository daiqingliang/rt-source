package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;

public final class SingletonResolver<T> extends AbstractInstanceResolver<T> {
  @NotNull
  private final T singleton;
  
  public SingletonResolver(@NotNull T paramT) { this.singleton = paramT; }
  
  @NotNull
  public T resolve(Packet paramPacket) { return (T)this.singleton; }
  
  public void start(WSWebServiceContext paramWSWebServiceContext, WSEndpoint paramWSEndpoint) {
    getResourceInjector(paramWSEndpoint).inject(paramWSWebServiceContext, this.singleton);
    invokeMethod(findAnnotatedMethod(this.singleton.getClass(), javax.annotation.PostConstruct.class), this.singleton, new Object[0]);
  }
  
  public void dispose() { invokeMethod(findAnnotatedMethod(this.singleton.getClass(), javax.annotation.PreDestroy.class), this.singleton, new Object[0]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\SingletonResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */