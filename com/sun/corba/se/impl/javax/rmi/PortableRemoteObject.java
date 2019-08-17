package com.sun.corba.se.impl.javax.rmi;

import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;

public class PortableRemoteObject implements PortableRemoteObjectDelegate {
  public void exportObject(Remote paramRemote) throws RemoteException {
    if (paramRemote == null)
      throw new NullPointerException("invalid argument"); 
    if (Util.getTie(paramRemote) != null)
      throw new ExportException(paramRemote.getClass().getName() + " already exported"); 
    Tie tie = Utility.loadTie(paramRemote);
    if (tie != null) {
      Util.registerTarget(tie, paramRemote);
    } else {
      UnicastRemoteObject.exportObject(paramRemote);
    } 
  }
  
  public Remote toStub(Remote paramRemote) throws NoSuchObjectException {
    Remote remote = null;
    if (paramRemote == null)
      throw new NullPointerException("invalid argument"); 
    if (StubAdapter.isStub(paramRemote))
      return paramRemote; 
    if (paramRemote instanceof java.rmi.server.RemoteStub)
      return paramRemote; 
    Tie tie = Util.getTie(paramRemote);
    if (tie != null) {
      remote = Utility.loadStub(tie, null, null, true);
    } else if (Utility.loadTie(paramRemote) == null) {
      remote = RemoteObject.toStub(paramRemote);
    } 
    if (remote == null)
      throw new NoSuchObjectException("object not exported"); 
    return remote;
  }
  
  public void unexportObject(Remote paramRemote) throws RemoteException {
    if (paramRemote == null)
      throw new NullPointerException("invalid argument"); 
    if (StubAdapter.isStub(paramRemote) || paramRemote instanceof java.rmi.server.RemoteStub)
      throw new NoSuchObjectException("Can only unexport a server object."); 
    Tie tie = Util.getTie(paramRemote);
    if (tie != null) {
      Util.unexportObject(paramRemote);
    } else if (Utility.loadTie(paramRemote) == null) {
      UnicastRemoteObject.unexportObject(paramRemote, true);
    } else {
      throw new NoSuchObjectException("Object not exported.");
    } 
  }
  
  public Object narrow(Object paramObject, Class paramClass) throws ClassCastException {
    Object object = null;
    if (paramObject == null)
      return null; 
    if (paramClass == null)
      throw new NullPointerException("invalid argument"); 
    try {
      if (paramClass.isAssignableFrom(paramObject.getClass()))
        return paramObject; 
      if (paramClass.isInterface() && paramClass != java.io.Serializable.class && paramClass != java.io.Externalizable.class) {
        Object object1 = (Object)paramObject;
        String str = RepositoryId.createForAnyType(paramClass);
        if (object1._is_a(str))
          return Utility.loadStub(object1, paramClass); 
        throw new ClassCastException("Object is not of remote type " + paramClass.getName());
      } 
      throw new ClassCastException("Class " + paramClass.getName() + " is not a valid remote interface");
    } catch (Exception exception) {
      ClassCastException classCastException = new ClassCastException();
      classCastException.initCause(exception);
      throw classCastException;
    } 
  }
  
  public void connect(Remote paramRemote1, Remote paramRemote2) throws RemoteException {
    if (paramRemote1 == null || paramRemote2 == null)
      throw new NullPointerException("invalid argument"); 
    ORB oRB = null;
    try {
      if (StubAdapter.isStub(paramRemote2)) {
        oRB = StubAdapter.getORB(paramRemote2);
      } else {
        Tie tie1 = Util.getTie(paramRemote2);
        if (tie1 != null)
          oRB = tie1.orb(); 
      } 
    } catch (SystemException systemException) {
      throw new RemoteException("'source' object not connected", systemException);
    } 
    boolean bool = false;
    Tie tie = null;
    if (StubAdapter.isStub(paramRemote1)) {
      bool = true;
    } else {
      tie = Util.getTie(paramRemote1);
      if (tie != null)
        bool = true; 
    } 
    if (!bool) {
      if (oRB != null)
        throw new RemoteException("'source' object exported to IIOP, 'target' is JRMP"); 
    } else {
      if (oRB == null)
        throw new RemoteException("'source' object is JRMP, 'target' is IIOP"); 
      try {
        if (tie != null) {
          try {
            ORB oRB1 = tie.orb();
            if (oRB1 == oRB)
              return; 
            throw new RemoteException("'target' object was already connected");
          } catch (SystemException systemException) {
            tie.orb(oRB);
          } 
        } else {
          StubAdapter.connect(paramRemote1, oRB);
        } 
      } catch (SystemException systemException) {
        throw new RemoteException("'target' object was already connected", systemException);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\javax\rmi\PortableRemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */