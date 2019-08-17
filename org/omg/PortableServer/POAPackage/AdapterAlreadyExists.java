package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class AdapterAlreadyExists extends UserException {
  public AdapterAlreadyExists() { super(AdapterAlreadyExistsHelper.id()); }
  
  public AdapterAlreadyExists(String paramString) { super(AdapterAlreadyExistsHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\AdapterAlreadyExists.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */