package sun.tracing;

import com.sun.tracing.ProviderFactory;

public class NullProviderFactory extends ProviderFactory {
  public <T extends com.sun.tracing.Provider> T createProvider(Class<T> paramClass) {
    NullProvider nullProvider = new NullProvider(paramClass);
    nullProvider.init();
    return (T)nullProvider.newProxyInstance();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\NullProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */