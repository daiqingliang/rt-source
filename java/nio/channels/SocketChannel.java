package java.nio.channels;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class SocketChannel extends AbstractSelectableChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, NetworkChannel {
  protected SocketChannel(SelectorProvider paramSelectorProvider) { super(paramSelectorProvider); }
  
  public static SocketChannel open() throws IOException { return SelectorProvider.provider().openSocketChannel(); }
  
  public static SocketChannel open(SocketAddress paramSocketAddress) throws IOException {
    SocketChannel socketChannel = open();
    try {
      socketChannel.connect(paramSocketAddress);
    } catch (Throwable throwable) {
      try {
        socketChannel.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    assert socketChannel.isConnected();
    return socketChannel;
  }
  
  public final int validOps() { return 13; }
  
  public abstract SocketChannel bind(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract <T> SocketChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException;
  
  public abstract SocketChannel shutdownInput() throws IOException;
  
  public abstract SocketChannel shutdownOutput() throws IOException;
  
  public abstract Socket socket();
  
  public abstract boolean isConnected();
  
  public abstract boolean isConnectionPending();
  
  public abstract boolean connect(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract boolean finishConnect();
  
  public abstract SocketAddress getRemoteAddress() throws IOException;
  
  public abstract int read(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  public final long read(ByteBuffer[] paramArrayOfByteBuffer) throws IOException { return read(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length); }
  
  public abstract int write(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  public final long write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException { return write(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length); }
  
  public abstract SocketAddress getLocalAddress() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\SocketChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */