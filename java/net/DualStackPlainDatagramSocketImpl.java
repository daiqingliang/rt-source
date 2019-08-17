package java.net;

import java.io.IOException;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

class DualStackPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl {
  static JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  private final boolean exclusiveBind;
  
  private boolean reuseAddressEmulated;
  
  private boolean isReuseAddress;
  
  DualStackPlainDatagramSocketImpl(boolean paramBoolean) { this.exclusiveBind = paramBoolean; }
  
  protected void datagramSocketCreate() throws SocketException {
    if (this.fd == null)
      throw new SocketException("Socket closed"); 
    int i = socketCreate(false);
    fdAccess.set(this.fd, i);
  }
  
  protected void bind0(int paramInt, InetAddress paramInetAddress) throws SocketException {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null)
      throw new NullPointerException("argument address"); 
    socketBind(i, paramInetAddress, paramInt, this.exclusiveBind);
    if (paramInt == 0) {
      this.localPort = socketLocalPort(i);
    } else {
      this.localPort = paramInt;
    } 
  }
  
  protected int peek(InetAddress paramInetAddress) throws IOException {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null)
      throw new NullPointerException("Null address in peek()"); 
    DatagramPacket datagramPacket = new DatagramPacket(new byte[1], 1);
    int j = peekData(datagramPacket);
    paramInetAddress = datagramPacket.getAddress();
    return j;
  }
  
  protected int peekData(DatagramPacket paramDatagramPacket) throws IOException {
    int i = checkAndReturnNativeFD();
    if (paramDatagramPacket == null)
      throw new NullPointerException("packet"); 
    if (paramDatagramPacket.getData() == null)
      throw new NullPointerException("packet buffer"); 
    return socketReceiveOrPeekData(i, paramDatagramPacket, this.timeout, this.connected, true);
  }
  
  protected void receive0(DatagramPacket paramDatagramPacket) throws IOException {
    int i = checkAndReturnNativeFD();
    if (paramDatagramPacket == null)
      throw new NullPointerException("packet"); 
    if (paramDatagramPacket.getData() == null)
      throw new NullPointerException("packet buffer"); 
    socketReceiveOrPeekData(i, paramDatagramPacket, this.timeout, this.connected, false);
  }
  
  protected void send(DatagramPacket paramDatagramPacket) throws IOException {
    int i = checkAndReturnNativeFD();
    if (paramDatagramPacket == null)
      throw new NullPointerException("null packet"); 
    if (paramDatagramPacket.getAddress() == null || paramDatagramPacket.getData() == null)
      throw new NullPointerException("null address || null buffer"); 
    socketSend(i, paramDatagramPacket.getData(), paramDatagramPacket.getOffset(), paramDatagramPacket.getLength(), paramDatagramPacket.getAddress(), paramDatagramPacket.getPort(), this.connected);
  }
  
  protected void connect0(InetAddress paramInetAddress, int paramInt) throws SocketException {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null)
      throw new NullPointerException("address"); 
    socketConnect(i, paramInetAddress, paramInt);
  }
  
  protected void disconnect0(int paramInt) {
    if (this.fd == null || !this.fd.valid())
      return; 
    socketDisconnect(fdAccess.get(this.fd));
  }
  
  protected void datagramSocketClose() throws SocketException {
    if (this.fd == null || !this.fd.valid())
      return; 
    socketClose(fdAccess.get(this.fd));
    fdAccess.set(this.fd, -1);
  }
  
  protected void socketSetOption(int paramInt, Object paramObject) throws SocketException {
    int i = checkAndReturnNativeFD();
    int j = 0;
    switch (paramInt) {
      case 3:
      case 4097:
      case 4098:
        j = ((Integer)paramObject).intValue();
        break;
      case 4:
        if (this.exclusiveBind && this.localPort != 0) {
          this.reuseAddressEmulated = true;
          this.isReuseAddress = ((Boolean)paramObject).booleanValue();
          return;
        } 
      case 32:
        j = ((Boolean)paramObject).booleanValue() ? 1 : 0;
        break;
      default:
        throw new SocketException("Option not supported");
    } 
    socketSetIntOption(i, paramInt, j);
  }
  
  protected Object socketGetOption(int paramInt) throws SocketException {
    int i = checkAndReturnNativeFD();
    if (paramInt == 15)
      return socketLocalAddress(i); 
    if (paramInt == 4 && this.reuseAddressEmulated)
      return Boolean.valueOf(this.isReuseAddress); 
    int j = socketGetIntOption(i, paramInt);
    null = null;
    switch (paramInt) {
      case 4:
      case 32:
        return (j == 0) ? Boolean.FALSE : Boolean.TRUE;
      case 3:
      case 4097:
      case 4098:
        return new Integer(j);
    } 
    throw new SocketException("Option not supported");
  }
  
  protected void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException { throw new IOException("Method not implemented!"); }
  
  protected void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException { throw new IOException("Method not implemented!"); }
  
  protected void setTimeToLive(int paramInt) { throw new IOException("Method not implemented!"); }
  
  protected int getTimeToLive() throws IOException { throw new IOException("Method not implemented!"); }
  
  @Deprecated
  protected void setTTL(byte paramByte) throws IOException { throw new IOException("Method not implemented!"); }
  
  @Deprecated
  protected byte getTTL() throws IOException { throw new IOException("Method not implemented!"); }
  
  private int checkAndReturnNativeFD() throws IOException {
    if (this.fd == null || !this.fd.valid())
      throw new SocketException("Socket closed"); 
    return fdAccess.get(this.fd);
  }
  
  private static native void initIDs() throws SocketException;
  
  private static native int socketCreate(boolean paramBoolean);
  
  private static native void socketBind(int paramInt1, InetAddress paramInetAddress, int paramInt2, boolean paramBoolean) throws SocketException;
  
  private static native void socketConnect(int paramInt1, InetAddress paramInetAddress, int paramInt2) throws SocketException;
  
  private static native void socketDisconnect(int paramInt);
  
  private static native void socketClose(int paramInt);
  
  private static native int socketLocalPort(int paramInt) throws SocketException;
  
  private static native Object socketLocalAddress(int paramInt) throws SocketException;
  
  private static native int socketReceiveOrPeekData(int paramInt1, DatagramPacket paramDatagramPacket, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws IOException;
  
  private static native void socketSend(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, InetAddress paramInetAddress, int paramInt4, boolean paramBoolean) throws IOException;
  
  private static native void socketSetIntOption(int paramInt1, int paramInt2, int paramInt3) throws SocketException;
  
  private static native int socketGetIntOption(int paramInt1, int paramInt2) throws SocketException;
  
  native int dataAvailable() throws IOException;
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\DualStackPlainDatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */