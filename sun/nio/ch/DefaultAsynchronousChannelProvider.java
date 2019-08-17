package sun.nio.ch;

import java.nio.channels.spi.AsynchronousChannelProvider;

public class DefaultAsynchronousChannelProvider {
  public static AsynchronousChannelProvider create() { return new WindowsAsynchronousChannelProvider(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\DefaultAsynchronousChannelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */