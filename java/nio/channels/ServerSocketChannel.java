package java.nio.channels;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class ServerSocketChannel extends AbstractSelectableChannel implements NetworkChannel {
  protected ServerSocketChannel(SelectorProvider paramSelectorProvider) { super(paramSelectorProvider); }
  
  public static ServerSocketChannel open() throws IOException { return SelectorProvider.provider().openServerSocketChannel(); }
  
  public final int validOps() { return 16; }
  
  public final ServerSocketChannel bind(SocketAddress paramSocketAddress) throws IOException { return bind(paramSocketAddress, 0); }
  
  public abstract ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException;
  
  public abstract <T> ServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException;
  
  public abstract ServerSocket socket();
  
  public abstract SocketChannel accept() throws IOException;
  
  public abstract SocketAddress getLocalAddress() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\ServerSocketChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */