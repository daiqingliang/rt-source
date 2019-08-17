package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class AdapterNonExistent extends UserException {
  public AdapterNonExistent() { super(AdapterNonExistentHelper.id()); }
  
  public AdapterNonExistent(String paramString) { super(AdapterNonExistentHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\AdapterNonExistent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */