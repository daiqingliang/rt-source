package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class InvalidTypeForEncoding extends UserException {
  public InvalidTypeForEncoding() { super(InvalidTypeForEncodingHelper.id()); }
  
  public InvalidTypeForEncoding(String paramString) { super(InvalidTypeForEncodingHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\CodecPackage\InvalidTypeForEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */