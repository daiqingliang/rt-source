package sun.rmi.registry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;

public final class RegistryImpl_Skel implements Skeleton {
  private static final Operation[] operations = { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
  
  private static final long interfaceHash = 4905912898345647071L;
  
  public Operation[] getOperations() { return (Operation[])operations.clone(); }
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong) throws Exception {
    String str2;
    String str1;
    if (paramLong != 4905912898345647071L)
      throw new SkeletonMismatchException("interface hash mismatch"); 
    RegistryImpl registryImpl = (RegistryImpl)paramRemote;
    switch (paramInt) {
      case 0:
        RegistryImpl.checkAccess("Registry.bind");
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          str2 = (String)objectInput.readObject();
          remote = (Remote)objectInput.readObject();
        } catch (IOException|ClassNotFoundException iOException) {
          throw new UnmarshalException("error unmarshalling arguments", iOException);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        registryImpl.bind(str2, remote);
        try {
          paramRemoteCall.getResultStream(true);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
      case 1:
        paramRemoteCall.releaseInputStream();
        str1 = registryImpl.list();
        try {
          ObjectOutput objectOutput = paramRemoteCall.getResultStream(true);
          objectOutput.writeObject(str1);
        } catch (IOException remote) {
          throw new MarshalException("error marshalling return", remote);
        } 
        return;
      case 2:
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          str1 = (String)objectInput.readObject();
        } catch (IOException|ClassNotFoundException remote) {
          throw new UnmarshalException("error unmarshalling arguments", remote);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        remote = registryImpl.lookup(str1);
        try {
          ObjectOutput objectOutput = paramRemoteCall.getResultStream(true);
          objectOutput.writeObject(remote);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
      case 3:
        RegistryImpl.checkAccess("Registry.rebind");
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          str1 = (String)objectInput.readObject();
          remote = (Remote)objectInput.readObject();
        } catch (IOException|ClassNotFoundException iOException) {
          throw new UnmarshalException("error unmarshalling arguments", iOException);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        registryImpl.rebind(str1, remote);
        try {
          paramRemoteCall.getResultStream(true);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
      case 4:
        RegistryImpl.checkAccess("Registry.unbind");
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          str1 = (String)objectInput.readObject();
        } catch (IOException|ClassNotFoundException remote) {
          throw new UnmarshalException("error unmarshalling arguments", remote);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        registryImpl.unbind(str1);
        try {
          paramRemoteCall.getResultStream(true);
        } catch (IOException remote) {
          throw new MarshalException("error marshalling return", remote);
        } 
        return;
    } 
    throw new UnmarshalException("invalid method number");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\registry\RegistryImpl_Skel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */