package java.rmi.dgc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;

public interface DGC extends Remote {
  Lease dirty(ObjID[] paramArrayOfObjID, long paramLong, Lease paramLease) throws RemoteException;
  
  void clean(ObjID[] paramArrayOfObjID, long paramLong, VMID paramVMID, boolean paramBoolean) throws RemoteException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\dgc\DGC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */