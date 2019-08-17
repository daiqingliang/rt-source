package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class NoServant extends UserException {
  public NoServant() { super(NoServantHelper.id()); }
  
  public NoServant(String paramString) { super(NoServantHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\NoServant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */