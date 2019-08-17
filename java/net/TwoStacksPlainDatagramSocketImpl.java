package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.net.ResourceManager;

class TwoStacksPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl {
  private FileDescriptor fd1;
  
  private InetAddress anyLocalBoundAddr = null;
  
  private int fduse = -1;
  
  private int lastfd = -1;
  
  private final boolean exclusiveBind;
  
  private boolean reuseAddressEmulated;
  
  private boolean isReuseAddress;
  
  TwoStacksPlainDatagramSocketImpl(boolean paramBoolean) { this.exclusiveBind = paramBoolean; }
  
  protected void create() throws SocketException {
    this.fd1 = new FileDescriptor();
    try {
      super.create();
    } catch (SocketException socketException) {
      this.fd1 = null;
      throw socketException;
    } 
  }
  
  protected void bind(int paramInt, InetAddress paramInetAddress) throws SocketException {
    super.bind(paramInt, paramInetAddress);
    if (paramInetAddress.isAnyLocalAddress())
      this.anyLocalBoundAddr = paramInetAddress; 
  }
  
  protected void bind0(int paramInt, InetAddress paramInetAddress) throws SocketException { bind0(paramInt, paramInetAddress, this.exclusiveBind); }
  
  protected void receive(DatagramPacket paramDatagramPacket) throws IOException {
    try {
      receive0(paramDatagramPacket);
    } finally {
      this.fduse = -1;
    } 
  }
  
  public Object getOption(int paramInt) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket Closed"); 
    if (paramInt == 15) {
      if (this.fd != null && this.fd1 != null && !this.connected)
        return this.anyLocalBoundAddr; 
      byte b = (this.connectedAddress == null) ? -1 : this.connectedAddress.holder().getFamily();
      return socketLocalAddress(b);
    } 
    return (paramInt == 4 && this.reuseAddressEmulated) ? Boolean.valueOf(this.isReuseAddress) : super.getOption(paramInt);
  }
  
  protected void socketSetOption(int paramInt, Object paramObject) throws SocketException {
    if (paramInt == 4 && this.exclusiveBind && this.localPort != 0) {
      this.reuseAddressEmulated = true;
      this.isReuseAddress = ((Boolean)paramObject).booleanValue();
    } else {
      socketNativeSetOption(paramInt, paramObject);
    } 
  }
  
  protected boolean isClosed() { return (this.fd == null && this.fd1 == null); }
  
  protected void close() throws SocketException {
    if (this.fd != null || this.fd1 != null) {
      datagramSocketClose();
      ResourceManager.afterUdpClose();
      this.fd = null;
      this.fd1 = null;
    } 
  }
  
  protected native void bind0(int paramInt, InetAddress paramInetAddress, boolean paramBoolean) throws SocketException;
  
  protected native void send(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected native int peek(InetAddress paramInetAddress) throws IOException;
  
  protected native int peekData(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected native void receive0(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected native void setTimeToLive(int paramInt) throws IOException;
  
  protected native int getTimeToLive() throws IOException;
  
  @Deprecated
  protected native void setTTL(byte paramByte) throws IOException;
  
  @Deprecated
  protected native byte getTTL() throws IOException;
  
  protected native void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected native void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected native void datagramSocketCreate() throws SocketException;
  
  protected native void datagramSocketClose() throws SocketException;
  
  protected native void socketNativeSetOption(int paramInt, Object paramObject) throws SocketException;
  
  protected native Object socketGetOption(int paramInt) throws SocketException;
  
  protected native void connect0(InetAddress paramInetAddress, int paramInt) throws SocketException;
  
  protected native Object socketLocalAddress(int paramInt) throws SocketException;
  
  protected native void disconnect0(int paramInt) throws IOException;
  
  native int dataAvailable() throws IOException;
  
  private static native void init() throws SocketException;
  
  static  {
    init();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\TwoStacksPlainDatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */