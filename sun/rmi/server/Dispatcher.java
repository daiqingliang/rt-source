package sun.rmi.server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;

public interface Dispatcher {
  void dispatch(Remote paramRemote, RemoteCall paramRemoteCall) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\Dispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */