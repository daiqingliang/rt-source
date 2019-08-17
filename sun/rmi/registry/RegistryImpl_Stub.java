package sun.rmi.registry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.MarshalException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.registry.Registry;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RegistryImpl_Stub extends RemoteStub implements Registry, Remote {
  private static final Operation[] operations = { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
  
  private static final long interfaceHash = 4905912898345647071L;
  
  public RegistryImpl_Stub() {}
  
  public RegistryImpl_Stub(RemoteRef paramRemoteRef) { super(paramRemoteRef); }
  
  public void bind(String paramString, Remote paramRemote) throws AccessException, AlreadyBoundException, RemoteException {
    try {
      RemoteCall remoteCall = this.ref.newCall(this, operations, 0, 4905912898345647071L);
      try {
        ObjectOutput objectOutput = remoteCall.getOutputStream();
        objectOutput.writeObject(paramString);
        objectOutput.writeObject(paramRemote);
      } catch (IOException iOException) {
        throw new MarshalException("error marshalling arguments", iOException);
      } 
      this.ref.invoke(remoteCall);
      this.ref.done(remoteCall);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (AlreadyBoundException alreadyBoundException) {
      throw alreadyBoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public String[] list() throws AccessException, RemoteException {
    try {
      String[] arrayOfString;
      remoteCall = this.ref.newCall(this, operations, 1, 4905912898345647071L);
      this.ref.invoke(remoteCall);
      try {
        ObjectInput objectInput = remoteCall.getInputStream();
        arrayOfString = (String[])objectInput.readObject();
      } catch (IOException iOException) {
        throw new UnmarshalException("error unmarshalling return", iOException);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new UnmarshalException("error unmarshalling return", classNotFoundException);
      } finally {
        this.ref.done(remoteCall);
      } 
      return arrayOfString;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Remote lookup(String paramString) throws AccessException, NotBoundException, RemoteException {
    try {
      Remote remote;
      remoteCall = this.ref.newCall(this, operations, 2, 4905912898345647071L);
      try {
        remote = remoteCall.getOutputStream();
        remote.writeObject(paramString);
      } catch (IOException null) {
        throw new MarshalException("error marshalling arguments", remote);
      } 
      this.ref.invoke(remoteCall);
      try {
        ObjectInput objectInput = remoteCall.getInputStream();
        remote = (Remote)objectInput.readObject();
      } catch (IOException iOException) {
        throw new UnmarshalException("error unmarshalling return", iOException);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new UnmarshalException("error unmarshalling return", classNotFoundException);
      } finally {
        this.ref.done(remoteCall);
      } 
      return remote;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (NotBoundException notBoundException) {
      throw notBoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void rebind(String paramString, Remote paramRemote) throws AccessException, AlreadyBoundException, RemoteException {
    try {
      RemoteCall remoteCall = this.ref.newCall(this, operations, 3, 4905912898345647071L);
      try {
        ObjectOutput objectOutput = remoteCall.getOutputStream();
        objectOutput.writeObject(paramString);
        objectOutput.writeObject(paramRemote);
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
  
  public void unbind(String paramString) throws AccessException, NotBoundException, RemoteException {
    try {
      RemoteCall remoteCall = this.ref.newCall(this, operations, 4, 4905912898345647071L);
      try {
        ObjectOutput objectOutput = remoteCall.getOutputStream();
        objectOutput.writeObject(paramString);
      } catch (IOException iOException) {
        throw new MarshalException("error marshalling arguments", iOException);
      } 
      this.ref.invoke(remoteCall);
      this.ref.done(remoteCall);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (NotBoundException notBoundException) {
      throw notBoundException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\registry\RegistryImpl_Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */