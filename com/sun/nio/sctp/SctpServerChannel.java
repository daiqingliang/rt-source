package com.sun.nio.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import jdk.Exported;
import sun.nio.ch.sctp.SctpServerChannelImpl;

@Exported
public abstract class SctpServerChannel extends AbstractSelectableChannel {
  protected SctpServerChannel(SelectorProvider paramSelectorProvider) { super(paramSelectorProvider); }
  
  public static SctpServerChannel open() throws IOException { return new SctpServerChannelImpl((SelectorProvider)null); }
  
  public abstract SctpChannel accept() throws IOException;
  
  public final SctpServerChannel bind(SocketAddress paramSocketAddress) throws IOException { return bind(paramSocketAddress, 0); }
  
  public abstract SctpServerChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException;
  
  public abstract SctpServerChannel bindAddress(InetAddress paramInetAddress) throws IOException;
  
  public abstract SctpServerChannel unbindAddress(InetAddress paramInetAddress) throws IOException;
  
  public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;
  
  public abstract <T> T getOption(SctpSocketOption<T> paramSctpSocketOption) throws IOException;
  
  public abstract <T> SctpServerChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT) throws IOException;
  
  public abstract Set<SctpSocketOption<?>> supportedOptions();
  
  public final int validOps() { return 16; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\SctpServerChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */