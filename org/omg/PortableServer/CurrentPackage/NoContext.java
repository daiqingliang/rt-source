package org.omg.PortableServer.CurrentPackage;

import org.omg.CORBA.UserException;

public final class NoContext extends UserException {
  public NoContext() { super(NoContextHelper.id()); }
  
  public NoContext(String paramString) { super(NoContextHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\CurrentPackage\NoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */