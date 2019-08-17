package java.net;

import java.io.IOException;
import java.util.Enumeration;

public class MulticastSocket extends DatagramSocket {
  private boolean interfaceSet;
  
  private Object ttlLock = new Object();
  
  private Object infLock = new Object();
  
  private InetAddress infAddress = null;
  
  public MulticastSocket() throws IOException { this(new InetSocketAddress(0)); }
  
  public MulticastSocket(int paramInt) throws IOException { this(new InetSocketAddress(paramInt)); }
  
  public MulticastSocket(SocketAddress paramSocketAddress) throws IOException {
    super((SocketAddress)null);
    setReuseAddress(true);
    if (paramSocketAddress != null)
      try {
        bind(paramSocketAddress);
      } finally {
        if (!isBound())
          close(); 
      }  
  }
  
  @Deprecated
  public void setTTL(byte paramByte) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setTTL(paramByte);
  }
  
  public void setTimeToLive(int paramInt) throws IOException {
    if (paramInt < 0 || paramInt > 255)
      throw new IllegalArgumentException("ttl out of range"); 
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setTimeToLive(paramInt);
  }
  
  @Deprecated
  public byte getTTL() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return getImpl().getTTL();
  }
  
  public int getTimeToLive() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return getImpl().getTimeToLive();
  }
  
  public void joinGroup(InetAddress paramInetAddress) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    checkAddress(paramInetAddress, "joinGroup");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkMulticast(paramInetAddress); 
    if (!paramInetAddress.isMulticastAddress())
      throw new SocketException("Not a multicast address"); 
    NetworkInterface networkInterface = NetworkInterface.getDefault();
    if (!this.interfaceSet && networkInterface != null)
      setNetworkInterface(networkInterface); 
    getImpl().join(paramInetAddress);
  }
  
  public void leaveGroup(InetAddress paramInetAddress) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    checkAddress(paramInetAddress, "leaveGroup");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkMulticast(paramInetAddress); 
    if (!paramInetAddress.isMulticastAddress())
      throw new SocketException("Not a multicast address"); 
    getImpl().leave(paramInetAddress);
  }
  
  public void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    if (this.oldImpl)
      throw new UnsupportedOperationException(); 
    checkAddress(((InetSocketAddress)paramSocketAddress).getAddress(), "joinGroup");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkMulticast(((InetSocketAddress)paramSocketAddress).getAddress()); 
    if (!((InetSocketAddress)paramSocketAddress).getAddress().isMulticastAddress())
      throw new SocketException("Not a multicast address"); 
    getImpl().joinGroup(paramSocketAddress, paramNetworkInterface);
  }
  
  public void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    if (this.oldImpl)
      throw new UnsupportedOperationException(); 
    checkAddress(((InetSocketAddress)paramSocketAddress).getAddress(), "leaveGroup");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkMulticast(((InetSocketAddress)paramSocketAddress).getAddress()); 
    if (!((InetSocketAddress)paramSocketAddress).getAddress().isMulticastAddress())
      throw new SocketException("Not a multicast address"); 
    getImpl().leaveGroup(paramSocketAddress, paramNetworkInterface);
  }
  
  public void setInterface(InetAddress paramInetAddress) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    checkAddress(paramInetAddress, "setInterface");
    synchronized (this.infLock) {
      getImpl().setOption(16, paramInetAddress);
      this.infAddress = paramInetAddress;
      this.interfaceSet = true;
    } 
  }
  
  public InetAddress getInterface() throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    synchronized (this.infLock) {
      InetAddress inetAddress = (InetAddress)getImpl().getOption(16);
      if (this.infAddress == null)
        return inetAddress; 
      if (inetAddress.equals(this.infAddress))
        return inetAddress; 
      try {
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        Enumeration enumeration = networkInterface.getInetAddresses();
        while (enumeration.hasMoreElements()) {
          InetAddress inetAddress1 = (InetAddress)enumeration.nextElement();
          if (inetAddress1.equals(this.infAddress))
            return this.infAddress; 
        } 
        this.infAddress = null;
        return inetAddress;
      } catch (Exception exception) {
        return inetAddress;
      } 
    } 
  }
  
  public void setNetworkInterface(NetworkInterface paramNetworkInterface) throws SocketException {
    synchronized (this.infLock) {
      getImpl().setOption(31, paramNetworkInterface);
      this.infAddress = null;
      this.interfaceSet = true;
    } 
  }
  
  public NetworkInterface getNetworkInterface() throws SocketException {
    NetworkInterface networkInterface = (NetworkInterface)getImpl().getOption(31);
    if (networkInterface.getIndex() == 0 || networkInterface.getIndex() == -1) {
      InetAddress[] arrayOfInetAddress = new InetAddress[1];
      arrayOfInetAddress[0] = InetAddress.anyLocalAddress();
      return new NetworkInterface(arrayOfInetAddress[0].getHostName(), 0, arrayOfInetAddress);
    } 
    return networkInterface;
  }
  
  public void setLoopbackMode(boolean paramBoolean) throws SocketException { getImpl().setOption(18, Boolean.valueOf(paramBoolean)); }
  
  public boolean getLoopbackMode() throws SocketException { return ((Boolean)getImpl().getOption(18)).booleanValue(); }
  
  @Deprecated
  public void send(DatagramPacket paramDatagramPacket, byte paramByte) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    checkAddress(paramDatagramPacket.getAddress(), "send");
    synchronized (this.ttlLock) {
      synchronized (paramDatagramPacket) {
        if (this.connectState == 0) {
          SecurityManager securityManager = System.getSecurityManager();
          if (securityManager != null)
            if (paramDatagramPacket.getAddress().isMulticastAddress()) {
              securityManager.checkMulticast(paramDatagramPacket.getAddress(), paramByte);
            } else {
              securityManager.checkConnect(paramDatagramPacket.getAddress().getHostAddress(), paramDatagramPacket.getPort());
            }  
        } else {
          InetAddress inetAddress = null;
          inetAddress = paramDatagramPacket.getAddress();
          if (inetAddress == null) {
            paramDatagramPacket.setAddress(this.connectedAddress);
            paramDatagramPacket.setPort(this.connectedPort);
          } else if (!inetAddress.equals(this.connectedAddress) || paramDatagramPacket.getPort() != this.connectedPort) {
            throw new SecurityException("connected address and packet address differ");
          } 
        } 
        b = getTTL();
        try {
          if (paramByte != b)
            getImpl().setTTL(paramByte); 
          getImpl().send(paramDatagramPacket);
        } finally {
          if (paramByte != b)
            getImpl().setTTL(b); 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\MulticastSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */