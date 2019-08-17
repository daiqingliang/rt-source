package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.util.HashSet;

class MembershipKeyImpl extends MembershipKey {
  private final MulticastChannel ch;
  
  private final InetAddress group;
  
  private final NetworkInterface interf;
  
  private final InetAddress source;
  
  private Object stateLock = new Object();
  
  private HashSet<InetAddress> blockedSet;
  
  private MembershipKeyImpl(MulticastChannel paramMulticastChannel, InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2) {
    this.ch = paramMulticastChannel;
    this.group = paramInetAddress1;
    this.interf = paramNetworkInterface;
    this.source = paramInetAddress2;
  }
  
  public boolean isValid() { return this.valid; }
  
  void invalidate() { this.valid = false; }
  
  public void drop() { ((DatagramChannelImpl)this.ch).drop(this); }
  
  public MulticastChannel channel() { return this.ch; }
  
  public InetAddress group() { return this.group; }
  
  public NetworkInterface networkInterface() { return this.interf; }
  
  public InetAddress sourceAddress() { return this.source; }
  
  public MembershipKey block(InetAddress paramInetAddress) throws IOException {
    if (this.source != null)
      throw new IllegalStateException("key is source-specific"); 
    synchronized (this.stateLock) {
      if (this.blockedSet != null && this.blockedSet.contains(paramInetAddress))
        return this; 
      ((DatagramChannelImpl)this.ch).block(this, paramInetAddress);
      if (this.blockedSet == null)
        this.blockedSet = new HashSet(); 
      this.blockedSet.add(paramInetAddress);
    } 
    return this;
  }
  
  public MembershipKey unblock(InetAddress paramInetAddress) throws IOException {
    synchronized (this.stateLock) {
      if (this.blockedSet == null || !this.blockedSet.contains(paramInetAddress))
        throw new IllegalStateException("not blocked"); 
      ((DatagramChannelImpl)this.ch).unblock(this, paramInetAddress);
      this.blockedSet.remove(paramInetAddress);
    } 
    return this;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(64);
    stringBuilder.append('<');
    stringBuilder.append(this.group.getHostAddress());
    stringBuilder.append(',');
    stringBuilder.append(this.interf.getName());
    if (this.source != null) {
      stringBuilder.append(',');
      stringBuilder.append(this.source.getHostAddress());
    } 
    stringBuilder.append('>');
    return stringBuilder.toString();
  }
  
  static class Type4 extends MembershipKeyImpl {
    private final int groupAddress;
    
    private final int interfAddress;
    
    private final int sourceAddress;
    
    Type4(MulticastChannel param1MulticastChannel, InetAddress param1InetAddress1, NetworkInterface param1NetworkInterface, InetAddress param1InetAddress2, int param1Int1, int param1Int2, int param1Int3) {
      super(param1MulticastChannel, param1InetAddress1, param1NetworkInterface, param1InetAddress2, null);
      this.groupAddress = param1Int1;
      this.interfAddress = param1Int2;
      this.sourceAddress = param1Int3;
    }
    
    int groupAddress() { return this.groupAddress; }
    
    int interfaceAddress() { return this.interfAddress; }
    
    int source() { return this.sourceAddress; }
  }
  
  static class Type6 extends MembershipKeyImpl {
    private final byte[] groupAddress;
    
    private final int index;
    
    private final byte[] sourceAddress;
    
    Type6(MulticastChannel param1MulticastChannel, InetAddress param1InetAddress1, NetworkInterface param1NetworkInterface, InetAddress param1InetAddress2, byte[] param1ArrayOfByte1, int param1Int, byte[] param1ArrayOfByte2) {
      super(param1MulticastChannel, param1InetAddress1, param1NetworkInterface, param1InetAddress2, null);
      this.groupAddress = param1ArrayOfByte1;
      this.index = param1Int;
      this.sourceAddress = param1ArrayOfByte2;
    }
    
    byte[] groupAddress() { return this.groupAddress; }
    
    int index() { return this.index; }
    
    byte[] source() { return this.sourceAddress; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\MembershipKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */