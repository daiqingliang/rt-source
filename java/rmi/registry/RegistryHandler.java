package java.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

@Deprecated
public interface RegistryHandler {
  @Deprecated
  Registry registryStub(String paramString, int paramInt) throws RemoteException, UnknownHostException;
  
  @Deprecated
  Registry registryImpl(int paramInt) throws RemoteException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\registry\RegistryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */