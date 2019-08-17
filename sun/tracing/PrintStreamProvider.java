package sun.tracing;

import com.sun.tracing.Provider;
import java.io.PrintStream;
import java.lang.reflect.Method;

class PrintStreamProvider extends ProviderSkeleton {
  private PrintStream stream;
  
  private String providerName;
  
  protected ProbeSkeleton createProbe(Method paramMethod) {
    String str = getAnnotationString(paramMethod, com.sun.tracing.ProbeName.class, paramMethod.getName());
    return new PrintStreamProbe(this, str, paramMethod.getParameterTypes());
  }
  
  PrintStreamProvider(Class<? extends Provider> paramClass, PrintStream paramPrintStream) {
    super(paramClass);
    this.stream = paramPrintStream;
    this.providerName = getProviderName();
  }
  
  PrintStream getStream() { return this.stream; }
  
  String getName() { return this.providerName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\PrintStreamProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */