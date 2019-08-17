package org.omg.PortableServer;

public interface ServantActivatorOperations extends ServantManagerOperations {
  Servant incarnate(byte[] paramArrayOfByte, POA paramPOA) throws ForwardRequest;
  
  void etherealize(byte[] paramArrayOfByte, POA paramPOA, Servant paramServant, boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\ServantActivatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */