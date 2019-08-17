package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

class DualStackPlainSocketImpl extends AbstractPlainSocketImpl {
  static JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  private final boolean exclusiveBind;
  
  private boolean isReuseAddress;
  
  static final int WOULDBLOCK = -2;
  
  public DualStackPlainSocketImpl(boolean paramBoolean) { this.exclusiveBind = paramBoolean; }
  
  public DualStackPlainSocketImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean) {
    this.fd = paramFileDescriptor;
    this.exclusiveBind = paramBoolean;
  }
  
  void socketCreate(boolean paramBoolean) {
    if (this.fd == null)
      throw new SocketException("Socket closed"); 
    int i = socket0(paramBoolean, false);
    fdAccess.set(this.fd, i);
  }
  
  void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException {
    i = checkAndReturnNativeFD();
    if (paramInetAddress == null)
      throw new NullPointerException("inet address argument is null."); 
    if (paramInt2 <= 0) {
      int j = connect0(i, paramInetAddress, paramInt1);
    } else {
      configureBlocking(i, false);
      try {
        int j = connect0(i, paramInetAddress, paramInt1);
        if (j == -2)
          waitForConnect(i, paramInt2); 
      } finally {
        configureBlocking(i, true);
      } 
    } 
    if (this.localport == 0)
      this.localport = localPort0(i); 
  }
  
  void socketBind(InetAddress paramInetAddress, int paramInt) throws IOException {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null)
      throw new NullPointerException("inet address argument is null."); 
    bind0(i, paramInetAddress, paramInt, this.exclusiveBind);
    if (paramInt == 0) {
      this.localport = localPort0(i);
    } else {
      this.localport = paramInt;
    } 
    this.address = paramInetAddress;
  }
  
  void socketListen(int paramInt) throws IOException {
    int i = checkAndReturnNativeFD();
    listen0(i, paramInt);
  }
  
  void socketAccept(SocketImpl paramSocketImpl) throws IOException {
    i = checkAndReturnNativeFD();
    if (paramSocketImpl == null)
      throw new NullPointerException("socket is null"); 
    int j = -1;
    InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
    if (this.timeout <= 0) {
      j = accept0(i, arrayOfInetSocketAddress);
    } else {
      configureBlocking(i, false);
      try {
        waitForNewConnection(i, this.timeout);
        j = accept0(i, arrayOfInetSocketAddress);
        if (j != -1)
          configureBlocking(j, true); 
      } finally {
        configureBlocking(i, true);
      } 
    } 
    fdAccess.set(paramSocketImpl.fd, j);
    InetSocketAddress inetSocketAddress = arrayOfInetSocketAddress[0];
    paramSocketImpl.port = inetSocketAddress.getPort();
    paramSocketImpl.address = inetSocketAddress.getAddress();
    paramSocketImpl.localport = this.localport;
  }
  
  int socketAvailable() throws IOException {
    int i = checkAndReturnNativeFD();
    return available0(i);
  }
  
  void socketClose0(boolean paramBoolean) {
    if (this.fd == null)
      throw new SocketException("Socket closed"); 
    if (!this.fd.valid())
      return; 
    int i = fdAccess.get(this.fd);
    fdAccess.set(this.fd, -1);
    close0(i);
  }
  
  void socketShutdown(int paramInt) throws IOException {
    int i = checkAndReturnNativeFD();
    shutdown0(i, paramInt);
  }
  
  void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject) throws SocketException {
    int i = checkAndReturnNativeFD();
    if (paramInt == 4102)
      return; 
    int j = 0;
    switch (paramInt) {
      case 4:
        if (this.exclusiveBind) {
          this.isReuseAddress = paramBoolean;
          return;
        } 
      case 1:
      case 8:
      case 4099:
        j = paramBoolean ? 1 : 0;
        break;
      case 3:
      case 4097:
      case 4098:
        j = ((Integer)paramObject).intValue();
        break;
      case 128:
        if (paramBoolean) {
          j = ((Integer)paramObject).intValue();
          break;
        } 
        j = -1;
        break;
      default:
        throw new SocketException("Option not supported");
    } 
    setIntOption(i, paramInt, j);
  }
  
  int socketGetOption(int paramInt, Object paramObject) throws SocketException {
    int i = checkAndReturnNativeFD();
    if (paramInt == 15) {
      localAddress(i, (InetAddressContainer)paramObject);
      return 0;
    } 
    if (paramInt == 4 && this.exclusiveBind)
      return this.isReuseAddress ? 1 : -1; 
    int j = getIntOption(i, paramInt);
    switch (paramInt) {
      case 1:
      case 4:
      case 8:
      case 4099:
        return (j == 0) ? -1 : 1;
    } 
    return j;
  }
  
  void socketSendUrgentData(int paramInt) throws IOException {
    int i = checkAndReturnNativeFD();
    sendOOB(i, paramInt);
  }
  
  private int checkAndReturnNativeFD() throws IOException {
    if (this.fd == null || !this.fd.valid())
      throw new SocketException("Socket closed"); 
    return fdAccess.get(this.fd);
  }
  
  static native void initIDs();
  
  static native int socket0(boolean paramBoolean1, boolean paramBoolean2) throws IOException;
  
  static native void bind0(int paramInt1, InetAddress paramInetAddress, int paramInt2, boolean paramBoolean) throws IOException;
  
  static native int connect0(int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException;
  
  static native void waitForConnect(int paramInt1, int paramInt2) throws IOException;
  
  static native int localPort0(int paramInt) throws IOException;
  
  static native void localAddress(int paramInt, InetAddressContainer paramInetAddressContainer) throws SocketException;
  
  static native void listen0(int paramInt1, int paramInt2) throws IOException;
  
  static native int accept0(int paramInt, InetSocketAddress[] paramArrayOfInetSocketAddress) throws IOException;
  
  static native void waitForNewConnection(int paramInt1, int paramInt2) throws IOException;
  
  static native int available0(int paramInt) throws IOException;
  
  static native void close0(int paramInt) throws IOException;
  
  static native void shutdown0(int paramInt1, int paramInt2) throws IOException;
  
  static native void setIntOption(int paramInt1, int paramInt2, int paramInt3) throws SocketException;
  
  static native int getIntOption(int paramInt1, int paramInt2) throws SocketException;
  
  static native void sendOOB(int paramInt1, int paramInt2) throws IOException;
  
  static native void configureBlocking(int paramInt, boolean paramBoolean) throws IOException;
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\DualStackPlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */