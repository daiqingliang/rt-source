package sun.rmi.server;

import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;

public abstract class ActivationGroupInit {
  public static void main(String[] paramArrayOfString) {
    try {
      if (System.getSecurityManager() == null)
        System.setSecurityManager(new SecurityManager()); 
      marshalInputStream = new MarshalInputStream(System.in);
      ActivationGroupID activationGroupID = (ActivationGroupID)marshalInputStream.readObject();
      ActivationGroupDesc activationGroupDesc = (ActivationGroupDesc)marshalInputStream.readObject();
      long l = marshalInputStream.readLong();
      ActivationGroup.createGroup(activationGroupID, activationGroupDesc, l);
    } catch (Exception exception) {
      System.err.println("Exception in starting ActivationGroupInit:");
      exception.printStackTrace();
    } finally {
      try {
        System.in.close();
      } catch (Exception exception) {}
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\ActivationGroupInit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */