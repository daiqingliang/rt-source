package sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class SocketAdaptor extends Socket {
  private final SocketChannelImpl sc;
  
  private InputStream socketInputStream = null;
  
  private SocketAdaptor(SocketChannelImpl paramSocketChannelImpl) throws SocketException {
    super((SocketImpl)null);
    this.sc = paramSocketChannelImpl;
  }
  
  public static Socket create(SocketChannelImpl paramSocketChannelImpl) {
    try {
      return new SocketAdaptor(paramSocketChannelImpl);
    } catch (SocketException socketException) {
      throw new InternalError("Should not reach here");
    } 
  }
  
  public SocketChannel getChannel() { return this.sc; }
  
  public void connect(SocketAddress paramSocketAddress) throws IOException { connect(paramSocketAddress, 0); }
  
  public void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    if (paramSocketAddress == null)
      throw new IllegalArgumentException("connect: The address can't be null"); 
    if (paramInt < 0)
      throw new IllegalArgumentException("connect: timeout can't be negative"); 
    synchronized (this.sc.blockingLock()) {
      if (!this.sc.isBlocking())
        throw new IllegalBlockingModeException(); 
      try {
        if (paramInt == 0) {
          this.sc.connect(paramSocketAddress);
          return;
        } 
        this.sc.configureBlocking(false);
        try {
          if (this.sc.connect(paramSocketAddress))
            return; 
          long l = paramInt;
          while (true) {
            if (!this.sc.isOpen())
              throw new ClosedChannelException(); 
            long l1 = System.currentTimeMillis();
            int i = this.sc.poll(Net.POLLCONN, l);
            if (i > 0 && this.sc.finishConnect())
              break; 
            l -= System.currentTimeMillis() - l1;
            if (l <= 0L) {
              try {
                this.sc.close();
              } catch (IOException iOException) {}
              throw new SocketTimeoutException();
            } 
          } 
        } finally {
          if (this.sc.isOpen())
            this.sc.configureBlocking(true); 
        } 
      } catch (Exception exception) {
        Net.translateException(exception, true);
      } 
    } 
  }
  
  public void bind(SocketAddress paramSocketAddress) throws IOException {
    try {
      this.sc.bind(paramSocketAddress);
    } catch (Exception exception) {
      Net.translateException(exception);
    } 
  }
  
  public InetAddress getInetAddress() {
    SocketAddress socketAddress = this.sc.remoteAddress();
    return (socketAddress == null) ? null : ((InetSocketAddress)socketAddress).getAddress();
  }
  
  public InetAddress getLocalAddress() {
    if (this.sc.isOpen()) {
      InetSocketAddress inetSocketAddress = this.sc.localAddress();
      if (inetSocketAddress != null)
        return Net.getRevealedLocalAddress(inetSocketAddress).getAddress(); 
    } 
    return (new InetSocketAddress(0)).getAddress();
  }
  
  public int getPort() {
    SocketAddress socketAddress = this.sc.remoteAddress();
    return (socketAddress == null) ? 0 : ((InetSocketAddress)socketAddress).getPort();
  }
  
  public int getLocalPort() {
    InetSocketAddress inetSocketAddress = this.sc.localAddress();
    return (inetSocketAddress == null) ? -1 : ((InetSocketAddress)inetSocketAddress).getPort();
  }
  
  public InputStream getInputStream() throws IOException {
    if (!this.sc.isOpen())
      throw new SocketException("Socket is closed"); 
    if (!this.sc.isConnected())
      throw new SocketException("Socket is not connected"); 
    if (!this.sc.isInputOpen())
      throw new SocketException("Socket input is shutdown"); 
    if (this.socketInputStream == null)
      try {
        this.socketInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
              public InputStream run() throws IOException { return new SocketAdaptor.SocketInputStream(SocketAdaptor.this, null); }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      }  
    return this.socketInputStream;
  }
  
  public OutputStream getOutputStream() throws IOException {
    if (!this.sc.isOpen())
      throw new SocketException("Socket is closed"); 
    if (!this.sc.isConnected())
      throw new SocketException("Socket is not connected"); 
    if (!this.sc.isOutputOpen())
      throw new SocketException("Socket output is shutdown"); 
    OutputStream outputStream = null;
    try {
      outputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
            public OutputStream run() throws IOException { return Channels.newOutputStream(SocketAdaptor.this.sc); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
    return outputStream;
  }
  
  private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean) throws SocketException {
    try {
      this.sc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
    } 
  }
  
  private void setIntOption(SocketOption<Integer> paramSocketOption, int paramInt) throws SocketException {
    try {
      this.sc.setOption(paramSocketOption, Integer.valueOf(paramInt));
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
    } 
  }
  
  private boolean getBooleanOption(SocketOption<Boolean> paramSocketOption) throws SocketException {
    try {
      return ((Boolean)this.sc.getOption(paramSocketOption)).booleanValue();
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
      return false;
    } 
  }
  
  private int getIntOption(SocketOption<Integer> paramSocketOption) throws SocketException {
    try {
      return ((Integer)this.sc.getOption(paramSocketOption)).intValue();
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
      return -1;
    } 
  }
  
  public void setTcpNoDelay(boolean paramBoolean) throws SocketException { setBooleanOption(StandardSocketOptions.TCP_NODELAY, paramBoolean); }
  
  public boolean getTcpNoDelay() throws SocketException { return getBooleanOption(StandardSocketOptions.TCP_NODELAY); }
  
  public void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException {
    if (!paramBoolean)
      paramInt = -1; 
    setIntOption(StandardSocketOptions.SO_LINGER, paramInt);
  }
  
  public int getSoLinger() { return getIntOption(StandardSocketOptions.SO_LINGER); }
  
  public void sendUrgentData(int paramInt) throws IOException {
    int i = this.sc.sendOutOfBandData((byte)paramInt);
    if (i == 0)
      throw new IOException("Socket buffer full"); 
  }
  
  public void setOOBInline(boolean paramBoolean) throws SocketException { setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, paramBoolean); }
  
  public boolean getOOBInline() throws SocketException { return getBooleanOption(ExtendedSocketOption.SO_OOBINLINE); }
  
  public void setSoTimeout(int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeout can't be negative"); 
    this.timeout = paramInt;
  }
  
  public int getSoTimeout() { return this.timeout; }
  
  public void setSendBufferSize(int paramInt) throws IOException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Invalid send size"); 
    setIntOption(StandardSocketOptions.SO_SNDBUF, paramInt);
  }
  
  public int getSendBufferSize() { return getIntOption(StandardSocketOptions.SO_SNDBUF); }
  
  public void setReceiveBufferSize(int paramInt) throws IOException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Invalid receive size"); 
    setIntOption(StandardSocketOptions.SO_RCVBUF, paramInt);
  }
  
  public int getReceiveBufferSize() { return getIntOption(StandardSocketOptions.SO_RCVBUF); }
  
  public void setKeepAlive(boolean paramBoolean) throws SocketException { setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, paramBoolean); }
  
  public boolean getKeepAlive() throws SocketException { return getBooleanOption(StandardSocketOptions.SO_KEEPALIVE); }
  
  public void setTrafficClass(int paramInt) throws IOException { setIntOption(StandardSocketOptions.IP_TOS, paramInt); }
  
  public int getTrafficClass() { return getIntOption(StandardSocketOptions.IP_TOS); }
  
  public void setReuseAddress(boolean paramBoolean) throws SocketException { setBooleanOption(StandardSocketOptions.SO_REUSEADDR, paramBoolean); }
  
  public boolean getReuseAddress() throws SocketException { return getBooleanOption(StandardSocketOptions.SO_REUSEADDR); }
  
  public void close() throws IOException { this.sc.close(); }
  
  public void shutdownInput() throws IOException {
    try {
      this.sc.shutdownInput();
    } catch (Exception exception) {
      Net.translateException(exception);
    } 
  }
  
  public void shutdownOutput() throws IOException {
    try {
      this.sc.shutdownOutput();
    } catch (Exception exception) {
      Net.translateException(exception);
    } 
  }
  
  public String toString() { return this.sc.isConnected() ? ("Socket[addr=" + getInetAddress() + ",port=" + getPort() + ",localport=" + getLocalPort() + "]") : "Socket[unconnected]"; }
  
  public boolean isConnected() throws SocketException { return this.sc.isConnected(); }
  
  public boolean isBound() throws SocketException { return (this.sc.localAddress() != null); }
  
  public boolean isClosed() throws SocketException { return !this.sc.isOpen(); }
  
  public boolean isInputShutdown() throws SocketException { return !this.sc.isInputOpen(); }
  
  public boolean isOutputShutdown() throws SocketException { return !this.sc.isOutputOpen(); }
  
  private class SocketInputStream extends ChannelInputStream {
    private SocketInputStream(SocketAdaptor this$0) { super(this$0.sc); }
    
    protected int read(ByteBuffer param1ByteBuffer) throws IOException {
      synchronized (this.this$0.sc.blockingLock()) {
        if (!this.this$0.sc.isBlocking())
          throw new IllegalBlockingModeException(); 
        if (this.this$0.timeout == 0)
          return this.this$0.sc.read(param1ByteBuffer); 
        this.this$0.sc.configureBlocking(false);
        try {
          int i;
          if ((i = this.this$0.sc.read(param1ByteBuffer)) != 0)
            return i; 
          long l = this.this$0.timeout;
          do {
            if (!this.this$0.sc.isOpen())
              throw new ClosedChannelException(); 
            long l1 = System.currentTimeMillis();
            int j = this.this$0.sc.poll(Net.POLLIN, l);
            if (j > 0 && (i = this.this$0.sc.read(param1ByteBuffer)) != 0)
              return i; 
            l -= System.currentTimeMillis() - l1;
          } while (l > 0L);
          throw new SocketTimeoutException();
        } finally {
          if (this.this$0.sc.isOpen())
            this.this$0.sc.configureBlocking(true); 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */