package java.rmi.activation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

public class ActivationID implements Serializable {
  private Activator activator;
  
  private UID uid = new UID();
  
  private static final long serialVersionUID = -4608673054848209235L;
  
  private static final AccessControlContext NOPERMS_ACC;
  
  public ActivationID(Activator paramActivator) { this.activator = paramActivator; }
  
  public Remote activate(boolean paramBoolean) throws ActivationException, UnknownObjectException, RemoteException {
    try {
      final MarshalledObject mobj = this.activator.activate(this, paramBoolean);
      return (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>() {
            public Remote run() throws IOException, ClassNotFoundException { return (Remote)mobj.get(); }
          },  NOPERMS_ACC);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = privilegedActionException.getException();
      if (exception instanceof RemoteException)
        throw (RemoteException)exception; 
      throw new UnmarshalException("activation failed", exception);
    } 
  }
  
  public int hashCode() { return this.uid.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ActivationID) {
      ActivationID activationID = (ActivationID)paramObject;
      return (this.uid.equals(activationID.uid) && this.activator.equals(activationID.activator));
    } 
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    RemoteRef remoteRef;
    paramObjectOutputStream.writeObject(this.uid);
    if (this.activator instanceof RemoteObject) {
      remoteRef = ((RemoteObject)this.activator).getRef();
    } else if (Proxy.isProxyClass(this.activator.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(this.activator);
      if (!(invocationHandler instanceof RemoteObjectInvocationHandler))
        throw new InvalidObjectException("unexpected invocation handler"); 
      remoteRef = ((RemoteObjectInvocationHandler)invocationHandler).getRef();
    } else {
      throw new InvalidObjectException("unexpected activator type");
    } 
    paramObjectOutputStream.writeUTF(remoteRef.getRefClass(paramObjectOutputStream));
    remoteRef.writeExternal(paramObjectOutputStream);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.uid = (UID)paramObjectInputStream.readObject();
    try {
      Class clazz = Class.forName("sun.rmi.server." + paramObjectInputStream.readUTF()).asSubclass(RemoteRef.class);
      RemoteRef remoteRef = (RemoteRef)clazz.newInstance();
      remoteRef.readExternal(paramObjectInputStream);
      this.activator = (Activator)Proxy.newProxyInstance(null, new Class[] { Activator.class }, new RemoteObjectInvocationHandler(remoteRef));
    } catch (InstantiationException instantiationException) {
      throw (IOException)(new InvalidObjectException("Unable to create remote reference")).initCause(instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw (IOException)(new InvalidObjectException("Unable to create remote reference")).initCause(illegalAccessException);
    } 
  }
  
  static  {
    Permissions permissions = new Permissions();
    ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, permissions) };
    NOPERMS_ACC = new AccessControlContext(arrayOfProtectionDomain);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivationID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */