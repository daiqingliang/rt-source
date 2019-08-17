package javax.management.remote.rmi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RMIServerImpl_Stub extends RemoteStub implements RMIServer {
  private static final long serialVersionUID = 2L;
  
  private static Method $method_getVersion_0;
  
  private static Method $method_newClient_1;
  
  static  {
    try {
      $method_getVersion_0 = RMIServer.class.getMethod("getVersion", new Class[0]);
      $method_newClient_1 = RMIServer.class.getMethod("newClient", new Class[] { Object.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError("stub class initialization failed");
    } 
  }
  
  public RMIServerImpl_Stub(RemoteRef paramRemoteRef) { super(paramRemoteRef); }
  
  public String getVersion() throws RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getVersion_0, null, -8081107751519807347L);
      return (String)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public RMIConnection newClient(Object paramObject) throws IOException {
    try {
      Object object = this.ref.invoke(this, $method_newClient_1, new Object[] { paramObject }, -1089742558549201240L);
      return (RMIConnection)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIServerImpl_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */