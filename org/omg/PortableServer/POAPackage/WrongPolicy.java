package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class WrongPolicy extends UserException {
  public WrongPolicy() { super(WrongPolicyHelper.id()); }
  
  public WrongPolicy(String paramString) { super(WrongPolicyHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\WrongPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */