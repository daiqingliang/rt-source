package sun.rmi.transport;

import java.rmi.RemoteException;

public interface Channel {
  Connection newConnection() throws RemoteException;
  
  Endpoint getEndpoint();
  
  void free(Connection paramConnection, boolean paramBoolean) throws RemoteException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */