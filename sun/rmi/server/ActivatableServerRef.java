package sun.rmi.server;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutput;
import java.rmi.activation.ActivationID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteRef;
import sun.rmi.transport.LiveRef;

public class ActivatableServerRef extends UnicastServerRef2 {
  private static final long serialVersionUID = 2002967993223003793L;
  
  private ActivationID id;
  
  public ActivatableServerRef(ActivationID paramActivationID, int paramInt) { this(paramActivationID, paramInt, null, null); }
  
  public ActivatableServerRef(ActivationID paramActivationID, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) {
    super(new LiveRef(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory));
    this.id = paramActivationID;
  }
  
  public String getRefClass(ObjectOutput paramObjectOutput) { return "ActivatableServerRef"; }
  
  protected RemoteRef getClientRef() { return new ActivatableRef(this.id, new UnicastRef2(this.ref)); }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException { throw new NotSerializableException("ActivatableServerRef not serializable"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\ActivatableServerRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */