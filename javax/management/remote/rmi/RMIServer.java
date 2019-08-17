package javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
  String getVersion() throws RemoteException;
  
  RMIConnection newClient(Object paramObject) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */