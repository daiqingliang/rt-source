package java.rmi.activation;

import java.io.Serializable;
import java.rmi.server.UID;

public class ActivationGroupID implements Serializable {
  private ActivationSystem system;
  
  private UID uid = new UID();
  
  private static final long serialVersionUID = -1648432278909740833L;
  
  public ActivationGroupID(ActivationSystem paramActivationSystem) { this.system = paramActivationSystem; }
  
  public ActivationSystem getSystem() { return this.system; }
  
  public int hashCode() { return this.uid.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ActivationGroupID) {
      ActivationGroupID activationGroupID = (ActivationGroupID)paramObject;
      return (this.uid.equals(activationGroupID.uid) && this.system.equals(activationGroupID.system));
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivationGroupID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */