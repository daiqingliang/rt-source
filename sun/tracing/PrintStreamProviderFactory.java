package sun.tracing;

import com.sun.tracing.ProviderFactory;
import java.io.PrintStream;

public class PrintStreamProviderFactory extends ProviderFactory {
  private PrintStream stream;
  
  public PrintStreamProviderFactory(PrintStream paramPrintStream) { this.stream = paramPrintStream; }
  
  public <T extends com.sun.tracing.Provider> T createProvider(Class<T> paramClass) {
    PrintStreamProvider printStreamProvider = new PrintStreamProvider(paramClass, this.stream);
    printStreamProvider.init();
    return (T)printStreamProvider.newProxyInstance();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\PrintStreamProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */