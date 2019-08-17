package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ObjectNotActive extends UserException {
  public ObjectNotActive() { super(ObjectNotActiveHelper.id()); }
  
  public ObjectNotActive(String paramString) { super(ObjectNotActiveHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\ObjectNotActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */