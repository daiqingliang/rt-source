package com.sun.nio.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import jdk.Exported;
import sun.nio.ch.sctp.SctpMultiChannelImpl;

@Exported
public abstract class SctpMultiChannel extends AbstractSelectableChannel {
  protected SctpMultiChannel(SelectorProvider paramSelectorProvider) { super(paramSelectorProvider); }
  
  public static SctpMultiChannel open() throws IOException { return new SctpMultiChannelImpl((SelectorProvider)null); }
  
  public abstract Set<Association> associations() throws IOException;
  
  public abstract SctpMultiChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException;
  
  public final SctpMultiChannel bind(SocketAddress paramSocketAddress) throws IOException { return bind(paramSocketAddress, 0); }
  
  public abstract SctpMultiChannel bindAddress(InetAddress paramInetAddress) throws IOException;
  
  public abstract SctpMultiChannel unbindAddress(InetAddress paramInetAddress) throws IOException;
  
  public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;
  
  public abstract Set<SocketAddress> getRemoteAddresses(Association paramAssociation) throws IOException;
  
  public abstract SctpMultiChannel shutdown(Association paramAssociation) throws IOException;
  
  public abstract <T> T getOption(SctpSocketOption<T> paramSctpSocketOption, Association paramAssociation) throws IOException;
  
  public abstract <T> SctpMultiChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT, Association paramAssociation) throws IOException;
  
  public abstract Set<SctpSocketOption<?>> supportedOptions();
  
  public final int validOps() { return 5; }
  
  public abstract <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler) throws IOException;
  
  public abstract int send(ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo) throws IOException;
  
  public abstract SctpChannel branch(Association paramAssociation) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\SctpMultiChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */