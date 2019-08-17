package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantNotActive extends UserException {
  public ServantNotActive() { super(ServantNotActiveHelper.id()); }
  
  public ServantNotActive(String paramString) { super(ServantNotActiveHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\ServantNotActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */