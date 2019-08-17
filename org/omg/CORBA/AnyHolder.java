package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AnyHolder implements Streamable {
  public Any value;
  
  public AnyHolder() {}
  
  public AnyHolder(Any paramAny) { this.value = paramAny; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_any(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_any(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_any); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\AnyHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */