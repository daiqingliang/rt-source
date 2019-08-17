package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class WrongAdapter extends UserException {
  public WrongAdapter() { super(WrongAdapterHelper.id()); }
  
  public WrongAdapter(String paramString) { super(WrongAdapterHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\WrongAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */