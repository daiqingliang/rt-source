package org.omg.PortableInterceptor;

import org.omg.CORBA.UserException;

public final class InvalidSlot extends UserException {
  public InvalidSlot() { super(InvalidSlotHelper.id()); }
  
  public InvalidSlot(String paramString) { super(InvalidSlotHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\InvalidSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */