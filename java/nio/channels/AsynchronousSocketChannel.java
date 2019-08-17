package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class AsynchronousSocketChannel implements AsynchronousByteChannel, NetworkChannel {
  private final AsynchronousChannelProvider provider;
  
  protected AsynchronousSocketChannel(AsynchronousChannelProvider paramAsynchronousChannelProvider) { this.provider = paramAsynchronousChannelProvider; }
  
  public final AsynchronousChannelProvider provider() { return this.provider; }
  
  public static AsynchronousSocketChannel open(AsynchronousChannelGroup paramAsynchronousChannelGroup) throws IOException {
    AsynchronousChannelProvider asynchronousChannelProvider = (paramAsynchronousChannelGroup == null) ? AsynchronousChannelProvider.provider() : paramAsynchronousChannelGroup.provider();
    return asynchronousChannelProvider.openAsynchronousSocketChannel(paramAsynchronousChannelGroup);
  }
  
  public static AsynchronousSocketChannel open() throws IOException { return open(null); }
  
  public abstract AsynchronousSocketChannel bind(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract <T> AsynchronousSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException;
  
  public abstract AsynchronousSocketChannel shutdownInput() throws IOException;
  
  public abstract AsynchronousSocketChannel shutdownOutput() throws IOException;
  
  public abstract SocketAddress getRemoteAddress() throws IOException;
  
  public abstract <A> void connect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler);
  
  public abstract Future<Void> connect(SocketAddress paramSocketAddress);
  
  public abstract <A> void read(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public final <A> void read(ByteBuffer paramByteBuffer, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) { read(paramByteBuffer, 0L, TimeUnit.MILLISECONDS, paramA, paramCompletionHandler); }
  
  public abstract Future<Integer> read(ByteBuffer paramByteBuffer);
  
  public abstract <A> void read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler);
  
  public abstract <A> void write(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public final <A> void write(ByteBuffer paramByteBuffer, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) { write(paramByteBuffer, 0L, TimeUnit.MILLISECONDS, paramA, paramCompletionHandler); }
  
  public abstract Future<Integer> write(ByteBuffer paramByteBuffer);
  
  public abstract <A> void write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler);
  
  public abstract SocketAddress getLocalAddress() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\AsynchronousSocketChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */