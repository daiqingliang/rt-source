package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.reflect.Method;

class NullProvider extends ProviderSkeleton {
  NullProvider(Class<? extends Provider> paramClass) { super(paramClass); }
  
  protected ProbeSkeleton createProbe(Method paramMethod) { return new NullProbe(paramMethod.getParameterTypes()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\NullProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */