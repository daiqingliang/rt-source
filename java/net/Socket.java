package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.net.ApplicationProxy;

public class Socket implements Closeable {
  private boolean created = false;
  
  private boolean bound = false;
  
  private boolean connected = false;
  
  private boolean closed = false;
  
  private Object closeLock = new Object();
  
  private boolean shutIn = false;
  
  private boolean shutOut = false;
  
  SocketImpl impl;
  
  private boolean oldImpl = false;
  
  private static SocketImplFactory factory = null;
  
  public Socket() { setImpl(); }
  
  public Socket(Proxy paramProxy) {
    if (paramProxy == null)
      throw new IllegalArgumentException("Invalid Proxy"); 
    Proxy proxy = (paramProxy == Proxy.NO_PROXY) ? Proxy.NO_PROXY : ApplicationProxy.create(paramProxy);
    Proxy.Type type = proxy.type();
    if (type == Proxy.Type.SOCKS || type == Proxy.Type.HTTP) {
      SecurityManager securityManager = System.getSecurityManager();
      InetSocketAddress inetSocketAddress = (InetSocketAddress)proxy.address();
      if (inetSocketAddress.getAddress() != null)
        checkAddress(inetSocketAddress.getAddress(), "Socket"); 
      if (securityManager != null) {
        if (inetSocketAddress.isUnresolved())
          inetSocketAddress = new InetSocketAddress(inetSocketAddress.getHostName(), inetSocketAddress.getPort()); 
        if (inetSocketAddress.isUnresolved()) {
          securityManager.checkConnect(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } else {
          securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        } 
      } 
      this.impl = (type == Proxy.Type.SOCKS) ? new SocksSocketImpl(proxy) : new HttpConnectSocketImpl(proxy);
      this.impl.setSocket(this);
    } else if (proxy == Proxy.NO_PROXY) {
      if (factory == null) {
        this.impl = new PlainSocketImpl();
        this.impl.setSocket(this);
      } else {
        setImpl();
      } 
    } else {
      throw new IllegalArgumentException("Invalid Proxy");
    } 
  }
  
  protected Socket(SocketImpl paramSocketImpl) throws SocketException {
    this.impl = paramSocketImpl;
    if (paramSocketImpl != null) {
      checkOldImpl();
      this.impl.setSocket(this);
    } 
  }
  
  public Socket(String paramString, int paramInt) throws UnknownHostException, IOException { this((paramString != null) ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt), (SocketAddress)null, true); }
  
  public Socket(InetAddress paramInetAddress, int paramInt) throws IOException { this((paramInetAddress != null) ? new InetSocketAddress(paramInetAddress, paramInt) : null, (SocketAddress)null, true); }
  
  public Socket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException { this((paramString != null) ? new InetSocketAddress(paramString, paramInt1) : new InetSocketAddress(InetAddress.getByName(null), paramInt1), new InetSocketAddress(paramInetAddress, paramInt2), true); }
  
  public Socket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2) throws IOException { this((paramInetAddress1 != null) ? new InetSocketAddress(paramInetAddress1, paramInt1) : null, new InetSocketAddress(paramInetAddress2, paramInt2), true); }
  
  @Deprecated
  public Socket(String paramString, int paramInt, boolean paramBoolean) throws IOException { this((paramString != null) ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt), (SocketAddress)null, paramBoolean); }
  
  @Deprecated
  public Socket(InetAddress paramInetAddress, int paramInt, boolean paramBoolean) throws IOException { this((paramInetAddress != null) ? new InetSocketAddress(paramInetAddress, paramInt) : null, new InetSocketAddress(0), paramBoolean); }
  
  private Socket(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2, boolean paramBoolean) throws IOException {
    setImpl();
    if (paramSocketAddress1 == null)
      throw new NullPointerException(); 
    try {
      createImpl(paramBoolean);
      if (paramSocketAddress2 != null)
        bind(paramSocketAddress2); 
      connect(paramSocketAddress1);
    } catch (IOException|IllegalArgumentException|SecurityException iOException) {
      try {
        close();
      } catch (IOException iOException1) {
        iOException.addSuppressed(iOException1);
      } 
      throw iOException;
    } 
  }
  
  void createImpl(boolean paramBoolean) throws SocketException {
    if (this.impl == null)
      setImpl(); 
    try {
      this.impl.create(paramBoolean);
      this.created = true;
    } catch (IOException iOException) {
      throw new SocketException(iOException.getMessage());
    } 
  }
  
  private void checkOldImpl() {
    if (this.impl == null)
      return; 
    this.oldImpl = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            Class clazz = Socket.this.impl.getClass();
            while (true) {
              try {
                clazz.getDeclaredMethod("connect", new Class[] { SocketAddress.class, int.class });
                return Boolean.FALSE;
              } catch (NoSuchMethodException noSuchMethodException) {
                clazz = clazz.getSuperclass();
                if (clazz.equals(SocketImpl.class))
                  break; 
              } 
            } 
            return Boolean.TRUE;
          }
        })).booleanValue();
  }
  
  void setImpl() {
    if (factory != null) {
      this.impl = factory.createSocketImpl();
      checkOldImpl();
    } else {
      this.impl = new SocksSocketImpl();
    } 
    if (this.impl != null)
      this.impl.setSocket(this); 
  }
  
  SocketImpl getImpl() throws SocketException {
    if (!this.created)
      createImpl(true); 
    return this.impl;
  }
  
  public void connect(SocketAddress paramSocketAddress) throws IOException { connect(paramSocketAddress, 0); }
  
  public void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    if (paramSocketAddress == null)
      throw new IllegalArgumentException("connect: The address can't be null"); 
    if (paramInt < 0)
      throw new IllegalArgumentException("connect: timeout can't be negative"); 
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!this.oldImpl && isConnected())
      throw new SocketException("already connected"); 
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    InetAddress inetAddress = inetSocketAddress.getAddress();
    int i = inetSocketAddress.getPort();
    checkAddress(inetAddress, "connect");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      if (inetSocketAddress.isUnresolved()) {
        securityManager.checkConnect(inetSocketAddress.getHostName(), i);
      } else {
        securityManager.checkConnect(inetAddress.getHostAddress(), i);
      }  
    if (!this.created)
      createImpl(true); 
    if (!this.oldImpl) {
      this.impl.connect(inetSocketAddress, paramInt);
    } else if (paramInt == 0) {
      if (inetSocketAddress.isUnresolved()) {
        this.impl.connect(inetAddress.getHostName(), i);
      } else {
        this.impl.connect(inetAddress, i);
      } 
    } else {
      throw new UnsupportedOperationException("SocketImpl.connect(addr, timeout)");
    } 
    this.connected = true;
    this.bound = true;
  }
  
  public void bind(SocketAddress paramSocketAddress) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!this.oldImpl && isBound())
      throw new SocketException("Already bound"); 
    if (paramSocketAddress != null && !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress != null && inetSocketAddress.isUnresolved())
      throw new SocketException("Unresolved address"); 
    if (inetSocketAddress == null)
      inetSocketAddress = new InetSocketAddress(0); 
    InetAddress inetAddress = inetSocketAddress.getAddress();
    int i = inetSocketAddress.getPort();
    checkAddress(inetAddress, "bind");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkListen(i); 
    getImpl().bind(inetAddress, i);
    this.bound = true;
  }
  
  private void checkAddress(InetAddress paramInetAddress, String paramString) {
    if (paramInetAddress == null)
      return; 
    if (!(paramInetAddress instanceof Inet4Address) && !(paramInetAddress instanceof Inet6Address))
      throw new IllegalArgumentException(paramString + ": invalid address type"); 
  }
  
  final void postAccept() {
    this.connected = true;
    this.created = true;
    this.bound = true;
  }
  
  void setCreated() { this.created = true; }
  
  void setBound() { this.bound = true; }
  
  void setConnected() { this.connected = true; }
  
  public InetAddress getInetAddress() {
    if (!isConnected())
      return null; 
    try {
      return getImpl().getInetAddress();
    } catch (SocketException socketException) {
      return null;
    } 
  }
  
  public InetAddress getLocalAddress() {
    if (!isBound())
      return InetAddress.anyLocalAddress(); 
    InetAddress inetAddress = null;
    try {
      inetAddress = (InetAddress)getImpl().getOption(15);
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkConnect(inetAddress.getHostAddress(), -1); 
      if (inetAddress.isAnyLocalAddress())
        inetAddress = InetAddress.anyLocalAddress(); 
    } catch (SecurityException securityException) {
      inetAddress = InetAddress.getLoopbackAddress();
    } catch (Exception exception) {
      inetAddress = InetAddress.anyLocalAddress();
    } 
    return inetAddress;
  }
  
  public int getPort() {
    if (!isConnected())
      return 0; 
    try {
      return getImpl().getPort();
    } catch (SocketException socketException) {
      return -1;
    } 
  }
  
  public int getLocalPort() {
    if (!isBound())
      return -1; 
    try {
      return getImpl().getLocalPort();
    } catch (SocketException socketException) {
      return -1;
    } 
  }
  
  public SocketAddress getRemoteSocketAddress() { return !isConnected() ? null : new InetSocketAddress(getInetAddress(), getPort()); }
  
  public SocketAddress getLocalSocketAddress() { return !isBound() ? null : new InetSocketAddress(getLocalAddress(), getLocalPort()); }
  
  public SocketChannel getChannel() { return null; }
  
  public InputStream getInputStream() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!isConnected())
      throw new SocketException("Socket is not connected"); 
    if (isInputShutdown())
      throw new SocketException("Socket input is shutdown"); 
    Socket socket = this;
    InputStream inputStream = null;
    try {
      inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
            public InputStream run() throws IOException { return Socket.this.impl.getInputStream(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
    return inputStream;
  }
  
  public OutputStream getOutputStream() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!isConnected())
      throw new SocketException("Socket is not connected"); 
    if (isOutputShutdown())
      throw new SocketException("Socket output is shutdown"); 
    Socket socket = this;
    OutputStream outputStream = null;
    try {
      outputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
            public OutputStream run() throws IOException { return Socket.this.impl.getOutputStream(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
    return outputStream;
  }
  
  public void setTcpNoDelay(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(1, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getTcpNoDelay() throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Boolean)getImpl().getOption(1)).booleanValue();
  }
  
  public void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!paramBoolean) {
      getImpl().setOption(128, new Boolean(paramBoolean));
    } else {
      if (paramInt < 0)
        throw new IllegalArgumentException("invalid value for SO_LINGER"); 
      if (paramInt > 65535)
        paramInt = 65535; 
      getImpl().setOption(128, new Integer(paramInt));
    } 
  }
  
  public int getSoLinger() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    Object object = getImpl().getOption(128);
    return (object instanceof Integer) ? ((Integer)object).intValue() : -1;
  }
  
  public void sendUrgentData(int paramInt) throws IOException {
    if (!getImpl().supportsUrgentData())
      throw new SocketException("Urgent data not supported"); 
    getImpl().sendUrgentData(paramInt);
  }
  
  public void setOOBInline(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4099, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getOOBInline() throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Boolean)getImpl().getOption(4099)).booleanValue();
  }
  
  public void setSoTimeout(int paramInt) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (paramInt < 0)
      throw new IllegalArgumentException("timeout can't be negative"); 
    getImpl().setOption(4102, new Integer(paramInt));
  }
  
  public int getSoTimeout() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    Object object = getImpl().getOption(4102);
    return (object instanceof Integer) ? ((Integer)object).intValue() : 0;
  }
  
  public void setSendBufferSize(int paramInt) throws IOException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("negative send size"); 
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4097, new Integer(paramInt));
  }
  
  public int getSendBufferSize() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    int i = 0;
    Object object = getImpl().getOption(4097);
    if (object instanceof Integer)
      i = ((Integer)object).intValue(); 
    return i;
  }
  
  public void setReceiveBufferSize(int paramInt) throws IOException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("invalid receive size"); 
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4098, new Integer(paramInt));
  }
  
  public int getReceiveBufferSize() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    int i = 0;
    Object object = getImpl().getOption(4098);
    if (object instanceof Integer)
      i = ((Integer)object).intValue(); 
    return i;
  }
  
  public void setKeepAlive(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(8, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getKeepAlive() throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Boolean)getImpl().getOption(8)).booleanValue();
  }
  
  public void setTrafficClass(int paramInt) throws IOException {
    if (paramInt < 0 || paramInt > 255)
      throw new IllegalArgumentException("tc is not in range 0 -- 255"); 
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    try {
      getImpl().setOption(3, Integer.valueOf(paramInt));
    } catch (SocketException socketException) {
      if (!isConnected())
        throw socketException; 
    } 
  }
  
  public int getTrafficClass() { return ((Integer)getImpl().getOption(3)).intValue(); }
  
  public void setReuseAddress(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getReuseAddress() throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Boolean)getImpl().getOption(4)).booleanValue();
  }
  
  public void close() {
    synchronized (this.closeLock) {
      if (isClosed())
        return; 
      if (this.created)
        this.impl.close(); 
      this.closed = true;
    } 
  }
  
  public void shutdownInput() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!isConnected())
      throw new SocketException("Socket is not connected"); 
    if (isInputShutdown())
      throw new SocketException("Socket input is already shutdown"); 
    getImpl().shutdownInput();
    this.shutIn = true;
  }
  
  public void shutdownOutput() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!isConnected())
      throw new SocketException("Socket is not connected"); 
    if (isOutputShutdown())
      throw new SocketException("Socket output is already shutdown"); 
    getImpl().shutdownOutput();
    this.shutOut = true;
  }
  
  public String toString() {
    try {
      if (isConnected())
        return "Socket[addr=" + getImpl().getInetAddress() + ",port=" + getImpl().getPort() + ",localport=" + getImpl().getLocalPort() + "]"; 
    } catch (SocketException socketException) {}
    return "Socket[unconnected]";
  }
  
  public boolean isConnected() throws SocketException { return (this.connected || this.oldImpl); }
  
  public boolean isBound() throws SocketException { return (this.bound || this.oldImpl); }
  
  public boolean isClosed() throws SocketException {
    synchronized (this.closeLock) {
      return this.closed;
    } 
  }
  
  public boolean isInputShutdown() throws SocketException { return this.shutIn; }
  
  public boolean isOutputShutdown() throws SocketException { return this.shutOut; }
  
  public static void setSocketImplFactory(SocketImplFactory paramSocketImplFactory) throws IOException {
    if (factory != null)
      throw new SocketException("factory already defined"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    factory = paramSocketImplFactory;
  }
  
  public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\Socket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */