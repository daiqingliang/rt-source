package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.server.provider.AsyncProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.SyncProviderInvokerTube;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ProviderInvokerTubeFactory<T> extends Object {
  private static final ProviderInvokerTubeFactory DEFAULT = new DefaultProviderInvokerTubeFactory(null);
  
  private static final Logger logger = Logger.getLogger(ProviderInvokerTubeFactory.class.getName());
  
  protected abstract ProviderInvokerTube<T> doCreate(@NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean);
  
  public static <T> ProviderInvokerTube<T> create(@Nullable ClassLoader paramClassLoader, @NotNull Container paramContainer, @NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean) {
    for (ProviderInvokerTubeFactory providerInvokerTubeFactory : ServiceFinder.find(ProviderInvokerTubeFactory.class, paramClassLoader, paramContainer)) {
      ProviderInvokerTube providerInvokerTube = providerInvokerTubeFactory.doCreate(paramClass, paramInvoker, paramProviderArgumentsBuilder, paramBoolean);
      if (providerInvokerTube != null) {
        if (logger.isLoggable(Level.FINE))
          logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { providerInvokerTubeFactory.getClass(), providerInvokerTube }); 
        return providerInvokerTube;
      } 
    } 
    return DEFAULT.createDefault(paramClass, paramInvoker, paramProviderArgumentsBuilder, paramBoolean);
  }
  
  protected ProviderInvokerTube<T> createDefault(@NotNull Class<T> paramClass, @NotNull Invoker paramInvoker, @NotNull ProviderArgumentsBuilder<?> paramProviderArgumentsBuilder, boolean paramBoolean) { return paramBoolean ? new AsyncProviderInvokerTube(paramInvoker, paramProviderArgumentsBuilder) : new SyncProviderInvokerTube(paramInvoker, paramProviderArgumentsBuilder); }
  
  private static class DefaultProviderInvokerTubeFactory<T> extends ProviderInvokerTubeFactory<T> {
    private DefaultProviderInvokerTubeFactory() {}
    
    public ProviderInvokerTube<T> doCreate(@NotNull Class<T> param1Class, @NotNull Invoker param1Invoker, @NotNull ProviderArgumentsBuilder<?> param1ProviderArgumentsBuilder, boolean param1Boolean) { return createDefault(param1Class, param1Invoker, param1ProviderArgumentsBuilder, param1Boolean); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\ProviderInvokerTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */