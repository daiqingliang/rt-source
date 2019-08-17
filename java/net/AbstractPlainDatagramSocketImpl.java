package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ResourceManager;
import sun.security.action.GetPropertyAction;

abstract class AbstractPlainDatagramSocketImpl extends DatagramSocketImpl {
  int timeout = 0;
  
  boolean connected = false;
  
  private int trafficClass = 0;
  
  protected InetAddress connectedAddress = null;
  
  private int connectedPort = -1;
  
  private static final String os = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
  
  private static final boolean connectDisabled = os.contains("OS X");
  
  protected void create() {
    ResourceManager.beforeUdpCreate();
    this.fd = new FileDescriptor();
    try {
      datagramSocketCreate();
    } catch (SocketException socketException) {
      ResourceManager.afterUdpClose();
      this.fd = null;
      throw socketException;
    } 
  }
  
  protected void bind(int paramInt, InetAddress paramInetAddress) throws SocketException { bind0(paramInt, paramInetAddress); }
  
  protected abstract void bind0(int paramInt, InetAddress paramInetAddress) throws SocketException;
  
  protected abstract void send(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected void connect(InetAddress paramInetAddress, int paramInt) throws SocketException {
    connect0(paramInetAddress, paramInt);
    this.connectedAddress = paramInetAddress;
    this.connectedPort = paramInt;
    this.connected = true;
  }
  
  protected void disconnect() {
    disconnect0(this.connectedAddress.holder().getFamily());
    this.connected = false;
    this.connectedAddress = null;
    this.connectedPort = -1;
  }
  
  protected abstract int peek(InetAddress paramInetAddress) throws IOException;
  
  protected abstract int peekData(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected void receive(DatagramPacket paramDatagramPacket) throws IOException { receive0(paramDatagramPacket); }
  
  protected abstract void receive0(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected abstract void setTimeToLive(int paramInt) throws IOException;
  
  protected abstract int getTimeToLive() throws IOException;
  
  @Deprecated
  protected abstract void setTTL(byte paramByte) throws IOException;
  
  @Deprecated
  protected abstract byte getTTL() throws IOException;
  
  protected void join(InetAddress paramInetAddress) throws IOException { join(paramInetAddress, null); }
  
  protected void leave(InetAddress paramInetAddress) throws IOException { leave(paramInetAddress, null); }
  
  protected void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface) throws IOException {
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    join(((InetSocketAddress)paramSocketAddress).getAddress(), paramNetworkInterface);
  }
  
  protected abstract void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface) throws IOException {
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    leave(((InetSocketAddress)paramSocketAddress).getAddress(), paramNetworkInterface);
  }
  
  protected abstract void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected void close() {
    if (this.fd != null) {
      datagramSocketClose();
      ResourceManager.afterUdpClose();
      this.fd = null;
    } 
  }
  
  protected boolean isClosed() { return (this.fd == null); }
  
  protected void finalize() { close(); }
  
  public void setOption(int paramInt, Object paramObject) throws SocketException {
    int i;
    if (isClosed())
      throw new SocketException("Socket Closed"); 
    switch (paramInt) {
      case 4102:
        if (paramObject == null || !(paramObject instanceof Integer))
          throw new SocketException("bad argument for SO_TIMEOUT"); 
        i = ((Integer)paramObject).intValue();
        if (i < 0)
          throw new IllegalArgumentException("timeout < 0"); 
        this.timeout = i;
        return;
      case 3:
        if (paramObject == null || !(paramObject instanceof Integer))
          throw new SocketException("bad argument for IP_TOS"); 
        this.trafficClass = ((Integer)paramObject).intValue();
        break;
      case 4:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad argument for SO_REUSEADDR"); 
        break;
      case 32:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad argument for SO_BROADCAST"); 
        break;
      case 15:
        throw new SocketException("Cannot re-bind Socket");
      case 4097:
      case 4098:
        if (paramObject == null || !(paramObject instanceof Integer) || ((Integer)paramObject).intValue() < 0)
          throw new SocketException("bad argument for SO_SNDBUF or SO_RCVBUF"); 
        break;
      case 16:
        if (paramObject == null || !(paramObject instanceof InetAddress))
          throw new SocketException("bad argument for IP_MULTICAST_IF"); 
        break;
      case 31:
        if (paramObject == null || !(paramObject instanceof NetworkInterface))
          throw new SocketException("bad argument for IP_MULTICAST_IF2"); 
        break;
      case 18:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad argument for IP_MULTICAST_LOOP"); 
        break;
      default:
        throw new SocketException("invalid option: " + paramInt);
    } 
    socketSetOption(paramInt, paramObject);
  }
  
  public Object getOption(int paramInt) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket Closed"); 
    switch (paramInt) {
      case 4102:
        return new Integer(this.timeout);
      case 3:
        null = socketGetOption(paramInt);
        if (((Integer)null).intValue() == -1)
          null = new Integer(this.trafficClass); 
        return null;
      case 4:
      case 15:
      case 16:
      case 18:
      case 31:
      case 32:
      case 4097:
      case 4098:
        return socketGetOption(paramInt);
    } 
    throw new SocketException("invalid option: " + paramInt);
  }
  
  protected abstract void datagramSocketCreate();
  
  protected abstract void datagramSocketClose();
  
  protected abstract void socketSetOption(int paramInt, Object paramObject) throws SocketException;
  
  protected abstract Object socketGetOption(int paramInt) throws SocketException;
  
  protected abstract void connect0(InetAddress paramInetAddress, int paramInt) throws SocketException;
  
  protected abstract void disconnect0(int paramInt) throws IOException;
  
  protected boolean nativeConnectDisabled() { return connectDisabled; }
  
  abstract int dataAvailable() throws IOException;
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\AbstractPlainDatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */