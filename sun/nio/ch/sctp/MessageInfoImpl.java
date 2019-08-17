package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import java.net.SocketAddress;

public class MessageInfoImpl extends MessageInfo {
  private final SocketAddress address;
  
  private final int bytes;
  
  private Association association;
  
  private int assocId;
  
  private int streamNumber;
  
  private boolean complete = true;
  
  private boolean unordered;
  
  private long timeToLive;
  
  private int ppid;
  
  public MessageInfoImpl(Association paramAssociation, SocketAddress paramSocketAddress, int paramInt) {
    this.association = paramAssociation;
    this.address = paramSocketAddress;
    this.streamNumber = paramInt;
    this.bytes = 0;
  }
  
  private MessageInfoImpl(int paramInt1, SocketAddress paramSocketAddress, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4) {
    this.assocId = paramInt1;
    this.address = paramSocketAddress;
    this.bytes = paramInt2;
    this.streamNumber = paramInt3;
    this.complete = paramBoolean1;
    this.unordered = paramBoolean2;
    this.ppid = paramInt4;
  }
  
  public Association association() { return this.association; }
  
  void setAssociation(Association paramAssociation) { this.association = paramAssociation; }
  
  int associationID() { return this.assocId; }
  
  public SocketAddress address() { return this.address; }
  
  public int bytes() { return this.bytes; }
  
  public int streamNumber() { return this.streamNumber; }
  
  public MessageInfo streamNumber(int paramInt) {
    if (paramInt < 0 || paramInt > 65536)
      throw new IllegalArgumentException("Invalid stream number"); 
    this.streamNumber = paramInt;
    return this;
  }
  
  public int payloadProtocolID() { return this.ppid; }
  
  public MessageInfo payloadProtocolID(int paramInt) {
    this.ppid = paramInt;
    return this;
  }
  
  public boolean isComplete() { return this.complete; }
  
  public MessageInfo complete(boolean paramBoolean) {
    this.complete = paramBoolean;
    return this;
  }
  
  public boolean isUnordered() { return this.unordered; }
  
  public MessageInfo unordered(boolean paramBoolean) {
    this.unordered = paramBoolean;
    return this;
  }
  
  public long timeToLive() { return this.timeToLive; }
  
  public MessageInfo timeToLive(long paramLong) {
    this.timeToLive = paramLong;
    return this;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append("[Address: ").append(this.address).append(", Association: ").append(this.association).append(", Assoc ID: ").append(this.assocId).append(", Bytes: ").append(this.bytes).append(", Stream Number: ").append(this.streamNumber).append(", Complete: ").append(this.complete).append(", isUnordered: ").append(this.unordered).append("]");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\sctp\MessageInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */