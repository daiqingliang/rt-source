package com.sun.nio.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import jdk.Exported;
import sun.nio.ch.sctp.SctpChannelImpl;

@Exported
public abstract class SctpChannel extends AbstractSelectableChannel {
  protected SctpChannel(SelectorProvider paramSelectorProvider) { super(paramSelectorProvider); }
  
  public static SctpChannel open() throws IOException { return new SctpChannelImpl((SelectorProvider)null); }
  
  public static SctpChannel open(SocketAddress paramSocketAddress, int paramInt1, int paramInt2) throws IOException {
    SctpChannel sctpChannel = open();
    sctpChannel.connect(paramSocketAddress, paramInt1, paramInt2);
    return sctpChannel;
  }
  
  public abstract Association association() throws IOException;
  
  public abstract SctpChannel bind(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract SctpChannel bindAddress(InetAddress paramInetAddress) throws IOException;
  
  public abstract SctpChannel unbindAddress(InetAddress paramInetAddress) throws IOException;
  
  public abstract boolean connect(SocketAddress paramSocketAddress) throws IOException;
  
  public abstract boolean connect(SocketAddress paramSocketAddress, int paramInt1, int paramInt2) throws IOException;
  
  public abstract boolean isConnectionPending();
  
  public abstract boolean finishConnect();
  
  public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;
  
  public abstract Set<SocketAddress> getRemoteAddresses() throws IOException;
  
  public abstract SctpChannel shutdown() throws IOException;
  
  public abstract <T> T getOption(SctpSocketOption<T> paramSctpSocketOption) throws IOException;
  
  public abstract <T> SctpChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT) throws IOException;
  
  public abstract Set<SctpSocketOption<?>> supportedOptions();
  
  public final int validOps() { return 13; }
  
  public abstract <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler) throws IOException;
  
  public abstract int send(ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\SctpChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */