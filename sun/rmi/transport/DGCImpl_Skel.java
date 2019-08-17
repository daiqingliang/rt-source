package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;

public final class DGCImpl_Skel implements Skeleton {
  private static final Operation[] operations = { new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)") };
  
  private static final long interfaceHash = -669196253586618813L;
  
  public Operation[] getOperations() { return (Operation[])operations.clone(); }
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong) throws Exception {
    Lease lease2;
    Lease lease1;
    long l;
    ObjID[] arrayOfObjID;
    if (paramLong != -669196253586618813L)
      throw new SkeletonMismatchException("interface hash mismatch"); 
    DGCImpl dGCImpl = (DGCImpl)paramRemote;
    switch (paramInt) {
      case 0:
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          arrayOfObjID = (ObjID[])objectInput.readObject();
          l = objectInput.readLong();
          lease1 = (VMID)objectInput.readObject();
          bool = objectInput.readBoolean();
        } catch (IOException iOException) {
          throw new UnmarshalException("error unmarshalling arguments", iOException);
        } catch (ClassNotFoundException classNotFoundException) {
          throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        dGCImpl.clean(arrayOfObjID, l, lease1, bool);
        try {
          paramRemoteCall.getResultStream(true);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
      case 1:
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          arrayOfObjID = (ObjID[])objectInput.readObject();
          l = objectInput.readLong();
          lease1 = (Lease)objectInput.readObject();
        } catch (IOException bool) {
          IOException iOException;
          throw new UnmarshalException("error unmarshalling arguments", iOException);
        } catch (ClassNotFoundException bool) {
          ClassNotFoundException classNotFoundException;
          throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        lease2 = dGCImpl.dirty(arrayOfObjID, l, lease1);
        try {
          ObjectOutput objectOutput = paramRemoteCall.getResultStream(true);
          objectOutput.writeObject(lease2);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
    } 
    throw new UnmarshalException("invalid method number");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\DGCImpl_Skel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */