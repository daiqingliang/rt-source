package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.security.AccessController;
import sun.misc.ObjectInputFilter;
import sun.rmi.transport.tcp.TCPConnection;

public final class DGCImpl_Stub extends RemoteStub implements DGC {
  private static final Operation[] operations = { new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)") };
  
  private static final long interfaceHash = -669196253586618813L;
  
  private static int DGCCLIENT_MAX_DEPTH = 6;
  
  private static int DGCCLIENT_MAX_ARRAY_SIZE = 10000;
  
  public DGCImpl_Stub() {}
  
  public DGCImpl_Stub(RemoteRef paramRemoteRef) { super(paramRemoteRef); }
  
  public void clean(ObjID[] paramArrayOfObjID, long paramLong, VMID paramVMID, boolean paramBoolean) throws RemoteException {
    try {
      RemoteCall remoteCall = this.ref.newCall(this, operations, 0, -669196253586618813L);
      try {
        ObjectOutput objectOutput = remoteCall.getOutputStream();
        objectOutput.writeObject(paramArrayOfObjID);
        objectOutput.writeLong(paramLong);
        objectOutput.writeObject(paramVMID);
        objectOutput.writeBoolean(paramBoolean);
      } catch (IOException iOException) {
        throw new MarshalException("error marshalling arguments", iOException);
      } 
      this.ref.invoke(remoteCall);
      this.ref.done(remoteCall);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Lease dirty(ObjID[] paramArrayOfObjID, long paramLong, Lease paramLease) throws RemoteException {
    try {
      Lease lease;
      remoteCall = this.ref.newCall(this, operations, 1, -669196253586618813L);
      try {
        lease = remoteCall.getOutputStream();
        lease.writeObject(paramArrayOfObjID);
        lease.writeLong(paramLong);
        lease.writeObject(paramLease);
      } catch (IOException null) {
        throw new MarshalException("error marshalling arguments", lease);
      } 
      this.ref.invoke(remoteCall);
      Connection connection = ((StreamRemoteCall)remoteCall).getConnection();
      try {
        ObjectInput objectInput = remoteCall.getInputStream();
        if (objectInput instanceof ObjectInputStream) {
          ObjectInputStream objectInputStream = (ObjectInputStream)objectInput;
          AccessController.doPrivileged(() -> {
                ObjectInputFilter.Config.setObjectInputFilter(paramObjectInputStream, DGCImpl_Stub::leaseFilter);
                return null;
              });
        } 
        lease = (Lease)objectInput.readObject();
      } catch (IOException|ClassNotFoundException iOException) {
        if (connection instanceof TCPConnection)
          ((TCPConnection)connection).getChannel().free(connection, false); 
        throw new UnmarshalException("error unmarshalling return", iOException);
      } finally {
        this.ref.done(remoteCall);
      } 
      return lease;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  private static ObjectInputFilter.Status leaseFilter(ObjectInputFilter.FilterInfo paramFilterInfo) {
    if (paramFilterInfo.depth() > DGCCLIENT_MAX_DEPTH)
      return ObjectInputFilter.Status.REJECTED; 
    Class clazz = paramFilterInfo.serialClass();
    if (clazz != null) {
      while (clazz.isArray()) {
        if (paramFilterInfo.arrayLength() >= 0L && paramFilterInfo.arrayLength() > DGCCLIENT_MAX_ARRAY_SIZE)
          return ObjectInputFilter.Status.REJECTED; 
        clazz = clazz.getComponentType();
      } 
      return clazz.isPrimitive() ? ObjectInputFilter.Status.ALLOWED : ((clazz == java.rmi.server.UID.class || clazz == VMID.class || clazz == Lease.class) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED);
    } 
    return ObjectInputFilter.Status.UNDECIDED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\DGCImpl_Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */