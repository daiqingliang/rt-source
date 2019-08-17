package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelector;

public class WindowsSelectorProvider extends SelectorProviderImpl {
  public AbstractSelector openSelector() throws IOException { return new WindowsSelectorImpl(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\WindowsSelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */