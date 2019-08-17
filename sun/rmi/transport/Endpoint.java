package sun.rmi.transport;

import java.rmi.RemoteException;

public interface Endpoint {
  Channel getChannel();
  
  void exportObject(Target paramTarget) throws RemoteException;
  
  Transport getInboundTransport();
  
  Transport getOutboundTransport();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\Endpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */