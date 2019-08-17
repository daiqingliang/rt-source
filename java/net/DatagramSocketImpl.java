package java.net;

import java.io.FileDescriptor;
import java.io.IOException;

public abstract class DatagramSocketImpl implements SocketOptions {
  protected int localPort;
  
  protected FileDescriptor fd;
  
  DatagramSocket socket;
  
  int dataAvailable() { return 0; }
  
  void setDatagramSocket(DatagramSocket paramDatagramSocket) { this.socket = paramDatagramSocket; }
  
  DatagramSocket getDatagramSocket() { return this.socket; }
  
  protected abstract void create();
  
  protected abstract void bind(int paramInt, InetAddress paramInetAddress) throws SocketException;
  
  protected abstract void send(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected void connect(InetAddress paramInetAddress, int paramInt) throws SocketException {}
  
  protected void disconnect() {}
  
  protected abstract int peek(InetAddress paramInetAddress) throws IOException;
  
  protected abstract int peekData(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected abstract void receive(DatagramPacket paramDatagramPacket) throws IOException;
  
  @Deprecated
  protected abstract void setTTL(byte paramByte) throws IOException;
  
  @Deprecated
  protected abstract byte getTTL() throws IOException;
  
  protected abstract void setTimeToLive(int paramInt) throws IOException;
  
  protected abstract int getTimeToLive();
  
  protected abstract void join(InetAddress paramInetAddress) throws IOException;
  
  protected abstract void leave(InetAddress paramInetAddress) throws IOException;
  
  protected abstract void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected abstract void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected abstract void close();
  
  protected int getLocalPort() { return this.localPort; }
  
  <T> void setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException {
    if (paramSocketOption == StandardSocketOptions.SO_SNDBUF) {
      setOption(4097, paramT);
    } else if (paramSocketOption == StandardSocketOptions.SO_RCVBUF) {
      setOption(4098, paramT);
    } else if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR) {
      setOption(4, paramT);
    } else if (paramSocketOption == StandardSocketOptions.IP_TOS) {
      setOption(3, paramT);
    } else if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF && getDatagramSocket() instanceof MulticastSocket) {
      setOption(31, paramT);
    } else if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL && getDatagramSocket() instanceof MulticastSocket) {
      if (!(paramT instanceof Integer))
        throw new IllegalArgumentException("not an integer"); 
      setTimeToLive(((Integer)paramT).intValue());
    } else if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP && getDatagramSocket() instanceof MulticastSocket) {
      setOption(18, paramT);
    } else {
      throw new UnsupportedOperationException("unsupported option");
    } 
  }
  
  <T> T getOption(SocketOption<T> paramSocketOption) throws IOException {
    if (paramSocketOption == StandardSocketOptions.SO_SNDBUF)
      return (T)getOption(4097); 
    if (paramSocketOption == StandardSocketOptions.SO_RCVBUF)
      return (T)getOption(4098); 
    if (paramSocketOption == StandardSocketOptions.SO_REUSEADDR)
      return (T)getOption(4); 
    if (paramSocketOption == StandardSocketOptions.IP_TOS)
      return (T)getOption(3); 
    if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF && getDatagramSocket() instanceof MulticastSocket)
      return (T)getOption(31); 
    if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL && getDatagramSocket() instanceof MulticastSocket)
      return (T)Integer.valueOf(getTimeToLive()); 
    if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP && getDatagramSocket() instanceof MulticastSocket)
      return (T)getOption(18); 
    throw new UnsupportedOperationException("unsupported option");
  }
  
  protected FileDescriptor getFileDescriptor() { return this.fd; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\DatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */