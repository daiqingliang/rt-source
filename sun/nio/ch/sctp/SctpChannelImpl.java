package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpSocketOption;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class SctpChannelImpl extends SctpChannel {
  private static final String message = "SCTP not supported on this platform";
  
  public SctpChannelImpl(SelectorProvider paramSelectorProvider) {
    super(paramSelectorProvider);
    throw new UnsupportedOperationException("SCTP not supported on this platform");
  }
  
  public Association association() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpChannel bind(SocketAddress paramSocketAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpChannel bindAddress(InetAddress paramInetAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpChannel unbindAddress(InetAddress paramInetAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public boolean connect(SocketAddress paramSocketAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public boolean connect(SocketAddress paramSocketAddress, int paramInt1, int paramInt2) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public boolean isConnectionPending() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public boolean finishConnect() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SocketAddress> getAllLocalAddresses() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SocketAddress> getRemoteAddresses() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpChannel shutdown() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> SctpChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SctpSocketOption<?>> supportedOptions() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public int send(ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public void implCloseSelectableChannel() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\sctp\SctpChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */