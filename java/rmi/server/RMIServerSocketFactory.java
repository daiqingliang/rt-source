package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;

public interface RMIServerSocketFactory {
  ServerSocket createServerSocket(int paramInt) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\RMIServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */