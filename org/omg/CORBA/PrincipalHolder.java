package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

@Deprecated
public final class PrincipalHolder implements Streamable {
  public Principal value;
  
  public PrincipalHolder() {}
  
  public PrincipalHolder(Principal paramPrincipal) { this.value = paramPrincipal; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_Principal(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_Principal(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_Principal); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PrincipalHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */