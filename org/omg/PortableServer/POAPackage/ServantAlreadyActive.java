package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantAlreadyActive extends UserException {
  public ServantAlreadyActive() { super(ServantAlreadyActiveHelper.id()); }
  
  public ServantAlreadyActive(String paramString) { super(ServantAlreadyActiveHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\ServantAlreadyActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */