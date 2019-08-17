package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.UserException;

public final class AdapterInactive extends UserException {
  public AdapterInactive() { super(AdapterInactiveHelper.id()); }
  
  public AdapterInactive(String paramString) { super(AdapterInactiveHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAManagerPackage\AdapterInactive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */