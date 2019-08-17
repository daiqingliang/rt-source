package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ConnectionResetException;
import sun.net.NetHooks;
import sun.net.ResourceManager;

abstract class AbstractPlainSocketImpl extends SocketImpl {
  int timeout;
  
  private int trafficClass;
  
  private boolean shut_rd = false;
  
  private boolean shut_wr = false;
  
  private SocketInputStream socketInputStream = null;
  
  private SocketOutputStream socketOutputStream = null;
  
  protected int fdUseCount = 0;
  
  protected final Object fdLock = new Object();
  
  protected boolean closePending = false;
  
  private int CONNECTION_NOT_RESET = 0;
  
  private int CONNECTION_RESET_PENDING = 1;
  
  private int CONNECTION_RESET = 2;
  
  private int resetState;
  
  private final Object resetLock = new Object();
  
  protected boolean stream;
  
  public static final int SHUT_RD = 0;
  
  public static final int SHUT_WR = 1;
  
  protected void create(boolean paramBoolean) throws IOException {
    this.stream = paramBoolean;
    if (!paramBoolean) {
      ResourceManager.beforeUdpCreate();
      this.fd = new FileDescriptor();
      try {
        socketCreate(false);
      } catch (IOException iOException) {
        ResourceManager.afterUdpClose();
        this.fd = null;
        throw iOException;
      } 
    } else {
      this.fd = new FileDescriptor();
      socketCreate(true);
    } 
    if (this.socket != null)
      this.socket.setCreated(); 
    if (this.serverSocket != null)
      this.serverSocket.setCreated(); 
  }
  
  protected void connect(String paramString, int paramInt) throws UnknownHostException, IOException {
    bool = false;
    try {
      inetAddress = InetAddress.getByName(paramString);
      this.port = paramInt;
      this.address = inetAddress;
      connectToAddress(inetAddress, paramInt, this.timeout);
      bool = true;
    } finally {
      if (!bool)
        try {
          close();
        } catch (IOException iOException) {} 
    } 
  }
  
  protected void connect(InetAddress paramInetAddress, int paramInt) throws IOException {
    this.port = paramInt;
    this.address = paramInetAddress;
    try {
      connectToAddress(paramInetAddress, paramInt, this.timeout);
      return;
    } catch (IOException iOException) {
      close();
      throw iOException;
    } 
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    bool = false;
    try {
      if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
        throw new IllegalArgumentException("unsupported address type"); 
      inetSocketAddress = (InetSocketAddress)paramSocketAddress;
      if (inetSocketAddress.isUnresolved())
        throw new UnknownHostException(inetSocketAddress.getHostName()); 
      this.port = inetSocketAddress.getPort();
      this.address = inetSocketAddress.getAddress();
      connectToAddress(this.address, this.port, paramInt);
      bool = true;
    } finally {
      if (!bool)
        try {
          close();
        } catch (IOException iOException) {} 
    } 
  }
  
  private void connectToAddress(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException {
    if (paramInetAddress.isAnyLocalAddress()) {
      doConnect(InetAddress.getLocalHost(), paramInt1, paramInt2);
    } else {
      doConnect(paramInetAddress, paramInt1, paramInt2);
    } 
  }
  
  public void setOption(int paramInt, Object paramObject) throws SocketException {
    int i;
    if (isClosedOrPending())
      throw new SocketException("Socket Closed"); 
    boolean bool = true;
    switch (paramInt) {
      case 128:
        if (paramObject == null || (!(paramObject instanceof Integer) && !(paramObject instanceof Boolean)))
          throw new SocketException("Bad parameter for option"); 
        if (paramObject instanceof Boolean)
          bool = false; 
        break;
      case 4102:
        if (paramObject == null || !(paramObject instanceof Integer))
          throw new SocketException("Bad parameter for SO_TIMEOUT"); 
        i = ((Integer)paramObject).intValue();
        if (i < 0)
          throw new IllegalArgumentException("timeout < 0"); 
        this.timeout = i;
        break;
      case 3:
        if (paramObject == null || !(paramObject instanceof Integer))
          throw new SocketException("bad argument for IP_TOS"); 
        this.trafficClass = ((Integer)paramObject).intValue();
        break;
      case 15:
        throw new SocketException("Cannot re-bind socket");
      case 1:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad parameter for TCP_NODELAY"); 
        bool = ((Boolean)paramObject).booleanValue();
        break;
      case 4097:
      case 4098:
        if (paramObject == null || !(paramObject instanceof Integer) || ((Integer)paramObject).intValue() <= 0)
          throw new SocketException("bad parameter for SO_SNDBUF or SO_RCVBUF"); 
        break;
      case 8:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad parameter for SO_KEEPALIVE"); 
        bool = ((Boolean)paramObject).booleanValue();
        break;
      case 4099:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad parameter for SO_OOBINLINE"); 
        bool = ((Boolean)paramObject).booleanValue();
        break;
      case 4:
        if (paramObject == null || !(paramObject instanceof Boolean))
          throw new SocketException("bad parameter for SO_REUSEADDR"); 
        bool = ((Boolean)paramObject).booleanValue();
        break;
      default:
        throw new SocketException("unrecognized TCP option: " + paramInt);
    } 
    socketSetOption(paramInt, bool, paramObject);
  }
  
  public Object getOption(int paramInt) throws SocketException {
    InetAddressContainer inetAddressContainer;
    if (isClosedOrPending())
      throw new SocketException("Socket Closed"); 
    if (paramInt == 4102)
      return new Integer(this.timeout); 
    int i = 0;
    switch (paramInt) {
      case 1:
        i = socketGetOption(paramInt, null);
        return Boolean.valueOf((i != -1));
      case 4099:
        i = socketGetOption(paramInt, null);
        return Boolean.valueOf((i != -1));
      case 128:
        i = socketGetOption(paramInt, null);
        return (i == -1) ? Boolean.FALSE : new Integer(i);
      case 4:
        i = socketGetOption(paramInt, null);
        return Boolean.valueOf((i != -1));
      case 15:
        inetAddressContainer = new InetAddressContainer();
        i = socketGetOption(paramInt, inetAddressContainer);
        return inetAddressContainer.addr;
      case 4097:
      case 4098:
        i = socketGetOption(paramInt, null);
        return new Integer(i);
      case 3:
        try {
          i = socketGetOption(paramInt, null);
          return (i == -1) ? Integer.valueOf(this.trafficClass) : Integer.valueOf(i);
        } catch (SocketException socketException) {
          return Integer.valueOf(this.trafficClass);
        } 
      case 8:
        i = socketGetOption(paramInt, null);
        return Boolean.valueOf((i != -1));
    } 
    return null;
  }
  
  void doConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.fdLock) {
      if (!this.closePending && (this.socket == null || !this.socket.isBound()))
        NetHooks.beforeTcpConnect(this.fd, paramInetAddress, paramInt1); 
    } 
    try {
      acquireFD();
      try {
        socketConnect(paramInetAddress, paramInt1, paramInt2);
        synchronized (this.fdLock) {
          if (this.closePending)
            throw new SocketException("Socket closed"); 
        } 
        if (this.socket != null) {
          this.socket.setBound();
          this.socket.setConnected();
        } 
      } finally {
        releaseFD();
      } 
    } catch (IOException iOException) {
      close();
      throw iOException;
    } 
  }
  
  protected void bind(InetAddress paramInetAddress, int paramInt) throws IOException {
    synchronized (this.fdLock) {
      if (!this.closePending && (this.socket == null || !this.socket.isBound()))
        NetHooks.beforeTcpBind(this.fd, paramInetAddress, paramInt); 
    } 
    socketBind(paramInetAddress, paramInt);
    if (this.socket != null)
      this.socket.setBound(); 
    if (this.serverSocket != null)
      this.serverSocket.setBound(); 
  }
  
  protected void listen(int paramInt) throws IOException { socketListen(paramInt); }
  
  protected void accept(SocketImpl paramSocketImpl) throws IOException {
    acquireFD();
    try {
      socketAccept(paramSocketImpl);
    } finally {
      releaseFD();
    } 
  }
  
  protected InputStream getInputStream() throws IOException {
    synchronized (this.fdLock) {
      if (isClosedOrPending())
        throw new IOException("Socket Closed"); 
      if (this.shut_rd)
        throw new IOException("Socket input is shutdown"); 
      if (this.socketInputStream == null)
        this.socketInputStream = new SocketInputStream(this); 
    } 
    return this.socketInputStream;
  }
  
  void setInputStream(SocketInputStream paramSocketInputStream) { this.socketInputStream = paramSocketInputStream; }
  
  protected OutputStream getOutputStream() throws IOException {
    synchronized (this.fdLock) {
      if (isClosedOrPending())
        throw new IOException("Socket Closed"); 
      if (this.shut_wr)
        throw new IOException("Socket output is shutdown"); 
      if (this.socketOutputStream == null)
        this.socketOutputStream = new SocketOutputStream(this); 
    } 
    return this.socketOutputStream;
  }
  
  void setFileDescriptor(FileDescriptor paramFileDescriptor) { this.fd = paramFileDescriptor; }
  
  void setAddress(InetAddress paramInetAddress) { this.address = paramInetAddress; }
  
  void setPort(int paramInt) throws IOException { this.port = paramInt; }
  
  void setLocalPort(int paramInt) throws IOException { this.localport = paramInt; }
  
  protected int available() throws IOException {
    if (isClosedOrPending())
      throw new IOException("Stream closed."); 
    if (isConnectionReset() || this.shut_rd)
      return 0; 
    int i = 0;
    try {
      i = socketAvailable();
      if (i == 0 && isConnectionResetPending())
        setConnectionReset(); 
    } catch (ConnectionResetException connectionResetException) {
      setConnectionResetPending();
      try {
        i = socketAvailable();
        if (i == 0)
          setConnectionReset(); 
      } catch (ConnectionResetException connectionResetException1) {}
    } 
    return i;
  }
  
  protected void close() {
    synchronized (this.fdLock) {
      if (this.fd != null) {
        if (!this.stream)
          ResourceManager.afterUdpClose(); 
        if (this.fdUseCount == 0) {
          if (this.closePending)
            return; 
          this.closePending = true;
          try {
            socketPreClose();
          } finally {
            socketClose();
          } 
          this.fd = null;
          return;
        } 
        if (!this.closePending) {
          this.closePending = true;
          this.fdUseCount--;
          socketPreClose();
        } 
      } 
    } 
  }
  
  void reset() {
    if (this.fd != null)
      socketClose(); 
    this.fd = null;
    super.reset();
  }
  
  protected void shutdownInput() {
    if (this.fd != null) {
      socketShutdown(0);
      if (this.socketInputStream != null)
        this.socketInputStream.setEOF(true); 
      this.shut_rd = true;
    } 
  }
  
  protected void shutdownOutput() {
    if (this.fd != null) {
      socketShutdown(1);
      this.shut_wr = true;
    } 
  }
  
  protected boolean supportsUrgentData() { return true; }
  
  protected void sendUrgentData(int paramInt) throws IOException {
    if (this.fd == null)
      throw new IOException("Socket Closed"); 
    socketSendUrgentData(paramInt);
  }
  
  protected void finalize() { close(); }
  
  FileDescriptor acquireFD() {
    synchronized (this.fdLock) {
      this.fdUseCount++;
      return this.fd;
    } 
  }
  
  void releaseFD() {
    synchronized (this.fdLock) {
      this.fdUseCount--;
      if (this.fdUseCount == -1 && this.fd != null)
        try {
          socketClose();
        } catch (IOException iOException) {
        
        } finally {
          this.fd = null;
        }  
    } 
  }
  
  public boolean isConnectionReset() {
    synchronized (this.resetLock) {
      return (this.resetState == this.CONNECTION_RESET);
    } 
  }
  
  public boolean isConnectionResetPending() {
    synchronized (this.resetLock) {
      return (this.resetState == this.CONNECTION_RESET_PENDING);
    } 
  }
  
  public void setConnectionReset() {
    synchronized (this.resetLock) {
      this.resetState = this.CONNECTION_RESET;
    } 
  }
  
  public void setConnectionResetPending() {
    synchronized (this.resetLock) {
      if (this.resetState == this.CONNECTION_NOT_RESET)
        this.resetState = this.CONNECTION_RESET_PENDING; 
    } 
  }
  
  public boolean isClosedOrPending() {
    synchronized (this.fdLock) {
      if (this.closePending || this.fd == null)
        return true; 
      return false;
    } 
  }
  
  public int getTimeout() throws IOException { return this.timeout; }
  
  private void socketPreClose() { socketClose0(true); }
  
  protected void socketClose() { socketClose0(false); }
  
  abstract void socketCreate(boolean paramBoolean) throws IOException;
  
  abstract void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2) throws IOException;
  
  abstract void socketBind(InetAddress paramInetAddress, int paramInt) throws IOException;
  
  abstract void socketListen(int paramInt) throws IOException;
  
  abstract void socketAccept(SocketImpl paramSocketImpl) throws IOException;
  
  abstract int socketAvailable() throws IOException;
  
  abstract void socketClose0(boolean paramBoolean) throws IOException;
  
  abstract void socketShutdown(int paramInt) throws IOException;
  
  abstract void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject) throws SocketException;
  
  abstract int socketGetOption(int paramInt, Object paramObject) throws SocketException;
  
  abstract void socketSendUrgentData(int paramInt) throws IOException;
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\AbstractPlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */