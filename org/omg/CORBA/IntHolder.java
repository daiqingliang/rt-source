package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class IntHolder implements Streamable {
  public int value;
  
  public IntHolder() {}
  
  public IntHolder(int paramInt) { this.value = paramInt; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_long(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_long(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_long); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\IntHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */