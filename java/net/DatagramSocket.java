package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class DatagramSocket implements Closeable {
  private boolean created = false;
  
  private boolean bound = false;
  
  private boolean closed = false;
  
  private Object closeLock = new Object();
  
  DatagramSocketImpl impl;
  
  boolean oldImpl = false;
  
  private boolean explicitFilter = false;
  
  private int bytesLeftToFilter;
  
  static final int ST_NOT_CONNECTED = 0;
  
  static final int ST_CONNECTED = 1;
  
  static final int ST_CONNECTED_NO_IMPL = 2;
  
  int connectState = 0;
  
  InetAddress connectedAddress = null;
  
  int connectedPort = -1;
  
  static Class<?> implClass = null;
  
  static DatagramSocketImplFactory factory;
  
  private void connectInternal(InetAddress paramInetAddress, int paramInt) throws SocketException {
    if (paramInt < 0 || paramInt > 65535)
      throw new IllegalArgumentException("connect: " + paramInt); 
    if (paramInetAddress == null)
      throw new IllegalArgumentException("connect: null address"); 
    checkAddress(paramInetAddress, "connect");
    if (isClosed())
      return; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      if (paramInetAddress.isMulticastAddress()) {
        securityManager.checkMulticast(paramInetAddress);
      } else {
        securityManager.checkConnect(paramInetAddress.getHostAddress(), paramInt);
        securityManager.checkAccept(paramInetAddress.getHostAddress(), paramInt);
      }  
    if (!isBound())
      bind(new InetSocketAddress(0)); 
    if (this.oldImpl || (this.impl instanceof AbstractPlainDatagramSocketImpl && ((AbstractPlainDatagramSocketImpl)this.impl).nativeConnectDisabled())) {
      this.connectState = 2;
    } else {
      try {
        getImpl().connect(paramInetAddress, paramInt);
        this.connectState = 1;
        int i = getImpl().dataAvailable();
        if (i == -1)
          throw new SocketException(); 
        this.explicitFilter = (i > 0);
        if (this.explicitFilter)
          this.bytesLeftToFilter = getReceiveBufferSize(); 
      } catch (SocketException socketException) {
        this.connectState = 2;
      } 
    } 
    this.connectedAddress = paramInetAddress;
    this.connectedPort = paramInt;
  }
  
  public DatagramSocket() throws SocketException { this(new InetSocketAddress(0)); }
  
  protected DatagramSocket(DatagramSocketImpl paramDatagramSocketImpl) {
    if (paramDatagramSocketImpl == null)
      throw new NullPointerException(); 
    this.impl = paramDatagramSocketImpl;
    checkOldImpl();
  }
  
  public DatagramSocket(SocketAddress paramSocketAddress) throws SocketException {
    createImpl();
    if (paramSocketAddress != null)
      try {
        bind(paramSocketAddress);
      } finally {
        if (!isBound())
          close(); 
      }  
  }
  
  public DatagramSocket(int paramInt) throws SocketException { this(paramInt, null); }
  
  public DatagramSocket(int paramInt, InetAddress paramInetAddress) throws SocketException { this(new InetSocketAddress(paramInetAddress, paramInt)); }
  
  private void checkOldImpl() throws SocketException {
    if (this.impl == null)
      return; 
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws NoSuchMethodException {
              Class[] arrayOfClass = new Class[1];
              arrayOfClass[0] = DatagramPacket.class;
              DatagramSocket.this.impl.getClass().getDeclaredMethod("peekData", arrayOfClass);
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      this.oldImpl = true;
    } 
  }
  
  void createImpl() throws SocketException {
    if (this.impl == null)
      if (factory != null) {
        this.impl = factory.createDatagramSocketImpl();
        checkOldImpl();
      } else {
        boolean bool = (this instanceof MulticastSocket);
        this.impl = DefaultDatagramSocketImplFactory.createDatagramSocketImpl(bool);
        checkOldImpl();
      }  
    this.impl.create();
    this.impl.setDatagramSocket(this);
    this.created = true;
  }
  
  DatagramSocketImpl getImpl() throws SocketException {
    if (!this.created)
      createImpl(); 
    return this.impl;
  }
  
  public void bind(SocketAddress paramSocketAddress) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (isBound())
      throw new SocketException("already bound"); 
    if (paramSocketAddress == null)
      paramSocketAddress = new InetSocketAddress(0); 
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type!"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress.isUnresolved())
      throw new SocketException("Unresolved address"); 
    InetAddress inetAddress = inetSocketAddress.getAddress();
    int i = inetSocketAddress.getPort();
    checkAddress(inetAddress, "bind");
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkListen(i); 
    try {
      getImpl().bind(i, inetAddress);
    } catch (SocketException socketException) {
      getImpl().close();
      throw socketException;
    } 
    this.bound = true;
  }
  
  void checkAddress(InetAddress paramInetAddress, String paramString) {
    if (paramInetAddress == null)
      return; 
    if (!(paramInetAddress instanceof Inet4Address) && !(paramInetAddress instanceof Inet6Address))
      throw new IllegalArgumentException(paramString + ": invalid address type"); 
  }
  
  public void connect(InetAddress paramInetAddress, int paramInt) throws SocketException {
    try {
      connectInternal(paramInetAddress, paramInt);
    } catch (SocketException socketException) {
      throw new Error("connect failed", socketException);
    } 
  }
  
  public void connect(SocketAddress paramSocketAddress) throws SocketException {
    if (paramSocketAddress == null)
      throw new IllegalArgumentException("Address can't be null"); 
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("Unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress.isUnresolved())
      throw new SocketException("Unresolved address"); 
    connectInternal(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
  }
  
  public void disconnect() throws SocketException {
    synchronized (this) {
      if (isClosed())
        return; 
      if (this.connectState == 1)
        this.impl.disconnect(); 
      this.connectedAddress = null;
      this.connectedPort = -1;
      this.connectState = 0;
      this.explicitFilter = false;
    } 
  }
  
  public boolean isBound() { return this.bound; }
  
  public boolean isConnected() { return (this.connectState != 0); }
  
  public InetAddress getInetAddress() { return this.connectedAddress; }
  
  public int getPort() { return this.connectedPort; }
  
  public SocketAddress getRemoteSocketAddress() { return !isConnected() ? null : new InetSocketAddress(getInetAddress(), getPort()); }
  
  public SocketAddress getLocalSocketAddress() { return isClosed() ? null : (!isBound() ? null : new InetSocketAddress(getLocalAddress(), getLocalPort())); }
  
  public void send(DatagramPacket paramDatagramPacket) throws IOException {
    InetAddress inetAddress = null;
    synchronized (paramDatagramPacket) {
      if (isClosed())
        throw new SocketException("Socket is closed"); 
      checkAddress(paramDatagramPacket.getAddress(), "send");
      if (this.connectState == 0) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          if (paramDatagramPacket.getAddress().isMulticastAddress()) {
            securityManager.checkMulticast(paramDatagramPacket.getAddress());
          } else {
            securityManager.checkConnect(paramDatagramPacket.getAddress().getHostAddress(), paramDatagramPacket.getPort());
          }  
      } else {
        inetAddress = paramDatagramPacket.getAddress();
        if (inetAddress == null) {
          paramDatagramPacket.setAddress(this.connectedAddress);
          paramDatagramPacket.setPort(this.connectedPort);
        } else if (!inetAddress.equals(this.connectedAddress) || paramDatagramPacket.getPort() != this.connectedPort) {
          throw new IllegalArgumentException("connected address and packet address differ");
        } 
      } 
      if (!isBound())
        bind(new InetSocketAddress(0)); 
      getImpl().send(paramDatagramPacket);
    } 
  }
  
  public void receive(DatagramPacket paramDatagramPacket) throws IOException {
    synchronized (paramDatagramPacket) {
      if (!isBound())
        bind(new InetSocketAddress(0)); 
      if (this.connectState == 0) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          while (true) {
            String str = null;
            int i = 0;
            if (!this.oldImpl) {
              DatagramPacket datagramPacket1 = new DatagramPacket(new byte[1], 1);
              i = getImpl().peekData(datagramPacket1);
              str = datagramPacket1.getAddress().getHostAddress();
            } else {
              InetAddress inetAddress = new InetAddress();
              i = getImpl().peek(inetAddress);
              str = inetAddress.getHostAddress();
            } 
            try {
              securityManager.checkAccept(str, i);
              break;
            } catch (SecurityException securityException) {
              DatagramPacket datagramPacket1 = new DatagramPacket(new byte[1], 1);
              getImpl().receive(datagramPacket1);
            } 
          }  
      } 
      DatagramPacket datagramPacket = null;
      if (this.connectState == 2 || this.explicitFilter) {
        boolean bool;
        for (bool = false; !bool; bool = true) {
          InetAddress inetAddress = null;
          int i = -1;
          if (!this.oldImpl) {
            DatagramPacket datagramPacket1 = new DatagramPacket(new byte[1], 1);
            i = getImpl().peekData(datagramPacket1);
            inetAddress = datagramPacket1.getAddress();
          } else {
            inetAddress = new InetAddress();
            i = getImpl().peek(inetAddress);
          } 
          if (!this.connectedAddress.equals(inetAddress) || this.connectedPort != i) {
            datagramPacket = new DatagramPacket(new byte[1024], 1024);
            getImpl().receive(datagramPacket);
            if (this.explicitFilter && checkFiltering(datagramPacket))
              bool = true; 
            continue;
          } 
        } 
      } 
      getImpl().receive(paramDatagramPacket);
      if (this.explicitFilter && datagramPacket == null)
        checkFiltering(paramDatagramPacket); 
    } 
  }
  
  private boolean checkFiltering(DatagramPacket paramDatagramPacket) throws SocketException {
    this.bytesLeftToFilter -= paramDatagramPacket.getLength();
    if (this.bytesLeftToFilter <= 0 || getImpl().dataAvailable() <= 0) {
      this.explicitFilter = false;
      return true;
    } 
    return false;
  }
  
  public InetAddress getLocalAddress() {
    if (isClosed())
      return null; 
    InetAddress inetAddress = null;
    try {
      inetAddress = (InetAddress)getImpl().getOption(15);
      if (inetAddress.isAnyLocalAddress())
        inetAddress = InetAddress.anyLocalAddress(); 
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkConnect(inetAddress.getHostAddress(), -1); 
    } catch (Exception exception) {
      inetAddress = InetAddress.anyLocalAddress();
    } 
    return inetAddress;
  }
  
  public int getLocalPort() {
    if (isClosed())
      return -1; 
    try {
      return getImpl().getLocalPort();
    } catch (Exception exception) {
      return 0;
    } 
  }
  
  public void setSoTimeout(int paramInt) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(4102, new Integer(paramInt));
  }
  
  public int getSoTimeout() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (getImpl() == null)
      return 0; 
    Object object = getImpl().getOption(4102);
    return (object instanceof Integer) ? ((Integer)object).intValue() : 0;
  }
  
  public void setSendBufferSize(int paramInt) throws SocketException {
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
  
  public void setReceiveBufferSize(int paramInt) throws SocketException {
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
  
  public void setReuseAddress(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (this.oldImpl) {
      getImpl().setOption(4, new Integer(paramBoolean ? -1 : 0));
    } else {
      getImpl().setOption(4, Boolean.valueOf(paramBoolean));
    } 
  }
  
  public boolean getReuseAddress() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    Object object = getImpl().getOption(4);
    return ((Boolean)object).booleanValue();
  }
  
  public void setBroadcast(boolean paramBoolean) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    getImpl().setOption(32, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getBroadcast() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Boolean)getImpl().getOption(32)).booleanValue();
  }
  
  public void setTrafficClass(int paramInt) throws SocketException {
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
  
  public int getTrafficClass() {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    return ((Integer)getImpl().getOption(3)).intValue();
  }
  
  public void close() throws SocketException {
    synchronized (this.closeLock) {
      if (isClosed())
        return; 
      this.impl.close();
      this.closed = true;
    } 
  }
  
  public boolean isClosed() {
    synchronized (this.closeLock) {
      return this.closed;
    } 
  }
  
  public DatagramChannel getChannel() { return null; }
  
  public static void setDatagramSocketImplFactory(DatagramSocketImplFactory paramDatagramSocketImplFactory) throws IOException {
    if (factory != null)
      throw new SocketException("factory already defined"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    factory = paramDatagramSocketImplFactory;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\DatagramSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */