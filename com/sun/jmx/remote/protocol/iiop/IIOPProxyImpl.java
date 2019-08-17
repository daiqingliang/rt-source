package com.sun.jmx.remote.protocol.iiop;

import com.sun.jmx.remote.internal.IIOPProxy;
import java.io.SerializablePermission;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Properties;
import javax.rmi.CORBA.Stub;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;

public class IIOPProxyImpl implements IIOPProxy {
  private static final AccessControlContext STUB_ACC;
  
  public boolean isStub(Object paramObject) { return paramObject instanceof Stub; }
  
  public Object getDelegate(Object paramObject) { return ((Stub)paramObject)._get_delegate(); }
  
  public void setDelegate(Object paramObject1, Object paramObject2) { ((Stub)paramObject1)._set_delegate((Delegate)paramObject2); }
  
  public Object getOrb(Object paramObject) {
    try {
      return ((Stub)paramObject)._orb();
    } catch (BAD_OPERATION bAD_OPERATION) {
      throw new UnsupportedOperationException(bAD_OPERATION);
    } 
  }
  
  public void connect(Object paramObject1, Object paramObject2) { ((Stub)paramObject1).connect((ORB)paramObject2); }
  
  public boolean isOrb(Object paramObject) { return paramObject instanceof ORB; }
  
  public Object createOrb(String[] paramArrayOfString, Properties paramProperties) { return ORB.init(paramArrayOfString, paramProperties); }
  
  public Object stringToObject(Object paramObject, String paramString) { return ((ORB)paramObject).string_to_object(paramString); }
  
  public String objectToString(Object paramObject1, Object paramObject2) { return ((ORB)paramObject1).object_to_string((Object)paramObject2); }
  
  public <T> T narrow(Object paramObject, Class<T> paramClass) { return (T)PortableRemoteObject.narrow(paramObject, paramClass); }
  
  public void exportObject(Remote paramRemote) throws RemoteException { PortableRemoteObject.exportObject(paramRemote); }
  
  public void unexportObject(Remote paramRemote) throws RemoteException { PortableRemoteObject.unexportObject(paramRemote); }
  
  public Remote toStub(final Remote obj) throws NoSuchObjectException {
    if (System.getSecurityManager() == null)
      return PortableRemoteObject.toStub(paramRemote); 
    try {
      return (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>() {
            public Remote run() throws Exception { return PortableRemoteObject.toStub(obj); }
          },  STUB_ACC);
    } catch (PrivilegedActionException privilegedActionException) {
      if (privilegedActionException.getException() instanceof NoSuchObjectException)
        throw (NoSuchObjectException)privilegedActionException.getException(); 
      throw new RuntimeException("Unexpected exception type", privilegedActionException.getException());
    } 
  }
  
  static  {
    Permissions permissions = new Permissions();
    permissions.add(new SerializablePermission("enableSubclassImplementation"));
    STUB_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\protocol\iiop\IIOPProxyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */