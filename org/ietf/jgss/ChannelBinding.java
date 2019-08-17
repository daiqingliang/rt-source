package org.ietf.jgss;

import java.net.InetAddress;
import java.util.Arrays;

public class ChannelBinding {
  private InetAddress initiator;
  
  private InetAddress acceptor;
  
  private byte[] appData;
  
  public ChannelBinding(InetAddress paramInetAddress1, InetAddress paramInetAddress2, byte[] paramArrayOfByte) {
    this.initiator = paramInetAddress1;
    this.acceptor = paramInetAddress2;
    if (paramArrayOfByte != null) {
      this.appData = new byte[paramArrayOfByte.length];
      System.arraycopy(paramArrayOfByte, 0, this.appData, 0, paramArrayOfByte.length);
    } 
  }
  
  public ChannelBinding(byte[] paramArrayOfByte) { this(null, null, paramArrayOfByte); }
  
  public InetAddress getInitiatorAddress() { return this.initiator; }
  
  public InetAddress getAcceptorAddress() { return this.acceptor; }
  
  public byte[] getApplicationData() {
    if (this.appData == null)
      return null; 
    byte[] arrayOfByte = new byte[this.appData.length];
    System.arraycopy(this.appData, 0, arrayOfByte, 0, this.appData.length);
    return arrayOfByte;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ChannelBinding))
      return false; 
    ChannelBinding channelBinding = (ChannelBinding)paramObject;
    return ((this.initiator != null && channelBinding.initiator == null) || (this.initiator == null && channelBinding.initiator != null)) ? false : ((this.initiator != null && !this.initiator.equals(channelBinding.initiator)) ? false : (((this.acceptor != null && channelBinding.acceptor == null) || (this.acceptor == null && channelBinding.acceptor != null)) ? false : ((this.acceptor != null && !this.acceptor.equals(channelBinding.acceptor)) ? false : Arrays.equals(this.appData, channelBinding.appData))));
  }
  
  public int hashCode() { return (this.initiator != null) ? this.initiator.hashCode() : ((this.acceptor != null) ? this.acceptor.hashCode() : ((this.appData != null) ? (new String(this.appData)).hashCode() : 1)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\ChannelBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */