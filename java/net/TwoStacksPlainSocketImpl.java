package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.net.ResourceManager;

class TwoStacksPlainSocketImpl extends AbstractPlainSocketImpl {
  private FileDescriptor fd1;
  
  private InetAddress anyLocalBoundAddr = null;
  
  private int lastfd = -1;
  
  private final boolean exclusiveBind;
  
  private boolean isReuseAddress;
  
  public TwoStacksPlainSocketImpl(boolean paramBoolean) { this.exclusiveBind = paramBoolean; }
  
  public TwoStacksPlainSocketImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean) {
    this.fd = paramFileDescriptor;
    this.exclusiveBind = paramBoolean;
  }
  
  protected void create(boolean paramBoolean) {
    this.fd1 = new FileDescriptor();
    try {
      super.create(paramBoolean);
    } catch (IOException iOException) {
      this.fd1 = null;
      throw iOException;
    } 
  }
  
  protected void bind(InetAddress paramInetAddress, int paramInt) throws IOException {
    super.bind(paramInetAddress, paramInt);
    if (paramInetAddress.isAnyLocalAddress())
      this.anyLocalBoundAddr = paramInetAddress; 
  }
  
  public Object getOption(int paramInt) throws SocketException {
    if (isClosedOrPending())
      throw new SocketException("Socket Closed"); 
    if (paramInt == 15) {
      if (this.fd != null && this.fd1 != null)
        return this.anyLocalBoundAddr; 
      InetAddressContainer inetAddressContainer = new InetAddressContainer();
      socketGetOption(paramInt, inetAddressContainer);
      return inetAddressContainer.addr;
    } 
    return (paramInt == 4 && this.exclusiveBind) ? Boolean.valueOf(this.isReuseAddress) : super.getOption(paramInt);
  }
  
  void socketBind(InetAddress paramInetAddress, int paramInt) throws IOException { socketBind(paramInetAddress, paramInt, this.exclusiveBind); }
  
  void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject) throws SocketException {
    if (paramInt == 4 && this.exclusiveBind) {
      this.isReuseAddress = paramBoolean;
    } else {
      socketNativeSetOption(paramInt, paramBoolean, paramObject);
    } 
  }
  
  protected void close() throws IOException {
    synchronized (this.fdLock) {
      if (this.fd != null || this.fd1 != null) {
        if (!this.stream)
          ResourceManager.afterUdpClose(); 
        if (this.fdUseCount == 0) {
          if (this.closePending)
            return; 
          this.closePending = true;
          socketClose();
          this.fd = null;
          this.fd1 = null;
          return;
        } 
        if (!this.closePending) {
          this.closePending = true;
          this.fdUseCount--;
          socketClose();
        } 
      } 
    } 
  }
  
  void reset() throws IOException {
    if (this.fd != null || this.fd1 != null)
      socketClose(); 
    this.fd = null;
    this.fd1 = null;
    super.reset();
  }
  
  public boolean isClosedOrPending() {
    synchronized (this.fdLock) {
      if (this.closePending || (this.fd == null && this.fd1 == null))
        return true; 
      return false;
    } 
  }
  
  static native void initProto() throws IOException;
  
  native void socketCreate(boolean paramBoolean);
  
  native void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException;
  
  native void socketBind(InetAddress paramInetAddress, int paramInt, boolean paramBoolean) throws IOException;
  
  native void socketListen(int paramInt) throws IOException;
  
  native void socketAccept(SocketImpl paramSocketImpl) throws IOException;
  
  native int socketAvailable() throws IOException;
  
  native void socketClose0(boolean paramBoolean);
  
  native void socketShutdown(int paramInt) throws IOException;
  
  native void socketNativeSetOption(int paramInt, boolean paramBoolean, Object paramObject) throws SocketException;
  
  native int socketGetOption(int paramInt, Object paramObject) throws SocketException;
  
  native void socketSendUrgentData(int paramInt) throws IOException;
  
  static  {
    initProto();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\TwoStacksPlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */