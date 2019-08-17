package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SocketImpl implements SocketOptions {
  Socket socket = null;
  
  ServerSocket serverSocket = null;
  
  protected FileDescriptor fd;
  
  protected InetAddress address;
  
  protected int port;
  
  protected int localport;
  
  protected abstract void create(boolean paramBoolean) throws IOException;
  
  protected abstract void connect(String paramString, int paramInt) throws IOException;
  
  protected abstract void connect(InetAddress paramInetAddress, int paramInt) throws IOException;
  
  protected abstract void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException;
  
  protected abstract void bind(InetAddress paramInetAddress, int paramInt) throws IOException;
  
  protected abstract void listen(int paramInt) throws IOException;
  
  protected abstract void accept(SocketImpl paramSocketImpl) throws IOException;
  
  protected abstract InputStream getInputStream() throws IOException;
  
  protected abstract OutputStream getOutputStream() throws IOException;
  
  protected abstract int available() throws IOException;
  
  protected abstract void close();
  
  protected void shutdownInput() { throw new IOException("Method not implemented!"); }
  
  protected void shutdownOutput() { throw new IOException("Method not implemented!"); }
  
  protected FileDescriptor getFileDescriptor() { return this.fd; }
  
  protected InetAddress getInetAddress() { return this.address; }
  
  protected int getPort() throws IOException { return this.port; }
  
  protected boolean supportsUrgentData() { return false; }
  
  protected abstract void sendUrgentData(int paramInt) throws IOException;
  
  protected int getLocalPort() throws IOException { return this.localport; }
  
  void setSocket(Socket paramSocket) { this.socket = paramSocket; }
  
  Socket getSocket() { return this.socket; }
  
  void setServerSocket(ServerSocket paramServerSocket) { this.serverSocket = paramServerSocket; }
  
  ServerSocket getServerSocket() { return this.serverSocket; }
  
  public String toString() { return "Socket[addr=" + getInetAddress() + ",port=" + getPort() + ",localport=" + getLocalPort() + "]"; }
  
  void reset() {
    this.address = null;
    this.port = 0;
    this.localport = 0;
  }
  
  protected void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3) {}
  
  <T> void setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (paramSocketOption == StandardSocketOptions.SO_KEEPALIVE) {
      setOption(8, paramT);
    } else if (paramSocketOption == StandardSocketOptions.SO_SNDBUF) {
      setOption(4097, paramT);
    } else if (paramSocketOption == StandardSocketOptions.SO_RCVBUF) {
      setOption(4098, paramT);
    } else if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR) {
      setOption(4, paramT);
    } else if (paramSocketOption == StandardSocketOptions.SO_LINGER) {
      setOption(128, paramT);
    } else if (paramSocketOption == StandardSocketOptions.IP_TOS) {
      setOption(3, paramT);
    } else if (paramSocketOption == StandardSocketOptions.TCP_NODELAY) {
      setOption(1, paramT);
    } else {
      throw new UnsupportedOperationException("unsupported option");
    } 
  }
  
  <T> T getOption(SocketOption<T> paramSocketOption) throws IOException {
    if (paramSocketOption == StandardSocketOptions.SO_KEEPALIVE)
      return (T)getOption(8); 
    if (paramSocketOption == StandardSocketOptions.SO_SNDBUF)
      return (T)getOption(4097); 
    if (paramSocketOption == StandardSocketOptions.SO_RCVBUF)
      return (T)getOption(4098); 
    if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR)
      return (T)getOption(4); 
    if (paramSocketOption == StandardSocketOptions.SO_LINGER)
      return (T)getOption(128); 
    if (paramSocketOption == StandardSocketOptions.IP_TOS)
      return (T)getOption(3); 
    if (paramSocketOption == StandardSocketOptions.TCP_NODELAY)
      return (T)getOption(1); 
    throw new UnsupportedOperationException("unsupported option");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */