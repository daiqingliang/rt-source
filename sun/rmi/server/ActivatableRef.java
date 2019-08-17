package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;
import java.rmi.UnmarshalException;
import java.rmi.activation.ActivateFailedException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.Operation;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public class ActivatableRef implements RemoteRef {
  private static final long serialVersionUID = 7579060052569229166L;
  
  protected ActivationID id;
  
  protected RemoteRef ref;
  
  boolean force = false;
  
  private static final int MAX_RETRIES = 3;
  
  private static final String versionComplaint = "activation requires 1.2 stubs";
  
  public ActivatableRef() {}
  
  public ActivatableRef(ActivationID paramActivationID, RemoteRef paramRemoteRef) {
    this.id = paramActivationID;
    this.ref = paramRemoteRef;
  }
  
  public static Remote getStub(ActivationDesc paramActivationDesc, ActivationID paramActivationID) throws StubNotFoundException {
    String str = paramActivationDesc.getClassName();
    try {
      Class clazz = RMIClassLoader.loadClass(paramActivationDesc.getLocation(), str);
      ActivatableRef activatableRef = new ActivatableRef(paramActivationID, null);
      return Util.createProxy(clazz, activatableRef, false);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new StubNotFoundException("class implements an illegal remote interface", illegalArgumentException);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new StubNotFoundException("unable to load class: " + str, classNotFoundException);
    } catch (MalformedURLException malformedURLException) {
      throw new StubNotFoundException("malformed URL", malformedURLException);
    } 
  }
  
  public Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong) throws Exception {
    RemoteRef remoteRef;
    boolean bool = false;
    ConnectIOException connectIOException = null;
    synchronized (this) {
      if (this.ref == null) {
        remoteRef = activate(bool);
        bool = true;
      } else {
        remoteRef = this.ref;
      } 
    } 
    for (byte b = 3; b > 0; b--) {
      try {
        return remoteRef.invoke(paramRemote, paramMethod, paramArrayOfObject, paramLong);
      } catch (NoSuchObjectException noSuchObjectException) {
        connectIOException = noSuchObjectException;
      } catch (ConnectException connectException) {
        connectIOException = connectException;
      } catch (UnknownHostException unknownHostException) {
        connectIOException = unknownHostException;
      } catch (ConnectIOException connectIOException1) {
        connectIOException = connectIOException1;
      } catch (MarshalException marshalException) {
        throw marshalException;
      } catch (ServerError serverError) {
        throw serverError;
      } catch (ServerException serverException) {
        throw serverException;
      } catch (RemoteException remoteException) {
        synchronized (this) {
          if (remoteRef == this.ref)
            this.ref = null; 
        } 
        throw remoteException;
      } 
      if (b > 1)
        synchronized (this) {
          if (remoteRef.remoteEquals(this.ref) || this.ref == null) {
            RemoteRef remoteRef1 = activate(bool);
            if (remoteRef1.remoteEquals(remoteRef) && connectIOException instanceof NoSuchObjectException && !bool)
              remoteRef1 = activate(true); 
            remoteRef = remoteRef1;
            bool = true;
          } else {
            remoteRef = this.ref;
            bool = false;
          } 
        }  
    } 
    throw connectIOException;
  }
  
  private RemoteRef getRef() throws RemoteException {
    if (this.ref == null)
      this.ref = activate(false); 
    return this.ref;
  }
  
  private RemoteRef activate(boolean paramBoolean) throws RemoteException {
    assert Thread.holdsLock(this);
    this.ref = null;
    try {
      Remote remote = this.id.activate(paramBoolean);
      ActivatableRef activatableRef = null;
      if (remote instanceof RemoteStub) {
        activatableRef = (ActivatableRef)((RemoteStub)remote).getRef();
      } else {
        RemoteObjectInvocationHandler remoteObjectInvocationHandler = (RemoteObjectInvocationHandler)Proxy.getInvocationHandler(remote);
        activatableRef = (ActivatableRef)remoteObjectInvocationHandler.getRef();
      } 
      this.ref = activatableRef.ref;
      return this.ref;
    } catch (ConnectException connectException) {
      throw new ConnectException("activation failed", connectException);
    } catch (RemoteException remoteException) {
      throw new ConnectIOException("activation failed", remoteException);
    } catch (UnknownObjectException unknownObjectException) {
      throw new NoSuchObjectException("object not registered");
    } catch (ActivationException activationException) {
      throw new ActivateFailedException("activation failed", activationException);
    } 
  }
  
  public RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong) throws RemoteException { throw new UnsupportedOperationException("activation requires 1.2 stubs"); }
  
  public void invoke(RemoteCall paramRemoteCall) throws Exception { throw new UnsupportedOperationException("activation requires 1.2 stubs"); }
  
  public void done(RemoteCall paramRemoteCall) throws Exception { throw new UnsupportedOperationException("activation requires 1.2 stubs"); }
  
  public String getRefClass(ObjectOutput paramObjectOutput) { return "ActivatableRef"; }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    RemoteRef remoteRef = this.ref;
    paramObjectOutput.writeObject(this.id);
    if (remoteRef == null) {
      paramObjectOutput.writeUTF("");
    } else {
      paramObjectOutput.writeUTF(remoteRef.getRefClass(paramObjectOutput));
      remoteRef.writeExternal(paramObjectOutput);
    } 
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    this.id = (ActivationID)paramObjectInput.readObject();
    this.ref = null;
    String str = paramObjectInput.readUTF();
    if (str.equals(""))
      return; 
    try {
      Class clazz = Class.forName("sun.rmi.server." + str);
      this.ref = (RemoteRef)clazz.newInstance();
      this.ref.readExternal(paramObjectInput);
    } catch (InstantiationException instantiationException) {
      throw new UnmarshalException("Unable to create remote reference", instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new UnmarshalException("Illegal access creating remote reference");
    } 
  }
  
  public String remoteToString() { return Util.getUnqualifiedName(getClass()) + " [remoteRef: " + this.ref + "]"; }
  
  public int remoteHashCode() { return this.id.hashCode(); }
  
  public boolean remoteEquals(RemoteRef paramRemoteRef) { return (paramRemoteRef instanceof ActivatableRef) ? this.id.equals(((ActivatableRef)paramRemoteRef).id) : 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\ActivatableRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */