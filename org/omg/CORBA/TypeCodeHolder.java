package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TypeCodeHolder implements Streamable {
  public TypeCode value;
  
  public TypeCodeHolder() {}
  
  public TypeCodeHolder(TypeCode paramTypeCode) { this.value = paramTypeCode; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_TypeCode(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_TypeCode(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_TypeCode); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\TypeCodeHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */