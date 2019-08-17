package java.net;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class ServerSocket implements Closeable {
  private boolean created = false;
  
  private boolean bound = false;
  
  private boolean closed = false;
  
  private Object closeLock = new Object();
  
  private SocketImpl impl;
  
  private boolean oldImpl = false;
  
  private static SocketImplFactory factory = null;
  
  ServerSocket(SocketImpl paramSocketImpl) {
    this.impl = paramSocketImpl;
    paramSocketImpl.setServerSocket(this);
  }
  
  public ServerSocket() throws IOException { setImpl(); }
  
  public ServerSocket(int paramInt) throws IOException { this(paramInt, 50, null); }
  
  public ServerSocket(int paramInt1, int paramInt2) throws IOException { this(paramInt1, paramInt2, null); }
  
  public ServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress) throws IOException {
    setImpl();
    if (paramInt1 < 0 || paramInt1 > 65535)
      throw new IllegalArgumentException("Port value out of range: " + paramInt1); 
    if (paramInt2 < 1)
      paramInt2 = 50; 
    try {
      bind(new InetSocketAddress(paramInetAddress, paramInt1), paramInt2);
    } catch (SecurityException securityException) {
      close();
      throw securityException;
    } catch (IOException iOException) {
      close();
      throw iOException;
    } 
  }
  
  SocketImpl getImpl() throws SocketException {
    if (!this.created)
      createImpl(); 
    return this.impl;
  }
  
  private void checkOldImpl() throws IOException {
    if (this.impl == null)
      return; 
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws NoSuchMethodException {
              ServerSocket.this.impl.getClass().getDeclaredMethod("connect", new Class[] { SocketAddress.class, int.class });
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      this.oldImpl = true;
    } 
  }
  
  private void setImpl() throws IOException {
    if (factory != null) {
      this.impl = factory.createSocketImpl();
      checkOldImpl();
    } else {
      this.impl = new SocksSocketImpl();
    } 
    if (this.impl != null)
      this.impl.setServerSocket(this); 
  }
  
  void createImpl() throws IOException {
    if (this.impl == null)
      setImpl(); 
    try {
      this.impl.create(true);
      this.created = true;
    } catch (IOException iOException) {
      throw new SocketException(iOException.getMessage());
    } 
  }
  
  public void bind(SocketAddress paramSocketAddress) throws IOException { bind(paramSocketAddress, 50); }
  
  public void bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!this.oldImpl && isBound())
      throw new SocketException("Already bound"); 
    if (paramSocketAddress == null)
      paramSocketAddress = new InetSocketAddress(0); 
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress.isUnresolved())
      throw new SocketException("Unresolved address"); 
    if (paramInt < 1)
      paramInt = 50; 
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkListen(inetSocketAddress.getPort()); 
      getImpl().bind(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
      getImpl().listen(paramInt);
      this.bound = true;
    } catch (SecurityException securityException) {
      this.bound = false;
      throw securityException;
    } catch (IOException iOException) {
      this.bound = false;
      throw iOException;
    } 
  }
  
  public InetAddress getInetAddress() {
    if (!isBound())
      return null; 
    try {
      InetAddress inetAddress = getImpl().getInetAddress();
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkConnect(inetAddress.getHostAddress(), -1); 
      return inetAddress;
    } catch (SecurityException securityException) {
      return InetAddress.getLoopbackAddress();
    } catch (SocketException socketException) {
      return null;
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
  
  public SocketAddress getLocalSocketAddress() { return !isBound() ? null : new InetSocketAddress(getInetAddress(), getLocalPort()); }
  
  public Socket accept() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!isBound())
      throw new SocketException("Socket is not bound yet"); 
    Socket socket = new Socket((SocketImpl)null);
    implAccept(socket);
    return socket;
  }
  
  protected final void implAccept(Socket paramSocket) throws IOException {
    SocketImpl socketImpl = null;
    try {
      if (paramSocket.impl == null) {
        paramSocket.setImpl();
      } else {
        paramSocket.impl.reset();
      } 
      socketImpl = paramSocket.impl;
      paramSocket.impl = null;
      socketImpl.address = new InetAddress();
      socketImpl.fd = new FileDescriptor();
      getImpl().accept(socketImpl);
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkAccept(socketImpl.getInetAddress().getHostAddress(), socketImpl.getPort()); 
    } catch (IOException iOException) {
      if (socketImpl != null)
        socketImpl.reset(); 
      paramSocket.impl = socketImpl;
      throw iOException;
    } catch (SecurityException securityException) {
      if (socketImpl != null)
        socketImpl.reset(); 
      paramSocket.impl = socketImpl;
      throw securityException;
    } 
    paramSocket.impl = socketImpl;
    paramSocket.postAccept();
  }
  
  public void close() throws IOException {
    synchronized (this.closeLock) {
      if (isClosed())
        return; 
      if (this.created)
        this.impl.close(); 
      this.closed = true;
    } 
  }
  
  public ServerSocketChannel getChannel() { return null; }
  
  public boolean isBound() { return (this.bound || this.oldImpl); }
  
  public boolean isClosed() {
    synchronized (this.closeLock) {
      return this.closed;
    } 
  }
  
  public void setSoTimeout(int paramInt) throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4102, new Integer(paramInt));
  }
  
  public int getSoTimeout() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    Object object = getImpl().getOption(4102);
    return (object instanceof Integer) ? ((Integer)object).intValue() : 0;
  }
  
  public void setReuseAddress(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getReuseAddress() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Boolean)getImpl().getOption(4)).booleanValue();
  }
  
  public String toString() {
    InetAddress inetAddress;
    if (!isBound())
      return "ServerSocket[unbound]"; 
    if (System.getSecurityManager() != null) {
      inetAddress = InetAddress.getLoopbackAddress();
    } else {
      inetAddress = this.impl.getInetAddress();
    } 
    return "ServerSocket[addr=" + inetAddress + ",localport=" + this.impl.getLocalPort() + "]";
  }
  
  void setBound() throws IOException { this.bound = true; }
  
  void setCreated() throws IOException { this.created = true; }
  
  public static void setSocketFactory(SocketImplFactory paramSocketImplFactory) throws IOException {
    if (factory != null)
      throw new SocketException("factory already defined"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    factory = paramSocketImplFactory;
  }
  
  public void setReceiveBufferSize(int paramInt) throws IOException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("negative receive size"); 
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
  
  public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\ServerSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */