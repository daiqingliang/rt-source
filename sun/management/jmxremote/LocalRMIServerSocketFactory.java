package sun.management.jmxremote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Enumeration;

public final class LocalRMIServerSocketFactory implements RMIServerSocketFactory {
  public ServerSocket createServerSocket(int paramInt) throws IOException { return new ServerSocket(paramInt) {
        public Socket accept() throws IOException {
          Enumeration enumeration;
          Socket socket = super.accept();
          InetAddress inetAddress = socket.getInetAddress();
          if (inetAddress == null) {
            enumeration = "";
            if (socket.isClosed()) {
              enumeration = " Socket is closed.";
            } else if (!socket.isConnected()) {
              enumeration = " Socket is not connected";
            } 
            try {
              socket.close();
            } catch (Exception exception) {}
            throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported. Couldn't determine client address." + enumeration);
          } 
          if (inetAddress.isLoopbackAddress())
            return socket; 
          try {
            enumeration = NetworkInterface.getNetworkInterfaces();
          } catch (SocketException socketException) {
            try {
              socket.close();
            } catch (IOException iOException) {}
            throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.", socketException);
          } 
          while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface)enumeration.nextElement();
            Enumeration enumeration1 = networkInterface.getInetAddresses();
            while (enumeration1.hasMoreElements()) {
              InetAddress inetAddress1 = (InetAddress)enumeration1.nextElement();
              if (inetAddress1.equals(inetAddress))
                return socket; 
            } 
          } 
          try {
            socket.close();
          } catch (IOException iOException) {}
          throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.");
        }
      }; }
  
  public boolean equals(Object paramObject) { return paramObject instanceof LocalRMIServerSocketFactory; }
  
  public int hashCode() { return getClass().hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jmxremote\LocalRMIServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */