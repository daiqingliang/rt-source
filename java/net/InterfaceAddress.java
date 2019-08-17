package java.net;

public class InterfaceAddress {
  private InetAddress address = null;
  
  private Inet4Address broadcast = null;
  
  private short maskLength = 0;
  
  public InetAddress getAddress() { return this.address; }
  
  public InetAddress getBroadcast() { return this.broadcast; }
  
  public short getNetworkPrefixLength() { return this.maskLength; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof InterfaceAddress))
      return false; 
    InterfaceAddress interfaceAddress = (InterfaceAddress)paramObject;
    return ((this.address == null) ? (interfaceAddress.address == null) : this.address.equals(interfaceAddress.address)) ? (((this.broadcast == null) ? (interfaceAddress.broadcast == null) : this.broadcast.equals(interfaceAddress.broadcast)) ? (!(this.maskLength != interfaceAddress.maskLength)) : false) : false;
  }
  
  public int hashCode() { return this.address.hashCode() + ((this.broadcast != null) ? this.broadcast.hashCode() : 0) + this.maskLength; }
  
  public String toString() { return this.address + "/" + this.maskLength + " [" + this.broadcast + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\InterfaceAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */