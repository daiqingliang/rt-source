package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ByteHolder implements Streamable {
  public byte value;
  
  public ByteHolder() {}
  
  public ByteHolder(byte paramByte) { this.value = paramByte; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_octet(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_octet(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_octet); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ByteHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */