package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public interface UtilDelegate {
  RemoteException mapSystemException(SystemException paramSystemException);
  
  void writeAny(OutputStream paramOutputStream, Object paramObject);
  
  Object readAny(InputStream paramInputStream);
  
  void writeRemoteObject(OutputStream paramOutputStream, Object paramObject);
  
  void writeAbstractObject(OutputStream paramOutputStream, Object paramObject);
  
  void registerTarget(Tie paramTie, Remote paramRemote);
  
  void unexportObject(Remote paramRemote) throws NoSuchObjectException;
  
  Tie getTie(Remote paramRemote);
  
  ValueHandler createValueHandler();
  
  String getCodebase(Class paramClass);
  
  Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader) throws ClassNotFoundException;
  
  boolean isLocal(Stub paramStub) throws RemoteException;
  
  RemoteException wrapException(Throwable paramThrowable);
  
  Object copyObject(Object paramObject, ORB paramORB) throws RemoteException;
  
  Object[] copyObjects(Object[] paramArrayOfObject, ORB paramORB) throws RemoteException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\UtilDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */