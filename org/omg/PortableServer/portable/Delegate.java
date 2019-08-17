package org.omg.PortableServer.portable;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public interface Delegate {
  ORB orb(Servant paramServant);
  
  Object this_object(Servant paramServant);
  
  POA poa(Servant paramServant);
  
  byte[] object_id(Servant paramServant);
  
  POA default_POA(Servant paramServant);
  
  boolean is_a(Servant paramServant, String paramString);
  
  boolean non_existent(Servant paramServant);
  
  Object get_interface_def(Servant paramServant);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\portable\Delegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */