package org.omg.stub.javax.management.remote.rmi;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.SerializablePermission;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import javax.management.remote.rmi.RMIConnection;
import javax.management.remote.rmi.RMIServer;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;

public class _RMIServer_Stub extends Stub implements RMIServer {
  private static final String[] _type_ids = { "RMI:javax.management.remote.rmi.RMIServer:0000000000000000" };
  
  private boolean _instantiated = false;
  
  public _RMIServer_Stub() {
    this(checkPermission());
    this._instantiated = true;
  }
  
  private _RMIServer_Stub(Void paramVoid) {}
  
  public String[] _ids() { return (String[])_type_ids.clone(); }
  
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation")); 
    return null;
  }
  
  public String getVersion() throws RemoteException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = _request("_get_version", true);
          inputStream = (InputStream)_invoke(outputStream);
          return (String)inputStream.read_value(String.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getVersion();
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("_get_version", RMIServer.class);
    if (servantObject == null)
      return getVersion(); 
    try {
      return ((RMIServer)servantObject.servant).getVersion();
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public RMIConnection newClient(Object paramObject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = _request("newClient", true);
          Util.writeAny(outputStream, paramObject);
          inputStream = (InputStream)_invoke(outputStream);
          return (RMIConnection)PortableRemoteObject.narrow(inputStream.read_Object(), RMIConnection.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return newClient(paramObject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("newClient", RMIServer.class);
    if (servantObject == null)
      return newClient(paramObject); 
    try {
      Object object = Util.copyObject(paramObject, _orb());
      RMIConnection rMIConnection = ((RMIServer)servantObject.servant).newClient(object);
      return (RMIConnection)Util.copyObject(rMIConnection, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    checkPermission();
    paramObjectInputStream.defaultReadObject();
    this._instantiated = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\stub\javax\management\remote\rmi\_RMIServer_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */