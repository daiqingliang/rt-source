package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.reflect.Method;
import java.util.Set;

class MultiplexProvider extends ProviderSkeleton {
  private Set<Provider> providers;
  
  protected ProbeSkeleton createProbe(Method paramMethod) { return new MultiplexProbe(paramMethod, this.providers); }
  
  MultiplexProvider(Class<? extends Provider> paramClass, Set<Provider> paramSet) {
    super(paramClass);
    this.providers = paramSet;
  }
  
  public void dispose() {
    for (Provider provider : this.providers)
      provider.dispose(); 
    super.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\MultiplexProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */