package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class FormatMismatch extends UserException {
  public FormatMismatch() { super(FormatMismatchHelper.id()); }
  
  public FormatMismatch(String paramString) { super(FormatMismatchHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\CodecPackage\FormatMismatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */