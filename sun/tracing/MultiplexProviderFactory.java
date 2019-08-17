package sun.tracing;

import com.sun.tracing.ProviderFactory;
import java.util.HashSet;
import java.util.Set;

public class MultiplexProviderFactory extends ProviderFactory {
  private Set<ProviderFactory> factories;
  
  public MultiplexProviderFactory(Set<ProviderFactory> paramSet) { this.factories = paramSet; }
  
  public <T extends com.sun.tracing.Provider> T createProvider(Class<T> paramClass) {
    HashSet hashSet = new HashSet();
    for (ProviderFactory providerFactory : this.factories)
      hashSet.add(providerFactory.createProvider(paramClass)); 
    MultiplexProvider multiplexProvider = new MultiplexProvider(paramClass, hashSet);
    multiplexProvider.init();
    return (T)multiplexProvider.newProxyInstance();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\MultiplexProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */