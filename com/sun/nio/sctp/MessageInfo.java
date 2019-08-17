package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;
import sun.nio.ch.sctp.MessageInfoImpl;

@Exported
public abstract class MessageInfo {
  public static MessageInfo createOutgoing(SocketAddress paramSocketAddress, int paramInt) {
    if (paramInt < 0 || paramInt > 65536)
      throw new IllegalArgumentException("Invalid stream number"); 
    return new MessageInfoImpl(null, paramSocketAddress, paramInt);
  }
  
  public static MessageInfo createOutgoing(Association paramAssociation, SocketAddress paramSocketAddress, int paramInt) {
    if (paramAssociation == null)
      throw new IllegalArgumentException("association cannot be null"); 
    if (paramInt < 0 || paramInt > 65536)
      throw new IllegalArgumentException("Invalid stream number"); 
    return new MessageInfoImpl(paramAssociation, paramSocketAddress, paramInt);
  }
  
  public abstract SocketAddress address();
  
  public abstract Association association();
  
  public abstract int bytes();
  
  public abstract boolean isComplete();
  
  public abstract MessageInfo complete(boolean paramBoolean);
  
  public abstract boolean isUnordered();
  
  public abstract MessageInfo unordered(boolean paramBoolean);
  
  public abstract int payloadProtocolID();
  
  public abstract MessageInfo payloadProtocolID(int paramInt);
  
  public abstract int streamNumber();
  
  public abstract MessageInfo streamNumber(int paramInt);
  
  public abstract long timeToLive();
  
  public abstract MessageInfo timeToLive(long paramLong);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\MessageInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */