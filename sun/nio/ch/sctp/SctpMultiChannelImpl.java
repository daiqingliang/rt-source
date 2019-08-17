package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpMultiChannel;
import com.sun.nio.sctp.SctpSocketOption;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class SctpMultiChannelImpl extends SctpMultiChannel {
  private static final String message = "SCTP not supported on this platform";
  
  public SctpMultiChannelImpl(SelectorProvider paramSelectorProvider) {
    super(paramSelectorProvider);
    throw new UnsupportedOperationException("SCTP not supported on this platform");
  }
  
  public Set<Association> associations() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpMultiChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpMultiChannel bindAddress(InetAddress paramInetAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpMultiChannel unbindAddress(InetAddress paramInetAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SocketAddress> getAllLocalAddresses() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SocketAddress> getRemoteAddresses(Association paramAssociation) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpMultiChannel shutdown(Association paramAssociation) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption, Association paramAssociation) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> SctpMultiChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT, Association paramAssociation) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SctpSocketOption<?>> supportedOptions() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public int send(ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpChannel branch(Association paramAssociation) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public void implCloseSelectableChannel() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\sctp\SctpMultiChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */