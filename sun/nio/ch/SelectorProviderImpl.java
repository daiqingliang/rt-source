package sun.nio.ch;

import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;

public abstract class SelectorProviderImpl extends SelectorProvider {
  public DatagramChannel openDatagramChannel() throws IOException { return new DatagramChannelImpl(this); }
  
  public DatagramChannel openDatagramChannel(ProtocolFamily paramProtocolFamily) throws IOException { return new DatagramChannelImpl(this, paramProtocolFamily); }
  
  public Pipe openPipe() throws IOException { return new PipeImpl(this); }
  
  public abstract AbstractSelector openSelector() throws IOException;
  
  public ServerSocketChannel openServerSocketChannel() throws IOException { return new ServerSocketChannelImpl(this); }
  
  public SocketChannel openSocketChannel() throws IOException { return new SocketChannelImpl(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SelectorProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */