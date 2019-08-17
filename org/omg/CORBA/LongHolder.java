package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongHolder implements Streamable {
  public long value;
  
  public LongHolder() {}
  
  public LongHolder(long paramLong) { this.value = paramLong; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_longlong(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_longlong(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_longlong); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\LongHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */