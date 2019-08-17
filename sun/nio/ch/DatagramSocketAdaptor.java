package sun.nio.ch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;

public class DatagramSocketAdaptor extends DatagramSocket {
  private final DatagramChannelImpl dc;
  
  private static final DatagramSocketImpl dummyDatagramSocket = new DatagramSocketImpl() {
      protected void create() {}
      
      protected void bind(int param1Int, InetAddress param1InetAddress) throws SocketException {}
      
      protected void send(DatagramPacket param1DatagramPacket) throws IOException {}
      
      protected int peek(InetAddress param1InetAddress) throws IOException { return 0; }
      
      protected int peekData(DatagramPacket param1DatagramPacket) throws IOException { return 0; }
      
      protected void receive(DatagramPacket param1DatagramPacket) throws IOException {}
      
      @Deprecated
      protected void setTTL(byte param1Byte) throws IOException {}
      
      @Deprecated
      protected byte getTTL() throws IOException { return 0; }
      
      protected void setTimeToLive(int param1Int) throws SocketException {}
      
      protected int getTimeToLive() { return 0; }
      
      protected void join(InetAddress param1InetAddress) throws IOException {}
      
      protected void leave(InetAddress param1InetAddress) throws IOException {}
      
      protected void joinGroup(SocketAddress param1SocketAddress, NetworkInterface param1NetworkInterface) throws IOException {}
      
      protected void leaveGroup(SocketAddress param1SocketAddress, NetworkInterface param1NetworkInterface) throws IOException {}
      
      protected void close() {}
      
      public Object getOption(int param1Int) throws SocketException { return null; }
      
      public void setOption(int param1Int, Object param1Object) throws SocketException {}
    };
  
  private DatagramSocketAdaptor(DatagramChannelImpl paramDatagramChannelImpl) throws IOException {
    super(dummyDatagramSocket);
    this.dc = paramDatagramChannelImpl;
  }
  
  public static DatagramSocket create(DatagramChannelImpl paramDatagramChannelImpl) {
    try {
      return new DatagramSocketAdaptor(paramDatagramChannelImpl);
    } catch (IOException iOException) {
      throw new Error(iOException);
    } 
  }
  
  private void connectInternal(SocketAddress paramSocketAddress) throws SocketException {
    InetSocketAddress inetSocketAddress = Net.asInetSocketAddress(paramSocketAddress);
    int i = inetSocketAddress.getPort();
    if (i < 0 || i > 65535)
      throw new IllegalArgumentException("connect: " + i); 
    if (paramSocketAddress == null)
      throw new IllegalArgumentException("connect: null address"); 
    if (isClosed())
      return; 
    try {
      this.dc.connect(paramSocketAddress);
    } catch (Exception exception) {
      Net.translateToSocketException(exception);
    } 
  }
  
  public void bind(SocketAddress paramSocketAddress) throws SocketException {
    try {
      if (paramSocketAddress == null)
        paramSocketAddress = new InetSocketAddress(0); 
      this.dc.bind(paramSocketAddress);
    } catch (Exception exception) {
      Net.translateToSocketException(exception);
    } 
  }
  
  public void connect(InetAddress paramInetAddress, int paramInt) {
    try {
      connectInternal(new InetSocketAddress(paramInetAddress, paramInt));
    } catch (SocketException socketException) {}
  }
  
  public void connect(SocketAddress paramSocketAddress) throws SocketException {
    if (paramSocketAddress == null)
      throw new IllegalArgumentException("Address can't be null"); 
    connectInternal(paramSocketAddress);
  }
  
  public void disconnect() {
    try {
      this.dc.disconnect();
    } catch (IOException iOException) {
      throw new Error(iOException);
    } 
  }
  
  public boolean isBound() { return (this.dc.localAddress() != null); }
  
  public boolean isConnected() { return (this.dc.remoteAddress() != null); }
  
  public InetAddress getInetAddress() { return isConnected() ? Net.asInetSocketAddress(this.dc.remoteAddress()).getAddress() : null; }
  
  public int getPort() { return isConnected() ? Net.asInetSocketAddress(this.dc.remoteAddress()).getPort() : -1; }
  
  public void send(DatagramPacket paramDatagramPacket) throws IOException {
    synchronized (this.dc.blockingLock()) {
      if (!this.dc.isBlocking())
        throw new IllegalBlockingModeException(); 
      try {
        synchronized (paramDatagramPacket) {
          ByteBuffer byteBuffer = ByteBuffer.wrap(paramDatagramPacket.getData(), paramDatagramPacket.getOffset(), paramDatagramPacket.getLength());
          if (this.dc.isConnected()) {
            if (paramDatagramPacket.getAddress() == null) {
              InetSocketAddress inetSocketAddress = (InetSocketAddress)this.dc.remoteAddress();
              paramDatagramPacket.setPort(inetSocketAddress.getPort());
              paramDatagramPacket.setAddress(inetSocketAddress.getAddress());
              this.dc.write(byteBuffer);
            } else {
              this.dc.send(byteBuffer, paramDatagramPacket.getSocketAddress());
            } 
          } else {
            this.dc.send(byteBuffer, paramDatagramPacket.getSocketAddress());
          } 
        } 
      } catch (IOException iOException) {
        Net.translateException(iOException);
      } 
    } 
  }
  
  private SocketAddress receive(ByteBuffer paramByteBuffer) throws IOException {
    if (this.timeout == 0)
      return this.dc.receive(paramByteBuffer); 
    this.dc.configureBlocking(false);
    try {
      SocketAddress socketAddress;
      if ((socketAddress = this.dc.receive(paramByteBuffer)) != null)
        return socketAddress; 
      long l = this.timeout;
      do {
        if (!this.dc.isOpen())
          throw new ClosedChannelException(); 
        long l1 = System.currentTimeMillis();
        int i = this.dc.poll(Net.POLLIN, l);
        if (i > 0 && (i & Net.POLLIN) != 0 && (socketAddress = this.dc.receive(paramByteBuffer)) != null)
          return socketAddress; 
        l -= System.currentTimeMillis() - l1;
      } while (l > 0L);
      throw new SocketTimeoutException();
    } finally {
      if (this.dc.isOpen())
        this.dc.configureBlocking(true); 
    } 
  }
  
  public void receive(DatagramPacket paramDatagramPacket) throws IOException {
    synchronized (this.dc.blockingLock()) {
      if (!this.dc.isBlocking())
        throw new IllegalBlockingModeException(); 
      try {
        synchronized (paramDatagramPacket) {
          ByteBuffer byteBuffer = ByteBuffer.wrap(paramDatagramPacket.getData(), paramDatagramPacket.getOffset(), paramDatagramPacket.getLength());
          SocketAddress socketAddress = receive(byteBuffer);
          paramDatagramPacket.setSocketAddress(socketAddress);
          paramDatagramPacket.setLength(byteBuffer.position() - paramDatagramPacket.getOffset());
        } 
      } catch (IOException iOException) {
        Net.translateException(iOException);
      } 
    } 
  }
  
  public InetAddress getLocalAddress() {
    if (isClosed())
      return null; 
    SocketAddress socketAddress = this.dc.localAddress();
    if (socketAddress == null)
      socketAddress = new InetSocketAddress(0); 
    InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkConnect(inetAddress.getHostAddress(), -1);
      } catch (SecurityException securityException) {
        return (new InetSocketAddress(0)).getAddress();
      }  
    return inetAddress;
  }
  
  public int getLocalPort() {
    if (isClosed())
      return -1; 
    try {
      SocketAddress socketAddress = this.dc.getLocalAddress();
      if (socketAddress != null)
        return ((InetSocketAddress)socketAddress).getPort(); 
    } catch (Exception exception) {}
    return 0;
  }
  
  public void setSoTimeout(int paramInt) throws SocketException { this.timeout = paramInt; }
  
  public int getSoTimeout() { return this.timeout; }
  
  private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean) throws SocketException {
    try {
      this.dc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
    } 
  }
  
  private void setIntOption(SocketOption<Integer> paramSocketOption, int paramInt) throws SocketException {
    try {
      this.dc.setOption(paramSocketOption, Integer.valueOf(paramInt));
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
    } 
  }
  
  private boolean getBooleanOption(SocketOption<Boolean> paramSocketOption) throws SocketException {
    try {
      return ((Boolean)this.dc.getOption(paramSocketOption)).booleanValue();
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
      return false;
    } 
  }
  
  private int getIntOption(SocketOption<Integer> paramSocketOption) throws SocketException {
    try {
      return ((Integer)this.dc.getOption(paramSocketOption)).intValue();
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
      return -1;
    } 
  }
  
  public void setSendBufferSize(int paramInt) throws SocketException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Invalid send size"); 
    setIntOption(StandardSocketOptions.SO_SNDBUF, paramInt);
  }
  
  public int getSendBufferSize() { return getIntOption(StandardSocketOptions.SO_SNDBUF); }
  
  public void setReceiveBufferSize(int paramInt) throws SocketException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Invalid receive size"); 
    setIntOption(StandardSocketOptions.SO_RCVBUF, paramInt);
  }
  
  public int getReceiveBufferSize() { return getIntOption(StandardSocketOptions.SO_RCVBUF); }
  
  public void setReuseAddress(boolean paramBoolean) throws SocketException { setBooleanOption(StandardSocketOptions.SO_REUSEADDR, paramBoolean); }
  
  public boolean getReuseAddress() { return getBooleanOption(StandardSocketOptions.SO_REUSEADDR); }
  
  public void setBroadcast(boolean paramBoolean) throws SocketException { setBooleanOption(StandardSocketOptions.SO_BROADCAST, paramBoolean); }
  
  public boolean getBroadcast() { return getBooleanOption(StandardSocketOptions.SO_BROADCAST); }
  
  public void setTrafficClass(int paramInt) throws SocketException { setIntOption(StandardSocketOptions.IP_TOS, paramInt); }
  
  public int getTrafficClass() { return getIntOption(StandardSocketOptions.IP_TOS); }
  
  public void close() {
    try {
      this.dc.close();
    } catch (IOException iOException) {
      throw new Error(iOException);
    } 
  }
  
  public boolean isClosed() { return !this.dc.isOpen(); }
  
  public DatagramChannel getChannel() { return this.dc; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\DatagramSocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */