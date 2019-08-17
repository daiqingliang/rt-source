package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidNameHolder implements Streamable {
  public InvalidName value = null;
  
  public InvalidNameHolder() {}
  
  public InvalidNameHolder(InvalidName paramInvalidName) { this.value = paramInvalidName; }
  
  public void _read(InputStream paramInputStream) { this.value = InvalidNameHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { InvalidNameHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return InvalidNameHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\InvalidNameHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */