package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;

@Exported
public abstract class PeerAddressChangeNotification implements Notification {
  public abstract SocketAddress address();
  
  public abstract Association association();
  
  public abstract AddressChangeEvent event();
  
  @Exported
  public enum AddressChangeEvent {
    ADDR_AVAILABLE, ADDR_UNREACHABLE, ADDR_REMOVED, ADDR_ADDED, ADDR_MADE_PRIMARY, ADDR_CONFIRMED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\PeerAddressChangeNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */