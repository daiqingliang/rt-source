package java.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

@Deprecated
public interface ServerRef extends RemoteRef {
  public static final long serialVersionUID = -4557750989390278438L;
  
  RemoteStub exportObject(Remote paramRemote, Object paramObject) throws RemoteException;
  
  String getClientHost() throws ServerNotActiveException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\ServerRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */