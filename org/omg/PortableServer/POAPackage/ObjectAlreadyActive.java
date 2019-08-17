package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ObjectAlreadyActive extends UserException {
  public ObjectAlreadyActive() { super(ObjectAlreadyActiveHelper.id()); }
  
  public ObjectAlreadyActive(String paramString) { super(ObjectAlreadyActiveHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\ObjectAlreadyActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */