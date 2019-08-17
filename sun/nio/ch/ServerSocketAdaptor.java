package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketAdaptor extends ServerSocket {
  private final ServerSocketChannelImpl ssc;
  
  public static ServerSocket create(ServerSocketChannelImpl paramServerSocketChannelImpl) {
    try {
      return new ServerSocketAdaptor(paramServerSocketChannelImpl);
    } catch (IOException iOException) {
      throw new Error(iOException);
    } 
  }
  
  private ServerSocketAdaptor(ServerSocketChannelImpl paramServerSocketChannelImpl) throws IOException { this.ssc = paramServerSocketChannelImpl; }
  
  public void bind(SocketAddress paramSocketAddress) throws IOException { bind(paramSocketAddress, 50); }
  
  public void bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    if (paramSocketAddress == null)
      paramSocketAddress = new InetSocketAddress(0); 
    try {
      this.ssc.bind(paramSocketAddress, paramInt);
    } catch (Exception exception) {
      Net.translateException(exception);
    } 
  }
  
  public InetAddress getInetAddress() { return !this.ssc.isBound() ? null : Net.getRevealedLocalAddress(this.ssc.localAddress()).getAddress(); }
  
  public int getLocalPort() { return !this.ssc.isBound() ? -1 : Net.asInetSocketAddress(this.ssc.localAddress()).getPort(); }
  
  public Socket accept() throws IOException {
    synchronized (this.ssc.blockingLock()) {
      if (!this.ssc.isBound())
        throw new IllegalBlockingModeException(); 
      try {
        if (this.timeout == 0) {
          SocketChannel socketChannel = this.ssc.accept();
          if (socketChannel == null && !this.ssc.isBlocking())
            throw new IllegalBlockingModeException(); 
          return socketChannel.socket();
        } 
        this.ssc.configureBlocking(false);
        try {
          SocketChannel socketChannel;
          if ((socketChannel = this.ssc.accept()) != null)
            return socketChannel.socket(); 
          long l = this.timeout;
          do {
            if (!this.ssc.isOpen())
              throw new ClosedChannelException(); 
            long l1 = System.currentTimeMillis();
            int i = this.ssc.poll(Net.POLLIN, l);
            if (i > 0 && (socketChannel = this.ssc.accept()) != null)
              return socketChannel.socket(); 
            l -= System.currentTimeMillis() - l1;
          } while (l > 0L);
          throw new SocketTimeoutException();
        } finally {
          if (this.ssc.isOpen())
            this.ssc.configureBlocking(true); 
        } 
      } catch (Exception exception) {
        Net.translateException(exception);
        assert false;
        return null;
      } 
    } 
  }
  
  public void close() throws IOException { this.ssc.close(); }
  
  public ServerSocketChannel getChannel() { return this.ssc; }
  
  public boolean isBound() { return this.ssc.isBound(); }
  
  public boolean isClosed() { return !this.ssc.isOpen(); }
  
  public void setSoTimeout(int paramInt) throws SocketException { this.timeout = paramInt; }
  
  public int getSoTimeout() { return this.timeout; }
  
  public void setReuseAddress(boolean paramBoolean) throws SocketException {
    try {
      this.ssc.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf(paramBoolean));
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
    } 
  }
  
  public boolean getReuseAddress() {
    try {
      return ((Boolean)this.ssc.getOption(StandardSocketOptions.SO_REUSEADDR)).booleanValue();
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
      return false;
    } 
  }
  
  public String toString() { return !isBound() ? "ServerSocket[unbound]" : ("ServerSocket[addr=" + getInetAddress() + ",localport=" + getLocalPort() + "]"); }
  
  public void setReceiveBufferSize(int paramInt) throws SocketException {
    if (paramInt <= 0)
      throw new IllegalArgumentException("size cannot be 0 or negative"); 
    try {
      this.ssc.setOption(StandardSocketOptions.SO_RCVBUF, Integer.valueOf(paramInt));
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
    } 
  }
  
  public int getReceiveBufferSize() {
    try {
      return ((Integer)this.ssc.getOption(StandardSocketOptions.SO_RCVBUF)).intValue();
    } catch (IOException iOException) {
      Net.translateToSocketException(iOException);
      return -1;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\ServerSocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */