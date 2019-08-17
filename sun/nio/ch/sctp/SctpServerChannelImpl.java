package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import com.sun.nio.sctp.SctpSocketOption;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class SctpServerChannelImpl extends SctpServerChannel {
  private static final String message = "SCTP not supported on this platform";
  
  public SctpServerChannelImpl(SelectorProvider paramSelectorProvider) {
    super(paramSelectorProvider);
    throw new UnsupportedOperationException("SCTP not supported on this platform");
  }
  
  public SctpChannel accept() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpServerChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpServerChannel bindAddress(InetAddress paramInetAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public SctpServerChannel unbindAddress(InetAddress paramInetAddress) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SocketAddress> getAllLocalAddresses() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public <T> SctpServerChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public Set<SctpSocketOption<?>> supportedOptions() { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
  
  public void implCloseSelectableChannel() throws IOException { throw new UnsupportedOperationException("SCTP not supported on this platform"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\sctp\SctpServerChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */