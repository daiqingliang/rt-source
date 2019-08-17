package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.omg.CORBA.ORB;

public class DefaultSocketFactory implements ORBSocketFactory {
  private ORB orb;
  
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
  
  public void setORB(ORB paramORB) { this.orb = paramORB; }
  
  public ServerSocket createServerSocket(String paramString, int paramInt) throws IOException {
    ServerSocket serverSocket;
    if (!paramString.equals("IIOP_CLEAR_TEXT"))
      throw wrapper.defaultCreateServerSocketGivenNonIiopClearText(paramString); 
    if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
      ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
      serverSocket = serverSocketChannel.socket();
    } else {
      serverSocket = new ServerSocket();
    } 
    serverSocket.bind(new InetSocketAddress(paramInt));
    return serverSocket;
  }
  
  public SocketInfo getEndPointInfo(ORB paramORB, IOR paramIOR, SocketInfo paramSocketInfo) {
    IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)paramIOR.getProfile().getTaggedProfileTemplate();
    IIOPAddress iIOPAddress = iIOPProfileTemplate.getPrimaryAddress();
    return new EndPointInfoImpl("IIOP_CLEAR_TEXT", iIOPAddress.getPort(), iIOPAddress.getHost().toLowerCase());
  }
  
  public Socket createSocket(SocketInfo paramSocketInfo) throws IOException, GetEndPointInfoAgainException {
    Socket socket;
    if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
      InetSocketAddress inetSocketAddress = new InetSocketAddress(paramSocketInfo.getHost(), paramSocketInfo.getPort());
      SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
      socket = socketChannel.socket();
    } else {
      socket = new Socket(paramSocketInfo.getHost(), paramSocketInfo.getPort());
    } 
    try {
      socket.setTcpNoDelay(true);
    } catch (Exception exception) {}
    return socket;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\DefaultSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */