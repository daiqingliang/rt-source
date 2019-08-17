package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.ProviderInvokerTubeFactory;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.server.InvokerTube;
import javax.xml.ws.Provider;

public abstract class ProviderInvokerTube<T> extends InvokerTube<Provider<T>> {
  protected ProviderArgumentsBuilder<T> argsBuilder;
  
  ProviderInvokerTube(Invoker paramInvoker, ProviderArgumentsBuilder<T> paramProviderArgumentsBuilder) {
    super(paramInvoker);
    this.argsBuilder = paramProviderArgumentsBuilder;
  }
  
  public static <T> ProviderInvokerTube<T> create(Class<T> paramClass, WSBinding paramWSBinding, Invoker paramInvoker, Container paramContainer) {
    ProviderEndpointModel providerEndpointModel = new ProviderEndpointModel(paramClass, paramWSBinding);
    ProviderArgumentsBuilder providerArgumentsBuilder = ProviderArgumentsBuilder.create(providerEndpointModel, paramWSBinding);
    if (paramWSBinding instanceof SOAPBindingImpl)
      ((SOAPBindingImpl)paramWSBinding).setMode(providerEndpointModel.mode); 
    return ProviderInvokerTubeFactory.create(null, paramContainer, paramClass, paramInvoker, providerArgumentsBuilder, providerEndpointModel.isAsync);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\provider\ProviderInvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */