package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PortableRemoteObjectDelegate {
  void exportObject(Remote paramRemote) throws RemoteException;
  
  Remote toStub(Remote paramRemote) throws NoSuchObjectException;
  
  void unexportObject(Remote paramRemote) throws RemoteException;
  
  Object narrow(Object paramObject, Class paramClass) throws ClassCastException;
  
  void connect(Remote paramRemote1, Remote paramRemote2) throws RemoteException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\PortableRemoteObjectDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */