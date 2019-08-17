package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidAddressHolder implements Streamable {
  public InvalidAddress value = null;
  
  public InvalidAddressHolder() {}
  
  public InvalidAddressHolder(InvalidAddress paramInvalidAddress) { this.value = paramInvalidAddress; }
  
  public void _read(InputStream paramInputStream) { this.value = InvalidAddressHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { InvalidAddressHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return InvalidAddressHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextExtPackage\InvalidAddressHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */