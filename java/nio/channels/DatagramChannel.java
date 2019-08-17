package java.nio.channels;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class DatagramChannel extends AbstractSelectableChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, MulticastChannel {
  protected DatagramChannel(SelectorProvider paramSelectorProvider) { super(paramSelectorProvider); }
  
  public static DatagramChannel open() throws IOException { return SelectorProvider.provider().openDatagramChannel(); }
  
  public static DatagramChannel open(ProtocolFamily paramProtocolFamily) throws IOException { return SelectorProvider.provider().openDatagramChannel(paramProtocolFamily); }
  
  public final int validOps() { return 5; }
  
  public abstract DatagramChannel bind(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract <T> DatagramChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException;
  
  public abstract DatagramSocket socket();
  
  public abstract boolean isConnected();
  
  public abstract DatagramChannel connect(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract DatagramChannel disconnect() throws IOException;
  
  public abstract SocketAddress getRemoteAddress() throws IOException;
  
  public abstract SocketAddress receive(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress) throws IOException;
  
  public abstract int read(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  public final long read(ByteBuffer[] paramArrayOfByteBuffer) throws IOException { return read(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length); }
  
  public abstract int write(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  public final long write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException { return write(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length); }
  
  public abstract SocketAddress getLocalAddress() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\DatagramChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */