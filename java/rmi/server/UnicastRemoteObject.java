package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.transport.ObjectTable;

public class UnicastRemoteObject extends RemoteServer {
  private int port = 0;
  
  private RMIClientSocketFactory csf = null;
  
  private RMIServerSocketFactory ssf = null;
  
  private static final long serialVersionUID = 4974527148936298033L;
  
  protected UnicastRemoteObject() throws RemoteException { this(0); }
  
  protected UnicastRemoteObject(int paramInt) throws RemoteException {
    this.port = paramInt;
    exportObject(this, paramInt);
  }
  
  protected UnicastRemoteObject(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) throws RemoteException {
    this.port = paramInt;
    this.csf = paramRMIClientSocketFactory;
    this.ssf = paramRMIServerSocketFactory;
    exportObject(this, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    reexport();
  }
  
  public Object clone() throws CloneNotSupportedException {
    try {
      UnicastRemoteObject unicastRemoteObject = (UnicastRemoteObject)super.clone();
      unicastRemoteObject.reexport();
      return unicastRemoteObject;
    } catch (RemoteException remoteException) {
      throw new ServerCloneException("Clone failed", remoteException);
    } 
  }
  
  private void reexport() throws RemoteException {
    if (this.csf == null && this.ssf == null) {
      exportObject(this, this.port);
    } else {
      exportObject(this, this.port, this.csf, this.ssf);
    } 
  }
  
  @Deprecated
  public static RemoteStub exportObject(Remote paramRemote) throws RemoteException { return (RemoteStub)exportObject(paramRemote, new UnicastServerRef(true)); }
  
  public static Remote exportObject(Remote paramRemote, int paramInt) throws RemoteException { return exportObject(paramRemote, new UnicastServerRef(paramInt)); }
  
  public static Remote exportObject(Remote paramRemote, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) throws RemoteException { return exportObject(paramRemote, new UnicastServerRef2(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory)); }
  
  public static boolean unexportObject(Remote paramRemote, boolean paramBoolean) throws NoSuchObjectException { return ObjectTable.unexportObject(paramRemote, paramBoolean); }
  
  private static Remote exportObject(Remote paramRemote, UnicastServerRef paramUnicastServerRef) throws RemoteException {
    if (paramRemote instanceof UnicastRemoteObject)
      ((UnicastRemoteObject)paramRemote).ref = paramUnicastServerRef; 
    return paramUnicastServerRef.exportObject(paramRemote, null, false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\UnicastRemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */