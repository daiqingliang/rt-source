package sun.nio.ch;

import java.nio.channels.spi.SelectorProvider;

public class DefaultSelectorProvider {
  public static SelectorProvider create() { return new WindowsSelectorProvider(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\DefaultSelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */