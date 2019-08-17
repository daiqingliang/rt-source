package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class DefaultSocketFactoryImpl implements ORBSocketFactory {
  private ORB orb;
  
  private static final boolean keepAlive = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str = System.getProperty("com.sun.CORBA.transport.enableTcpKeepAlive");
          return (str != null) ? new Boolean(!"false".equalsIgnoreCase(str)) : Boolean.FALSE;
        }
      })).booleanValue();
  
  public void setORB(ORB paramORB) { this.orb = paramORB; }
  
  public ServerSocket createServerSocket(String paramString, InetSocketAddress paramInetSocketAddress) throws IOException {
    ServerSocketChannel serverSocketChannel = null;
    ServerSocket serverSocket = null;
    if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
      serverSocketChannel = ServerSocketChannel.open();
      serverSocket = serverSocketChannel.socket();
    } else {
      serverSocket = new ServerSocket();
    } 
    serverSocket.bind(paramInetSocketAddress);
    return serverSocket;
  }
  
  public Socket createSocket(String paramString, InetSocketAddress paramInetSocketAddress) throws IOException {
    SocketChannel socketChannel = null;
    Socket socket = null;
    if (this.orb.getORBData().connectionSocketType().equals("SocketChannel")) {
      socketChannel = SocketChannel.open(paramInetSocketAddress);
      socket = socketChannel.socket();
    } else {
      socket = new Socket(paramInetSocketAddress.getHostName(), paramInetSocketAddress.getPort());
    } 
    socket.setTcpNoDelay(true);
    if (keepAlive)
      socket.setKeepAlive(true); 
    return socket;
  }
  
  public void setAcceptedSocketOptions(Acceptor paramAcceptor, ServerSocket paramServerSocket, Socket paramSocket) throws SocketException {
    paramSocket.setTcpNoDelay(true);
    if (keepAlive)
      paramSocket.setKeepAlive(true); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\DefaultSocketFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */