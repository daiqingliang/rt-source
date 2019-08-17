package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Activator extends Remote {
  MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean) throws ActivationException, UnknownObjectException, RemoteException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\Activator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */