package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public final class Secrets {
  private static SelectorProvider provider() {
    SelectorProvider selectorProvider = SelectorProvider.provider();
    if (!(selectorProvider instanceof SelectorProviderImpl))
      throw new UnsupportedOperationException(); 
    return selectorProvider;
  }
  
  public static SocketChannel newSocketChannel(FileDescriptor paramFileDescriptor) {
    try {
      return new SocketChannelImpl(provider(), paramFileDescriptor, false);
    } catch (IOException iOException) {
      throw new AssertionError(iOException);
    } 
  }
  
  public static ServerSocketChannel newServerSocketChannel(FileDescriptor paramFileDescriptor) {
    try {
      return new ServerSocketChannelImpl(provider(), paramFileDescriptor, false);
    } catch (IOException iOException) {
      throw new AssertionError(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\Secrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */